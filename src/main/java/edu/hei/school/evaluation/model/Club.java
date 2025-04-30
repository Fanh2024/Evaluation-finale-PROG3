package edu.hei.school.evaluation.model;

public class Club {
    private String id;
    private String name;
    private String acronym;
    private int creationYear;
    private String stadiumName;
    private Championship championship;

    public Club(String id, String name, String acronym, int creationYear, String stadiumName, Championship championship) {
        this.id = id;
        this.name = name;
        this.acronym = acronym;
        this.creationYear = creationYear;
        this.stadiumName = stadiumName;
        this.championship = championship;
    }

    public Club() {

    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public int getCreationYear() {
        return creationYear;
    }

    public void setCreationYear(int creationYear) {
        this.creationYear = creationYear;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }

    public Championship getChampionship() {
        return championship;
    }

    public void setChampionship(Championship championship) {
        this.championship = championship;
    }
}
