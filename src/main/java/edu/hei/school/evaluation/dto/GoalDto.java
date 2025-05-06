package edu.hei.school.evaluation.dto;

public class GoalDto {
    private String id;
    private String playerId;
    private String matchId;
    private int minute;
    private boolean penalty;
    private boolean ownGoal;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GoalDto() {}

    // Getters et setters
    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isPenalty() {
        return penalty;
    }

    public void setPenalty(boolean penalty) {
        this.penalty = penalty;
    }

    public boolean isOwnGoal() {
        return ownGoal;
    }

    public void setOwnGoal(boolean ownGoal) {
        this.ownGoal = ownGoal;
    }
}