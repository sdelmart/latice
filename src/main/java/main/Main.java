package main;

import javafx.application.Application;

/**
 * Point d'entrée du jar autonome (`java -jar latice.jar`).
 * Nécessaire car une classe qui étend directement javafx.application.Application ne peut pas
 * être lancée via `java -jar` sans module-path JavaFX explicite : passer par une classe de
 * lancement séparée qui ne l'étend pas contourne cette vérification.
 */
public class Main {
    public static void main(String[] args) {
        Application.launch(LancementLatice.class, args);
    }
}
