package main;

import javafx.application.Application;
import javafx.stage.Stage;
import latice.controleur.Musique;
import latice.controleur.Navigation;

public class LancementLatice extends Application {

    private final Musique musique = new Musique();

    @Override
    public void start(Stage primaryStage) throws Exception {
        musique.demarrerAvecPreferences();
        Navigation.ouvrirMenuPrincipal(primaryStage, musique);
        primaryStage.setOnCloseRequest(event -> musique.arreterMusique());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
