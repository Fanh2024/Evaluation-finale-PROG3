package edu.hei.school.evaluation.repository;

import edu.hei.school.evaluation.config.DataBaseConnexion;
import edu.hei.school.evaluation.model.Championship;
import edu.hei.school.evaluation.model.Season;
import edu.hei.school.evaluation.model.SeasonStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SeasonRepository {
    private final DataBaseConnexion db = new DataBaseConnexion();

    public List<Season> getAllSeasons() {
        List<Season> seasons = new ArrayList<>();
        String sql = """
            SELECT s.id, s.start_year, s.end_year, s.season_status,
                   c.id AS champ_id, c.name AS champ_name, c.country AS champ_country
            FROM Season s
            LEFT JOIN Championship c ON s.championship_id = c.id
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Championship championship = null;
                String champId = rs.getString("champ_id");
                String champName = rs.getString("champ_name");
                String champCountry = rs.getString("champ_country");
                if (champId != null && champName != null && champCountry != null) {
                    championship = new Championship(champId, champName, champCountry);
                }

                String statusStr = rs.getString("season_status");
                SeasonStatus seasonStatus = null;
                if (statusStr != null) {
                    try {
                        seasonStatus = SeasonStatus.valueOf(statusStr);
                    } catch (IllegalArgumentException ignored) {
                    }
                }

                Season season = new Season(
                        rs.getString("id"),
                        rs.getInt("start_year"),
                        rs.getInt("end_year"),
                        championship,
                        seasonStatus
                );
                seasons.add(season);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des saisons", e);
        }

        return seasons;
    }

    public void createSeason(Season season) {
        String sql = """
            INSERT INTO Season (id, start_year, end_year, championship_id, season_status)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, season.getId());
            stmt.setInt(2, season.getStartYear());
            stmt.setInt(3, season.getEndYear());
            if (season.getChampionship() != null) {
                stmt.setString(4, season.getChampionship().getId());
            } else {
                stmt.setNull(4, Types.VARCHAR);
            }
            stmt.setString(5, season.getSeasonStatus().name());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la saison", e);
        }
    }
}