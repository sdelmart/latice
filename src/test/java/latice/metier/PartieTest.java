package latice.metier;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import latice.enumeration.Couleur;
import latice.enumeration.Symbole;

class PartieTest {

    private static Tuile t(Couleur couleur, Symbole symbole) {
        return new Tuile(couleur, symbole);
    }

    /** Remplace le rack du joueur courant par des tuiles connues, pour des tests déterministes. */
    private static void donnerAuJoueurCourant(Partie partie, Tuile... tuiles) {
        Joueur joueur = partie.joueurCourant();
        joueur.rack().vider();
        for (Tuile tuile : tuiles) {
            joueur.rack().ajouterTuile(tuile);
        }
    }

    @Test
    void nouvellePartieDistribueCinqTuilesEtDixTours() {
        Partie partie = new Partie("Alice", "Bob");

        for (Joueur joueur : List.of(partie.joueur1(), partie.joueur2())) {
            assertEquals(Regles.TAILLE_RACK, joueur.rack().taille());
            assertEquals(36, joueur.pioche().avoirTuiles().size());
            assertEquals(Regles.TAILLE_RACK, joueur.pioche().indexCourant());
            assertEquals(Regles.TOURS_PAR_JOUEUR, partie.toursRestants(joueur));
            assertEquals(0, partie.tuilesPosees(joueur));
        }
        assertTrue(partie.tuilesPlacees().isEmpty());
        assertFalse(partie.estTerminee());
    }

    @Test
    void laPremiereTuileSePoseAuCentreEtPasAilleurs() {
        Partie partie = new Partie("Alice", "Bob");
        Tuile tuile = t(Couleur.ROUGE, Symbole.FLEUR);
        donnerAuJoueurCourant(partie, tuile);

        assertFalse(partie.peutPlacer(new Position(0, 0), tuile));
        assertTrue(partie.peutPlacer(Regles.centre(partie.plateau()), tuile));

        partie.placer(Regles.centre(partie.plateau()), tuile);

        assertEquals(1, partie.tuilesPlacees().size());
        assertEquals(Regles.TAILLE_RACK, partie.joueurCourant().rack().taille(), "le rack est complété depuis la pioche");
    }

    @Test
    void uneSeuleTuileParTourSansActionSupplementaire() {
        Partie partie = new Partie("Alice", "Bob");
        Tuile premiere = t(Couleur.ROUGE, Symbole.FLEUR);
        Tuile seconde = t(Couleur.ROUGE, Symbole.OISEAU);
        donnerAuJoueurCourant(partie, premiere, seconde);

        partie.placer(new Position(4, 4), premiere);

        assertEquals(0, partie.actionsRestantes());
        assertFalse(partie.peutPlacer(new Position(5, 4), seconde));
        assertThrows(IllegalStateException.class, () -> partie.placer(new Position(5, 4), seconde));
    }

    @Test
    void acheterUneActionPermetUneSecondeTuileMaisCoute2Points() {
        Partie partie = new Partie("Alice", "Bob");
        Joueur joueur = partie.joueurCourant();
        joueur.points = 3;
        Tuile premiere = t(Couleur.ROUGE, Symbole.FLEUR);
        Tuile seconde = t(Couleur.ROUGE, Symbole.OISEAU);
        donnerAuJoueurCourant(partie, premiere, seconde);

        assertTrue(partie.acheterActionSupplementaire());
        assertEquals(1, joueur.points());
        assertEquals(2, partie.actionsRestantes());

        partie.placer(new Position(4, 4), premiere);
        partie.placer(new Position(5, 4), seconde);

        assertEquals(0, partie.actionsRestantes());
        assertEquals(0, joueur.nbActionsSup, "l'action supplémentaire est consommée");
        assertEquals(2, partie.tuilesPosees(joueur));
    }

    @Test
    void acheterEstRefuseSansAssezDePointsOuApresAvoirPose() {
        Partie partie = new Partie("Alice", "Bob");
        Joueur joueur = partie.joueurCourant();
        joueur.points = 1;
        assertFalse(partie.acheterActionSupplementaire());

        joueur.points = 10;
        Tuile tuile = t(Couleur.ROUGE, Symbole.FLEUR);
        donnerAuJoueurCourant(partie, tuile);
        partie.placer(new Position(4, 4), tuile);
        assertFalse(partie.acheterActionSupplementaire(), "pas d'achat après avoir posé");
        assertEquals(10, joueur.points());
    }

    @Test
    void poserUneTuileEntreDeuxVoisinesRapporteUnPoint() {
        Partie partie = new Partie("Alice", "Bob");
        // Quatre tours, un par joueur à tour de rôle ; tout est rouge donc toujours compatible.
        jouerUnCoup(partie, t(Couleur.ROUGE, Symbole.FLEUR), new Position(4, 4));
        jouerUnCoup(partie, t(Couleur.ROUGE, Symbole.OISEAU), new Position(5, 4));
        jouerUnCoup(partie, t(Couleur.ROUGE, Symbole.TORTUE), new Position(5, 5));

        // (4,5) touche (4,4) et (5,5) : deux voisines, donc 1 point, sur une case sans bonus.
        Joueur joueur = partie.joueurCourant();
        int pointsAvant = joueur.points();
        int gagnes = jouerUnCoup(partie, t(Couleur.ROUGE, Symbole.DAUPHIN), new Position(4, 5));

        assertEquals(1, gagnes);
        assertEquals(pointsAvant + 1, joueur.points());
    }

    private static int jouerUnCoup(Partie partie, Tuile tuile, Position pos) {
        donnerAuJoueurCourant(partie, tuile);
        int points = partie.placer(pos, tuile);
        partie.finirTour();
        return points;
    }

    @Test
    void finirTourDonneLaMainALAutreEtDecompteUnTour() {
        Partie partie = new Partie("Alice", "Bob");
        Joueur premier = partie.joueurCourant();
        Joueur second = premier == partie.joueur1() ? partie.joueur2() : partie.joueur1();

        partie.finirTour();

        assertSame(second, partie.joueurCourant());
        assertEquals(Regles.TOURS_PAR_JOUEUR - 1, partie.toursRestants(premier));
        assertEquals(Regles.TOURS_PAR_JOUEUR, partie.toursRestants(second));
        assertEquals(0, partie.tuilesPoseesCeTour());
    }

    @Test
    void echangerRemplaceLeRackEtTermineLeTour() {
        Partie partie = new Partie("Alice", "Bob");
        Joueur premier = partie.joueurCourant();
        int taillePiocheAvant = premier.pioche().avoirTuiles().size();

        partie.echanger();

        assertEquals(Regles.TAILLE_RACK, premier.rack().taille());
        assertEquals(taillePiocheAvant + Regles.TAILLE_RACK, premier.pioche().avoirTuiles().size(),
                "les 5 tuiles échangées retournent dans la pioche");
        assertNotSame(premier, partie.joueurCourant(), "l'échange termine le tour");
        assertEquals(Regles.TOURS_PAR_JOUEUR - 1, partie.toursRestants(premier));
    }

    @Test
    void echangerEstInterditApresAvoirPose() {
        Partie partie = new Partie("Alice", "Bob");
        Tuile tuile = t(Couleur.ROUGE, Symbole.FLEUR);
        donnerAuJoueurCourant(partie, tuile);
        partie.placer(new Position(4, 4), tuile);

        assertFalse(partie.peutAcheterOuEchanger());
        assertThrows(IllegalStateException.class, partie::echanger);
    }

    @Test
    void placerUneTuileQuiNEstPasDansLeRackEstUneErreur() {
        Partie partie = new Partie("Alice", "Bob");
        donnerAuJoueurCourant(partie, t(Couleur.ROUGE, Symbole.FLEUR));

        assertThrows(IllegalArgumentException.class,
                () -> partie.placer(new Position(4, 4), t(Couleur.ROUGE, Symbole.FLEUR)));
    }

    @Test
    void lAchevementDesVingtToursTermineLaPartieEtDesigneLeVainqueurAuxTuilesPosees() {
        Partie partie = new Partie("Alice", "Bob");
        // Les deux joueurs passent leurs 10 tours : égalité 0 - 0.
        for (int i = 0; i < 2 * Regles.TOURS_PAR_JOUEUR; i++) {
            assertFalse(partie.estTerminee());
            partie.finirTour();
        }
        assertTrue(partie.estTerminee());
        assertEquals(Optional.empty(), partie.vainqueur());
    }

    @Test
    void celuiQuiPoseLePlusDeTuilesGagneALaFinDesTours() {
        Partie partie = new Partie("Alice", "Bob");
        Joueur poseur = partie.joueurCourant();
        Tuile tuile = t(Couleur.ROUGE, Symbole.FLEUR);
        donnerAuJoueurCourant(partie, tuile);
        partie.placer(new Position(4, 4), tuile);
        for (int i = 0; i < 2 * Regles.TOURS_PAR_JOUEUR; i++) {
            partie.finirTour();
        }

        assertTrue(partie.estTerminee());
        assertEquals(Optional.of(poseur), partie.vainqueur());
    }

    @Test
    void viderSonRackEtSaPiocheTermineImmediatementLaPartieEtFaitGagner() {
        Partie partie = new Partie("Alice", "Bob");
        Joueur gagnant = partie.joueur2();
        gagnant.rack().vider();
        gagnant.fairePioche(new Pioche(List.of()));

        assertTrue(partie.estTerminee());
        assertEquals(Optional.of(gagnant), partie.vainqueur());
        Tuile tuile = t(Couleur.ROUGE, Symbole.FLEUR);
        assertFalse(partie.peutPlacer(Regles.centre(partie.plateau()), tuile), "plus aucun coup après la fin");
    }

    /** Partie complète IA contre IA : aucune exception, et le plateau final respecte les règles. */
    @Test
    void unePartieIaContreIaSeTermineEtRespecteLesRegles() {
        for (int essai = 0; essai < 20; essai++) {
            Partie partie = new Partie("IA 1", "IA 2");
            int garde = 0;
            while (!partie.estTerminee() && garde++ < 100) {
                Joueur courant = partie.joueurCourant();
                Optional<JoueurArtificiel.Coup> coup = JoueurArtificiel.choisirCoup(
                        partie.plateau(), courant.rack().obtenirTuilesRack(), partie.tuilesPlacees());
                coup.ifPresent(c -> partie.placer(c.position(), c.tuile()));
                if (!partie.estTerminee()) {
                    partie.finirTour();
                }
            }
            assertTrue(partie.estTerminee(), "la partie doit se terminer");
            assertEquals(partie.tuilesPosees(partie.joueur1()) + partie.tuilesPosees(partie.joueur2()),
                    partie.tuilesPlacees().size());
            verifierQueToutesLesTuilesAdjacentesSontCompatibles(partie);
        }
    }

    private static void verifierQueToutesLesTuilesAdjacentesSontCompatibles(Partie partie) {
        Map<Position, Tuile> plateau = partie.tuilesPlacees();
        plateau.forEach((pos, tuile) -> {
            for (int[] d : new int[][] { {1, 0}, {0, 1} }) {
                Tuile voisine = plateau.get(new Position(pos.posX() + d[0], pos.posY() + d[1]));
                if (voisine != null) {
                    assertTrue(Regles.sontCompatibles(tuile, voisine),
                            "tuiles adjacentes incompatibles en " + pos.posX() + "," + pos.posY());
                }
            }
        });
    }
}
