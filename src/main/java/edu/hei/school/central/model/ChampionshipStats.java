package edu.hei.school.central.model;

import java.util.Collections;
import java.util.List;

public class ChampionshipStats {
    private String championshipId;
    private List<Integer> goalDifferences;

    public double computeMedian() {
        if (goalDifferences == null || goalDifferences.isEmpty()) return 0;

        Collections.sort(goalDifferences);
        int size = goalDifferences.size();

        if (size % 2 == 1) {
            return goalDifferences.get(size / 2);
        } else {
            return (goalDifferences.get(size / 2 - 1) + goalDifferences.get(size / 2)) / 2.0;
        }
    }

    public String getChampionshipId() {
        return championshipId;
    }

    public void setChampionshipId(String championshipId) {
        this.championshipId = championshipId;
    }

    public List<Integer> getGoalDifferences() {
        return goalDifferences;
    }

    public void setGoalDifferences(List<Integer> goalDifferences) {
        this.goalDifferences = goalDifferences;
    }
}
