package edu.hei.school.evaluation.model;

import java.time.LocalDateTime;

public class MatchLight {
    private String id;
    private String championshipId;
    private String homeClubId;
    private String awayClubId;
    private String stadium;
    private LocalDateTime dateTime;
    private MatchStatus matchStatus;

    // Constructeur
    public MatchLight(String id, String championshipId, String homeClubId,
                      String awayClubId, String stadium, LocalDateTime dateTime, MatchStatus matchStatus) {
        this.id = id;
        this.championshipId = championshipId;
        this.homeClubId = homeClubId;
        this.awayClubId = awayClubId;
        this.stadium = stadium;
        this.dateTime = dateTime;
        this.matchStatus = matchStatus;
    }

    // Getters
    public String getId() { return id; }
    public String getChampionshipId() { return championshipId; }
    public String getHomeClubId() { return homeClubId; }
    public String getAwayClubId() { return awayClubId; }
    public String getStadium() { return stadium; }
    public LocalDateTime getDateTime() { return dateTime; }
    public MatchStatus getMatchStatus() { return matchStatus; }
}
