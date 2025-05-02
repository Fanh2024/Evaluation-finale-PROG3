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
public class ClubRepository {
    private final DataBaseConnexion db = new DataBaseConnexion();

    public List<Club> getAllClubs() {
        List<Club> clubs = new ArrayList<>();
        String sql = """
            SELECT c.id, c.name, c.acronym, c.creation_year, c.stadium_name,
                   ch.id AS champ_id, ch.name AS champ_name, ch.country
            FROM Club c
            LEFT JOIN Championship ch ON c.championship_id = ch.id
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Championship championship = null;
                if (rs.getString("champ_id") != null) {
                    championship = new Championship(
                            rs.getString("champ_id"),
                            rs.getString("champ_name"),
                            rs.getString("country")
                    );
                }

                Club club = new Club(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("acronym"),
                        rs.getInt("creation_year"),
                        rs.getString("stadium_name"),
                        championship
                );
                clubs.add(club);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des clubs", e);
        }
        return clubs;
    }

    public void upsertClub(Club club) {
        String sql = """
            INSERT INTO Club (id, name, acronym, creation_year, stadium_name, championship_id)
            VALUES (?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                name = EXCLUDED.name,
                acronym = EXCLUDED.acronym,
                creation_year = EXCLUDED.creation_year,
                stadium_name = EXCLUDED.stadium_name,
                championship_id = EXCLUDED.championship_id
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, club.getId());
            stmt.setString(2, club.getName());
            stmt.setString(3, club.getAcronym());
            stmt.setInt(4, club.getCreationYear());
            stmt.setString(5, club.getStadiumName());

            if (club.getChampionship() != null) {
                stmt.setString(6, club.getChampionship().getId());
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'insertion/mise à jour du club", e);
        }
    }

    public List<Player> getPlayersByClubId(String clubId) {
        List<Player> players = new ArrayList<>();
        String sql = "SELECT * FROM Player WHERE club_id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, clubId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    players.add(new Player(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getInt("number"),
                            rs.getString("position"),
                            rs.getString("nationality"),
                            rs.getInt("age"),
                            null // Pour éviter boucle infinie, club non chargé
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des joueurs du club", e);
        }

        return players;
    }

    public void insertPlayer(Player player) {
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

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

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
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'insertion/mise à jour du joueur", e);
        }
    }

    public List<Player> updatePlayersForClub(String clubId, List<Player> newPlayers) {
        String checkClubSql = "SELECT COUNT(*) FROM Club WHERE id = ?";
        String selectExistingSql = "SELECT id FROM Player WHERE club_id = ?";
        String detachSql = "UPDATE Player SET club_id = NULL WHERE club_id = ?";
        String attachSql = "UPDATE Player SET club_id = ? WHERE id = ?";
        String checkAlreadyAssignedSql = "SELECT id FROM Player WHERE id = ? AND (club_id IS NOT NULL AND club_id != ?)";

        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);

            // Vérification de l'existence du club
            try (PreparedStatement checkStmt = conn.prepareStatement(checkClubSql)) {
                checkStmt.setString(1, clubId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next() || rs.getInt(1) == 0) {
                        throw new MatchRepository.NotFoundException("Club introuvable avec l'ID " + clubId);
                    }
                }
            }

            // Vérification : les nouveaux joueurs ne doivent pas déjà appartenir à un autre club
            for (Player p : newPlayers) {
                try (PreparedStatement checkAssignedStmt = conn.prepareStatement(checkAlreadyAssignedSql)) {
                    checkAssignedStmt.setString(1, p.getId());
                    checkAssignedStmt.setString(2, clubId);
                    try (ResultSet rs = checkAssignedStmt.executeQuery()) {
                        if (rs.next()) {
                            conn.rollback();
                            throw new MatchRepository.BadRequestException("Le joueur " + p.getId() + " est déjà affecté à un autre club.");
                        }
                    }
                }
            }

            // Détacher les anciens joueurs du club (sans supprimer)
            try (PreparedStatement detachStmt = conn.prepareStatement(detachSql)) {
                detachStmt.setString(1, clubId);
                detachStmt.executeUpdate();
            }

            // Attacher les nouveaux joueurs au club
            try (PreparedStatement attachStmt = conn.prepareStatement(attachSql)) {
                for (Player p : newPlayers) {
                    attachStmt.setString(1, clubId);
                    attachStmt.setString(2, p.getId());
                    attachStmt.executeUpdate();
                }
            }

            conn.commit();

            // Retourner les nouveaux joueurs associés
            return newPlayers;

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour des joueurs du club " + clubId, e);
        }
    }

}
