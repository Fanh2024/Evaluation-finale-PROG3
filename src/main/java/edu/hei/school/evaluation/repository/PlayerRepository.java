package edu.hei.school.evaluation.repository;

import edu.hei.school.evaluation.config.DataBaseConnexion;
import edu.hei.school.evaluation.model.Championship;
import edu.hei.school.evaluation.model.Club;
import edu.hei.school.evaluation.model.Player;
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
}
