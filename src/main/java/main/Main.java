package main;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.application.Application;

/**
 * Point d'entrée du jar autonome (`java -jar latice.jar`) et de l'application empaquetée.
 * Nécessaire car une classe qui étend directement javafx.application.Application ne peut pas
 * être lancée via `java -jar` sans module-path JavaFX explicite : passer par une classe de
 * lancement séparée qui ne l'étend pas contourne cette vérification.
 *
 * <p>{@code --verifier-ressources [rapport.txt]} ne lance pas le jeu : vérifie que le build contient
 * toutes ses ressources et renvoie un code de sortie (utilisé par la CI sur chaque plateforme).
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && "--verifier-ressources".equals(args[0])) {
            Path rapport = args.length > 1 ? Paths.get(args[1]) : null;
            System.exit(VerificationRessources.executer(rapport));
        }
        Application.launch(LancementLatice.class, args);
    }
}
