package edu.hei.school.central.repository;

import edu.hei.school.central.config.DataBaseConnexion;
import edu.hei.school.central.model.StatisticsClub;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class StatisticsClubRepository {

    private final DataBaseConnexion dataBaseConnexion = new DataBaseConnexion();

    public void deleteAllBySeasonYear(int seasonYear) throws SQLException {
        // Étape 1 : Récupérer l'ID de la saison à partir de l'année
        String getSeasonIdSQL = "SELECT id FROM season WHERE start_year = ?";
        String deleteSQL = "DELETE FROM club_statistics WHERE season_id = ?";

        try (Connection conn = dataBaseConnexion.getConnection();
             PreparedStatement getSeasonStmt = conn.prepareStatement(getSeasonIdSQL)) {

            getSeasonStmt.setInt(1, seasonYear);
            ResultSet rs = getSeasonStmt.executeQuery();

            if (rs.next()) {
                String seasonId = rs.getString("id");

                // Étape 2 : Supprimer les statistiques avec l'ID de la saison
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL)) {
                    deleteStmt.setString(1, seasonId);
                    deleteStmt.executeUpdate();
                }
            } else {
                System.out.println("Aucune saison trouvée pour l'année : " + seasonYear);
            }
        }
    }


    public void saveAll(List<StatisticsClub> statsList) throws SQLException {
        String getSeasonIdSQL = "SELECT id FROM season WHERE start_year = ?";
        String sql = "INSERT INTO club_statistics " +
                "(id, club_id, season_id, points, goals_scored, goals_conceded, goal_difference, clean_sheets) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataBaseConnexion.getConnection();
             PreparedStatement getSeasonStmt = conn.prepareStatement(getSeasonIdSQL);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (StatisticsClub s : statsList) {
                // Récupérer l'ID de la saison à partir du startYear
                getSeasonStmt.setInt(1, s.getSeason().getStartYear());
                ResultSet rs = getSeasonStmt.executeQuery();

                if (rs.next()) {
                    String seasonId = rs.getString("id");

                    // Insérer les statistiques avec le season_id récupéré
                    stmt.setString(1, s.getId());
                    stmt.setString(2, s.getClub().getId());
                    stmt.setString(3, s.getSeason().getId()); // Utiliser l'ID de la saison
                    stmt.setInt(4, s.getPoints());
                    stmt.setInt(5, s.getGoalsScored());
                    stmt.setInt(6, s.getGoalsConceded());
                    stmt.setInt(7, s.getGoalDifference());
                    stmt.setInt(8, s.getCleanSheets());
                    stmt.addBatch();
                } else {
                    System.out.println("Aucune saison trouvée pour l'année : " + s.getSeason().getStartYear());
                }
            }
            stmt.executeBatch();
        }
    }

    public Map<String, List<Integer>> getGoalDifferencesByChampionship(int seasonYear) throws SQLException {
        String sql = """
        SELECT c.championship_id, sc.goal_difference
        FROM club_statistics sc
        JOIN club c ON sc.club_id = c.id
        JOIN season s ON sc.season_id = s.id
        WHERE s.start_year = ?
    """;

        Map<String, List<Integer>> map = new HashMap<>();

        try (Connection conn = dataBaseConnexion.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, seasonYear);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String champId = rs.getString("championship_id");
                int diff = rs.getInt("goal_difference");

                map.computeIfAbsent(champId, k -> new ArrayList<>()).add(diff);
            }
        }
        return map;
    }

}