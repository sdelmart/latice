package main;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import latice.controleur.ControleurAccueil;
import latice.controleur.Musique;

public class LancementLatice extends Application {

    private final Musique musique = new Musique();
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        musique.jouerMusique("/musique/MusLatice.mp3");
        afficherEcranAccueil();
    }

    private void afficherEcranAccueil() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ihm/Accueil.fxml"));
        Parent root = loader.load();
        
        ControleurAccueil controleur = loader.getController();
        controleur.mettreMusique(musique);
        
        Scene scene = new Scene(root);
        primaryStage.setTitle("Jeu Latice");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/img_latice/img/icone.png")));
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event -> musique.arreterMusique());
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}