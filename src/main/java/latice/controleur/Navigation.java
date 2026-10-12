package latice.controleur;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import latice.enumeration.Couleur;
import latice.metier.Partie;
import latice.metier.Sauvegarde;

/** Points d'entrée réutilisés pour ouvrir l'accueil ou démarrer/reprendre une partie depuis n'importe où
 * (lancement de l'appli, boutons "Continuer", "Rejouer" et "Menu principal"). */
public final class Navigation {

    private Navigation() {
    }

    public static void ouvrirMenuPrincipal(Stage stage, Musique musique) throws IOException {
        FXMLLoader loader = new FXMLLoader(Navigation.class.getResource("/ihm/Accueil.fxml"));
        Parent root = loader.load();
        ControleurAccueil controleur = loader.getController();
        controleur.mettreMusique(musique);

        stage.setTitle("Jeu Latice");
        stage.getIcons().setAll(new Image(Navigation.class.getResourceAsStream("/images/img_Latice/img/icone.png")));
        stage.setScene(new Scene(root));
        stage.setResizable(false);
        stage.show();
    }

    public static void demarrerPartie(Stage stage, Musique musique, String nomJoueur1, String nomJoueur2,
            Couleur couleurJoueur1, Couleur couleurJoueur2, boolean solo) throws IOException {
        afficherPartie(stage, musique, new Partie(nomJoueur1, nomJoueur2), couleurJoueur1, couleurJoueur2, solo);
    }

    public static void reprendrePartie(Stage stage, Musique musique, Sauvegarde sauvegarde) throws IOException {
        afficherPartie(stage, musique, Partie.restaurer(sauvegarde),
                sauvegarde.joueurs().get(0).couleur(), sauvegarde.joueurs().get(1).couleur(), sauvegarde.solo());
    }

    private static void afficherPartie(Stage stage, Musique musique, Partie partie, Couleur couleurJoueur1,
            Couleur couleurJoueur2, boolean solo) throws IOException {
        FXMLLoader loader = new FXMLLoader(Navigation.class.getResource("/ihm/Latice.fxml"));
        Parent root = loader.load();

        ControleurDeJeu controleurDeJeu = loader.getController();
        controleurDeJeu.demarrer(partie, musique, couleurJoueur1, couleurJoueur2, solo);

        stage.setTitle("Latice - En cours");
        stage.getIcons().setAll(new Image(Navigation.class.getResourceAsStream("/images/img_Latice/img/icone.png")));
        stage.setScene(new Scene(root));
        stage.setOnCloseRequest(event -> musique.arreterMusique());
        stage.setResizable(false);
        stage.show();
    }
}
