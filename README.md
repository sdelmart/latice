# Latice

[![CI](https://github.com/sdelmart/latice/actions/workflows/ci.yml/badge.svg)](https://github.com/sdelmart/latice/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
![Java 17](https://img.shields.io/badge/Java-17-orange)

Jeu de plateau (type Qwirkle) en JavaFX, développé initialement en SAE 1ère année de BUT Informatique
puis repris et enrichi individuellement.

## Télécharger

Les [**releases**](https://github.com/sdelmart/latice/releases/latest) fournissent le jeu prêt à lancer, **sans rien installer** (l'application embarque son environnement Java) :

| Système | Fichier |
|---|---|
| Windows (x64) | `Latice-<version>-windows-x64.zip` (ou `.tar.gz`, ou `.msi`) |
| macOS Apple Silicon | `Latice-<version>-macos-arm64.tar.gz` (ou `.dmg`) |
| macOS Intel | `Latice-<version>-macos-x64.tar.gz` (ou `.dmg`) |
| Linux x86-64 | `Latice-<version>-linux-x64.tar.gz` (ou `.deb`) |
| Linux ARM64 | `Latice-<version>-linux-arm64.tar.gz` (ou `.deb`) |

Extraire l'archive puis lancer `Latice` (`Latice.exe` sous Windows, `Latice.app` sous macOS, `bin/Latice` sous Linux).
L'application n'étant pas signée par un éditeur certifié, Windows SmartScreen et macOS Gatekeeper peuvent afficher un
avertissement au premier lancement (détails dans les notes de chaque release). Sommes de contrôle : `SHA256SUMS.txt`.

## Fonctionnalités

- Menu principal façon jeu vidéo (Jouer, Profils, Règles, Paramètres, Crédits) avec navigation en fondu.
- **Mode 2 joueurs** ou **mode solo contre une IA** (l'IA cherche le coup légal le plus rentable sur son rack).
- Aide au placement : les cases où une tuile peut être posée s'illuminent pendant le glisser-déposer.
- **Sauvegarde automatique et reprise** : la partie est sauvegardée à chaque tour, le bouton *Continuer* du menu la reprend.
- Règles de Latice appliquées dans un seul module testé : une tuile doit être compatible (même couleur ou même symbole) avec **toutes** les tuiles qu'elle touche ; la partie s'arrête aussi quand un joueur vide son rack et sa pioche.
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

Produit un `.jar` exécutable incluant toutes les dépendances, **pour le système sur lequel il a été construit** (les bibliothèques natives de JavaFX diffèrent selon l'OS et le processeur) ; un JDK 17+ est nécessaire pour le lancer. Pour distribuer le jeu, utiliser les paquets de la release.

Contrôle d'un build : `java -jar target/latice-*.jar --verifier-ressources` vérifie que toutes les ressources (images, sons, écrans, SQLite, natifs JavaFX) sont présentes ; la CI le lance sur chaque paquet de chaque plateforme.

## Lancer les tests

```bash
./mvnw test
```

Génère aussi un rapport de couverture JaCoCo dans `target/site/jacoco/index.html`.

Les tests d'interface (TestFX, dossier `ControleurAccueilUITest`) nécessitent un affichage réel et
sont donc exclus par défaut (utile en CI headless). Pour les inclure sur un poste avec écran :

```bash
./mvnw test -Dtests.exclus=aucun
```

## Compiler sans lancer

```bash
./mvnw compile
```

## Intégration continue

Chaque push/PR sur `main` déclenche [la CI GitHub Actions](.github/workflows/ci.yml) : les 65 tests (hors tests d'interface, voir ci-dessus) tournent sur Ubuntu, Windows et macOS, un jar exécutable est construit et vérifié, et l'analyse statique (SpotBugs) doit passer sans aucun signalement.

Pousser un tag `vX.Y.Z` (égal à la version du `pom.xml`) lance [le workflow de release](.github/workflows/release.yml) : l'application est empaquetée avec `jpackage` sur Linux x64 et ARM64, macOS Intel et Apple Silicon, et Windows, chaque paquet est vérifié, puis la release GitHub est publiée avec les archives, les installeurs et `SHA256SUMS.txt`.

## Architecture

Voir [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) pour le détail (diagrammes de classes, séquence du tour de l'IA, choix de conception). En résumé :

- `latice.metier` — logique de jeu pure, sans dépendance JavaFX : `Regles` (placement, points, constantes), `Partie` (état et déroulement d'une partie), `Sauvegarde`, `JoueurArtificiel` (IA), `HistoriqueParties`, et les classes de base `Plateau`, `Joueur`, `Pioche`, `Rack`, `Tuile`, `Arbitre`.
- `latice.controleur` — contrôleurs JavaFX (un par écran FXML) + utilitaires transverses (`Musique`, `EffetsSonores`, `ThemeVisuel`, `Navigation`).
- `src/main/resources/ihm/*.fxml` — mise en page de chaque écran, stylée via `src/main/resources/css/theme.css`.
- Les préférences utilisateur (dernier pseudo/couleur, musique, volume, fond d'écran, effets sonores) sont persistées via `java.util.prefs.Preferences` ; l'historique des parties est stocké dans une base SQLite locale (`~/.latice/historique.db`) et la partie en cours dans `~/.latice/partie_en_cours.properties`. Ce dossier peut être redirigé avec `-Dlatice.donnees=...` (les tests l'utilisent pour ne jamais toucher aux données réelles).

## Historique des versions

Voir [`CHANGELOG.md`](CHANGELOG.md).

## Licence

[MIT](LICENSE) — voir le fichier pour la note sur l'origine du projet (travail de groupe repris individuellement).

## Crédits & licences des assets ajoutés

- Images de fond : [Pexels](https://www.pexels.com) (licence Pexels — libres d'usage personnel et commercial).
- Effets sonores : [Mixkit](https://mixkit.co) (licence Mixkit — libres d'usage, sans attribution obligatoire).

Le détail (titre, auteur, lien) est aussi visible dans le jeu via **Menu principal → Crédits**.
