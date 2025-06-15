package com.xoudou.bo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe représentant une partie de jeu
 */
public class Game {
    private int id;
    private String player1Username;
    private String player2Username;
    private String result; // "Victoire J1", "Victoire J2", "Égalité", "Abandonnée", "En cours"
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String boardState; // État du plateau sauvegardé en JSON ou format sérialisé
    private boolean isFinished;

    // Constructeurs
    public Game() {
        this.startTime = LocalDateTime.now();
        this.isFinished = false;
    }

    public Game(String player1Username, String player2Username) {
        this();
        this.player1Username = player1Username;
        this.player2Username = player2Username;
        this.result = "En cours";
    }

    public Game(int id, String player1Username, String player2Username, String result,
            LocalDateTime startTime, LocalDateTime endTime, String boardState, boolean isFinished) {
        this.id = id;
        this.player1Username = player1Username;
        this.player2Username = player2Username;
        this.result = result;
        this.startTime = startTime;
        this.endTime = endTime;
        this.boardState = boardState;
        this.isFinished = isFinished;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlayer1Username() {
        return player1Username;
    }

    public void setPlayer1Username(String player1Username) {
        this.player1Username = player1Username;
    }

    public String getPlayer2Username() {
        return player2Username;
    }

    public void setPlayer2Username(String player2Username) {
        this.player2Username = player2Username;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getBoardState() {
        return boardState;
    }

    public void setBoardState(String boardState) {
        this.boardState = boardState;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    /**
     * Termine la partie avec un résultat
     */
    public void finishGame(String gameResult) {
        this.result = gameResult;
        this.endTime = LocalDateTime.now();
        this.isFinished = true;
    }

    /**
     * Calcule la durée de la partie
     */
    public String getDuration() {
        if (endTime == null)
            return "En cours";

        long minutes = java.time.Duration.between(startTime, endTime).toMinutes();
        return minutes + " minutes";
    }

    /**
     * Formate la date pour l'affichage
     */
    public String getFormattedStartTime() {
        return startTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    @Override
    public String toString() {
        return String.format("Game{%s vs %s - %s - %s}",
                player1Username, player2Username, result, getFormattedStartTime());
    }
}
