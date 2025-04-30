package edu.hei.school.evaluation.repository;

import edu.hei.school.evaluation.config.DataBaseConnexion;
import edu.hei.school.evaluation.model.Championship;
import edu.hei.school.evaluation.model.Club;
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
}
