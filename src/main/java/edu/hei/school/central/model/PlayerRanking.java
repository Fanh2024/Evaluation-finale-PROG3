package edu.hei.school.central.model;

public class PlayerRanking {
    private String playerId;
    private String playerName;
    private int goals;
    private int minutesPlayed;

    public PlayerRanking(String playerId, String playerName, int goals, int minutesPlayed) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.goals = goals;
        this.minutesPlayed = minutesPlayed;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getGoals() {
        return goals;
    }

    public int getMinutesPlayed() {
        return minutesPlayed;
    }
}