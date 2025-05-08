package edu.hei.school.evaluation.model;

public class Transfert {
    private Player player;
    private String type; // IN ou OUT

    public Transfert(Player player, String type) {
        this.player = player;
        this.type = type;
    }

    // getters et setters

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
