package edu.hei.school.evaluation.repository;

import edu.hei.school.evaluation.config.DataBaseConnexion;
import edu.hei.school.evaluation.model.*;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TransfertRepository {
    private final DataBaseConnexion dataBaseConnexion;

    public TransfertRepository(DataBaseConnexion dataBaseConnexion) {
        this.dataBaseConnexion = dataBaseConnexion;
    }

    public void saveTransfert(Player player, Club club, String type) {
        String sql = "INSERT INTO Transfert (player_id, club_id, type) VALUES (?, ?, ?)";

        try (Connection conn = dataBaseConnexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, player.getId());
            if (club != null) {
                stmt.setString(2, club.getId());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }
            stmt.setString(3, type);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'enregistrement du transfert", e);
        }
    }

    public List<Transfert> findAll() {
        List<Transfert> transferts = new ArrayList<>();

        String sql = """
        SELECT t.type,
               p.id AS player_id, p.name AS player_name, p.number, p.position, p.nationality, p.age,
               c.id AS club_id, c.name AS club_name, c.acronym, c.creation_year, c.stadium_name,
               champ.id AS champ_id, champ.name AS champ_name, champ.country
        FROM Transfert t
        JOIN Player p ON t.player_id = p.id
        LEFT JOIN Club c ON t.club_id = c.id
        LEFT JOIN Championship champ ON c.championship_id = champ.id
        ORDER BY t.created_at DESC
    """;

        try (Connection connection = dataBaseConnexion.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String type = rs.getString("type");

                Club club = null;
                if ("IN".equals(type)) { // uniquement si transfert entrant
                    String clubId = rs.getString("club_id");
                    if (clubId != null) {
                        Championship champ = new Championship(
                                rs.getString("champ_id"),
                                rs.getString("champ_name"),
                                rs.getString("country")
                        );
                        club = new Club(
                                clubId,
                                rs.getString("club_name"),
                                rs.getString("acronym"),
                                rs.getInt("creation_year"),
                                rs.getString("stadium_name"),
                                champ
                        );
                    }
                }

                Player player = new Player(
                        rs.getString("player_id"),
                        rs.getString("player_name"),
                        rs.getInt("number"),
                        rs.getString("position"),
                        rs.getString("nationality"),
                        rs.getInt("age"),
                        club // null si OUT, club si IN
                );

                Transfert transfert = new Transfert(player, type);
                transferts.add(transfert);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des transferts", e);
        }

        return transferts;
    }
}
