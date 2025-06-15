import com.xoudou.bll.PlayerManager;
import com.xoudou.bll.GameBoard;
import com.xoudou.bo.Player;
import com.xoudou.bo.Game;
import java.util.Scanner;
import java.util.List;

/**
 * Classe principale du jeu Xou Dou Qi
 * Interface console avec menu complet et gestion des parties
 */
public class XouDouQiMain {
    private Scanner scanner;
    private PlayerManager playerManager;
    private GameBoard gameBoard;
    private Player player1, player2;
    private Player currentPlayer;    
    public XouDouQiMain() {
        try {
            scanner = new Scanner(System.in, "UTF-8");
        } catch (Exception e) {
            scanner = new Scanner(System.in);
        }
        playerManager = new PlayerManager();
        gameBoard = new GameBoard();
    }public static void main(String[] args) {
        // Configuration complète de l'encodage pour supporter les emojis
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("console.encoding", "UTF-8");
        
        // Pour Windows - configuration avancée UTF-8
        try {
            System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
            System.setErr(new java.io.PrintStream(System.err, true, "UTF-8"));
        } catch (Exception e) {
            // Ignorer les erreurs d'encodage
        }
        
        System.out.println("🎮 Démarrage du jeu Xou Dou Qi...");
        XouDouQiMain game = new XouDouQiMain();
        game.showWelcome();
        game.run();
    }

    /**
     * Affiche l'écran d'accueil
     */
    private void showWelcome() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║                                                      ║");
        System.out.println("║          🐉 XOU DOU QI - DOU SHOU QI 🐉              ║");
        System.out.println("║              Jeu traditionnel chinois                ║");
        System.out.println("║                                                      ║");
        System.out.println("║  🎯 Objectif: Atteindre le sanctuaire ennemi         ║");
        System.out.println("║  ⚔️  Hiérarchie: Éléphant > Lion > Tigre > ...       ║");
        System.out.println("║  🐭 Exception: Le Rat peut capturer l'Éléphant       ║");
        System.out.println("║                                                      ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        System.out.println("\nAppuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }

    /**
     * Boucle principale du jeu
     */
    public void run() {
        boolean running = true;

        while (running) {
            showMainMenu();
            int choice = getChoice();

            switch (choice) {
                case 1:
                    createAccount();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    if (bothPlayersConnected()) {
                        startNewGame();
                    } else {
                        System.out.println("❌ Il faut connecter deux joueurs d'abord!");
                        pauseForInput();
                    }
                    break;
                case 4:
                    resumeGame();
                    break;
                case 5:
                    showGameHistory();
                    break;
                case 6:
                    showPlayerStatistics();
                    break;
                case 7:
                    showGameRules();
                    break;
                case 8:
                    System.out.println("\n👋 Merci d'avoir joué à Xou Dou Qi!");
                    System.out.println("🎮 À bientôt pour de nouvelles parties!");
                    running = false;
                    break;
                default:
                    System.out.println("❌ Choix invalide! Veuillez choisir entre 1 et 8.");
                    pauseForInput();
            }
        }

        cleanup();
    }

    /**
     * Affiche le menu principal
     */
    private void showMainMenu() {
        clearScreen();
        System.out.println("╔═══════════════ MENU PRINCIPAL  ═══════════════╗");
        System.out.println("║                                               ║");
        System.out.println("║  1. 👤 Créer un compte                        ║");
        System.out.println("║  2. 🔐 Se connecter                           ║");
        System.out.println("║  3. 🎯 Nouvelle partie                        ║");
        System.out.println("║  4. ⏯️  Reprendre une partie                  ║");
        System.out.println("║  5. 📜 Historique des parties                 ║");
        System.out.println("║  6. 📊 Statistiques                           ║");
        System.out.println("║  7. 📖 Règles du jeu                          ║");
        System.out.println("║  8. 🚪 Quitter                                ║");
        System.out.println("║                                               ║");
        System.out.println("╚═══════════════════════════════════════════════╝");

        // Afficher les joueurs connectés
        showConnectedPlayers();

        System.out.print("\n🎯 Votre choix (1-8): ");
    }

    /**
     * Affiche les joueurs connectés
     */
    private void showConnectedPlayers() {
        System.out.println("\n┌───  Joueurs connectés  ───┐");
        if (player1 != null) {
            System.out.println("│ 🔹 Joueur 1: " + String.format("%-13s", player1.getUsername()) + "│");
        } else {
            System.out.println("│ ⭕ Joueur 1: Non connecté │");
        }

        if (player2 != null) {
            System.out.println("│ 🔸 Joueur 2: " + String.format("%-13s", player2.getUsername()) + "│");
        } else {
            System.out.println("│ ⭕ Joueur 2: Non connecté │");
        }
        System.out.println("└───────────────────────────┘");
    }

    /**
     * Gère la création de compte
     */
    private void createAccount() {
        clearScreen();
        System.out.println("╔═══════════════ CRÉER UN COMPTE ═══════════════╗");
        System.out.println("║                                               ║");

        System.out.print("║ 👤 Nom d'utilisateur (min 3 caractères): ");
        String username = scanner.nextLine().trim();

        System.out.print("║ 🔐 Mot de passe (min 4 caractères): ");
        String password = scanner.nextLine().trim();

        System.out.println("║                                               ║");
        System.out.println("╚═══════════════════════════════════════════════╝");

        if (playerManager.createPlayer(username, password)) {
            System.out.println("\n✅ Compte créé avec succès pour: " + username);
            System.out.println("🎉 Vous pouvez maintenant vous connecter!");
        } else {
            System.out.println("\n❌ Erreur: Ce nom d'utilisateur existe déjà!");
        }

        pauseForInput();
    }

    /**
     * Gère la connexion des joueurs
     */
    private void login() {
        clearScreen();
        System.out.println("╔═══════════════   CONNEXION   ═══════════════╗");
        System.out.println("║                                             ║");

        System.out.print("║ 👤 Nom d'utilisateur: ");
        String username = scanner.nextLine().trim();

        System.out.print("║ 🔐 Mot de passe: ");
        String password = scanner.nextLine().trim();

        System.out.println("║                                             ║");
        System.out.println("╚═════════════════════════════════════════════╝");

        Player player = playerManager.login(username, password);
        if (player != null) {
            if (player1 == null) {
                player1 = player;
                System.out.println("\n✅ Joueur 1 connecté: " + username);
            } else if (player2 == null && !player.getUsername().equals(player1.getUsername())) {
                player2 = player;
                System.out.println("\n✅ Joueur 2 connecté: " + username);
            } else if (player.getUsername().equals(player1.getUsername()) ||
                    (player2 != null && player.getUsername().equals(player2.getUsername()))) {
                System.out.println("\n⚠️  Ce joueur est déjà connecté!");
            } else {
                System.out.println("\n⚠️  Les deux places sont déjà prises!");
                System.out.println("💡 Déconnectez un joueur d'abord ou créez un autre compte.");
            }
        } else {
            System.out.println("\n❌ Nom d'utilisateur ou mot de passe incorrect!");
        }

        pauseForInput();
    }

    /**
     * Démarre une nouvelle partie
     */
    private void startNewGame() {
        clearScreen();
        System.out.println("╔═══════════════ NOUVELLE PARTIE ═══════════════╗");
        System.out.println("║  🔹 Joueur 1: " + String.format("%-29s", player1.getUsername()) + "║");
        System.out.println("║  🔸 Joueur 2: " + String.format("%-29s", player2.getUsername()) + "║");
        System.out.println("╚════════════════════════════════════════════════╝");

        gameBoard.initializeBoard();
        currentPlayer = player1; // Joueur 1 commence toujours

        playGame();
    }

    /**
     * Reprend une partie sauvegardée
     */
    private void resumeGame() {
        if (player1 == null) {
            System.out.println("❌ Connectez-vous d'abord pour voir vos parties sauvegardées!");
            pauseForInput();
            return;
        }

        List<Game> unfinishedGames = playerManager.getUnfinishedGames(player1.getUsername());
        if (player2 != null) {
            unfinishedGames.addAll(playerManager.getUnfinishedGames(player2.getUsername()));
        }

        if (unfinishedGames.isEmpty()) {
            System.out.println("📭 Aucune partie sauvegardée trouvée.");
            pauseForInput();
            return;
        }

        clearScreen();
        System.out.println("╔══════════ PARTIES SAUVEGARDÉES ══════════╗");
        for (int i = 0; i < unfinishedGames.size(); i++) {
            Game game = unfinishedGames.get(i);
            System.out.printf("║ %d. %s vs %s - %s  ║%n",
                    i + 1, game.getPlayer1Username(),
                    game.getPlayer2Username(), game.getFormattedStartTime());
        }
        System.out.println("╚══════════════════════════════════════════╝");

        System.out.print("Choisir une partie (0 pour annuler): ");
        int choice = getChoice();

        if (choice > 0 && choice <= unfinishedGames.size()) {
            Game selectedGame = unfinishedGames.get(choice - 1);

            // Charger les joueurs appropriés
            loadPlayersForGame(selectedGame);

            // Restaurer l'état du plateau
            gameBoard.deserializeBoardState(selectedGame.getBoardState());
            currentPlayer = player1; // Par défaut, reprendre avec joueur 1

            System.out.println("✅ Partie restaurée! Reprise du jeu...");
            pauseForInput();

            playGame();
        }
    }

    /**
     * Charge les joueurs appropriés pour une partie sauvegardée
     */
    private void loadPlayersForGame(Game game) {
        // S'assurer que les bons joueurs sont chargés
        if (player1 == null || !player1.getUsername().equals(game.getPlayer1Username())) {
            // Charger le joueur 1 depuis la base
            Player loadedPlayer1 = playerManager.getPlayerByUsername(game.getPlayer1Username());
            if (loadedPlayer1 != null) {
                player1 = loadedPlayer1;
            }
        }

        if (player2 == null || !player2.getUsername().equals(game.getPlayer2Username())) {
            // Charger le joueur 2 depuis la base
            Player loadedPlayer2 = playerManager.getPlayerByUsername(game.getPlayer2Username());
            if (loadedPlayer2 != null) {
                player2 = loadedPlayer2;
            }
        }
    }

    /**
     * Boucle principale de jeu
     */
    private void playGame() {
        while (!gameBoard.isGameOver()) {
            clearScreen();
            gameBoard.displayBoard();

            System.out.println("\n🎯 Tour de: " + currentPlayer.getUsername() +
                    (currentPlayer == player1 ? " (🔹)" : " (🔸)"));
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📝 Format    : ligne_départ colonne_départ ligne_arrivée colonne_arrivée");
            System.out.println("💡 Exemple   : 6 0 5 0 (déplacer pièce de (6,0) vers (5,0))");
            System.out.println("⌨️ Commandes : 'save' pour sauvegarder, 'quit' pour abandonner");
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            System.out.print("\n🎮 Votre mouvement: ");

            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("quit")) {
                handleGameAbandon();
                break;
            } else if (input.equalsIgnoreCase("save")) {
                saveCurrentGame();
                continue;
            }

            if (processMove(input)) {
                // Changer de joueur seulement si le mouvement est valide
                currentPlayer = (currentPlayer == player1) ? player2 : player1;
            } else {
                System.out.println("\n❌ Mouvement invalide! Réessayez...");
                pauseForInput();
            }
        }

        if (gameBoard.isGameOver()) {
            finishGame();
        }
    }

    /**
     * Traite un mouvement saisi par le joueur
     */
    private boolean processMove(String input) {
        try {
            String[] parts = input.split("\\s+");
            if (parts.length != 4) {
                System.out.println(
                        "❌ Format incorrect! Utilisez: ligne_départ colonne_départ ligne_arrivée colonne_arrivée");
                return false;
            }

            int fromRow = Integer.parseInt(parts[0]);
            int fromCol = Integer.parseInt(parts[1]);
            int toRow = Integer.parseInt(parts[2]);
            int toCol = Integer.parseInt(parts[3]);

            return gameBoard.makeMove(fromRow, fromCol, toRow, toCol, currentPlayer == player1);

        } catch (NumberFormatException e) {
            System.out.println("❌ Veuillez saisir des nombres valides!");
            return false;
        }
    }

    /**
     * Gère l'abandon d'une partie
     */
    private void handleGameAbandon() {
        System.out.println("\n🏳️ " + currentPlayer.getUsername() + " abandonne la partie!");
        Player winner = (currentPlayer == player1) ? player2 : player1;
        String result = "Victoire " + winner.getUsername() + " (abandon)";

        playerManager.saveGameResult(player1.getUsername(), player2.getUsername(), result);

        System.out.println("🏆 " + winner.getUsername() + " remporte la partie par abandon!");
        pauseForInput();
    }

    /**
     * Sauvegarde la partie en cours
     */
    private void saveCurrentGame() {
        String boardState = gameBoard.serializeBoardState();
        int gameId = playerManager.saveGameInProgress(player1.getUsername(), player2.getUsername(), boardState);

        if (gameId > 0) {
            System.out.println("💾 Partie sauvegardée avec succès! (ID: " + gameId + ")");
            System.out.println("💡 Vous pourrez reprendre cette partie plus tard.");
        } else {
            System.out.println("❌ Erreur lors de la sauvegarde.");
        }
        pauseForInput();
    }

    /**
     * Termine une partie et affiche les résultats
     */
    private void finishGame() {
        clearScreen();
        gameBoard.displayBoard();

        String gameResult = gameBoard.getGameResult(player1.getUsername(), player2.getUsername());

        System.out.println("\n🎊 ═══════════════ FIN DE PARTIE ═══════════════ 🎊");
        System.out.println("🏆 " + gameResult);
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // Sauvegarder le résultat
        playerManager.saveGameResult(player1.getUsername(), player2.getUsername(), gameResult);

        // Afficher les nouvelles statistiques
        System.out.println("\n📊 Statistiques mises à jour:");
        playerManager.showPlayerStats(player1.getUsername());
        playerManager.showPlayerStats(player2.getUsername());

        System.out.println("\n🎮 Partie terminée! Retour au menu principal...");
        pauseForInput();
    }

    /**
     * Affiche l'historique des parties
     */
    private void showGameHistory() {
        clearScreen();
        playerManager.showHistory();
        pauseForInput();
    }

    /**
     * Affiche les statistiques des joueurs
     */
    private void showPlayerStatistics() {
        clearScreen();
        System.out.println("╔══════════ STATISTIQUES ═══════════╗");

        if (player1 != null) {
            playerManager.showPlayerStats(player1.getUsername());
        }
        if (player2 != null) {
            playerManager.showPlayerStats(player2.getUsername());
        }
        if (player1 == null && player2 == null) {
            System.out.println("║ ⚠️  Aucun joueur connecté.        ║");
            System.out.println("╚═══════════════════════════════════╝");
        }

        pauseForInput();
    }

    /**
     * Affiche les règles du jeu
     */
    private void showGameRules() {
        clearScreen();
        System.out.println("╔═══════════════════ RÈGLES DU JEU ════════════════════╗");
        System.out.println("║                                                      ║");
        System.out.println("║ 🎯 OBJECTIF                                          ║");
        System.out.println("║ • Atteindre le sanctuaire ennemi pour gagner         ║");
        System.out.println("║                                                      ║");
        System.out.println("║ 🐉 HIÉRARCHIE DES PIÈCES (du plus fort au faible)    ║");
        System.out.println("║ 1. 🐘 Éléphant  2. 🦁 Lion     3. 🐅 Tigre           ║");
        System.out.println("║ 4. 🐆 Panthère  5. 🐕 Chien    6. 🐺 Loup            ║");
        System.out.println("║ 7. 🐱 Chat      8. 🐭 Rat                            ║");
        System.out.println("║                                                      ║");
        System.out.println("║ ⚔️  RÈGLES DE CAPTURE                                ║");
        System.out.println("║ • Une pièce capture les pièces de rang ≤ au sien     ║");
        System.out.println("║ • EXCEPTION: Le Rat peut capturer l'Éléphant         ║");
        System.out.println("║                                                      ║");
        System.out.println("║ 🏞️  TERRAIN SPÉCIAL                                  ║");
        System.out.println("║ • 🌊 Rivière: Seul le Rat peut nager                 ║");
        System.out.println("║ • 🦁🐅 Lion/Tigre peuvent sauter par-dessus          ║");
        System.out.println("║ • 💥 Pièges: Rendent vulnérables les pièces ennemies ║");
        System.out.println("║ • 🏛️ Sanctuaires: Interdit d'entrer dans le sien     ║");
        System.out.println("║                                                      ║");
        System.out.println("║ 🚫 RESTRICTIONS                                      ║");
        System.out.println("║ • Déplacement: 1 case horizontale ou verticale       ║");
        System.out.println("║ • Pas de diagonales                                  ║");
        System.out.println("║ • Le Rat ne peut capturer en sortant de l'eau        ║");
        System.out.println("║                                                      ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");
        pauseForInput();
    }

    // === MÉTHODES UTILITAIRES ===

    /**
     * Vérifie si les deux joueurs sont connectés
     */
    private boolean bothPlayersConnected() {
        return player1 != null && player2 != null;
    }

    /**
     * Obtient un choix numérique de l'utilisateur
     */
    private int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Fait une pause en attendant une entrée utilisateur
     */
    private void pauseForInput() {
        System.out.print("\nAppuyez sur Entrée pour continuer...");
        scanner.nextLine();
    }

    /**
     * Simule un nettoyage d'écran
     */
    private void clearScreen() {
        // Sur Windows/Linux/Mac - ajouter des lignes vides pour "nettoyer" l'écran
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }

    /**
     * Nettoie les ressources avant fermeture
     */
    private void cleanup() {
        if (playerManager != null) {
            playerManager.cleanup();
        }
        if (scanner != null) {
            scanner.close();
        }
    }
}
