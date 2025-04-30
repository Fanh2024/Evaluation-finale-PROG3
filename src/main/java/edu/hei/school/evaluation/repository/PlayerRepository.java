package edu.hei.school.evaluation.repository;

import edu.hei.school.evaluation.config.DataBaseConnexion;
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
        StringBuilder sql = new StringBuilder(
                "SELECT p.* FROM Player p LEFT JOIN Club c ON p.club_id = c.id WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (name != null) {
            sql.append(" AND LOWER(p.nom) LIKE ?");
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
            sql.append(" AND LOWER(c.nom) LIKE ?");
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
            INSERT INTO Player (id, nom, numero, poste, nationalite, age, club_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
              nom = EXCLUDED.nom,
              numero = EXCLUDED.numero,
              poste = EXCLUDED.poste,
              nationalite = EXCLUDED.nationalite,
              age = EXCLUDED.age,
              club_id = EXCLUDED.club_id
        """;

        try (Connection connection = dataBaseConnexion.getConnection()) {
            for (Player player : players) {
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, player.getId());
                    stmt.setString(2, player.getNom());
                    stmt.setInt(3, player.getNumero());
                    stmt.setString(4, player.getPoste());
                    stmt.setString(5, player.getNationalite());
                    stmt.setInt(6, player.getAge());
                    if (player.getClubId() != null) {
                        stmt.setString(7, player.getClubId());
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
        return new Player(
                rs.getString("id"),
                rs.getString("nom"),
                rs.getInt("numero"),
                rs.getString("poste"),
                rs.getString("nationalite"),
                rs.getInt("age"),
                rs.getString("club_id")
        );
    }
}
