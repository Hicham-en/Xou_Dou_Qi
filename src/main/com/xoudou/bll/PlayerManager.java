package com.xoudou.bll;

import com.xoudou.bo.Player;
import com.xoudou.bo.Game;
import com.xoudou.dal.DatabaseManager;
import java.util.List;

/**
 * Gestionnaire des joueurs - Logique métier
 */
public class PlayerManager {
    private DatabaseManager dbManager;

    public PlayerManager() {
        this.dbManager = new DatabaseManager();
    }

    /**
     * Crée un nouveau compte joueur
     */
    public boolean createPlayer(String username, String password) {
        // Validation des données
        if (username == null || username.trim().isEmpty()) {
            System.out.println("✗ Le nom d'utilisateur ne peut pas être vide");
            return false;
        }

        if (password == null || password.trim().isEmpty()) {
            System.out.println("✗ Le mot de passe ne peut pas être vide");
            return false;
        }

        if (username.length() < 3) {
            System.out.println("✗ Le nom d'utilisateur doit faire au moins 3 caractères");
            return false;
        }

        if (password.length() < 4) {
            System.out.println("✗ Le mot de passe doit faire au moins 4 caractères");
            return false;
        }

        return dbManager.createPlayer(username.trim(), password);
    }

    /**
     * Authentifie un joueur
     */
    public Player login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        return dbManager.authenticatePlayer(username.trim(), password);
    }

    /**
     * Met à jour les statistiques d'un joueur après une partie
     */
    public void updatePlayerStatsAfterGame(String username, String gameResult) {
        Player player = dbManager.getPlayerStats(username);
        if (player == null)
            return;

        int wins = player.getWins();
        int losses = player.getLosses();
        int draws = player.getDraws();

        // Analyser le résultat de la partie
        if (gameResult.contains("Victoire") && gameResult.contains(username)) {
            wins++;
        } else if (gameResult.contains("Égalité")) {
            draws++;
        } else if (gameResult.contains("Victoire")) {
            losses++;
        }

        dbManager.updatePlayerStats(username, wins, losses, draws);
    }

    /**
     * Affiche les statistiques d'un joueur
     */
    public void showPlayerStats(String username) {
        Player player = dbManager.getPlayerStats(username);
        if (player == null) {
            System.out.println("✗ Joueur introuvable: " + username);
            return;
        }

        System.out.println("\n╔═══ Statistiques de " + String.format("%-10s", username) + "  ═══╗");
        System.out.println("║ Victoires: " + String.format("%3d", player.getWins()) + "                    ║");
        System.out.println("║ Défaites:  " + String.format("%3d", player.getLosses()) + "                    ║");
        System.out.println("║ Égalités:  " + String.format("%3d", player.getDraws()) + "                    ║");
        System.out.println("║ Total:     " + String.format("%3d", player.getTotalGames()) + "                    ║");
        System.out.println("║ Taux de victoire: " + String.format("%5.1f", player.getWinRate()) + "%          ║");
        System.out.println("╚═══════════════════════════════════╝");
    }

    /**
     * Sauvegarde le résultat d'une partie
     */
    public void saveGameResult(String player1Username, String player2Username, String result) {
        Game game = new Game(player1Username, player2Username);
        game.finishGame(result);

        int gameId = dbManager.saveGame(game);
        if (gameId > 0) {
            // Mettre à jour les statistiques des deux joueurs
            updatePlayerStatsAfterGame(player1Username, result);
            updatePlayerStatsAfterGame(player2Username, result);
            System.out.println("✓ Partie sauvegardée (ID: " + gameId + ")");
        } else {
            System.out.println("✗ Erreur lors de la sauvegarde de la partie");
        }
    }

    /**
     * Sauvegarde une partie en cours
     */
    public int saveGameInProgress(String player1Username, String player2Username, String boardState) {
        Game game = new Game(player1Username, player2Username);
        game.setBoardState(boardState);
        game.setResult("En cours");
        game.setFinished(false);

        int gameId = dbManager.saveGame(game);
        if (gameId > 0) {
            System.out.println("✓ Partie sauvegardée pour reprise ultérieure (ID: " + gameId + ")");
        }
        return gameId;
    }

    /**
     * Met à jour une partie existante
     */
    public void updateGame(Game game) {
        dbManager.updateGame(game);

        // Si la partie est terminée, mettre à jour les stats
        if (game.isFinished()) {
            updatePlayerStatsAfterGame(game.getPlayer1Username(), game.getResult());
            updatePlayerStatsAfterGame(game.getPlayer2Username(), game.getResult());
        }
    }

    /**
     * Affiche l'historique des parties
     */
    public void showHistory() {
        List<Game> games = dbManager.getGameHistory();

        if (games.isEmpty()) {
            System.out.println("Aucune partie dans l'historique.");
            return;
        }

        System.out.println("\n╔═══════════════ HISTORIQUE DES PARTIES  ══════════════════════════════════╗");
        System.out.println("║ Date        │ Joueur 1    │ Joueur 2    │ Résultat                       ║");
        System.out.println("╠═════════════╪═════════════╪═════════════╪════════════════════════════════╣");

        for (Game game : games) {
            String date = game.getFormattedStartTime();
            String player1 = game.getPlayer1Username();
            String player2 = game.getPlayer2Username();
            String result = game.getResult();

            // // Tronquer si trop long
            // if (player1.length() > 11)
            //     player1 = player1.substring(0, 8) + "...";
            // if (player2.length() > 11)
            //     player2 = player2.substring(0, 8) + "...";
            // if (result.length() > 15)
            //     result = result.substring(0, 12) + "...";

            System.out.printf("║ %-11s │ %-11s │ %-11s │ %-30s ║%n",
                    date.substring(0, Math.min(11, date.length())),
                    player1, player2, result);
        }
        System.out.println("╚══════════════════════════════════════════════════════════════════════════╝");
    }

    /**
     * Récupère les parties non terminées d'un joueur
     */
    public List<Game> getUnfinishedGames(String username) {
        return dbManager.getUnfinishedGames(username);
    }

    /**
     * Récupère un joueur par son nom d'utilisateur
     */
    public Player getPlayerByUsername(String username) {
        return dbManager.getPlayerStats(username);
    }

    /**
     * Affiche les parties non terminées d'un joueur pour permettre la reprise
     */
    public void showUnfinishedGames(String username) {
        List<Game> games = getUnfinishedGames(username);

        if (games.isEmpty()) {
            System.out.println("Aucune partie en cours pour " + username);
            return;
        }

        System.out.println("\n╔═══ PARTIES EN COURS POUR " + username.toUpperCase() + " ═══╗");
        for (int i = 0; i < games.size(); i++) {
            Game game = games.get(i);
            String opponent = game.getPlayer1Username().equals(username) ? game.getPlayer2Username()
                    : game.getPlayer1Username();
            System.out.println("║ " + (i + 1) + ". Contre " + opponent + " - " +
                    game.getFormattedStartTime() + " ║");
        }
        System.out.println("╚════════════════════════════════════════╝");
    }

    /**
     * Nettoie les ressources
     */
    public void cleanup() {
        if (dbManager != null) {
            dbManager.closeConnection();
        }
    }
}
