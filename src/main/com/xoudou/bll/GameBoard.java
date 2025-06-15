package com.xoudou.bll;

import com.xoudou.bo.Piece;

/**
 * Plateau de jeu Xou Dou Qi avec toutes les règles implémentées
 * Plateau 9x7 avec rivière, pièges et sanctuaires
 */
public class GameBoard {
    private static final int ROWS = 9;
    private static final int COLS = 7;
    private Piece[][] board;
    private boolean gameOver;
    private String gameResult;

    // Positions spéciales du plateau
    private static final int[][] RIVER_POSITIONS = {
            { 3, 1 }, { 3, 2 }, { 4, 1 }, { 4, 2 }, { 5, 1 }, { 5, 2 }, // Rivière gauche
            { 3, 4 }, { 3, 5 }, { 4, 4 }, { 4, 5 }, { 5, 4 }, { 5, 5 } // Rivière droite
    };

    private static final int[][] TRAP_POSITIONS = {
            { 0, 2 }, { 0, 4 }, { 1, 3 }, // Pièges joueur 2 (haut)
            { 8, 2 }, { 8, 4 }, { 7, 3 } // Pièges joueur 1 (bas)
    };

    private static final int[] SANCTUARY_PLAYER1 = { 8, 3 }; // Sanctuaire joueur 1 (bas)
    private static final int[] SANCTUARY_PLAYER2 = { 0, 3 }; // Sanctuaire joueur 2 (haut)

    public GameBoard() {
        board = new Piece[ROWS][COLS];
        gameOver = false;
        gameResult = "";
    }

    /**
     * Initialise le plateau avec les pièces dans leurs positions de départ
     */
    public void initializeBoard() {
        // Nettoyer le plateau
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = null;
            }
        }

        // Placer les pièces du Joueur 2 (haut du plateau)
        board[2][6] = new Piece(1, false); // Éléphant
        board[0][0] = new Piece(2, false); // Lion
        board[0][6] = new Piece(3, false); // Tigre
        board[2][2] = new Piece(4, false); // Panthère
        board[1][1] = new Piece(5, false); // Chien
        board[2][4] = new Piece(6, false); // Loup
        board[1][5] = new Piece(7, false); // Chat
        board[2][0] = new Piece(8, false); // Rat

        // Placer les pièces du Joueur 1 (bas du plateau) - positions miroir
        board[6][0] = new Piece(1, true); // Éléphant
        board[8][6] = new Piece(2, true); // Lion
        board[8][0] = new Piece(3, true); // Tigre
        board[6][4] = new Piece(4, true); // Panthère
        board[7][5] = new Piece(5, true); // Chien
        board[6][2] = new Piece(6, true); // Loup
        board[7][1] = new Piece(7, true); // Chat
        board[6][6] = new Piece(8, true); // Rat

        gameOver = false;
        gameResult = "";
    }

    /**
     * Affiche le plateau de jeu avec des traits décoratifs
     */
    public void displayBoard() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║                   🎯 PLATEAU XOU DOU QI 🎯                    ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");

        // En-tête avec numéros de colonnes
        System.out.print("║     ");
        for (int j = 0; j < COLS; j++) {
            System.out.printf("   %d    ", j);
        }
        System.out.println("  ║");

        System.out.println("║   ╔═════════════════════════════════════════════════════════╗ ║");

        for (int i = 0; i < ROWS; i++) {
            // Ligne avec numéro de rangée
            System.out.printf("║ %d ║  ", i);

            for (int j = 0; j < COLS; j++) {
                String cellContent = getCellDisplay(i, j);
                System.out.print(" " + cellContent + " ");

                // Séparateur vertical
                if (j < COLS - 1) {
                    System.out.print("│");
                }
            }
            System.out.println("║ ║");

            // Ligne horizontale entre les rangées (sauf dernière)
            if (i < ROWS - 1) {
                System.out.print("║   ║──");
                for (int j = 0; j < COLS; j++) {
                    if (isRiver(i, j) || isRiver(i + 1, j)) {
                        System.out.print("═══════");
                    } else {
                        System.out.print("───────");
                    }
                    if (j < COLS - 1) {
                        System.out.print("┼");
                    }
                }
                System.out.println("║ ║");
            }
        }

        System.out.println("║   ╚═════════════════════════════════════════════════════════╝ ║");
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");
        System.out.println("║    🔹J1: emojis normaux     │ 🔸 J2: emojis avec '            ║");
        System.out.println("║        🌊 =Rivière  │  💥 T=Piège  │  🏛️ S=Sanctuaire         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
    }

    /**
     * Obtient l'affichage d'une cellule
     */
    private String getCellDisplay(int row, int col) {
        if (board[row][col] != null) {
            Piece piece = board[row][col];

            String symbol = switch (piece.getType()) {
                case 1 -> "🐘";
                case 2 -> "🦁";
                case 3 -> "🐯";
                case 4 -> "🐆";
                case 5 -> "🐶";
                case 6 -> "🐺";
                case 7 -> "🐱";
                case 8 -> "🐭"; 
                default -> "?";
            };
            
            if (!piece.isPlayer1()) {
                symbol += "'";
            }
            return String.format(" %3s ", symbol);
        } else {
            // Afficher le terrain spécial
            if (isRiver(row, col)) {
                return " 🌊  ";
            } else if (isTrap(row, col)) {
                return " 💥  ";
            } else if (isSanctuaire(row, col)) {
                return " 🏛️  ";
            } else {
                return " ..  ";
            }
        }
    }

    /**
     * Vérifie si une position est valide sur le plateau
     */
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < ROWS && col >= 0 && col < COLS;
    }

    /**
     * Vérifie si une position est dans la rivière
     */
    public boolean isRiver(int row, int col) {
        for (int[] pos : RIVER_POSITIONS) {
            if (pos[0] == row && pos[1] == col) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si une position est un piège
     */
    public boolean isTrap(int row, int col) {
        for (int[] pos : TRAP_POSITIONS) {
            if (pos[0] == row && pos[1] == col) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si une position est un sanctuaire
     */
    public boolean isSanctuaire(int row, int col) {
        return (row == SANCTUARY_PLAYER1[0] && col == SANCTUARY_PLAYER1[1]) ||
                (row == SANCTUARY_PLAYER2[0] && col == SANCTUARY_PLAYER2[1]);
    }

    /**
     * Effectue un mouvement sur le plateau
     */
    public boolean makeMove(int fromRow, int fromCol, int toRow, int toCol, boolean isPlayer1) {
        // Validation de base
        if (!isValidPosition(fromRow, fromCol) || !isValidPosition(toRow, toCol)) {
            System.out.println("❌ Position invalide!");
            return false;
        }

        Piece piece = board[fromRow][fromCol];
        if (piece == null) {
            System.out.println("❌ Aucune pièce à cette position!");
            return false;
        }

        if (piece.isPlayer1() != isPlayer1) {
            System.out.println("❌ Cette pièce ne vous appartient pas!");
            return false;
        }

        // Ne peut pas entrer dans son propre sanctuaire
        if (isSanctuaire(toRow, toCol)) {
            boolean isOwnSanctuary = (isPlayer1 && toRow == SANCTUARY_PLAYER1[0] && toCol == SANCTUARY_PLAYER1[1]) ||
                    (!isPlayer1 && toRow == SANCTUARY_PLAYER2[0] && toCol == SANCTUARY_PLAYER2[1]);
            if (isOwnSanctuary) {
                System.out.println("❌ Vous ne pouvez pas entrer dans votre propre sanctuaire!");
                return false;
            }
        }

        // Vérifier si le mouvement est valide selon les règles
        if (!isValidMove(piece, fromRow, fromCol, toRow, toCol)) {
            return false;
        }

        // Vérifier la capture
        Piece targetPiece = board[toRow][toCol];
        if (targetPiece != null) {
            if (targetPiece.isPlayer1() == piece.isPlayer1()) {
                System.out.println("❌ Vous ne pouvez pas capturer vos propres pièces!");
                return false;
            }

            // Vérifier si la capture est possible selon les règles de hiérarchie
            if (!canCapture(piece, targetPiece, fromRow, fromCol, toRow, toCol)) {
                return false;
            }

            System.out.println("🎯 " + piece.getName() + " capture " + targetPiece.getName() + "!");
        }

        // Effectuer le mouvement
        board[toRow][toCol] = piece;
        board[fromRow][fromCol] = null;

        // Vérifier les conditions de fin de jeu
        checkGameEnd();

        return true;
    }

    /**
     * Vérifie si un mouvement est valide selon les règles du jeu
     */
    private boolean isValidMove(Piece piece, int fromRow, int fromCol, int toRow, int toCol) {
        int deltaRow = Math.abs(toRow - fromRow);
        int deltaCol = Math.abs(toCol - fromCol);

        // Mouvement de base: une case horizontalement ou verticalement
        boolean isBasicMove = (deltaRow == 1 && deltaCol == 0) || (deltaRow == 0 && deltaCol == 1);

        // Le Rat ne peut pas capturer en sortant de l'eau
        if (piece.getType() == 8 && isRiver(fromRow, fromCol) && !isRiver(toRow, toCol)) {
            Piece target = board[toRow][toCol];
            if (target != null && target.isPlayer1() != piece.isPlayer1()) {
                System.out.println("❌ Le Rat ne peut pas capturer en sortant de la rivière!");
                return false;
            }
        }

        // Vérifier les mouvements spéciaux
        if (piece.canJumpRiver()) {
            // Lion et Tigre peuvent sauter par-dessus la rivière
            if (isRiverJump(fromRow, fromCol, toRow, toCol)) {
                return isRiverJumpClear(fromRow, fromCol, toRow, toCol);
            }
        }

        // Seul le Rat peut se déplacer dans l'eau
        if (isRiver(toRow, toCol) && !piece.canSwim()) {
            System.out.println("❌ Seul le Rat peut nager dans la rivière!");
            return false;
        }

        if (!isBasicMove) {
            System.out.println("❌ Les pièces ne peuvent se déplacer que d'une case horizontalement ou verticalement!");
            return false;
        }

        return true;
    }

    /**
     * Vérifie si c'est un saut par-dessus la rivière
     * Lion et Tigre peuvent sauter par-dessus la rivière dans le sens de la largeur
     * et de la longueur
     */
    private boolean isRiverJump(int fromRow, int fromCol, int toRow, int toCol) {
        // Saut horizontal (largeur) - sauter d'un côté à l'autre de la rivière
        if (fromRow == toRow) {
            // Rivière occupe colonnes 1,2 et 4,5 (séparées par colonne 3)
            // Saut possible de 0->3, 3->0, 3->6, 6->3
            int deltaCol = Math.abs(toCol - fromCol);
            if (deltaCol >= 2) { // Au moins 2 cases pour traverser la rivière
                return crossesRiver(fromRow, fromCol, toRow, toCol, true);
            }
        }

        // Saut vertical (longueur) - sauter du haut vers le bas de la rivière
        if (fromCol == toCol) {
            // Rivière occupe lignes 3,4,5
            // Saut possible de 2->6, 6->2, etc.
            int deltaRow = Math.abs(toRow - fromRow);
            if (deltaRow >= 2) { // Au moins 2 cases pour traverser la rivière
                return crossesRiver(fromRow, fromCol, toRow, toCol, false);
            }
        }

        return false;
    }

    /**
     * Vérifie si le mouvement traverse bien la rivière
     */
    private boolean crossesRiver(int fromRow, int fromCol, int toRow, int toCol, boolean horizontal) {
        if (horizontal) {
            // Vérifier qu'on traverse bien les zones de rivière
            int minCol = Math.min(fromCol, toCol);
            int maxCol = Math.max(fromCol, toCol);
            boolean crossesRiverZone = false;

            for (int col = minCol + 1; col < maxCol; col++) {
                if (isRiver(fromRow, col)) {
                    crossesRiverZone = true;
                    break;
                }
            }
            return crossesRiverZone;
        } else {
            // Vérifier qu'on traverse bien les zones de rivière verticalement
            int minRow = Math.min(fromRow, toRow);
            int maxRow = Math.max(fromRow, toRow);
            boolean crossesRiverZone = false;

            for (int row = minRow + 1; row < maxRow; row++) {
                if (isRiver(row, fromCol)) {
                    crossesRiverZone = true;
                    break;
                }
            }
            return crossesRiverZone;
        }
    }

    /**
     * Vérifie si le chemin de saut par-dessus la rivière est libre
     * Spécialement vérifie s'il y a un rat nageant sur la trajectoire
     */
    private boolean isRiverJumpClear(int fromRow, int fromCol, int toRow, int toCol) {
        if (fromRow == toRow) {
            // Saut horizontal - vérifier chaque case sur le chemin
            int minCol = Math.min(fromCol, toCol);
            int maxCol = Math.max(fromCol, toCol);
            for (int col = minCol + 1; col < maxCol; col++) {
                Piece pieceOnPath = board[fromRow][col];
                if (pieceOnPath != null) {
                    // Si c'est un rat dans la rivière, il bloque le saut
                    if (pieceOnPath.getType() == 8 && isRiver(fromRow, col)) {
                        System.out.println("❌ Le saut est bloqué par un rat nageant!");
                        return false;
                    }
                    // Toute autre pièce bloque aussi le saut
                    if (!isRiver(fromRow, col)) {
                        System.out.println("❌ Le chemin de saut est bloqué!");
                        return false;
                    }
                }
            }
        } else {
            // Saut vertical - vérifier chaque case sur le chemin
            int minRow = Math.min(fromRow, toRow);
            int maxRow = Math.max(fromRow, toRow);
            for (int row = minRow + 1; row < maxRow; row++) {
                Piece pieceOnPath = board[row][fromCol];
                if (pieceOnPath != null) {
                    // Si c'est un rat dans la rivière, il bloque le saut
                    if (pieceOnPath.getType() == 8 && isRiver(row, fromCol)) {
                        System.out.println("❌ Le saut est bloqué par un rat nageant!");
                        return false;
                    }
                    // Toute autre pièce bloque aussi le saut
                    if (!isRiver(row, fromCol)) {
                        System.out.println("❌ Le chemin de saut est bloqué!");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Vérifie si une pièce peut en capturer une autre
     */
    private boolean canCapture(Piece attacker, Piece defender, int fromRow, int fromCol, int toRow, int toCol) {
        // Dans un piège, les pièces ennemies sont vulnérables à toutes les attaques
        if (isTrap(toRow, toCol)) {
            boolean isDefenderInEnemyTrap = (defender.isPlayer1() && toRow <= 1) || // J1 dans piège J2
                    (!defender.isPlayer1() && toRow >= 7); // J2 dans piège J1

            if (isDefenderInEnemyTrap) {
                System.out.println("⚡ Pièce prise au piège - capture possible!");
                return true;
            }
        }

        // Règles normales de capture
        if (!attacker.canCapture(defender)) {
            System.out.println("❌ " + attacker.getName() + " ne peut pas capturer " + defender.getName() + "!");
            return false;
        }

        return true;
    }

    /**
     * Vérifie les conditions de fin de jeu
     */
    private void checkGameEnd() {
        // Vérifier si quelqu'un a atteint le sanctuaire ennemi
        Piece sanctuaryJ1 = board[SANCTUARY_PLAYER1[0]][SANCTUARY_PLAYER1[1]];
        Piece sanctuaryJ2 = board[SANCTUARY_PLAYER2[0]][SANCTUARY_PLAYER2[1]];

        if (sanctuaryJ1 != null && !sanctuaryJ1.isPlayer1()) {
            gameOver = true;
            gameResult = "Victoire Joueur 2 - Sanctuaire atteint!";
            return;
        }

        if (sanctuaryJ2 != null && sanctuaryJ2.isPlayer1()) {
            gameOver = true;
            gameResult = "Victoire Joueur 1 - Sanctuaire atteint!";
            return;
        }

        // Vérifier s'il reste des pièces pour chaque joueur
        boolean hasPlayer1Pieces = false;
        boolean hasPlayer2Pieces = false;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] != null) {
                    if (board[i][j].isPlayer1()) {
                        hasPlayer1Pieces = true;
                    } else {
                        hasPlayer2Pieces = true;
                    }
                }
            }
        }

        if (!hasPlayer1Pieces) {
            gameOver = true;
            gameResult = "Victoire Joueur 2 - Plus de pièces pour Joueur 1!";
        } else if (!hasPlayer2Pieces) {
            gameOver = true;
            gameResult = "Victoire Joueur 1 - Plus de pièces pour Joueur 2!";
        }
    }

    /**
     * Obtient le résultat de la partie formaté
     */
    public String getGameResult(String player1Name, String player2Name) {
        if (!gameOver)
            return "Partie en cours";

        if (gameResult.contains("Victoire Joueur 1")) {
            return "Victoire " + player1Name;
        } else if (gameResult.contains("Victoire Joueur 2")) {
            return "Victoire " + player2Name;
        } else {
            return gameResult;
        }
    }

    // Getters
    public boolean isGameOver() {
        return gameOver;
    }

    public String getGameResult() {
        return gameResult;
    }

    public Piece[][] getBoard() {
        return board;
    }

    /**
     * Sérialise l'état du plateau pour sauvegarde
     */
    public String serializeBoardState() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                if (board[i][j] != null) {
                    Piece piece = board[i][j];
                    sb.append(i).append(",").append(j).append(",")
                            .append(piece.getType()).append(",")
                            .append(piece.isPlayer1() ? "1" : "0").append(";");
                }
            }
        }
        return sb.toString();
    }

    /**
     * Restaure l'état du plateau depuis une sauvegarde
     */
    public void deserializeBoardState(String boardState) {
        // Nettoyer le plateau
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                board[i][j] = null;
            }
        }

        if (boardState == null || boardState.trim().isEmpty()) {
            return;
        }

        // Restaurer les pièces
        String[] pieces = boardState.split(";");
        for (String pieceData : pieces) {
            if (!pieceData.trim().isEmpty()) {
                String[] parts = pieceData.split(",");
                if (parts.length == 4) {
                    int row = Integer.parseInt(parts[0]);
                    int col = Integer.parseInt(parts[1]);
                    int type = Integer.parseInt(parts[2]);
                    boolean isPlayer1 = parts[3].equals("1");

                    if (isValidPosition(row, col)) {
                        board[row][col] = new Piece(type, isPlayer1);
                    }
                }
            }
        }
    }
}
