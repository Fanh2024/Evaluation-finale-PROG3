package edu.hei.school.central.repository;

import edu.hei.school.central.config.DataBaseConnexion;
import edu.hei.school.central.model.ChampionshipRankingResponse;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

@Repository
public class ChampionshipRankingRepository {
    private final DataBaseConnexion dataBaseConnexion;

    public ChampionshipRankingRepository(DataBaseConnexion dataBaseConnexion) {
        this.dataBaseConnexion = dataBaseConnexion;
    }

    public List<ChampionshipRankingResponse> getRankings() {
        List<ChampionshipRankingResponse> responses = new ArrayList<>();

        String query = """
            SELECT c.id AS championship_id, c.name, c.country, cs.goal_difference
            FROM Championship c
            JOIN Championship_Ranking cr ON c.id = cr.championship_id
            JOIN Club_Statistics cs ON cr.club_id = cs.club_id AND cr.season_id = cs.season_id
        """;

        try (Connection conn = dataBaseConnexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            Map<String, List<Integer>> goalDiffsPerChamp = new HashMap<>();
            Map<String, String> nameByChampId = new HashMap<>();
            Map<String, String> countryByChampId = new HashMap<>();

            while (rs.next()) {
                String champId = rs.getString("championship_id");
                String name = rs.getString("name");
                String country = rs.getString("country");
                int goalDiff = rs.getInt("goal_difference");

                goalDiffsPerChamp
                        .computeIfAbsent(champId, k -> new ArrayList<>())
                        .add(goalDiff);

                nameByChampId.putIfAbsent(champId, name);
                countryByChampId.putIfAbsent(champId, country);
            }

            for (String champId : goalDiffsPerChamp.keySet()) {
                List<Integer> diffs = goalDiffsPerChamp.get(champId);
                double median = computeMedian(diffs);
                responses.add(new ChampionshipRankingResponse(
                        champId,
                        nameByChampId.get(champId),
                        countryByChampId.get(champId),
                        median
                ));
            }

            // Tri décroissant par médiane
            responses.sort((a, b) -> Double.compare(b.medianGoalDifference, a.medianGoalDifference));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return responses;
    }

    private double computeMedian(List<Integer> list) {
        Collections.sort(list);
        int n = list.size();
        if (n % 2 == 1) {
            return list.get(n / 2);
        } else {
            return (list.get(n / 2 - 1) + list.get(n / 2)) / 2.0;
        }
    }
}
