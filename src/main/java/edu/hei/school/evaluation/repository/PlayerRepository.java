package edu.hei.school.evaluation.repository;

import edu.hei.school.evaluation.config.DataBaseConnexion;
import edu.hei.school.evaluation.model.*;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class PlayerRepository {
    private final DataBaseConnexion dataBaseConnexion;

    public PlayerRepository(DataBaseConnexion dataBaseConnexion) {
        this.dataBaseConnexion = dataBaseConnexion;
    }

    public List<Player> findAllByFilters(String name, Integer ageMin, Integer ageMax, String clubName) {
        List<Player> players = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT p.id, p.name, p.number, p.position, p.nationality, p.age,
                   c.id AS c_id, c.name AS c_name, c.acronym AS c_acronym,
                   c.creation_year AS c_creation_year, c.stadium_name AS c_stadium_name,
                   ch.id AS ch_id, ch.name AS ch_name, ch.country AS ch_country
            FROM Player p
            LEFT JOIN Club c ON p.club_id = c.id
            LEFT JOIN Championship ch ON c.championship_id = ch.id
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (name != null) {
            sql.append(" AND LOWER(p.name) LIKE ?");
            params.add("%" + name.toLowerCase() + "%");
        }
        if (ageMin != null) {
            sql.append(" AND p.age >= ?");
            params.add(ageMin);
        }
        if (ageMax != null) {
            sql.append(" AND p.age <= ?");
            params.add(ageMax);
        }
        if (clubName != null) {
            sql.append(" AND LOWER(c.name) LIKE ?");
            params.add("%" + clubName.toLowerCase() + "%");
        }

        try (Connection connection = dataBaseConnexion.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                players.add(mapToPlayer(rs));
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving players", e);
        }

        return players;
    }

    public List<Player> saveOrUpdateAll(List<Player> players) {
        List<Player> saved = new ArrayList<>();

        String sql = """
            INSERT INTO Player (id, name, number, position, nationality, age, club_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
              name = EXCLUDED.name,
              number = EXCLUDED.number,
              position = EXCLUDED.position,
              nationality = EXCLUDED.nationality,
              age = EXCLUDED.age,
              club_id = EXCLUDED.club_id
        """;

        try (Connection connection = dataBaseConnexion.getConnection()) {
            for (Player player : players) {
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, player.getId());
                    stmt.setString(2, player.getName());
                    stmt.setInt(3, player.getNumber());
                    stmt.setString(4, player.getPosition());
                    stmt.setString(5, player.getNationality());
                    stmt.setInt(6, player.getAge());
                    if (player.getClub() != null) {
                        stmt.setString(7, player.getClub().getId());
                    } else {
                        stmt.setNull(7, Types.VARCHAR);
                    }
                    stmt.executeUpdate();
                    saved.add(player);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving/updating players", e);
        }

        return saved;
    }

    private Player mapToPlayer(ResultSet rs) throws SQLException {
        Championship championship = null;
        String championshipId = rs.getString("ch_id");

        if (championshipId != null) {
            championship = new Championship();
            championship.setId(championshipId);
            championship.setName(rs.getString("ch_name"));
            championship.setCountry(rs.getString("ch_country"));
        }

        Club club = null;
        String clubId = rs.getString("c_id");

        if (clubId != null) {
            club = new Club();
            club.setId(clubId);
            club.setName(rs.getString("c_name"));
            club.setAcronym(rs.getString("c_acronym"));
            club.setCreationYear(rs.getInt("c_creation_year"));
            club.setStadiumName(rs.getString("c_stadium_name"));
            club.setChampionship(championship);
        }

        return new Player(
                rs.getString("id"),
                rs.getString("name"),
                rs.getInt("number"),
                rs.getString("position"),
                rs.getString("nationality"),
                rs.getInt("age"),
                club
        );
    }


    public PlayerStatistics getPlayerStatistics(String playerId, String seasonYear) {
        PlayerStatistics playerStatistics = null;
        String sql = """
            SELECT ps.id, ps.goals, ps.assists, ps.yellow_cards, ps.red_cards, ps.minutes_played,
                   p.id AS player_id, p.name AS player_name, p.number, p.position, p.nationality, p.age,
                   s.id AS season_id, s.start_year, s.end_year
            FROM Player_Statistics ps
            JOIN Player p ON ps.player_id = p.id
            JOIN Season s ON ps.season_id = s.id
            WHERE ps.player_id = ? AND s.start_year = ?;
        """;

        try (Connection connection = dataBaseConnexion.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, playerId);
            stmt.setInt(2, Integer.parseInt(seasonYear));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Player player = new Player(
                        rs.getString("player_id"),
                        rs.getString("player_name"),
                        rs.getInt("number"),
                        rs.getString("position"),
                        rs.getString("nationality"),
                        rs.getInt("age"),
                        null // Le club n'est pas nécessaire ici
                );

                Season season = new Season(
                        rs.getString("season_id"),
                        rs.getInt("start_year"),
                        rs.getInt("end_year"),
                        null, // Le championnat n'est pas nécessaire ici
                        null
                );

                playerStatistics = new PlayerStatistics(
                        rs.getString("id"),
                        player,
                        season,
                        rs.getInt("goals"),
                        rs.getInt("assists"),
                        rs.getInt("yellow_cards"),
                        rs.getInt("red_cards"),
                        rs.getInt("minutes_played")
                );
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving player statistics", e);
        }

        return playerStatistics;
    }

    public PlayerStatistics saveOrUpdatePlayerStatistics(String playerId, String seasonId, PlayerStatistics stats) {
        String checkPlayerSql = "SELECT 1 FROM Player WHERE id = ?";
        String checkSeasonSql = "SELECT start_year FROM Season WHERE id = ?";

        String checkStatSql = "SELECT id FROM Player_Statistics WHERE player_id = ? AND season_id = ?";
        String updateSql = """
        UPDATE Player_Statistics
        SET goals = ?, assists = ?, yellow_cards = ?, red_cards = ?, minutes_played = ?
        WHERE player_id = ? AND season_id = ?
    """;
        String insertSql = """
        INSERT INTO Player_Statistics (id, player_id, season_id, goals, assists, yellow_cards, red_cards, minutes_played)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = dataBaseConnexion.getConnection()) {
            // Vérifie si le joueur existe
            try (PreparedStatement checkPlayer = conn.prepareStatement(checkPlayerSql)) {
                checkPlayer.setString(1, playerId);
                if (!checkPlayer.executeQuery().next()) {
                    throw new RuntimeException("Player not found with id: " + playerId);
                }
            }

            int startYear;
            // Vérifie si la saison existe
            try (PreparedStatement checkSeason = conn.prepareStatement(checkSeasonSql)) {
                checkSeason.setString(1, seasonId);
                ResultSet rs = checkSeason.executeQuery();
                if (rs.next()) {
                    startYear = rs.getInt("start_year");
                } else {
                    throw new RuntimeException("Season not found with id: " + seasonId);
                }
            }

            String statId;
            // Vérifie si les stats existent déjà
            try (PreparedStatement checkStat = conn.prepareStatement(checkStatSql)) {
                checkStat.setString(1, playerId);
                checkStat.setString(2, seasonId);
                ResultSet rs = checkStat.executeQuery();
                if (rs.next()) {
                    statId = rs.getString("id");

                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        updateStmt.setInt(1, stats.getGoals());
                        updateStmt.setInt(2, stats.getAssists());
                        updateStmt.setInt(3, stats.getYellowCards());
                        updateStmt.setInt(4, stats.getRedCards());
                        updateStmt.setInt(5, stats.getMinutesPlayed());
                        updateStmt.setString(6, playerId);
                        updateStmt.setString(7, seasonId);
                        updateStmt.executeUpdate();
                    }
                } else {
                    statId = "STAT_" + playerId + "_" + seasonId;
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setString(1, statId);
                        insertStmt.setString(2, playerId);
                        insertStmt.setString(3, seasonId);
                        insertStmt.setInt(4, stats.getGoals());
                        insertStmt.setInt(5, stats.getAssists());
                        insertStmt.setInt(6, stats.getYellowCards());
                        insertStmt.setInt(7, stats.getRedCards());
                        insertStmt.setInt(8, stats.getMinutesPlayed());
                        insertStmt.executeUpdate();
                    }
                }
            }

            return getPlayerStatistics(playerId, String.valueOf(startYear));
        } catch (SQLException e) {
            throw new RuntimeException("Error while saving player statistics", e);
        }
    }

}
