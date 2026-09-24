# Latice

[![CI](https://github.com/sdelmart/latice/actions/workflows/ci.yml/badge.svg)](https://github.com/sdelmart/latice/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
![Java 17](https://img.shields.io/badge/Java-17-orange)

Jeu de plateau (type Qwirkle) en JavaFX, développé initialement en SAE 1ère année de BUT Informatique
puis repris et enrichi individuellement.

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

### Jar autonome

```bash
./mvnw package -DskipTests
java -jar target/latice-*.jar
```

Produit un `.jar` exécutable incluant toutes les dépendances — utilisable sans Maven ni build local, à condition d'avoir un JDK 17+ installé.

## Lancer les tests

```bash
./mvnw test
```

Génère aussi un rapport de couverture JaCoCo dans `target/site/jacoco/index.html`.

Les tests d'interface (TestFX, dossier `ControleurAccueilUITest`) nécessitent un affichage réel et
sont donc exclus par défaut (utile en CI headless). Pour les inclure sur un poste avec écran :

```bash
./mvnw test -DexcludedGroups=
```

## Compiler sans lancer

```bash
./mvnw compile
```

## Intégration continue

Chaque push/PR sur `main` déclenche [la CI GitHub Actions](.github/workflows/ci.yml) : les 36 tests (hors tests d'interface, voir ci-dessus) tournent sur Ubuntu, Windows et macOS, un jar exécutable est construit, et une analyse statique (SpotBugs) s'exécute en non-bloquant.

## Architecture

Voir [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) pour le détail (diagrammes de classes, séquence du tour de l'IA, choix de conception). En résumé :

- `latice.metier` — logique de jeu pure, sans dépendance JavaFX (`Plateau`, `Joueur`, `Pioche`, `Rack`, `Tuile`, `Arbitre`, `JoueurArtificiel` pour l'IA, `HistoriqueParties` pour la persistance des scores).
- `latice.controleur` — contrôleurs JavaFX (un par écran FXML) + utilitaires transverses (`Musique`, `EffetsSonores`, `ThemeVisuel`, `Navigation`).
- `src/main/resources/ihm/*.fxml` — mise en page de chaque écran, stylée via `src/main/resources/css/theme.css`.
- Les préférences utilisateur (dernier pseudo/couleur, musique, volume, fond d'écran, effets sonores) sont persistées via `java.util.prefs.Preferences` ; l'historique des parties est stocké dans une base SQLite locale (`~/.latice/historique.db`).

## Historique des versions

Voir [`CHANGELOG.md`](CHANGELOG.md).

## Licence

[MIT](LICENSE) — voir le fichier pour la note sur l'origine du projet (travail de groupe repris individuellement).

## Crédits & licences des assets ajoutés

- Images de fond : [Pexels](https://www.pexels.com) (licence Pexels — libres d'usage personnel et commercial).
- Effets sonores : [Mixkit](https://mixkit.co) (licence Mixkit — libres d'usage, sans attribution obligatoire).

Le détail (titre, auteur, lien) est aussi visible dans le jeu via **Menu principal → Crédits**.
