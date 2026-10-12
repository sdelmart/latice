package latice.metier;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import latice.enumeration.Couleur;
import latice.enumeration.Symbole;
import latice.metier.JoueurArtificiel.Coup;

class JoueurArtificielTest {

    private final Plateau plateau = new Plateau(Regles.TAILLE_PLATEAU);

    @Test
    void aucunCoupSiRackVide() {
        Optional<Coup> coup = JoueurArtificiel.choisirCoup(plateau, List.of(), Map.of());
        assertTrue(coup.isEmpty());
    }

    @Test
    void premierCoupObligatoirementAuCentre() {
        List<Tuile> rack = List.of(new Tuile(Couleur.ROUGE, Symbole.FLEUR));

        Optional<Coup> coup = JoueurArtificiel.choisirCoup(plateau, rack, new HashMap<>());

        assertTrue(coup.isPresent());
        assertEquals(Regles.centre(plateau), coup.get().position());
    }

    @Test
    void refuseUnCoupSansTuileCompatible() {
        Map<Position, Tuile> tuilesPlacees = new HashMap<>();
        tuilesPlacees.put(new Position(4, 4), new Tuile(Couleur.ROUGE, Symbole.FLEUR));
        List<Tuile> rack = List.of(new Tuile(Couleur.BLEU, Symbole.OISEAU));

        assertTrue(JoueurArtificiel.choisirCoup(plateau, rack, tuilesPlacees).isEmpty());
    }

    @Test
    void choisitUneCaseAdjacenteCompatible() {
        Map<Position, Tuile> tuilesPlacees = new HashMap<>();
        tuilesPlacees.put(new Position(4, 4), new Tuile(Couleur.ROUGE, Symbole.FLEUR));
        List<Tuile> rack = List.of(new Tuile(Couleur.ROUGE, Symbole.OISEAU));

        Optional<Coup> coup = JoueurArtificiel.choisirCoup(plateau, rack, tuilesPlacees);

        assertTrue(coup.isPresent());
        Position pos = coup.get().position();
        int distance = Math.abs(pos.posX() - 4) + Math.abs(pos.posY() - 4);
        assertEquals(1, distance, "Le coup doit être adjacent à la tuile déjà posée");
    }

    @Test
    void neJoueJamaisUnCoupIncompatibleAvecUneDesVoisines() {
        Map<Position, Tuile> tuilesPlacees = new HashMap<>();
        tuilesPlacees.put(new Position(3, 4), new Tuile(Couleur.ROUGE, Symbole.FLEUR));
        tuilesPlacees.put(new Position(5, 4), new Tuile(Couleur.BLEU, Symbole.OISEAU));
        // Une rouge/tortue est compatible avec la tuile de gauche (rouge) mais pas avec celle de droite :
        // elle ne doit pas être jouée entre les deux, en (4,4).
        List<Tuile> rack = List.of(new Tuile(Couleur.ROUGE, Symbole.TORTUE));

        Optional<Coup> coup = JoueurArtificiel.choisirCoup(plateau, rack, tuilesPlacees);

        coup.ifPresent(c -> assertNotEquals(new Position(4, 4), c.position()));
    }

    @Test
    void prefereLeCoupQuiRapporteLeMieux() {
        Map<Position, Tuile> tuilesPlacees = new HashMap<>();
        // (3,5) et (5,5) rouges : poser une rouge en (4,5) touche deux tuiles (1 point).
        tuilesPlacees.put(new Position(3, 5), new Tuile(Couleur.ROUGE, Symbole.FLEUR));
        tuilesPlacees.put(new Position(5, 5), new Tuile(Couleur.ROUGE, Symbole.FLEUR));
        List<Tuile> rack = List.of(new Tuile(Couleur.ROUGE, Symbole.TORTUE));

        Optional<Coup> coup = JoueurArtificiel.choisirCoup(plateau, rack, tuilesPlacees);

        assertTrue(coup.isPresent());
        assertEquals(1, coup.get().score());
        assertEquals(new Position(4, 5), coup.get().position());
    }
}
