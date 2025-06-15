package com.xoudou.dal;

import com.xoudou.bo.Player;
import com.xoudou.bo.Game;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire de base de données pour le jeu Xou Dou Qi
 * Gère les connexions SQLite et les opérations CRUD
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:database/xoudouqi.db";
    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            updateDatabaseSchema();
            System.out.println("✓ Base de données SQLite connectée avec succès!");
        } catch (Exception e) {
            System.err.println("✗ Erreur de connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crée les tables nécessaires si elles n'existent pas
     */
    private void createTables() {
        try {
            Statement stmt = connection.createStatement();

            // Table des joueurs
            String createPlayersTable = """
                    CREATE TABLE IF NOT EXISTS players (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        username TEXT UNIQUE NOT NULL,
                        password TEXT NOT NULL,
                        wins INTEGER DEFAULT 0,
                        losses INTEGER DEFAULT 0,
                        draws INTEGER DEFAULT 0,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )""";

            // Table de l'historique des parties
            String createGamesTable = """
                    CREATE TABLE IF NOT EXISTS games (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        player1_username TEXT NOT NULL,
                        player2_username TEXT NOT NULL,
                        result TEXT NOT NULL,
                        board_state TEXT,
                        is_finished BOOLEAN DEFAULT 0,
                        start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        end_time TIMESTAMP,
                        FOREIGN KEY (player1_username) REFERENCES players(username),
                        FOREIGN KEY (player2_username) REFERENCES players(username)
                    )""";

            stmt.execute(createPlayersTable);
            stmt.execute(createGamesTable);
            stmt.close();

            System.out.println("✓ Tables de base de données créées/vérifiées");
        } catch (SQLException e) {
            System.err.println("✗ Erreur lors de la création des tables: " + e.getMessage());
        }
    }

    /**
     * Met à jour le schéma de base de données pour compatibilité
     */
    private void updateDatabaseSchema() {
        try {
            Statement stmt = connection.createStatement();

            // Vérifier si la table game_history existe encore (ancien format)
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet rs = meta.getTables(null, null, "game_history", null);

            if (rs.next()) {
                // Migrer les données de l'ancienne table vers la nouvelle
                migrateGameHistory();
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du schéma: " + e.getMessage());
        }
    }

    /**
     * Migre les données de l'ancienne table game_history vers games
     */
    private void migrateGameHistory() {
        try {
            String selectOld = "SELECT * FROM game_history";
            String insertNew = """
                        INSERT OR IGNORE INTO games (player1_username, player2_username, result, is_finished, start_time)
                        VALUES (?, ?, ?, 1, ?)
                    """;

            PreparedStatement selectStmt = connection.prepareStatement(selectOld);
            PreparedStatement insertStmt = connection.prepareStatement(insertNew);

            ResultSet rs = selectStmt.executeQuery();
            while (rs.next()) {
                insertStmt.setString(1, rs.getString("player1"));
                insertStmt.setString(2, rs.getString("player2"));
                insertStmt.setString(3, rs.getString("result"));
                insertStmt.setString(4, rs.getString("date"));
                insertStmt.executeUpdate();
            }

            rs.close();
            selectStmt.close();
            insertStmt.close();

            System.out.println("✓ Migration des données effectuée");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la migration: " + e.getMessage());
        }
    }

    // === MÉTHODES UTILITAIRES ===

    /**
     * Parse une date depuis SQLite (format: YYYY-MM-DD HH:MM:SS)
     */
    private LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) {
            return null;
        }

        try {
            // Format SQLite: "2025-06-13 02:19:01"
            DateTimeFormatter sqliteFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(dateTimeString, sqliteFormatter);
        } catch (Exception e) {
            try {
                // Fallback: format ISO standard
                return LocalDateTime.parse(dateTimeString);
            } catch (Exception e2) {
                System.err.println("Erreur parsing date: " + dateTimeString + " - " + e2.getMessage());
                return LocalDateTime.now(); // Fallback vers maintenant
            }
        }
    }

    // === MÉTHODES POUR LES JOUEURS ===

    /**
     * Crée un nouveau joueur dans la base de données
     */
    public boolean createPlayer(String username, String password) {
        String sql = "INSERT INTO players (username, password) VALUES (?, ?)";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.executeUpdate();
            stmt.close();
            return true;
        } catch (SQLException e) {
            return false; // Username déjà existant
        }
    }

    /**
     * Authentifie un joueur
     */
    public Player authenticatePlayer(String username, String password) {
        String sql = "SELECT * FROM players WHERE username = ? AND password = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Player player = new Player(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("wins"),
                        rs.getInt("losses"),
                        rs.getInt("draws"));
                rs.close();
                stmt.close();
                return player;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Erreur d'authentification: " + e.getMessage());
        }
        return null;
    }

    /**
     * Met à jour les statistiques d'un joueur
     */
    public void updatePlayerStats(String username, int wins, int losses, int draws) {
        String sql = "UPDATE players SET wins = ?, losses = ?, draws = ? WHERE username = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, wins);
            stmt.setInt(2, losses);
            stmt.setInt(3, draws);
            stmt.setString(4, username);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Erreur mise à jour stats: " + e.getMessage());
        }
    }

    /**
     * Récupère les statistiques d'un joueur
     */
    public Player getPlayerStats(String username) {
        String sql = "SELECT * FROM players WHERE username = ?";
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Player player = new Player(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getInt("wins"),
                        rs.getInt("losses"),
                        rs.getInt("draws"));
                rs.close();
                stmt.close();
                return player;
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Erreur récupération stats: " + e.getMessage());
        }
        return null;
    }

    // === MÉTHODES POUR LES PARTIES ===

    /**
     * Sauvegarde une nouvelle partie
     */
    public int saveGame(Game game) {
        String sql = """
                    INSERT INTO games (player1_username, player2_username, result, board_state, is_finished, start_time)
                    VALUES (?, ?, ?, ?, ?, ?)
                """;
        try {
            PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, game.getPlayer1Username());
            stmt.setString(2, game.getPlayer2Username());
            stmt.setString(3, game.getResult());
            stmt.setString(4, game.getBoardState());
            stmt.setBoolean(5, game.isFinished());
            stmt.setString(6, game.getStartTime().toString());

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            int gameId = 0;
            if (keys.next()) {
                gameId = keys.getInt(1);
            }
            keys.close();
            stmt.close();
            return gameId;
        } catch (SQLException e) {
            System.err.println("Erreur sauvegarde partie: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Met à jour une partie existante
     */
    public void updateGame(Game game) {
        String sql = """
                    UPDATE games SET result = ?, board_state = ?, is_finished = ?, end_time = ?
                    WHERE id = ?
                """;
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, game.getResult());
            stmt.setString(2, game.getBoardState());
            stmt.setBoolean(3, game.isFinished());
            stmt.setString(4, game.getEndTime() != null ? game.getEndTime().toString() : null);
            stmt.setInt(5, game.getId());
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Erreur mise à jour partie: " + e.getMessage());
        }
    }

    /**
     * Récupère l'historique des parties
     */
    public List<Game> getGameHistory() {
        List<Game> games = new ArrayList<>();
        String sql = "SELECT * FROM games ORDER BY start_time DESC LIMIT 20";

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);            
            while (rs.next()) {
                Game game = new Game(
                        rs.getInt("id"),
                        rs.getString("player1_username"),
                        rs.getString("player2_username"),
                        rs.getString("result"),
                        parseDateTime(rs.getString("start_time")),
                        parseDateTime(rs.getString("end_time")),
                        rs.getString("board_state"),
                        rs.getBoolean("is_finished"));
                games.add(game);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Erreur récupération historique: " + e.getMessage());
        }
        return games;
    }

    /**
     * Récupère les parties non terminées d'un joueur
     */
    public List<Game> getUnfinishedGames(String username) {
        List<Game> games = new ArrayList<>();
        String sql = """
                    SELECT * FROM games
                    WHERE (player1_username = ? OR player2_username = ?) AND is_finished = 0
                    ORDER BY start_time DESC
                """;

        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, username);
            ResultSet rs = stmt.executeQuery();            while (rs.next()) {
                Game game = new Game(
                        rs.getInt("id"),
                        rs.getString("player1_username"),
                        rs.getString("player2_username"),
                        rs.getString("result"),
                        parseDateTime(rs.getString("start_time")),
                        parseDateTime(rs.getString("end_time")),
                        rs.getString("board_state"),
                        rs.getBoolean("is_finished"));
                games.add(game);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Erreur récupération parties non terminées: " + e.getMessage());
        }
        return games;
    }

    /**
     * Ferme la connexion à la base de données
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("✓ Connexion à la base de données fermée");
            }
        } catch (SQLException e) {
            System.err.println("Erreur fermeture connexion: " + e.getMessage());
        }
    }
}
