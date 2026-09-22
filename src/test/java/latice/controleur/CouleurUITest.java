package latice.controleur;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import latice.enumeration.Couleur;

class CouleurUITest {

    @Test
    void chaqueCouleurDeJeuADistincteTeinte() {
        var teintes = new java.util.HashSet<String>();
        for (Couleur couleur : Couleur.values()) {
            String hex = CouleurUI.hex(couleur);
            assertNotNull(hex);
            assertTrue(hex.matches("#[0-9a-fA-F]{6}"), "Doit être un code hexadécimal valide : " + hex);
            teintes.add(hex);
        }
        assertEquals(Couleur.values().length, teintes.size(), "Chaque couleur doit avoir une teinte distincte");
    }
}
