package edu.hei.school.evaluation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Match {
    private String id;
    private Championship championship;
    private Club homeClubId;
    private Club awayClubId;
    private String stadium;
    private LocalDateTime dateTime;
    private Season season;
    private MatchStatus matchStatus;

    // Liste des buts
    private List<Goal> goals = new ArrayList<>();

    public Match() {}

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Championship getChampionship() {
        return championship;
    }

    public void setChampionship(Championship championship) {
        this.championship = championship;
    }

    public Club getHomeClubId() {
        return homeClubId;
    }

    public void setHomeClubId(Club homeClubId) {
        this.homeClubId = homeClubId;
    }

    public Club getAwayClubId() {
        return awayClubId;
    }

    public void setAwayClubId(Club awayClubId) {
        this.awayClubId = awayClubId;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public MatchStatus getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(MatchStatus matchStatus) {
        this.matchStatus = matchStatus;
    }

    public void setGoals(List<Goal> goals) {
        this.goals = goals;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    @JsonProperty("homeGoals")
    public int getHomeGoals() {
        return (int) goals.stream()
                .filter(goal -> {
                    if (goal.getPlayer() == null || goal.getPlayer().getClub() == null) return false;
                    Club scorerClub = goal.getPlayer().getClub();
                    if (!goal.isOwnGoal()) {
                        return scorerClub.equals(this.homeClubId);
                    } else {
                        return scorerClub.equals(this.awayClubId);
                    }
                }).count();
    }

    @JsonProperty("awayGoals")
    public int getAwayGoals() {
        return (int) goals.stream()
                .filter(goal -> {
                    if (goal.getPlayer() == null || goal.getPlayer().getClub() == null) return false;
                    Club scorerClub = goal.getPlayer().getClub();
                    if (!goal.isOwnGoal()) {
                        return scorerClub.equals(this.awayClubId);
                    } else {
                        return scorerClub.equals(this.homeClubId);
                    }
                }).count();
    }
}
