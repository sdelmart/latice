package latice.controleur;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;


public class ControleurFinDePartie {
    @FXML 
    private Label labelTitre;
    @FXML 
    private Label labelResultat;
    @FXML
    private MediaView mediaView;
    @FXML
    private StackPane rootPane;
    private MediaPlayer mediaPlayer;
    
    @FXML
    public void initialize() {
        String cheminVideo = getClass().getResource("/video/Confetis.mp4").toExternalForm();
        Media media = new Media(cheminVideo);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaView.setMediaPlayer(mediaPlayer);
        mediaView.fitWidthProperty().bind(rootPane.widthProperty());
        mediaView.fitHeightProperty().bind(rootPane.heightProperty());
        mediaPlayer.play();
    }

    public void definirResultat(String resultat) {
        labelResultat.setText(resultat);
    }

    @FXML
    private void fermerFenetre() {
        Stage stage = (Stage) labelTitre.getScene().getWindow();
        stage.close();
    }
}
