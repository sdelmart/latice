# Latice — texte de présentation (portfolio / CV)

## Pitch court (une phrase)

Jeu de société *Latice* en Java/JavaFX, repris en solo après le projet de groupe initial (SAE 1ère année BUT Informatique) : mode solo contre une IA, aide au placement, personnalisation, historique des parties et thèmes visuels, avec un build Maven multiplateforme et 35 tests JUnit 5.

## Description longue

Latice est un jeu de plateau au tour par tour (type Qwirkle) développé en Java/JavaFX. Initialement réalisé en équipe pendant une SAE de 1ère année de BUT Informatique, le projet a été repris et considérablement enrichi en solo :

- **Mode solo contre une IA** qui choisit, à chaque tour, le coup légal le plus rentable sur son rack (règles d'adjacence couleur/symbole identiques à un joueur humain).
- **Aide au placement** : les cases où une tuile peut être posée s'illuminent pendant le glisser-déposer.
- **Menu principal façon jeu vidéo** avec navigation en fondu entre écrans (Jouer, Profils, Règles, Paramètres, Crédits).
- **Personnalisation** : pseudo et couleur par joueur, mémorisés d'une partie à l'autre.
- **Historique des parties** persistant localement.
- **Musique, effets sonores et thèmes visuels** (dégradés et photos) sélectionnables et sauvegardés.
- **Build Maven multiplateforme** (Windows/Linux/macOS) via Maven Wrapper — aucune installation de Maven nécessaire.
- **Architecture MVC testée** : logique métier (`latice.metier`) totalement découplée de JavaFX, 35 tests JUnit 5 (règles de jeu, IA, format de l'historique).

## Stack technique

Java 17 · JavaFX 21 · Maven · JUnit 5 · CSS (thème JavaFX) · `java.util.prefs` pour la persistance des préférences.

## Points techniques à mettre en avant à l'oral/entretien

- Extraction du cœur de la logique de placement (`placerTuile`) pour être réutilisée à l'identique par le joueur humain (drag & drop) et par l'IA — pas de duplication de la logique de score.
- IA gloutonne pure (`JoueurArtificiel`), sans dépendance JavaFX, donc testable unitairement sans lancer d'interface graphique.
- Bug corrigé : l'écran de fin de partie fermait silencieusement toute l'application (dernière fenêtre JavaFX fermée) — remplacé par une navigation explicite (Rejouer / Menu principal).
- Portabilité Windows/Linux/macOS vérifiée : chemins de ressources sensibles à la casse corrigés, dépendances JavaFX natives résolues automatiquement par plateforme via Maven.
