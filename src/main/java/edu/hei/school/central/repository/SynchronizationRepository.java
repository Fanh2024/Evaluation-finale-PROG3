package edu.hei.school.central.repository;

import edu.hei.school.central.config.DataBaseConnexionCentral;
import edu.hei.school.central.model.*;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;

@Repository
public class SynchronizationRepository {

    public void saveClubs(List<Club> clubs) {
        String sql = """
            INSERT INTO Club(id, name, acronym, creation_year, stadium_name)
            VALUES (?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                name = EXCLUDED.name,
                acronym = EXCLUDED.acronym,
                creation_year = EXCLUDED.creation_year,
                stadium_name = EXCLUDED.stadium_name
        """;
        try (Connection conn = DataBaseConnexionCentral.getCentralConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Club club : clubs) {
                stmt.setString(1, club.getId());
                stmt.setString(2, club.getName());
                stmt.setString(3, club.getAcronym());
                stmt.setInt(4, club.getCreationYear());
                stmt.setString(5, club.getStadiumName());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'insertion des clubs", e);
        }
    }

    public void savePlayers(List<Player> players) {
        String sql = """
            INSERT INTO Player(id, name, number, position, nationality, age, club_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                name = EXCLUDED.name,
                number = EXCLUDED.number,
                position = EXCLUDED.position,
                nationality = EXCLUDED.nationality,
                age = EXCLUDED.age,
                club_id = EXCLUDED.club_id
        """;
        try (Connection conn = DataBaseConnexionCentral.getCentralConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (Player player : players) {
                stmt.setString(1, player.getId());
                stmt.setString(2, player.getName());
                stmt.setInt(3, player.getNumber());
                stmt.setString(4, player.getPosition());
                stmt.setString(5, player.getNationality());
                stmt.setInt(6, player.getAge());
                stmt.setString(7, player.getClub() != null ? player.getClub().getId() : null);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'insertion des joueurs", e);
        }
    }

    public void saveStatisticsClubs(List<StatisticsClub> statisticsClubs, int seasonYear) {
        if (statisticsClubs.isEmpty()) return;

        String getSeasonIdSQL = "SELECT id FROM season WHERE start_year = ?";
        String insertSql = """
            INSERT INTO Club_Statistics(id, club_id, season_id, points, goals_scored, goals_conceded, goal_difference, clean_sheets)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                points = EXCLUDED.points,
                goals_scored = EXCLUDED.goals_scored,
                goals_conceded = EXCLUDED.goals_conceded,
                goal_difference = EXCLUDED.goal_difference,
                clean_sheets = EXCLUDED.clean_sheets
        """;

        try (Connection conn = DataBaseConnexionCentral.getCentralConnection();
             PreparedStatement getSeasonStmt = conn.prepareStatement(getSeasonIdSQL);
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {

            getSeasonStmt.setInt(1, seasonYear);
            try (ResultSet rs = getSeasonStmt.executeQuery()) {
                if (!rs.next()) {
                    throw new RuntimeException("Aucune saison trouvée pour l'année : " + seasonYear);
                }

                String seasonId = rs.getString("id");

                for (StatisticsClub stats : statisticsClubs) {
                    stmt.setString(1, stats.getId());
                    stmt.setString(2, stats.getClub().getId());
                    stmt.setString(3, seasonId);
                    stmt.setInt(4, stats.getPoints());
                    stmt.setInt(5, stats.getGoalsScored());
                    stmt.setInt(6, stats.getGoalsConceded());
                    stmt.setInt(7, stats.getGoalDifference());
                    stmt.setInt(8, stats.getCleanSheets());
                    stmt.addBatch();
                }

                stmt.executeBatch();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'insertion des statistiques clubs", e);
        }
    }

    public void savePlayerStatistics(List<PlayerStatistics> playerStats) {
        String sql = """
            INSERT INTO Player_Statistics(id, player_id, season_id, assists, yellow_cards, red_cards, minutes_played)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                assists = EXCLUDED.assists,
                yellow_cards = EXCLUDED.yellow_cards,
                red_cards = EXCLUDED.red_cards,
                minutes_played = EXCLUDED.minutes_played
        """;
        try (Connection conn = DataBaseConnexionCentral.getCentralConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (PlayerStatistics stats : playerStats) {
                stmt.setString(1, stats.getId());
                stmt.setString(2, stats.getPlayer().getId());
                stmt.setString(3, stats.getSeason().getId());
                stmt.setInt(4, stats.getAssists());
                stmt.setInt(5, stats.getYellowCards());
                stmt.setInt(6, stats.getRedCards());
                stmt.setInt(7, stats.getMinutesPlayed());
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'insertion des statistiques joueurs", e);
        }
    }
}
