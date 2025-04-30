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

    public void advanceSeasonStatus(String seasonId) {
        String selectSql = "SELECT season_status FROM Season WHERE id = ?";
        String updateSql = "UPDATE Season SET season_status = ? WHERE id = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {

            selectStmt.setString(1, seasonId);
            ResultSet rs = selectStmt.executeQuery();

            if (!rs.next()) {
                throw new RuntimeException("Aucune saison trouvée avec l'id : " + seasonId);
            }

            String currentStatusStr = rs.getString("season_status");
            SeasonStatus currentStatus = SeasonStatus.valueOf(currentStatusStr);
            SeasonStatus nextStatus;

            switch (currentStatus) {
                case NOT_STARTED -> nextStatus = SeasonStatus.STARTED;
                case STARTED -> nextStatus = SeasonStatus.FINISHED;
                case FINISHED -> throw new RuntimeException("La saison est déjà terminée.");
                default -> throw new RuntimeException("Statut inconnu : " + currentStatus);
            }

            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, nextStatus.name());
                updateStmt.setString(2, seasonId);
                updateStmt.executeUpdate();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du statut de la saison", e);
        }
    }

}