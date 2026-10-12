package latice.metier;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import latice.enumeration.Couleur;
import latice.enumeration.Symbole;

class ReglesTest {

    private final Plateau plateau = new Plateau(Regles.TAILLE_PLATEAU);

    private static Tuile t(Couleur couleur, Symbole symbole) {
        return new Tuile(couleur, symbole);
    }

    @Test
    void premiereTuileObligatoirementAuCentre() {
        Map<Position, Tuile> vide = new HashMap<>();
        Tuile tuile = t(Couleur.ROUGE, Symbole.FLEUR);

        assertTrue(Regles.estPlacementValide(plateau, vide, new Position(4, 4), tuile));
        assertFalse(Regles.estPlacementValide(plateau, vide, new Position(3, 4), tuile));
        assertFalse(Regles.estPlacementValide(plateau, vide, new Position(0, 0), tuile));
    }

    @Test
    void uneTuileDoitToucherUneTuileDejaPosee() {
        Map<Position, Tuile> posees = new HashMap<>();
        posees.put(new Position(4, 4), t(Couleur.ROUGE, Symbole.FLEUR));
        Tuile tuile = t(Couleur.ROUGE, Symbole.OISEAU);

        assertTrue(Regles.estPlacementValide(plateau, posees, new Position(5, 4), tuile));
        assertFalse(Regles.estPlacementValide(plateau, posees, new Position(6, 4), tuile), "pas de voisine");
        assertFalse(Regles.estPlacementValide(plateau, posees, new Position(5, 5), tuile), "diagonale seulement");
    }

    @Test
    void laTuileDoitEtreCompatibleParCouleurOuParSymbole() {
        Map<Position, Tuile> posees = new HashMap<>();
        posees.put(new Position(4, 4), t(Couleur.ROUGE, Symbole.FLEUR));

        assertTrue(Regles.estPlacementValide(plateau, posees, new Position(5, 4), t(Couleur.ROUGE, Symbole.OISEAU)));
        assertTrue(Regles.estPlacementValide(plateau, posees, new Position(5, 4), t(Couleur.BLEU, Symbole.FLEUR)));
        assertFalse(Regles.estPlacementValide(plateau, posees, new Position(5, 4), t(Couleur.BLEU, Symbole.OISEAU)));
    }

    /** Le défaut corrigé : avant, une seule voisine compatible suffisait à autoriser le coup. */
    @Test
    void laTuileDoitEtreCompatibleAvecToutesSesVoisines() {
        Map<Position, Tuile> posees = new HashMap<>();
        posees.put(new Position(3, 4), t(Couleur.ROUGE, Symbole.FLEUR));
        posees.put(new Position(5, 4), t(Couleur.BLEU, Symbole.OISEAU));
        Position entreLesDeux = new Position(4, 4);

        // rouge/tortue : compatible avec la gauche (rouge) mais ni avec la droite (bleu/oiseau)
        assertFalse(Regles.estPlacementValide(plateau, posees, entreLesDeux, t(Couleur.ROUGE, Symbole.TORTUE)));
        // rouge/oiseau : rouge comme la gauche, oiseau comme la droite
        assertTrue(Regles.estPlacementValide(plateau, posees, entreLesDeux, t(Couleur.ROUGE, Symbole.OISEAU)));
    }

    @Test
    void refuseUneCaseOccupeeOuHorsPlateau() {
        Map<Position, Tuile> posees = new HashMap<>();
        posees.put(new Position(4, 4), t(Couleur.ROUGE, Symbole.FLEUR));
        Tuile tuile = t(Couleur.ROUGE, Symbole.FLEUR);

        assertFalse(Regles.estPlacementValide(plateau, posees, new Position(4, 4), tuile), "case occupée");
        assertFalse(Regles.estPlacementValide(plateau, posees, new Position(9, 4), tuile), "hors plateau");
        assertFalse(Regles.estPlacementValide(plateau, posees, new Position(-1, 4), tuile), "hors plateau");
    }

    @Test
    void pointsSelonLeNombreDeVoisines() {
        Map<Position, Tuile> posees = new HashMap<>();
        Tuile quelconque = t(Couleur.ROUGE, Symbole.FLEUR);
        Position cible = new Position(4, 3); // case normale

        assertEquals(0, Regles.pointsGagnes(plateau, posees, cible));
        posees.put(new Position(3, 3), quelconque);
        assertEquals(0, Regles.pointsGagnes(plateau, posees, cible), "une voisine : pas de point");
        posees.put(new Position(5, 3), quelconque);
        assertEquals(1, Regles.pointsGagnes(plateau, posees, cible), "deux voisines : 1 point");
        posees.put(new Position(4, 2), quelconque);
        assertEquals(2, Regles.pointsGagnes(plateau, posees, cible), "trois voisines : 2 points");
        posees.put(new Position(4, 4), quelconque);
        assertEquals(4, Regles.pointsGagnes(plateau, posees, cible), "quatre voisines : 4 points");
    }

    @Test
    void uneCaseSoleilRapporteDeuxPointsDeBonus() {
        assertEquals(Plateau.TypeCase.SOLEIL, plateau.casePlateau(2, 2));
        assertEquals(2, Regles.pointsGagnes(plateau, new HashMap<>(), new Position(2, 2)));
    }

    @Test
    void joueurAyantVideSonRackEtSaPioche() {
        Joueur joueur = new Joueur("Alice");
        joueur.fairePioche(new Pioche(java.util.List.of()));
        assertTrue(Regles.aVideSonRackEtSaPioche(joueur));

        joueur.rack().ajouterTuile(t(Couleur.ROUGE, Symbole.FLEUR));
        assertFalse(Regles.aVideSonRackEtSaPioche(joueur), "il lui reste une tuile dans le rack");
    }
}
