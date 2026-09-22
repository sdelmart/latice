package latice.controleur;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import latice.metier.Arbitre;
import latice.metier.Joueur;
import latice.metier.Plateau;
import javafx.animation.ScaleTransition;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

public class ControleurAccueil {

    @FXML private TextField nomJoueur1;
    @FXML private TextField nomJoueur2;
    @FXML private Button boutonParametresAccueil;
    @FXML private Label labelErreur;
    @FXML private Region fondRegion;

    private Musique musique;

    @FXML private Pane floatingPane;

    @FXML
    public void initialize() {
        ThemeVisuel.lierFond(fondRegion);

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

    public void mettreMusique(Musique musique) {
        this.musique = musique;
    }

    @FXML
    private void validerJouerLatice(Event event) {
        String joueur1 = nomJoueur1.getText().trim();
        String joueur2 = nomJoueur2.getText().trim();

        if (joueur1.isEmpty() || joueur2.isEmpty()) {
            afficherErreur("Les deux pseudos doivent être renseignés.");
            return;
        }
        if (joueur1.equalsIgnoreCase(joueur2)) {
            afficherErreur("Les deux joueurs doivent avoir des pseudos différents.");
            return;
        }
        masquerErreur();

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ihm/Latice.fxml"));
        Parent root = loader.load();

        ControleurDeJeu controleurDeJeu = loader.getController();
        
        controleurDeJeu.mettreMusique(musique);
        Joueur joueurN1 = new Joueur(joueur1);
        Joueur joueurN2 = new Joueur(joueur2);
        Arbitre arbitre = new Arbitre(joueurN1, joueurN2);
        controleurDeJeu.setArbitre(arbitre);
        controleurDeJeu.nomsJoueurs(joueur1, joueur2);
        
        Plateau plateau = new Plateau(9);
        controleurDeJeu.initialiserPlateau(plateau);
		controleurDeJeu.initialiserRackJoueurs(joueurN1, joueurN2);
        controleurDeJeu.initialiserDragAndDrop();
        controleurDeJeu.mettreAJourTour();
        controleurDeJeu.initialiserTours();

        Scene scene = new Scene(root);
        primaryStage.setTitle("Latice - En cours");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/img_Latice/img/icone.png")));
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> musique.arreterMusique());
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @FXML
    private void ouvrirParametresAccueil() {
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