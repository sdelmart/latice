package latice.controleur;

import java.util.concurrent.TimeUnit;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import latice.enumeration.Couleur;
import latice.metier.JoueurArtificiel;
import latice.metier.Partie;
import latice.metier.Sauvegarde;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

/**
 * Tests d'interface de l'écran de jeu (nécessitent un affichage, donc exclus du `mvn test` par défaut :
 * `mvn test -Dtests.exclus=aucun` pour les inclure). Les données sont redirigées vers target/ par le pom.
 */
@Tag("ui")
class ControleurDeJeuUITest extends ApplicationTest {

    private ControleurDeJeu controleur;
    private Stage fenetre;

    @Override
    public void start(Stage stage) {
        fenetre = stage;
    }

    @BeforeEach
    void nettoyerLaSauvegarde() {
        Sauvegarde.supprimer();
    }

    private void afficher(Partie partie, boolean solo) {
        interact(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ihm/Latice.fxml"));
                Parent racine = loader.load();
                controleur = loader.getController();
                controleur.demarrer(partie, null, Couleur.BLEU, Couleur.ROUGE, solo);
                fenetre.setScene(new Scene(racine));
                fenetre.show();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
        waitForFxEvents();
    }

    private int nombreEnfants(String idGrille) {
        return ((GridPane) lookup("#" + idGrille).query()).getChildren().size();
    }

    @Test
    void uneNouvellePartieAfficheLePlateauLesRacksEtLesScores() {
        afficher(new Partie("Alice", "Bob"), false);

        assertEquals(81, nombreEnfants("gridPaneCentral"), "9x9 cases, aucune tuile posée");
        assertEquals(5, nombreEnfants("rack1"));
        assertEquals(5, nombreEnfants("rack2"));
        assertEquals("Alice", ((Label) lookup("#labelJoueur1").query()).getText());
        assertEquals("Points: 0", ((Label) lookup("#labelPointsJoueur1").query()).getText());
        assertEquals("Tours : 10", ((Label) lookup("#labelToursJoueur2").query()).getText());
        assertTrue(Sauvegarde.existe(), "la partie est sauvegardée dès le premier tour");
    }

    @Test
    void enModeSoloLIAJoueSonTourPuisRendLaMain() {
        Partie partie = new Partie("Alice", "IA");
        afficher(partie, true);
        if (partie.joueurCourant() == partie.joueur1()) {
            interact(() -> ((Button) lookup("#btnPasse").query()).fire());
        }
        sleep(2, TimeUnit.SECONDS);
        waitForFxEvents();

        assertEquals(1, partie.tuilesPlacees().size(), "l'IA a posé la première tuile au centre");
        assertSame(partie.joueur1(), partie.joueurCourant(), "la main est revenue au joueur humain");
        assertEquals(82, nombreEnfants("gridPaneCentral"), "81 cases + 1 tuile affichée");
    }

    @Test
    void uneSauvegardeReprendLaPartieOuElleEtait() {
        Partie origine = new Partie("Alice", "Bob");
        for (int tour = 0; tour < 5; tour++) {
            JoueurArtificiel.choisirCoup(origine.plateau(), origine.joueurCourant().rack().obtenirTuilesRack(),
                    origine.tuilesPlacees()).ifPresent(c -> origine.placer(c.position(), c.tuile()));
            origine.finirTour();
        }
        Partie reprise = Partie.restaurer(origine.instantane(false, Couleur.BLEU, Couleur.ROUGE));

        afficher(reprise, false);

        assertEquals(81 + origine.tuilesPlacees().size(), nombreEnfants("gridPaneCentral"));
        assertEquals(reprise.joueur1().rack().taille(), nombreEnfants("rack1"));
        assertEquals(reprise.joueur2().rack().taille(), nombreEnfants("rack2"));
        assertEquals("Tour de " + origine.joueurCourant().nom(), ((Label) lookup("#labelTourActuel").query()).getText());
    }
}
