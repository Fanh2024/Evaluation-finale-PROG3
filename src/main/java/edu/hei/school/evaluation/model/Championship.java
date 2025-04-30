package edu.hei.school.evaluation.model;

public class Championship {
    private String id;
    private String name;
    private String country;

    public Championship(String champId, String champName, String country) {
        this.id = champId;
        this.name = champName;
        this.country = country;
    }

    public Championship() {

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
