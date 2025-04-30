package edu.hei.school.evaluation.model;

public class Player {
    private String id;
    private String nom;
    private int numero;
    private String poste;
    private String nationalite;
    private int age;
    private String clubId; // Peut être null

    public Player() {}

    public Player(String id, String nom, int numero, String poste, String nationalite, int age, String clubId) {
        this.id = id;
        this.nom = nom;
        this.numero = numero;
        this.poste = poste;
        this.nationalite = nationalite;
        this.age = age;
        this.clubId = clubId;
    }

    // Getters & Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }
}
