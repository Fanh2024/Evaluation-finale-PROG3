package edu.hei.school.central.model;

public class Season {
    private String id;
    private int startYear;
    private int endYear;
    private Championship championship;
    private SeasonStatus seasonStatus;

    public Season(String id, int startYear, int endYear, Object o, Object o1) {
        this.id = id;
        this.startYear = startYear;
        this.endYear = endYear;
    }

    public Season(String id, int startYear, int endYear, Championship championship, SeasonStatus seasonStatus) {
        this.id = id;
        this.startYear = startYear;
        this.endYear = endYear;
        this.championship = championship;
        this.seasonStatus = seasonStatus;
    }

    public Season() {
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getEndYear() {
        return endYear;
    }

    public void setEndYear(int endYear) {
        this.endYear = endYear;
    }

    public Championship getChampionship() {
        return championship;
    }

    public void setChampionship(Championship championship) {
        this.championship = championship;
    }

    public SeasonStatus getSeasonStatus() {
        return seasonStatus;
    }

    public void setSeasonStatus(SeasonStatus seasonStatus) {
        this.seasonStatus = seasonStatus;
    }
}
