package edu.hei.school.central.repository;

import edu.hei.school.central.config.DataBaseConnexion;
import edu.hei.school.central.model.*;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BestPlayerRepository {
    private final DataBaseConnexion dataBaseConnexion;

    public BestPlayerRepository(DataBaseConnexion dataBaseConnexion) {
        this.dataBaseConnexion = dataBaseConnexion;
    }

    public List<PlayerStatistics> getAllPlayerStatisticsForLastSeason() {
        List<PlayerStatistics> stats = new ArrayList<>();

        String sql = """
            SELECT 
                ps.id AS ps_id,
                ps.assists, ps.yellow_cards, ps.red_cards, ps.minutes_played,

                p.id AS player_id, p.name AS player_name, p.number, p.position, p.nationality, p.age,

                c.id AS club_id, c.name AS club_name, c.acronym, c.creation_year, c.stadium_name,
                ch.id AS ch_id, ch.name AS ch_name, ch.country AS ch_country,

                s.id AS season_id, s.start_year, s.end_year,

                (
                    SELECT COUNT(*) 
                    FROM Goal g 
                    JOIN Match m ON g.match_id = m.id 
                    WHERE g.player_id = p.id 
                      AND g.is_own_goal = FALSE 
                      AND m.season_id = s.id
                ) AS goals

            FROM Player_Statistics ps
            JOIN Player p ON ps.player_id = p.id
            LEFT JOIN Club c ON p.club_id = c.id
            LEFT JOIN Championship ch ON c.championship_id = ch.id
            JOIN Season s ON ps.season_id = s.id
            WHERE s.start_year = (SELECT MAX(start_year) FROM Season)
        """;

        try (Connection connection = dataBaseConnexion.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Championship championship = rs.getString("ch_id") != null
                        ? new Championship(
                        rs.getString("ch_id"),
                        rs.getString("ch_name"),
                        rs.getString("ch_country"))
                        : null;

                Club club = rs.getString("club_id") != null
                        ? new Club(
                        rs.getString("club_id"),
                        rs.getString("club_name"),
                        rs.getString("acronym"),
                        rs.getInt("creation_year"),
                        rs.getString("stadium_name"),
                        championship)
                        : null;

                Player player = new Player(
                        rs.getString("player_id"),
                        rs.getString("player_name"),
                        rs.getInt("number"),
                        rs.getString("position"),
                        rs.getString("nationality"),
                        rs.getInt("age"),
                        club
                );

                Season season = new Season(
                        rs.getString("season_id"),
                        rs.getInt("start_year"),
                        rs.getInt("end_year"),
                        null, null
                );

                PlayerStatistics playerStats = new PlayerStatistics(
                        rs.getString("ps_id"),
                        player,
                        season,
                        rs.getInt("goals"),
                        rs.getInt("assists"),
                        rs.getInt("yellow_cards"),
                        rs.getInt("red_cards"),
                        rs.getInt("minutes_played")
                );

                stats.add(playerStats);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load player statistics", e);
        }

        return stats;
    }
}
