package latice.metier;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import latice.enumeration.Couleur;

class SauvegardeTest {

    private Path fichier;

    @BeforeEach
    void preparer() throws IOException {
        fichier = Files.createTempFile("latice-sauvegarde-test", ".properties");
        Files.deleteIfExists(fichier);
    }

    @AfterEach
    void nettoyer() throws IOException {
        Files.deleteIfExists(fichier);
        Files.deleteIfExists(fichier.resolveSibling(fichier.getFileName() + ".tmp"));
    }

    /** Joue quelques tours avec l'IA pour obtenir une partie en cours réaliste (plateau, scores, pioches entamées). */
    private static Partie partieEnCours() {
        Partie partie = new Partie("Alice", "Bob é\"ç;=");
        for (int tour = 0; tour < 7; tour++) {
            Joueur courant = partie.joueurCourant();
            JoueurArtificiel.choisirCoup(partie.plateau(), courant.rack().obtenirTuilesRack(), partie.tuilesPlacees())
                    .ifPresent(c -> partie.placer(c.position(), c.tuile()));
            partie.finirTour();
        }
        partie.joueur1().nbActionsSup = 2;
        return partie;
    }

    private static void assertMemeTuile(Tuile attendue, Tuile obtenue) {
        assertEquals(attendue.obtenirCouleur(), obtenue.obtenirCouleur());
        assertEquals(attendue.obtenirSymbole(), obtenue.obtenirSymbole());
    }

    private static void assertMemesTuiles(List<Tuile> attendues, List<Tuile> obtenues) {
        assertEquals(attendues.size(), obtenues.size());
        for (int i = 0; i < attendues.size(); i++) {
            assertMemeTuile(attendues.get(i), obtenues.get(i));
        }
    }

    @Test
    void laSauvegardeSeRelitIdentique() {
        Partie partie = partieEnCours();
        Sauvegarde originale = partie.instantane(true, Couleur.CYAN, Couleur.MAGENTA);

        Sauvegarde.enregistrer(fichier, originale);
        Optional<Sauvegarde> relue = Sauvegarde.charger(fichier);

        assertTrue(relue.isPresent());
        assertEquals(originale.solo(), relue.get().solo());
        assertEquals(originale.joueurCourant(), relue.get().joueurCourant());
        assertEquals(originale.plateau().size(), relue.get().plateau().size());
        for (int i = 0; i < 2; i++) {
            Sauvegarde.EtatJoueur a = originale.joueurs().get(i);
            Sauvegarde.EtatJoueur b = relue.get().joueurs().get(i);
            assertEquals(a.nom(), b.nom());
            assertEquals(a.couleur(), b.couleur());
            assertEquals(a.points(), b.points());
            assertEquals(a.actionsSup(), b.actionsSup());
            assertEquals(a.toursRestants(), b.toursRestants());
            assertEquals(a.tuilesPosees(), b.tuilesPosees());
            assertEquals(a.indexPioche(), b.indexPioche());
            assertMemesTuiles(a.rack(), b.rack());
            assertMemesTuiles(a.pioche(), b.pioche());
        }
    }

    @Test
    void restaurerReconstruitLaMemePartieEtElleSePoursuit() {
        Partie avant = partieEnCours();
        Sauvegarde.enregistrer(fichier, avant.instantane(false, Couleur.BLEU, Couleur.ROUGE));

        Partie apres = Partie.restaurer(Sauvegarde.charger(fichier).orElseThrow());

        assertEquals(avant.joueurCourant().nom(), apres.joueurCourant().nom());
        assertEquals(avant.tuilesPlacees().size(), apres.tuilesPlacees().size());
        avant.tuilesPlacees().forEach((pos, tuile) -> assertMemeTuile(tuile, apres.tuilesPlacees().get(pos)));
        Joueur[] anciens = { avant.joueur1(), avant.joueur2() };
        Joueur[] nouveaux = { apres.joueur1(), apres.joueur2() };
        for (int i = 0; i < 2; i++) {
            assertEquals(anciens[i].nom(), nouveaux[i].nom());
            assertEquals(anciens[i].points(), nouveaux[i].points());
            assertEquals(anciens[i].nbActionsSup, nouveaux[i].nbActionsSup);
            assertEquals(avant.toursRestants(anciens[i]), apres.toursRestants(nouveaux[i]));
            assertEquals(avant.tuilesPosees(anciens[i]), apres.tuilesPosees(nouveaux[i]));
            assertMemesTuiles(anciens[i].rack().obtenirTuilesRack(), nouveaux[i].rack().obtenirTuilesRack());
            assertEquals(anciens[i].pioche().indexCourant(), nouveaux[i].pioche().indexCourant());
        }

        // Les deux parties, jouées de la même façon, évoluent à l'identique (pioches restaurées à l'identique).
        for (int tour = 0; tour < 6 && !avant.estTerminee(); tour++) {
            Partie[] parties = { avant, apres };
            int[] points = new int[2];
            for (int p = 0; p < 2; p++) {
                Joueur courant = parties[p].joueurCourant();
                Optional<JoueurArtificiel.Coup> coup = JoueurArtificiel.choisirCoup(parties[p].plateau(),
                        courant.rack().obtenirTuilesRack(), parties[p].tuilesPlacees());
                if (coup.isPresent()) {
                    points[p] = parties[p].placer(coup.get().position(), coup.get().tuile());
                }
                parties[p].finirTour();
            }
            assertEquals(points[0], points[1]);
        }
        assertEquals(avant.tuilesPlacees().size(), apres.tuilesPlacees().size());
    }

    @Test
    void unePartieSansTuilePoseeEtAvecUnePiocheVideSeSauvegarde() {
        Partie partie = new Partie("A", "B");
        partie.joueur1().rack().vider();
        partie.joueur1().fairePioche(new Pioche(List.of()));

        Sauvegarde.enregistrer(fichier, partie.instantane(false, Couleur.VERT, Couleur.JAUNE));
        Partie restauree = Partie.restaurer(Sauvegarde.charger(fichier).orElseThrow());

        assertTrue(restauree.tuilesPlacees().isEmpty());
        assertEquals(0, restauree.joueur1().rack().taille());
        assertTrue(restauree.joueur1().pioche().estVide());
    }

    @Test
    void fichierAbsentCorrompuOuDUneAutreVersionEstIgnore() throws IOException {
        assertTrue(Sauvegarde.charger(fichier).isEmpty(), "fichier absent");

        Files.writeString(fichier, "n'importe quoi\nce n'est pas une sauvegarde", StandardCharsets.UTF_8);
        assertTrue(Sauvegarde.charger(fichier).isEmpty(), "contenu invalide");

        Sauvegarde.enregistrer(fichier, new Partie("A", "B").instantane(false, Couleur.BLEU, Couleur.ROUGE));
        String contenu = Files.readString(fichier, StandardCharsets.UTF_8).replace("version=1", "version=99");
        Files.writeString(fichier, contenu, StandardCharsets.UTF_8);
        assertTrue(Sauvegarde.charger(fichier).isEmpty(), "version non supportée");
    }

    @Test
    void uneTuileInconnueDansLeFichierRendLaSauvegardeInvalide() throws IOException {
        Sauvegarde.enregistrer(fichier, new Partie("A", "B").instantane(false, Couleur.BLEU, Couleur.ROUGE));
        String contenu = Files.readString(fichier, StandardCharsets.UTF_8).replaceFirst("joueur.0.rack=[^\\n]*", "joueur.0.rack=ORANGE/LICORNE");
        Files.writeString(fichier, contenu, StandardCharsets.UTF_8);

        assertTrue(Sauvegarde.charger(fichier).isEmpty());
    }

    @Test
    void supprimerEffaceLeFichier() {
        Sauvegarde.enregistrer(fichier, new Partie("A", "B").instantane(false, Couleur.BLEU, Couleur.ROUGE));
        assertTrue(Files.exists(fichier));

        Sauvegarde.supprimer(fichier);

        assertFalse(Files.exists(fichier));
    }
}
