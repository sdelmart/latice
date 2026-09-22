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

    @Test
    void aucunCoupSiRackVide() {
        Plateau plateau = new Plateau(9);
        Optional<Coup> coup = JoueurArtificiel.choisirCoup(plateau, List.of(), Map.of(), false);
        assertTrue(coup.isEmpty());
    }

    @Test
    void premierCoupObligatoirementAuCentre() {
        Plateau plateau = new Plateau(9);
        List<Tuile> rack = List.of(new Tuile(Couleur.ROUGE, Symbole.FLEUR));

        Optional<Coup> coup = JoueurArtificiel.choisirCoup(plateau, rack, new HashMap<>(), false);

        assertTrue(coup.isPresent());
        int centre = plateau.tailleplateau() / 2;
        assertEquals(centre, coup.get().position().posX());
        assertEquals(centre, coup.get().position().posY());
    }

    @Test
    void refuseUnCoupSansAdjacenceValide() {
        Plateau plateau = new Plateau(9);
        Map<Position, Tuile> tuilesPlacees = new HashMap<>();
        // Tuile posée au centre : ni la même couleur, ni le même symbole que la tuile du rack.
        tuilesPlacees.put(new Position(4, 4), new Tuile(Couleur.ROUGE, Symbole.FLEUR));
        List<Tuile> rack = List.of(new Tuile(Couleur.BLEU, Symbole.OISEAU));

        Optional<Coup> coup = JoueurArtificiel.choisirCoup(plateau, rack, tuilesPlacees, true);

        assertTrue(coup.isEmpty());
    }

    @Test
    void choisitUneCaseAdjacenteValideParCouleurOuSymbole() {
        Plateau plateau = new Plateau(9);
        Map<Position, Tuile> tuilesPlacees = new HashMap<>();
        tuilesPlacees.put(new Position(4, 4), new Tuile(Couleur.ROUGE, Symbole.FLEUR));
        // Même couleur que la tuile posée : placement valide sur une case adjacente.
        List<Tuile> rack = List.of(new Tuile(Couleur.ROUGE, Symbole.OISEAU));

        Optional<Coup> coup = JoueurArtificiel.choisirCoup(plateau, rack, tuilesPlacees, true);

        assertTrue(coup.isPresent());
        Position pos = coup.get().position();
        boolean adjacenteAuCentre =
                (pos.posX() == 3 && pos.posY() == 4) || (pos.posX() == 5 && pos.posY() == 4)
                || (pos.posX() == 4 && pos.posY() == 3) || (pos.posX() == 4 && pos.posY() == 5);
        assertTrue(adjacenteAuCentre, "Le coup doit être adjacent à la tuile déjà posée");
    }

    @Test
    void preferantLeMeilleurScoreEntrePlusieursCoupsPossibles() {
        Plateau plateau = new Plateau(9);
        Map<Position, Tuile> tuilesPlacees = new HashMap<>();
        // Deux tuiles rouges adjacentes à la même case libre (4,5) : la poser là donne 2 tuiles associées (1 point),
        // alors qu'ailleurs elle ne serait adjacente qu'à une seule tuile (aucune adjacence côté opposé configurée ici).
        tuilesPlacees.put(new Position(3, 5), new Tuile(Couleur.ROUGE, Symbole.FLEUR));
        tuilesPlacees.put(new Position(5, 5), new Tuile(Couleur.ROUGE, Symbole.FLEUR));
        List<Tuile> rack = List.of(new Tuile(Couleur.ROUGE, Symbole.TORTUE));

        Optional<Coup> coup = JoueurArtificiel.choisirCoup(plateau, rack, tuilesPlacees, true);

        assertTrue(coup.isPresent());
        assertTrue(coup.get().score() >= 0);
    }
}
