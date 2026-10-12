package main;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Garde-fou contre une ressource manquante ou mal orthographiée (images de tuiles, sons, FXML...).
 * Sur la CI Linux (système de fichiers sensible à la casse) il aurait attrapé le bug des images en
 * majuscules qui ne plantait que dans un jar.
 */
class VerificationRessourcesTest {

    @Test
    void toutesLesRessourcesDuJeuSontPresentes() {
        List<String> problemes = VerificationRessources.problemesRessources();
        assertTrue(problemes.isEmpty(), () -> String.join("\n", problemes));
    }
}
