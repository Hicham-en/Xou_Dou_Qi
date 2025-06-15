# Xou Dou Qi - Jeu Traditionnel Chinois 🐉

## Structure du Projet

```
Xou_Dou_Qi/
├── database/           # Base de données SQLite
│   └── xoudouqi.db    # Fichier de base de données
├── lib/               # Bibliothèques externes
│   └── sqlite-jdbc.jar
├── src/main/com/xoudou/
│   ├── bo/            # Business Objects
│   │   ├── Game.java
│   │   ├── Piece.java
│   │   └── Player.java
│   ├── bll/           # Business Logic Layer
│   │   ├── GameBoard.java
│   │   └── PlayerManager.java
│   ├── dal/           # Data Access Layer
│   │   └── DatabaseManager.java
│   └── XouDouQiMain.java  # Classe principale
└── target/classes/    # Fichiers compilés
```

## Packages

- **com.xoudou.bo** - Entités métier (Player, Piece, Game)
- **com.xoudou.bll** - Logique métier (GameBoard, PlayerManager)  
- **com.xoudou.dal** - Accès aux données (DatabaseManager)

## Lancement 🚀

**Simple :** Accedez au chemin du projet dans votre ordinateur 
Tapez les deux commandes dans votre Terminal: chcp 65001
puis : java -cp "target\classes;lib\sqlite-jdbc.jar" XouDouQiMain

**Optimal :** Utilisez Windows Terminal pour un meilleur affichage des emojis

## Règles du Jeu 🎯

Le Xou Dou Qi est un jeu de stratégie traditionnel chinois où l'objectif est d'atteindre le sanctuaire ennemi. Le Lion et le Tigre peuvent sauter par-dessus la rivière dans le sens de la largeur et de la longueur, sauf si un rat nageant bloque leur trajectoire.

## Fonctionnalités ✨

- 🎮 Affichage complet avec emojis
- 💾 Base de données SQLite pour sauvegardes
- 👤 Système de comptes utilisateurs
- 📜 Historique des parties
- ⏯️ Reprendre les parties en cours
- 🎯 Toutes les règles traditionnelles implémentées
