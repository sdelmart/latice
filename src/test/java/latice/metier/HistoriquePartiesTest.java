package latice.metier;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import latice.metier.HistoriqueParties.Partie;

/**
 * Utilise une base SQLite temporaire (jamais ~/.latice/historique.db) pour tester
 * la vraie logique d'enregistrement/lecture sans polluer le dossier de l'utilisateur.
 */
class HistoriquePartiesTest {

    private Path fichierDb;

    @BeforeEach
    void creerBaseTemporaire() throws IOException {
        fichierDb = Files.createTempFile("latice-historique-test", ".db");
        Files.deleteIfExists(fichierDb); // laisser HistoriqueParties créer le fichier lui-même
    }

    @AfterEach
    void supprimerBaseTemporaire() throws IOException {
        Files.deleteIfExists(fichierDb);
    }

    @Test
    void aucuneEntreeSiLaBaseNexistePasEncore() {
        assertTrue(HistoriqueParties.chargerHistorique(fichierDb).isEmpty());
    }

    @Test
    void unePartieEnregistreeSeRetrouveALaLecture() {
        HistoriqueParties.enregistrer(fichierDb, "Alice", 12, "Bob", 9, "Alice");

        List<Partie> historique = HistoriqueParties.chargerHistorique(fichierDb);

        assertEquals(1, historique.size());
        Partie partie = historique.get(0);
        assertEquals("Alice", partie.joueur1());
        assertEquals(12, partie.score1());
        assertEquals("Bob", partie.joueur2());
        assertEquals(9, partie.score2());
        assertEquals("Alice", partie.vainqueur());
    }

    @Test
    void lesPartiesLesPlusRecentesArriventEnPremier() {
        HistoriqueParties.enregistrer(fichierDb, "Alice", 5, "Bob", 3, "Alice");
        HistoriqueParties.enregistrer(fichierDb, "Camille", 8, "David", 2, "Camille");

        List<Partie> historique = HistoriqueParties.chargerHistorique(fichierDb);

        assertEquals(2, historique.size());
        assertEquals("Camille", historique.get(0).joueur1(), "La partie la plus récente doit arriver en premier");
        assertEquals("Alice", historique.get(1).joueur1());
    }

    @Test
    void resumeContientLesNomsEtLesScores() {
        HistoriqueParties.enregistrer(fichierDb, "Alice", 12, "Bob", 9, "Alice");
        String resume = HistoriqueParties.chargerHistorique(fichierDb).get(0).resume();

        assertTrue(resume.contains("Alice"));
        assertTrue(resume.contains("Bob"));
        assertTrue(resume.contains("12"));
        assertTrue(resume.contains("9"));
    }
}
