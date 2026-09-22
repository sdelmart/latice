package latice.controleur;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import latice.enumeration.Couleur;
import latice.metier.Arbitre;
import latice.metier.Joueur;
import latice.metier.Plateau;

/** Points d'entrée réutilisés pour ouvrir l'accueil ou démarrer une partie depuis n'importe où
 * (lancement de l'appli, ou boutons "Rejouer"/"Menu principal" de l'écran de fin de partie). */
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
        FXMLLoader loader = new FXMLLoader(Navigation.class.getResource("/ihm/Latice.fxml"));
        Parent root = loader.load();

        ControleurDeJeu controleurDeJeu = loader.getController();
        controleurDeJeu.mettreMusique(musique);

        Joueur joueur1 = new Joueur(nomJoueur1);
        Joueur joueur2 = new Joueur(nomJoueur2);
        Arbitre arbitre = new Arbitre(joueur1, joueur2);
        controleurDeJeu.setArbitre(arbitre);
        controleurDeJeu.nomsJoueurs(nomJoueur1, nomJoueur2);
        controleurDeJeu.definirCouleursJoueurs(couleurJoueur1, couleurJoueur2);
        controleurDeJeu.definirModeSolo(solo);

        Plateau plateau = new Plateau(9);
        controleurDeJeu.initialiserPlateau(plateau);
        controleurDeJeu.initialiserRackJoueurs(joueur1, joueur2);
        controleurDeJeu.initialiserDragAndDrop();
        controleurDeJeu.mettreAJourTour();
        controleurDeJeu.initialiserTours();

        stage.setTitle("Latice - En cours");
        stage.getIcons().setAll(new Image(Navigation.class.getResourceAsStream("/images/img_Latice/img/icone.png")));
        stage.setScene(new Scene(root));
        stage.setOnCloseRequest(event -> musique.arreterMusique());
        stage.setResizable(false);
        stage.show();
    }
}
