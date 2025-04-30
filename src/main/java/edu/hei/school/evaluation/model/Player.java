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
    // ...
}
