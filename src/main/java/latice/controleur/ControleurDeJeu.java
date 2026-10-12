package latice.controleur;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.util.Duration;
import latice.enumeration.Couleur;
import latice.metier.HistoriqueParties;
import latice.metier.Joueur;
import latice.metier.JoueurArtificiel;
import latice.metier.Partie;
import latice.metier.Plateau;
import latice.metier.Position;
import latice.metier.Sauvegarde;
import latice.metier.Tuile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Écran de jeu : affiche l'état d'une {@link Partie} et traduit les gestes du joueur (glisser-déposer,
 * boutons) en appels à celle-ci. Toutes les règles vivent dans {@code latice.metier}.
 */
public class ControleurDeJeu {

    private static final Logger LOG = LoggerFactory.getLogger(ControleurDeJeu.class);

    @FXML
    private GridPane rack1;
    @FXML
    private GridPane rack2;
    @FXML
    private Button btnEchange;
    @FXML
    private Button btnPasse;
    @FXML
    private Button btnJouer;
    @FXML
    private Button btnAcheter;
    @FXML
    private Label labelJoueur1;
    @FXML
    private Label labelJoueur2;
    @FXML
    private GridPane gridPaneCentral;
    @FXML
    private Label labelToursJoueur1;
    @FXML
    private Label labelToursJoueur2;
    @FXML
    private Label labelPointsJoueur1;
    @FXML
    private Label labelPointsJoueur2;
    @FXML
    private Label labelActionsJoueur1;
    @FXML
    private Label labelActionsJoueur2;
    @FXML
    private Label labelTourActuel;
    @FXML
    private Button btnParametresLatice;
    @FXML
    private Region fondRegion;

    private Partie partie;
    private Musique musique;
    private boolean modeSolo;
    private Couleur couleurJoueur1 = Couleur.BLEU;
    private Couleur couleurJoueur2 = Couleur.ROUGE;

    private final Map<Position, ImageView> casesPlateau = new HashMap<>();
    private Tuile tuileEnCoursDeplacement;
    private boolean tourIAEnCours;
    private boolean partieTerminee;

    @FXML
    private void initialize() {
        ThemeVisuel.lierFond(fondRegion);
    }

    /** Point d'entrée unique : affiche la partie (neuve ou reprise) et lance le premier tour. */
    public void demarrer(Partie partie, Musique musique, Couleur couleurJoueur1, Couleur couleurJoueur2,
            boolean modeSolo) {
        this.partie = partie;
        this.musique = musique;
        this.couleurJoueur1 = couleurJoueur1;
        this.couleurJoueur2 = couleurJoueur2;
        this.modeSolo = modeSolo;

        labelJoueur1.setText(partie.joueur1().nom());
        labelJoueur2.setText(partie.joueur2().nom());
        labelJoueur1.setStyle(labelJoueur1.getStyle() + " -fx-text-fill: " + CouleurUI.hex(couleurJoueur1) + ";");
        labelJoueur2.setStyle(labelJoueur2.getStyle() + " -fx-text-fill: " + CouleurUI.hex(couleurJoueur2) + ";");

        construireCasesDuPlateau(partie.plateau());
        partie.tuilesPlacees().forEach(this::afficherTuileSurPlateau);
        rafraichirRacks();
        configurerDragAndDrop();
        demarrerTour();
    }

    public Partie partie() {
        return partie;
    }

    // --- Affichage ------------------------------------------------------------------------------

    private void construireCasesDuPlateau(Plateau plateau) {
        gridPaneCentral.getChildren().clear();
        casesPlateau.clear();
        for (int ligne = 0; ligne < plateau.tailleplateau(); ligne++) {
            for (int col = 0; col < plateau.tailleplateau(); col++) {
                String image = switch (plateau.casePlateau(ligne, col)) {
                    case SOLEIL -> "/images/img_Latice/img/bg_sun.png";
                    case LUNE -> "/images/img_Latice/img/bg_moon.png";
                    case NORMALE -> "/images/img_Latice/img/bg_sea.png";
                };
                ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(image)));
                imageView.setFitWidth(68);
                imageView.setFitHeight(68);
                gridPaneCentral.add(imageView, col, ligne);
                casesPlateau.put(new Position(col, ligne), imageView);
            }
        }
    }

    private void afficherTuileSurPlateau(Position pos, Tuile tuile) {
        ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(tuile.cheminImage())));
        imageView.setFitWidth(67);
        imageView.setFitHeight(67);
        gridPaneCentral.add(imageView, pos.posX(), pos.posY());
    }

    private void rafraichirRacks() {
        remplirRack(rack1, partie.joueur1());
        remplirRack(rack2, partie.joueur2());
    }

    private void remplirRack(GridPane rack, Joueur proprietaire) {
        rack.getChildren().clear();
        int ligne = 0;
        for (Tuile tuile : proprietaire.rack().obtenirTuilesRack()) {
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(tuile.cheminImage())));
            imageView.setFitWidth(115);
            imageView.setFitHeight(102);
            rack.add(imageView, 0, ligne++);
            imageView.setOnDragDetected(event -> {
                if (tourIAEnCours || partieTerminee || partie.joueurCourant() != proprietaire) {
                    event.consume();
                    return;
                }
                Dragboard dragboard = imageView.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putImage(imageView.getImage());
                dragboard.setContent(content);
                tuileEnCoursDeplacement = tuile;
                surlignerCasesValides(tuile);
                event.consume();
            });
            imageView.setOnDragDone(event -> nettoyerSurlignage());
        }
    }

    private void rafraichir() {
        Joueur j1 = partie.joueur1();
        Joueur j2 = partie.joueur2();
        labelToursJoueur1.setText("Tours : " + partie.toursRestants(j1));
        labelToursJoueur2.setText("Tours : " + partie.toursRestants(j2));
        labelPointsJoueur1.setText("Points: " + j1.points());
        labelPointsJoueur2.setText("Points: " + j2.points());
        labelActionsJoueur1.setText("Actions supplémentaires: " + j1.nbActionsSup);
        labelActionsJoueur2.setText("Actions supplémentaires: " + j2.nbActionsSup);
        labelTourActuel.setText("Tour de " + partie.joueurCourant().nom());
    }

    private void activerBoutons(boolean actifs) {
        btnJouer.setDisable(!actifs);
        btnEchange.setDisable(!actifs);
        btnAcheter.setDisable(!actifs);
        btnPasse.setDisable(!actifs);
    }

    // --- Aide au placement -------------------------------------------------------------------------

    private void surlignerCasesValides(Tuile tuile) {
        for (Map.Entry<Position, ImageView> entree : casesPlateau.entrySet()) {
            if (partie.peutPlacer(entree.getKey(), tuile)) {
                entree.getValue().getStyleClass().add("case-valide");
            }
        }
    }

    private void nettoyerSurlignage() {
        for (ImageView imageView : casesPlateau.values()) {
            imageView.getStyleClass().remove("case-valide");
        }
    }

    // --- Glisser-déposer ---------------------------------------------------------------------------

    private void configurerDragAndDrop() {
        gridPaneCentral.setOnDragOver(event -> {
            if (event.getGestureSource() != gridPaneCentral && event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        gridPaneCentral.setOnDragDropped(event -> {
            boolean succes = false;
            if (event.getDragboard().hasImage() && tuileEnCoursDeplacement != null) {
                Position pos = positionSousLeCurseur(event.getX(), event.getY());
                if (partie.peutPlacer(pos, tuileEnCoursDeplacement)) {
                    appliquerPlacement(pos, tuileEnCoursDeplacement);
                    succes = true;
                }
            }
            event.setDropCompleted(succes);
            if (!succes) {
                EffetsSonores.jouer(EffetsSonores.Effet.ERREUR);
            }
            tuileEnCoursDeplacement = null;
            event.consume();
            if (succes && partie.estTerminee()) {
                terminerPartie();
            }
        });
    }

    private Position positionSousLeCurseur(double x, double y) {
        int taille = partie.plateau().tailleplateau();
        int col = (int) (x / (gridPaneCentral.getWidth() / taille));
        int ligne = (int) (y / (gridPaneCentral.getHeight() / taille));
        return new Position(Math.max(0, Math.min(taille - 1, col)), Math.max(0, Math.min(taille - 1, ligne)));
    }

    /** Pose la tuile du joueur courant (humain ou IA) et met l'affichage à jour. */
    private void appliquerPlacement(Position pos, Tuile tuile) {
        Joueur joueur = partie.joueurCourant();
        EffetsSonores.jouer(EffetsSonores.Effet.POSE_TUILE);
        partie.placer(pos, tuile);
        afficherTuileSurPlateau(pos, tuile);
        remplirRack(joueur == partie.joueur1() ? rack1 : rack2, joueur);
        btnEchange.setDisable(true);
        btnAcheter.setDisable(true);
        btnPasse.setDisable(true);
        rafraichir();
    }

    // --- Actions du tour ----------------------------------------------------------------------------

    @FXML
    private void actionJouer(ActionEvent event) {
        EffetsSonores.jouer(EffetsSonores.Effet.CLIC);
        if (tourIAEnCours || partieTerminee) {
            return;
        }
        partie.finirTour();
        rafraichirRacks();
        demarrerTour();
    }

    @FXML
    private void actionPasse(ActionEvent event) {
        EffetsSonores.jouer(EffetsSonores.Effet.CLIC);
        if (tourIAEnCours || partieTerminee) {
            return;
        }
        partie.finirTour();
        demarrerTour();
    }

    @FXML
    private void actionEchanger(ActionEvent event) {
        EffetsSonores.jouer(EffetsSonores.Effet.CLIC);
        if (tourIAEnCours || partieTerminee) {
            return;
        }
        if (!partie.peutAcheterOuEchanger()) {
            EffetsSonores.jouer(EffetsSonores.Effet.ERREUR);
            return;
        }
        partie.echanger();
        rafraichirRacks();
        demarrerTour();
    }

    @FXML
    private void actionAcheter(ActionEvent event) {
        EffetsSonores.jouer(EffetsSonores.Effet.CLIC);
        if (tourIAEnCours || partieTerminee) {
            return;
        }
        if (!partie.acheterActionSupplementaire()) {
            LOG.info("Achat d'action refusé");
            EffetsSonores.jouer(EffetsSonores.Effet.ERREUR);
        }
        rafraichir();
    }

    private void demarrerTour() {
        if (partie.estTerminee()) {
            terminerPartie();
            return;
        }
        boolean tourDeLIA = modeSolo && partie.joueurCourant() == partie.joueur2();
        activerBoutons(!tourDeLIA);
        rafraichir();
        EffetsSonores.jouer(EffetsSonores.Effet.CHANGEMENT_TOUR);
        Sauvegarde.enregistrer(partie.instantane(modeSolo, couleurJoueur1, couleurJoueur2));
        if (tourDeLIA) {
            jouerTourIA();
        }
    }

    // --- Mode solo (IA) -----------------------------------------------------------------------------

    private void jouerTourIA() {
        tourIAEnCours = true;
        activerBoutons(false);
        PauseTransition pause = new PauseTransition(Duration.millis(700));
        pause.setOnFinished(e -> executerCoupIA());
        pause.play();
    }

    private void executerCoupIA() {
        Joueur ia = partie.joueurCourant();
        JoueurArtificiel.choisirCoup(partie.plateau(), ia.rack().obtenirTuilesRack(), partie.tuilesPlacees())
                .ifPresent(coup -> appliquerPlacement(coup.position(), coup.tuile()));
        tourIAEnCours = false;
        if (partie.estTerminee()) {
            terminerPartie();
            return;
        }
        partie.finirTour();
        demarrerTour();
    }

    // --- Fin de partie ------------------------------------------------------------------------------

    private void terminerPartie() {
        if (partieTerminee) {
            return;
        }
        partieTerminee = true;
        String vainqueur = partie.vainqueur().map(Joueur::nom).orElse("Égalité");
        EffetsSonores.jouer(EffetsSonores.Effet.VICTOIRE);
        HistoriqueParties.enregistrer(partie.joueur1().nom(), partie.joueur1().points(),
                partie.joueur2().nom(), partie.joueur2().points(), vainqueur);
        Sauvegarde.supprimer();
        afficherFinDePartie(vainqueur);
        if (labelTourActuel.getScene() != null) {
            ((Stage) labelTourActuel.getScene().getWindow()).close();
        }
    }

    private void afficherFinDePartie(String vainqueur) {
        Joueur j1 = partie.joueur1();
        Joueur j2 = partie.joueur2();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ihm/FinDePartie.fxml"));
            Parent root = loader.load();
            ControleurFinDePartie controller = loader.getController();
            controller.definirResultat(
                    j1.nom(), j1.points(), partie.tuilesPosees(j1), couleurJoueur1,
                    j2.nom(), j2.points(), partie.tuilesPosees(j2), couleurJoueur2,
                    vainqueur);

            Stage stage = new Stage();
            controller.mettreSurRejouer(() -> {
                try {
                    Navigation.demarrerPartie(stage, musique, j1.nom(), j2.nom(),
                            couleurJoueur1, couleurJoueur2, modeSolo);
                } catch (IOException e) {
                    LOG.error("Impossible de relancer une partie", e);
                }
            });
            controller.mettreSurMenuPrincipal(() -> {
                try {
                    Navigation.ouvrirMenuPrincipal(stage, musique);
                } catch (IOException e) {
                    LOG.error("Impossible d'ouvrir le menu principal", e);
                }
            });

            stage.setTitle("Fin de la partie");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            LOG.error("Impossible d'afficher l'écran de fin de partie", e);
        }
    }

    // --- Paramètres ---------------------------------------------------------------------------------

    @FXML
    private void ouvrirParametresLatice() {
        EffetsSonores.jouer(EffetsSonores.Effet.CLIC);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ihm/Parametre.fxml"));
            Parent root = fxmlLoader.load();
            ControleurParametres controleur = fxmlLoader.getController();
            controleur.mettreMusique(musique);
            Stage stage = new Stage();
            stage.setTitle("Paramètres");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/img_Latice/img/parametre.png")));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            LOG.error("Impossible d'ouvrir les paramètres", e);
        }
    }
}
