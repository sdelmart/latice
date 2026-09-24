# Changelog

Toutes les évolutions notables du projet, de la reprise après la SAE de groupe jusqu'à aujourd'hui.

## En cours

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
