# Latice

Jeu de plateau (type Qwirkle) en JavaFX, développé initialement en SAE 1ère année de BUT Informatique.

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
