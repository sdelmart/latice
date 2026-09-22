package latice.metier;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import latice.metier.HistoriqueParties.Partie;

/**
 * Teste uniquement le format ligne <-> objet (pur, sans toucher au disque) :
 * enregistrer()/chargerHistorique() lisent/écrivent dans le dossier utilisateur réel
 * et ne sont volontairement pas testés ici pour ne pas polluer ~/.latice pendant les tests.
 */
class HistoriquePartiesTest {

    @Test
    void uneLigneSeRelitIdentique() {
        Partie originale = new Partie(
                LocalDateTime.of(2026, 9, 22, 18, 30),
                "Alice", 12, "Bob", 9, "Alice");

        Partie relue = Partie.depuisLigne(originale.versLigne());

        assertNotNull(relue);
        assertEquals(originale.date(), relue.date());
        assertEquals(originale.joueur1(), relue.joueur1());
        assertEquals(originale.score1(), relue.score1());
        assertEquals(originale.joueur2(), relue.joueur2());
        assertEquals(originale.score2(), relue.score2());
        assertEquals(originale.vainqueur(), relue.vainqueur());
    }

    @Test
    void ligneMalFormeeRenvoieNull() {
        assertNull(Partie.depuisLigne("n'importe quoi;pas le bon format"));
    }

    @Test
    void resumeContientLesNomsEtLeVainqueur() {
        Partie partie = new Partie(LocalDateTime.of(2026, 9, 22, 18, 30), "Alice", 12, "Bob", 9, "Alice");
        String resume = partie.resume();
        assertTrue(resume.contains("Alice"));
        assertTrue(resume.contains("Bob"));
        assertTrue(resume.contains("12"));
        assertTrue(resume.contains("9"));
    }
}
