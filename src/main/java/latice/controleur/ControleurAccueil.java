package latice.controleur;

import java.io.IOException;
import java.util.prefs.Preferences;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import latice.enumeration.Couleur;
import latice.metier.HistoriqueParties;

public class ControleurAccueil {

    private static final Preferences PREFS = Preferences.userNodeForPackage(ControleurAccueil.class);
    private static final String CLE_PSEUDO1 = "dernier_pseudo1";
    private static final String CLE_PSEUDO2 = "dernier_pseudo2";
    private static final String CLE_COULEUR1 = "derniere_couleur1";
    private static final String CLE_COULEUR2 = "derniere_couleur2";

    @FXML private TextField nomJoueur1;
    @FXML private TextField nomJoueur2;
    @FXML private Button boutonParametresAccueil;
    @FXML private Label labelErreur;
    @FXML private Region fondRegion;
    @FXML private Pane floatingPane;
    @FXML private HBox swatchesJoueur1;
    @FXML private HBox swatchesJoueur2;
    @FXML private ListView<String> listeHistorique;
    @FXML private Button boutonMode2Joueurs;
    @FXML private Button boutonModeSolo;
    @FXML private VBox blocJoueur2;

    @FXML private Region panneauMenu;
    @FXML private Region panneauNouvellePartie;
    @FXML private Region panneauProfils;
    @FXML private Region panneauCredits;

    private Musique musique;
    private Couleur couleurJoueur1;
    private Couleur couleurJoueur2;
    private boolean modeSolo = false;

    @FXML
    public void initialize() {
        ThemeVisuel.lierFond(fondRegion);
        creerDecorationsFlottantes();

        couleurJoueur1 = lireCouleurSauvegardee(CLE_COULEUR1, Couleur.BLEU);
        couleurJoueur2 = lireCouleurSauvegardee(CLE_COULEUR2, Couleur.ROUGE);
        remplirSwatches(swatchesJoueur1, couleurJoueur1, c -> couleurJoueur1 = c);
        remplirSwatches(swatchesJoueur2, couleurJoueur2, c -> couleurJoueur2 = c);

        nomJoueur1.setText(PREFS.get(CLE_PSEUDO1, ""));
        nomJoueur2.setText(PREFS.get(CLE_PSEUDO2, ""));

        listeHistorique.setPlaceholder(new Label("Aucune partie jouée pour l'instant."));
    }

    private void creerDecorationsFlottantes() {
        // Décorations discrètes, cantonnées aux bords gauche/droite pour ne jamais recouvrir la carte centrale.
        String[] images = {
            "/images/img_Latice/img/FLEUR_BLEU.png",
            "/images/img_Latice/img/OISEAU_ROUGE.png",
            "/images/img_Latice/img/TORTUE_VERT.png",
            "/images/img_Latice/img/DAUPHIN_CYAN.png",
            "/images/img_Latice/img/LEZARD_MAGENTA.png",
            "/images/img_Latice/img/PLUME_JAUNE.png"
        };

        double[][] positions = {
            {40, 60},
            {830, 60},
            {40, 320},
            {830, 320},
            {40, 570},
            {830, 570}
        };

        for (int i = 0; i < images.length; i++) {
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(images[i])));
            Rectangle clip = new Rectangle(28, 28);
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            imageView.setClip(clip);
            imageView.setFitWidth(28);
            imageView.setFitHeight(28);
            imageView.setOpacity(0.55);

            imageView.setLayoutX(positions[i][0]);
            imageView.setLayoutY(positions[i][1]);

            ScaleTransition zoom = new ScaleTransition(Duration.seconds(2.5), imageView);
            zoom.setFromX(1.0);
            zoom.setFromY(1.0);
            zoom.setToX(1.12);
            zoom.setToY(1.12);
            zoom.setAutoReverse(true);
            zoom.setCycleCount(ScaleTransition.INDEFINITE);
            zoom.play();

            floatingPane.getChildren().add(imageView);
        }
    }

    private Couleur lireCouleurSauvegardee(String cle, Couleur parDefaut) {
        try {
            return Couleur.valueOf(PREFS.get(cle, parDefaut.name()));
        } catch (IllegalArgumentException e) {
            return parDefaut;
        }
    }

    private void remplirSwatches(HBox conteneur, Couleur selection, java.util.function.Consumer<Couleur> onChoix) {
        for (Couleur couleur : Couleur.values()) {
            Button pastille = new Button();
            pastille.getStyleClass().add("swatch");
            pastille.setStyle("-fx-background-color: " + CouleurUI.hex(couleur) + ";");
            if (couleur == selection) {
                pastille.getStyleClass().add("swatch-selectionne");
            }
            pastille.setOnAction(e -> {
                EffetsSonores.jouer(EffetsSonores.Effet.CLIC);
                onChoix.accept(couleur);
                for (javafx.scene.Node n : conteneur.getChildren()) {
                    n.getStyleClass().remove("swatch-selectionne");
                }
                pastille.getStyleClass().add("swatch-selectionne");
            });
            conteneur.getChildren().add(pastille);
        }
    }

    public void mettreMusique(Musique musique) {
        this.musique = musique;
    }

    // --- Mode de jeu -------------------------------------------------------------------

    @FXML
    private void choisirMode2Joueurs() {
        EffetsSonores.jouer(EffetsSonores.Effet.CLIC);
        modeSolo = false;
        blocJoueur2.setVisible(true);
        blocJoueur2.setManaged(true);
        boutonMode2Joueurs.getStyleClass().add("swatch-selectionne");
        boutonModeSolo.getStyleClass().remove("swatch-selectionne");
    }

    @FXML
    private void choisirModeSolo() {
        EffetsSonores.jouer(EffetsSonores.Effet.CLIC);
        modeSolo = true;
        blocJoueur2.setVisible(false);
        blocJoueur2.setManaged(false);
        boutonModeSolo.getStyleClass().add("swatch-selectionne");
        boutonMode2Joueurs.getStyleClass().remove("swatch-selectionne");
    }

    // --- Navigation entre panneaux -------------------------------------------------

    @FXML
    private void afficherPanneauMenu() {
        EffetsSonores.jouer(EffetsSonores.Effet.CLIC);
        basculerVers(panneauMenu);
    }

    @FXML
    private void afficherPanneauNouvellePartie() {
        EffetsSonores.jouer(EffetsSonores.Effet.CLIC);
        basculerVers(panneauNouvellePartie);
    }

    @FXML
    private void afficherPanneauProfils() {
        EffetsSonores.jouer(EffetsSonores.Effet.CLIC);
        listeHistorique.getItems().setAll(
                HistoriqueParties.chargerHistorique().stream().map(HistoriqueParties.Partie::resume).toList());
        basculerVers(panneauProfils);
    }

    @FXML
    private void afficherPanneauCredits() {
        EffetsSonores.jouer(EffetsSonores.Effet.CLIC);
        basculerVers(panneauCredits);
    }

    private void basculerVers(Region panneauCible) {
        for (Region panneau : new Region[] {panneauMenu, panneauNouvellePartie, panneauProfils, panneauCredits}) {
            if (panneau != panneauCible && panneau.isVisible()) {
                FadeTransition sortie = new FadeTransition(Duration.millis(160), panneau);
                sortie.setToValue(0);
                sortie.setOnFinished(e -> {
                    panneau.setVisible(false);
                    panneau.setManaged(false);
                });
                sortie.play();
            }
        }
        panneauCible.setOpacity(0);
        panneauCible.setVisible(true);
        panneauCible.setManaged(true);
        FadeTransition entree = new FadeTransition(Duration.millis(220), panneauCible);
        entree.setToValue(1);
        entree.play();
    }

    // --- Nouvelle partie -------------------------------------------------------------

    @FXML
    private void validerJouerLatice(Event event) {
        EffetsSonores.jouer(EffetsSonores.Effet.CLIC);
        String joueur1 = nomJoueur1.getText().trim();
        String joueur2 = modeSolo ? "IA" : nomJoueur2.getText().trim();

        if (joueur1.isEmpty() || (!modeSolo && joueur2.isEmpty())) {
            afficherErreur("Les pseudos doivent être renseignés.");
            return;
        }
        if (!modeSolo && joueur1.equalsIgnoreCase(joueur2)) {
            afficherErreur("Les deux joueurs doivent avoir des pseudos différents.");
            return;
        }
        masquerErreur();

        PREFS.put(CLE_PSEUDO1, joueur1);
        PREFS.put(CLE_COULEUR1, couleurJoueur1.name());
        if (!modeSolo) {
            PREFS.put(CLE_PSEUDO2, joueur2);
            PREFS.put(CLE_COULEUR2, couleurJoueur2.name());
        }

        afficherEcranChargement((Stage) nomJoueur1.getScene().getWindow(), joueur1, joueur2);
    }

    private void afficherErreur(String message) {
        labelErreur.setText(message);
        labelErreur.setVisible(true);
        labelErreur.setManaged(true);
    }

    private void masquerErreur() {
        labelErreur.setVisible(false);
        labelErreur.setManaged(false);
    }

    @FXML
    private void ouvrirReglesAccueil() {
        EffetsSonores.jouer(EffetsSonores.Effet.CLIC);
        ControleurParametres.afficherRegles();
    }

    private void afficherEcranChargement(Stage primaryStage, String joueur1, String joueur2) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ihm/Chargement.fxml"));
            Parent root = loader.load();

            ControleurChargement controleurChargement = loader.getController();

            controleurChargement.mettreSurChargementTermine(() -> {
                try {
                    chargerPlateauDeJeu(primaryStage, joueur1, joueur2);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            Scene scene = new Scene(root);
            primaryStage.setTitle("Chargement du jeu...");
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/img_Latice/img/icone.png")));
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void chargerPlateauDeJeu(Stage primaryStage, String joueur1, String joueur2) throws IOException {
        Navigation.demarrerPartie(primaryStage, musique, joueur1, joueur2, couleurJoueur1, couleurJoueur2, modeSolo);
    }

    @FXML
    private void ouvrirParametresAccueil() {
        EffetsSonores.jouer(EffetsSonores.Effet.CLIC);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ihm/Parametre.fxml"));
            Parent root = fxmlLoader.load();

            ControleurParametres controleurParametres = fxmlLoader.getController();

            controleurParametres.mettreMusique(musique);

            Stage stage = new Stage();
            stage.setTitle("Paramètres");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/img_Latice/img/parametre.png")));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void quitterApplication() {
        Platform.exit();
    }
}
