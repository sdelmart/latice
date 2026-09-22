package latice.controleur;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Circle;
import latice.enumeration.Couleur;


public class ControleurFinDePartie {
    @FXML
    private Label labelTitre;
    @FXML
    private Label labelVainqueur;
    @FXML
    private VBox carteJoueur1;
    @FXML
    private VBox carteJoueur2;
    @FXML
    private Circle pastilleJoueur1;
    @FXML
    private Circle pastilleJoueur2;
    @FXML
    private Label labelNomJoueur1;
    @FXML
    private Label labelNomJoueur2;
    @FXML
    private Label labelPointsJoueur1;
    @FXML
    private Label labelPointsJoueur2;
    @FXML
    private Label labelTuilesJoueur1;
    @FXML
    private Label labelTuilesJoueur2;
    @FXML
    private MediaView mediaView;
    @FXML
    private StackPane rootPane;
    @FXML
    private Region fondRegion;
    private MediaPlayer mediaPlayer;

    private Runnable actionRejouer;
    private Runnable actionMenuPrincipal;

    @FXML
    public void initialize() {
        ThemeVisuel.lierFond(fondRegion);
        String cheminVideo = getClass().getResource("/video/Confetis.mp4").toExternalForm();
        Media media = new Media(cheminVideo);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaView.fitWidthProperty().bind(rootPane.widthProperty());
        mediaView.fitHeightProperty().bind(rootPane.heightProperty());
        mediaPlayer.play();
    }

    public void definirResultat(String nomJoueur1, int pointsJoueur1, int tuilesJoueur1, Couleur couleurJoueur1,
            String nomJoueur2, int pointsJoueur2, int tuilesJoueur2, Couleur couleurJoueur2, String vainqueur) {

        labelNomJoueur1.setText(nomJoueur1);
        labelPointsJoueur1.setText("Points : " + pointsJoueur1);
        labelTuilesJoueur1.setText(tuilesJoueur1 + " tuile(s) posée(s)");
        pastilleJoueur1.setStyle("-fx-fill: " + CouleurUI.hex(couleurJoueur1) + ";");

        labelNomJoueur2.setText(nomJoueur2);
        labelPointsJoueur2.setText("Points : " + pointsJoueur2);
        labelTuilesJoueur2.setText(tuilesJoueur2 + " tuile(s) posée(s)");
        pastilleJoueur2.setStyle("-fx-fill: " + CouleurUI.hex(couleurJoueur2) + ";");

        if (vainqueur.equals(nomJoueur1)) {
            labelVainqueur.setText("🏆 " + nomJoueur1 + " remporte la partie !");
            carteJoueur1.getStyleClass().add("swatch-selectionne");
        } else if (vainqueur.equals(nomJoueur2)) {
            labelVainqueur.setText("🏆 " + nomJoueur2 + " remporte la partie !");
            carteJoueur2.getStyleClass().add("swatch-selectionne");
        } else {
            labelVainqueur.setText("Égalité !");
        }
    }

    public void mettreSurRejouer(Runnable action) {
        this.actionRejouer = action;
    }

    public void mettreSurMenuPrincipal(Runnable action) {
        this.actionMenuPrincipal = action;
    }

    @FXML
    private void rejouer() {
        EffetsSonores.jouer(EffetsSonores.Effet.CLIC);
        if (actionRejouer != null) {
            actionRejouer.run();
        }
    }

    @FXML
    private void retourMenuPrincipal() {
        EffetsSonores.jouer(EffetsSonores.Effet.CLIC);
        if (actionMenuPrincipal != null) {
            actionMenuPrincipal.run();
        }
    }
}
