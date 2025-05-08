package edu.hei.school.central.model;

public class ChampionshipRankingResponse {
    public String championshipId;
    public String name;
    public String country;
    public double medianGoalDifference;

    public ChampionshipRankingResponse(String championshipId, String name, String country, double medianGoalDifference) {
        this.championshipId = championshipId;
        this.name = name;
        this.country = country;
        this.medianGoalDifference = medianGoalDifference;
    }
}
