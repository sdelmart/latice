# Latice

Jeu de plateau (type Qwirkle) en JavaFX, développé initialement en SAE 1ère année de BUT Informatique.

## Fonctionnalités

- Menu principal façon jeu vidéo (Jouer, Profils, Règles, Paramètres, Crédits) avec navigation en fondu.
- **Mode 2 joueurs** ou **mode solo contre une IA** (l'IA cherche le coup légal le plus rentable sur son rack).
- Aide au placement : les cases où une tuile peut être posée s'illuminent pendant le glisser-déposer.
- Personnalisation : pseudo + couleur par joueur (mémorisés d'une partie à l'autre).
- Historique des parties jouées (panneau "Profils").
- Musique et effets sonores sélectionnables, plusieurs fonds d'écran (dégradés et photos), tout configurable et persistant dans Paramètres.
- Écran de fin de partie avec scores détaillés, et boutons Rejouer / Menu principal.

## Prérequis

- **JDK 17 ou plus récent** (aucune autre installation nécessaire : le Maven Wrapper fourni télécharge Maven tout seul).

## Lancer le jeu

Depuis la racine du projet :

**macOS / Linux**
```bash
./mvnw javafx:run
```

**Windows**
```bash
mvnw.cmd javafx:run
```

Le wrapper télécharge automatiquement la bonne version de Maven puis les dépendances JavaFX natives correspondant à votre OS/architecture (Windows, Linux, macOS Intel/Apple Silicon).

## Lancer les tests

```bash
./mvnw test
```

## Compiler sans lancer

```bash
./mvnw compile
```

## Architecture (aperçu rapide)

- `latice.metier` — logique de jeu pure, sans dépendance JavaFX (`Plateau`, `Joueur`, `Pioche`, `Rack`, `Tuile`, `Arbitre`, `JoueurArtificiel` pour l'IA, `HistoriqueParties` pour la persistance des scores).
- `latice.controleur` — contrôleurs JavaFX (un par écran FXML) + utilitaires transverses (`Musique`, `EffetsSonores`, `ThemeVisuel`, `Navigation`).
- `src/main/resources/ihm/*.fxml` — mise en page de chaque écran, stylée via `src/main/resources/css/theme.css`.
- Les préférences utilisateur (dernier pseudo/couleur, musique, volume, fond d'écran, effets sonores) sont persistées via `java.util.prefs.Preferences` ; l'historique des parties est écrit dans `~/.latice/historique.csv`.

## Crédits & licences des assets ajoutés

- Images de fond : [Pexels](https://www.pexels.com) (licence Pexels — libres d'usage personnel et commercial).
- Effets sonores : [Mixkit](https://mixkit.co) (licence Mixkit — libres d'usage, sans attribution obligatoire).

Le détail (titre, auteur, lien) est aussi visible dans le jeu via **Menu principal → Crédits**.
