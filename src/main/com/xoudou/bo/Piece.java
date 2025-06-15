package com.xoudou.bo;

/**
 * Classe représentant une pièce du jeu Xou Dou Qi
 * Hiérarchie: 1=Éléphant, 2=Lion, 3=Tigre, 4=Panthère, 5=Chien, 6=Loup, 7=Chat,
 * 8=Rat
 */
public class Piece {
    private int type; // 1-8 selon la hiérarchie
    private boolean isPlayer1;
    private String name;

    // Noms des pièces
    private static final String[] PIECE_NAMES = {
            "", "Éléphant", "Lion", "Tigre", "Panthère", "Chien", "Loup", "Chat", "Rat"
    };

    public Piece(int type, boolean isPlayer1) {
        this.type = type;
        this.isPlayer1 = isPlayer1;
        this.name = PIECE_NAMES[type];
    }

    // Getters
    public int getType() {
        return type;
    }

    public boolean isPlayer1() {
        return isPlayer1;
    }

    public String getName() {
        return name;
    }

    /**
     * Vérifie si cette pièce peut capturer une autre pièce
     * Règles: Une pièce peut capturer une pièce de rang inférieur ou égal
     * Exception: Le Rat (8) peut capturer l'Éléphant (1)
     */
    public boolean canCapture(Piece other) {
        if (other == null)
            return false;

        // Exception spéciale: Rat peut capturer Éléphant
        if (this.type == 8 && other.type == 1) {
            return true;
        }

        // Règle générale: peut capturer rang inférieur ou égal
        return this.type <= other.type;
    }

    /**
     * Vérifie si cette pièce peut sauter par-dessus la rivière
     * Seuls le Lion (2) et le Tigre (3) peuvent sauter
     */
    public boolean canJumpRiver() {
        return type == 2 || type == 3; // Lion ou Tigre
    }

    /**
     * Vérifie si cette pièce peut nager dans l'eau
     * Seul le Rat (8) peut nager
     */
    public boolean canSwim() {
        return type == 8; // Rat
    }

    /**
     * Obtient la force de la pièce (inversement proportionnelle au type)
     * Plus le type est petit, plus la pièce est forte
     */
    public int getStrength() {
        return 9 - type; // Éléphant=8, Lion=7, ..., Rat=1
    }

    @Override
    public String toString() {
        return name + (isPlayer1 ? "(J1)" : "(J2)");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Piece piece = (Piece) obj;
        return type == piece.type && isPlayer1 == piece.isPlayer1;
    }

    @Override
    public int hashCode() {
        return type * 31 + (isPlayer1 ? 1 : 0);
    }
}
