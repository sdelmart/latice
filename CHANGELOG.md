# Changelog

Toutes les évolutions notables du projet, de la reprise après la SAE de groupe jusqu'à aujourd'hui.

## 1.0.0

### Règles (corrigées et complétées)
- **Règle de placement corrigée** : une tuile doit être compatible (même couleur ou même symbole) avec *toutes* les tuiles qu'elle touche, et non avec une seule. Le défaut était identique dans les trois copies de la règle (interface, IA, version console) ; elles appellent désormais toutes `latice.metier.Regles`.
- **Fin de partie par épuisement** : la règle affichée dans le jeu (« un joueur qui vide son rack et sa pioche gagne ») n'était pas implémentée dans l'interface graphique.
- Position de dépôt bornée au plateau (plus de risque d'indice hors limites sur un dépôt en bordure).

### Fonctionnalités
- **Sauvegarde automatique et reprise de partie** : sauvegarde à chaque tour, bouton *Continuer* dans le menu, confirmation avant de remplacer une partie sauvegardée.

### Architecture et qualité
- Nouvelle classe métier `Partie` (état et déroulement d'une partie, sans JavaFX) ; `ControleurDeJeu` devient une simple couche d'affichage. Suppression des doubles copies de l'état du plateau.
- 66 tests (contre 36) : règles, parties IA contre IA complètes avec vérification des invariants du plateau, sauvegarde/reprise, présence des ressources, tests d'interface de l'écran de jeu.
- Les tests n'écrivent plus jamais dans `~/.latice` (dossier de données redirigeable).
- Option `--verifier-ressources` : vérifie qu'un build contient toutes ses ressources, SQLite et bibliothèques natives JavaFX.

### Distribution
- **Releases GitHub** : archives `.tar.gz` pour Linux x64 et ARM64, macOS Intel et Apple Silicon et Windows x64 (+ `.zip` Windows, `.dmg`, `.deb`, `.msi` quand l'outillage de la machine de build le permet), avec l'environnement Java embarqué et `SHA256SUMS.txt`, produites par `.github/workflows/release.yml` à chaque tag `vX.Y.Z`.

### Décision
- **Jeu à 3-4 joueurs : non retenu.** Latice est un jeu à deux joueurs (c'est aussi ce qu'indiquent les règles affichées dans le jeu) ; un mode à 3-4 joueurs serait une variante de règles inventée, pas une fonctionnalité du jeu.

## Avant la 1.0.0

- Tests d'interface réels (TestFX) sur la navigation de l'accueil, exclus du run par défaut (nécessitent un affichage) mais documentés et exécutables localement.
- **Bug corrigé** : les images de tuiles avaient des noms de fichiers en majuscules alors que le code les cherchait en minuscules — invisible via `mvn javafx:run` (système de fichiers macOS insensible à la casse) mais faisait planter toute partie dès qu'on lisait les ressources depuis un vrai `.jar` (sensible à la casse partout). Trouvé en testant réellement le jar packagé avec `jpackage`.
- Journalisation SLF4J/Logback à la place des `System.out.println`/`printStackTrace`.
- Historique des parties migré du CSV vers une base SQLite embarquée, testée bout en bout (base temporaire en test).
- Couverture de tests mesurée (JaCoCo).
- Jar exécutable autonome (`mvn package` → `java -jar target/latice-*.jar`).
- Analyse statique (SpotBugs) en CI, non bloquante.
- Licence MIT + note d'origine (projet de groupe repris individuellement).
- Documentation d'architecture avec diagrammes (`docs/ARCHITECTURE.md`).

## v0.4 — Sérieux du rendu

- 9 tests supplémentaires (IA, format de l'historique, couleurs) — 35 tests au total.
- README étoffé (fonctionnalités, architecture, crédits).
- Passe de nettoyage (imports inutiles, chemins cassés) sans rien trouver.

## v0.3 — Mode solo, aide au placement, fin de partie

- Mode solo contre une IA gloutonne (meilleur coup légal sur le rack).
- Aide au placement : cases légales en surbrillance pendant le glisser-déposer.
- Écran de fin de partie repensé (scores détaillés, Rejouer/Menu principal) — corrige un bug où fermer la popup de résultat quittait toute l'application.
- 3 effets sonores supplémentaires (victoire, erreur, changement de tour).

## v0.2 — Vrai menu principal et fonctionnalités

- Accueil transformé en menu façon jeu vidéo (panneaux en fondu : Jouer, Profils, Règles, Paramètres, Crédits).
- Profils : pseudo, couleur par joueur, historique des parties.
- Fonds d'écran sélectionnables (dégradés + photos), musique et effets sonores personnalisables, tout persistant.

## v0.1 — Remise en état et refonte visuelle

- Build Maven réparé (dépendances JavaFX manquantes, Java 8 → 17), Maven Wrapper pour un lancement multiplateforme sans installation.
- Thème visuel cohérent sur tous les écrans (au lieu de styles disparates copiés-collés).
- Correction de bugs latents : chemins de ressources sensibles à la casse (cassaient sous Linux), CSS JavaFX invalide empêchant l'assombrissement du fond.
