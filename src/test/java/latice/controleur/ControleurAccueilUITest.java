package latice.controleur;

import java.util.concurrent.TimeUnit;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

/**
 * Test d'interface réel (TestFX) : charge le vrai FXML dans une vraie fenêtre JavaFX et déclenche
 * les actions des boutons pour vérifier la navigation entre panneaux. Nécessite un affichage —
 * exclu de `mvn test` par défaut (voir <excludedGroups> dans le pom), donc absent des runners CI
 * headless. Lancer avec `mvn test -DexcludedGroups=` sur un poste avec écran pour les inclure.
 *
 * <p>Utilise {@code Button.fire()} plutôt que le robot souris de TestFX (clickOn) : sur cet
 * environnement, les clics simulés au niveau OS n'atteignent pas de façon fiable une fenêtre qui
 * n'a pas le focus système — {@code fire()} déclenche le même gestionnaire d'action via la vraie
 * scène JavaFX, donc teste la même logique (FXML, contrôleur, navigation) sans dépendre du focus
 * de la fenêtre.
 */
@Tag("ui")
class ControleurAccueilUITest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ihm/Accueil.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }

    @BeforeEach
    void revenirAuMenuPrincipal() {
        interact(() -> {
            basculer("#panneauMenu", true);
            basculer("#panneauNouvellePartie", false);
            basculer("#panneauProfils", false);
            basculer("#panneauCredits", false);
        });
    }

    private void basculer(String selecteur, boolean visible) {
        Node panneau = lookup(selecteur).query();
        panneau.setVisible(visible);
        panneau.setManaged(visible);
        panneau.setOpacity(visible ? 1 : 0);
    }

    private Button boutonParTexte(String texte) {
        return lookup(".button").queryAll().stream()
                .filter(Button.class::isInstance)
                .map(Button.class::cast)
                .filter(b -> texte.equals(b.getText()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Bouton introuvable : " + texte));
    }

    private void declencher(Button bouton) {
        interact(bouton::fire);
        waitForFxEvents();
        sleep(300, TimeUnit.MILLISECONDS);
    }

    @Test
    void cliquerSurJouerAfficheLePanneauNouvellePartie() {
        declencher(boutonParTexte("JOUER"));
        assertTrue(lookup("#panneauNouvellePartie").query().isVisible());
    }

    @Test
    void boutonRetourRevientAuMenuPrincipal() {
        declencher(boutonParTexte("JOUER"));
        declencher(boutonParTexte("← Retour"));
        assertTrue(lookup("#panneauMenu").query().isVisible());
    }

    @Test
    void cliquerSurProfilsAfficheLHistorique() {
        declencher(boutonParTexte("PROFILS"));
        assertTrue(lookup("#panneauProfils").query().isVisible());
    }
}
