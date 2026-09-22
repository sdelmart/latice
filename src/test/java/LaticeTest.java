
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import latice.enumeration.Couleur;
import latice.enumeration.Symbole;
import latice.metier.Arbitre;
import latice.metier.Joueur;
import latice.metier.Pioche;
import latice.metier.Plateau;
import latice.metier.Position;
import latice.metier.Rack;
import latice.metier.Tuile;

public class LaticeTest {

		//Tests de la classe Pioche
		@Test
		public void testPiocheVideApresPiochage() {
	        Pioche pioche = new Pioche();
	        int compteur = 0;
	        while (pioche.piocher() != null) {
	        	compteur++;
	        }
	        assertTrue(pioche.estVide());
	        assertTrue(compteur > 0);
	    }
		
		@Test
		public void testPremiereTuileEtPiocheNonVide() {
	        Pioche pioche = new Pioche();
	        Tuile t = pioche.piocher();
	        assertNotNull(t);
	        assertFalse(pioche.estVide());
	    }
		
		
		//Tests de la classe Plateau
		@Test
		public void testInitialisationPlateau() {
	        Plateau plateau = new Plateau(9);
	        assertEquals(9, plateau.tailleplateau());
	        assertEquals(Plateau.TypeCase.SOLEIL, plateau.casePlateau(0, 0));
	        assertEquals(Plateau.TypeCase.LUNE, plateau.casePlateau(4, 4));
	        assertEquals(Plateau.TypeCase.NORMALE, plateau.casePlateau(3, 3));
	    }

		@Test
		public void testPlacerEtObtenirTuile() {
	        Plateau plateau = new Plateau(9);
	        Position pos = new Position(2, 3);
	        Tuile tuile = new Tuile(Couleur.MAGENTA, Symbole.DAUPHIN);
	        assertTrue(plateau.placerTuile(pos, tuile));
	        assertEquals(tuile, plateau.obtenirTuile(pos));
	        assertFalse(plateau.placerTuile(pos, new Tuile(Couleur.MAGENTA, Symbole.DAUPHIN)));
	    }
		 
		@Test
	    public void testObtenirTuilesPlacees() {
	        Plateau plateau = new Plateau(9);
	        Position pos1 = new Position(1, 1);
	        Position pos2 = new Position(2, 2);
	        Tuile tuile1 = new Tuile(null, null);
	        Tuile tuile2 = new Tuile(null, null);
	        plateau.placerTuile(pos1, tuile1);
	        plateau.placerTuile(pos2, tuile2);
	        Map<Position, Tuile> tuiles = plateau.obtenirTuilesPlacees();
	        assertEquals(2, tuiles.size());
	        assertEquals(tuile1, tuiles.get(pos1));
	        assertEquals(tuile2, tuiles.get(pos2));
	    }
		
		@Test
	    public void testAfficherPlateau() {
	        Plateau plateau = new Plateau(9);
	        plateau.afficher();
	    }
		
		
		//Tests de la classe Arbitre
		@Test
		public void testConstructeurEtInitialisation() {
	        Joueur j1 = new Joueur("Alice");
	        Joueur j2 = new Joueur("Bob");
	        Arbitre arbitre = new Arbitre(j1, j2);

	        assertNotNull(arbitre.joueur1);
	        assertNotNull(arbitre.joueur2);
	        assertNotNull(arbitre.plateau);
	        assertNotNull(j1.pioche());
	        assertNotNull(j2.pioche());
	        assertNotNull(j1.rack());
	        assertNotNull(j2.rack());
	        assertTrue(j1.rack().taille() > 0 || j2.rack().taille() > 0);
	    }
		
		@Test
		public void testConstructeurAvecNoms() {
	        Arbitre arbitre = new Arbitre("Alice", "Bob");
	        assertEquals("Alice", arbitre.joueur1.nom());
	        assertEquals("Bob", arbitre.joueur2.nom());
	    }
		
		@Test
		public void testJoueurCourantEtChangementTour() {
	        Arbitre arbitre = new Arbitre("Alice", "Bob");
	        Joueur courant = arbitre.joueurCourant();
	        assertTrue(courant == arbitre.joueur1 || courant == arbitre.joueur2);

	        arbitre.gererTours();
	        Joueur nouveauCourant = arbitre.joueurCourant();
	        assertNotNull(nouveauCourant);
	        assertNotEquals(courant, nouveauCourant);
	    }
		
		@Test
		public void testDistributionTuilesAuxRacks() {
	        Arbitre arbitre = new Arbitre("Alice", "Bob");
	        assertTrue(arbitre.joueur1.rack().taille() > 0);
	        assertTrue(arbitre.joueur2.rack().taille() > 0);
	        assertTrue(arbitre.joueur1.pioche().taille() >= 0);
	        assertTrue(arbitre.joueur2.pioche().taille() >= 0);
	    }
		
		//Tests de la classe Joueur
		@Test
		public void testNomEtConstructeur() {
	        Joueur joueur = new Joueur("Alice");
	        assertEquals("Alice", joueur.nom());
	        assertNotNull(joueur.rack());
	        assertNull(joueur.pioche());
	        assertEquals(0, (int) joueur.points());
	        assertEquals(1, joueur.nbActions);
	        assertEquals(0, joueur.nbActionsSup);
	    }
		
		@Test
		public void testFairePiocheEtPioche() {
	        Joueur joueur = new Joueur("Bob");
	        Pioche pioche = new Pioche();
	        joueur.fairePioche(pioche);
	        assertEquals(pioche, joueur.pioche());
	    }
		
		@Test
		public void testPointsEtAjouterPoints() {
	        Joueur joueur = new Joueur("Eve");
	        assertEquals(0, (int) joueur.points());
	        joueur.ajouterPoints(5);
	        assertEquals(5, (int) joueur.points());
	        joueur.ajouterPoints(-2);
	        assertEquals(3, (int) joueur.points());
	    }
		
		@Test
		public void testRackFonctionnalites() {
	        Joueur joueur = new Joueur("Dan");
	        assertNotNull(joueur.rack());
	        assertTrue(joueur.rack().obtenirTuilesRack().isEmpty());
	    }
		
		//Tests de la classe Rack
		@Test
	    public void testAjouterTuileEtEstPlein() {
	        Rack rack = new Rack();
	        for (int i = 0; i < 5; i++) {
	            assertTrue(rack.ajouterTuile(new Tuile(Couleur.BLEU, Symbole.FLEUR)));
	        }
	        assertTrue(rack.estPlein());
	        assertFalse(rack.ajouterTuile(new Tuile(Couleur.BLEU, Symbole.FLEUR)));
	    }
		
		@Test
	    public void testObtenirTuilesRack() {
	        Rack rack = new Rack();
	        Tuile t1 = new Tuile(Couleur.BLEU, Symbole.FLEUR);
	        Tuile t2 = new Tuile(Couleur.ROUGE, Symbole.OISEAU);
	        rack.ajouterTuile(t1);
	        rack.ajouterTuile(t2);
	        List<Tuile> tuiles = rack.obtenirTuilesRack();
	        assertEquals(2, tuiles.size());
	        assertTrue(tuiles.contains(t1));
	        assertTrue(tuiles.contains(t2));
	    }
		
		@Test
	    public void testVider() {
	        Rack rack = new Rack();
	        rack.ajouterTuile(new Tuile(Couleur.BLEU, Symbole.FLEUR));
	        rack.ajouterTuile(new Tuile(Couleur.ROUGE, Symbole.OISEAU));
	        rack.vider();
	        assertTrue(rack.obtenirTuilesRack().isEmpty());
	        assertEquals(0, rack.taille());
	    }
		
		@Test
	    public void testRetirerTuile() {
	        Rack rack = new Rack();
	        Tuile t1 = new Tuile(Couleur.BLEU, Symbole.FLEUR);
	        Tuile t2 = new Tuile(Couleur.ROUGE, Symbole.OISEAU);
	        rack.ajouterTuile(t1);
	        rack.ajouterTuile(t2);
	        assertTrue(rack.retirerTuile(t1));
	        assertFalse(rack.obtenirTuilesRack().contains(t1));
	        assertEquals(1, rack.taille());
	        // Try to remove a tile not present
	        Tuile t3 = new Tuile(Couleur.JAUNE, Symbole.TORTUE);
	        assertFalse(rack.retirerTuile(t3));
	    }
		
		@Test
	    public void testTaille() {
	        Rack rack = new Rack();
	        assertEquals(0, rack.taille());
	        rack.ajouterTuile(new Tuile(Couleur.BLEU, Symbole.FLEUR));
	        assertEquals(1, rack.taille());
	    }
		
		//Tests de la classe Position
		@Test
	    public void testConstructorAndGetters() {
	        Position pos = new Position(3, 5);
	        assertEquals(3, pos.posX());
	        assertEquals(5, pos.posY());
	    }
		
		@Test
	    public void testSetters() {
	        Position pos = new Position(0, 0);
	        pos.setX(7);
	        pos.setY(2);
	        assertEquals(7, pos.posX());
	        assertEquals(2, pos.posY());
	    }
		
		@Test
	    public void testEqualsAndHashCode() {
	        Position pos1 = new Position(1, 2);
	        Position pos2 = new Position(1, 2);
	        Position pos3 = new Position(2, 1);

	        assertEquals(pos1, pos2);
	        assertEquals(pos1.hashCode(), pos2.hashCode());
	        assertNotEquals(pos1, pos3);
	        assertNotEquals(pos1.hashCode(), pos3.hashCode());
	        assertNotEquals(pos1, null);
	        assertNotEquals(pos1, "not a position");
	    }
		
		//Tests de la classe Tuile
		@Test
	    public void testConstructorAndGettersTuile() {
	        Tuile tuile = new Tuile(Couleur.BLEU, Symbole.FLEUR);
	        assertEquals(Couleur.BLEU, tuile.obtenirCouleur());
	        assertEquals(Symbole.FLEUR, tuile.obtenirSymbole());
	    }
		
		@Test
	    public void testToString() {
	        Tuile tuile = new Tuile(Couleur.ROUGE, Symbole.OISEAU);
	        assertEquals("OISEAU ROUGE", tuile.toString());
	    }
		
		@Test
	    public void testCheminImage() {
	        Tuile tuile = new Tuile(Couleur.JAUNE, Symbole.TORTUE);
	        String chemin = tuile.cheminImage();
	        assertTrue(chemin.contains("tortue_jaune.png"));
	        assertTrue(chemin.startsWith("/images/img_Latice/img/"));
	    }
		
		@Test
	    public void testConvertirEmoji() {
	        Tuile tuileFleur = new Tuile(Couleur.VERT, Symbole.FLEUR);
	        String emojiFleur = tuileFleur.convertirEmoji();
	        assertTrue(emojiFleur.contains("\u2740")); // FLEUR symbol

	        Tuile tuileOiseau = new Tuile(Couleur.BLEU, Symbole.OISEAU);
	        String emojiOiseau = tuileOiseau.convertirEmoji();
	        assertTrue(emojiOiseau.contains("\uD83D\uDC26")); // OISEAU symbol

	        Tuile tuileTortue = new Tuile(Couleur.JAUNE, Symbole.TORTUE);
	        assertTrue(tuileTortue.convertirEmoji().length() > 0);
	        
	        Tuile tuileDauphin = new Tuile(Couleur.CYAN, Symbole.DAUPHIN);
	        String emojiDauphin = tuileDauphin.convertirEmoji();
	        assertTrue(emojiDauphin.contains("\uD83D\uDC2C"));

	        Tuile tuilePlume = new Tuile(Couleur.MAGENTA, Symbole.PLUME);
	        String emojiPlume = tuilePlume.convertirEmoji();
	        assertTrue(emojiPlume.contains("\uD83E\uDEB6"));
	        
	        Tuile tuileLezard = new Tuile(Couleur.ROUGE, Symbole.LEZARD);
	        String emojiLezard = tuileLezard.convertirEmoji();
	        assertTrue(emojiLezard.contains("\uD80C\uDD88"));
	    }
		
}
