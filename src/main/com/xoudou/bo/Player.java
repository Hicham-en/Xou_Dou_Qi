package com.xoudou.bo;

/**
 * Classe représentant un joueur dans le jeu Xou Dou Qi
 */
public class Player {
    private int id;
    private String username;
    private String password;
    private int wins;
    private int losses;
    private int draws;

    // Constructeurs
    public Player() {
    }

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.wins = 0;
        this.losses = 0;
        this.draws = 0;
    }

    public Player(int id, String username, String password, int wins, int losses, int draws) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getTotalGames() {
        return wins + losses + draws;
    }

    public double getWinRate() {
        int total = getTotalGames();
        return total > 0 ? (double) wins / total * 100 : 0.0;
    }

    @Override
    public String toString() {
        return String.format("Player{username='%s', wins=%d, losses=%d, draws=%d}",
                username, wins, losses, draws);
    }
}
