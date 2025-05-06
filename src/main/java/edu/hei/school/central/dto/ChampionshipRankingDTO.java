package edu.hei.school.central.dto;

import edu.hei.school.central.model.Championship;

public class ChampionshipRankingDTO {
    private int rank;
    private Championship championship;
    private double differenceGoalsMedian;

    public ChampionshipRankingDTO() {
    }

    public ChampionshipRankingDTO(int rank, Championship championship, double differenceGoalsMedian) {
        this.rank = rank;
        this.championship = championship;
        this.differenceGoalsMedian = differenceGoalsMedian;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Championship getChampionship() {
        return championship;
    }

    public void setChampionship(Championship championship) {
        this.championship = championship;
    }

    public double getDifferenceGoalsMedian() {
        return differenceGoalsMedian;
    }

    public void setDifferenceGoalsMedian(double differenceGoalsMedian) {
        this.differenceGoalsMedian = differenceGoalsMedian;
    }
}