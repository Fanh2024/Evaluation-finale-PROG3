package edu.hei.school.central.repository;

import edu.hei.school.central.config.DataBaseConnexion;
import edu.hei.school.central.model.Championship;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ChampionshipRepository {

    private final DataBaseConnexion dataBaseConnexion = new DataBaseConnexion();

    public List<Championship> findAll() throws SQLException {
        List<Championship> championships = new ArrayList<>();
        String sql = "SELECT * FROM championship";

        try (Connection conn = dataBaseConnexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Championship c = new Championship(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("country")
                );
                championships.add(c);
            }
        }

        return championships;
    }


    public Championship findById(String id) throws SQLException {
        String sql = "SELECT * FROM championship WHERE id = ?";
        try (Connection conn = dataBaseConnexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Championship(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("country")
                );
            } else {
                return null;
            }
        }
    }

}
