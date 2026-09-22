package latice.controleur;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ControleurParametres {

	@FXML
    private Slider sliderVolume;
    @FXML
    private ComboBox<PisteMusicale> choixMusique;

    private Musique musique;

    public void mettreMusique(Musique musique) {
        this.musique = musique;
        if (sliderVolume != null && musique != null) {
            definirControles();
        }
    }

    @FXML
    private void initialize() {
        choixMusique.getItems().setAll(PisteMusicale.values());
        if (musique != null && sliderVolume != null) {
            definirControles();
        }
    }

    private void definirControles() {
        sliderVolume.setValue(musique.recupereVolume());
        sliderVolume.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (this.musique != null) {
                this.musique.definirVolume(newVal.doubleValue());
            }
        });

        choixMusique.setValue(musique.pisteActuelle());
        choixMusique.valueProperty().addListener((obs, oldVal, nouvellePiste) -> {
            if (this.musique != null && nouvellePiste != null) {
                this.musique.changerPiste(nouvellePiste);
            }
        });
    }

    @FXML
    private void ouvrirRegles() {
        afficherRegles();
    }

    public static void afficherRegles() {
        TextArea zoneRegles = new TextArea();
        zoneRegles.setWrapText(true);
        zoneRegles.setEditable(false);
        zoneRegles.getStyleClass().add("text-area-ocean");
        zoneRegles.setText(
        	    "Règles du jeu Latice :\n\n"
        	    + "• Nombre de joueurs :\n"
        	    + "  - Le jeu se joue uniquement à 2 joueurs dans cette version simplifiée.\n\n"
        	    + "• Comment gagner des points :\n"
        	    + "  - On gagne des points lorsqu’un joueur pose une tuile, en fonction des tuiles adjacentes (non diagonales).\n"
        	    + "  - 3 tuiles associées font gagner 1 point.\n"
        	    + "  - 4 tuiles associées font gagner 2 points.\n"
        	    + "  - 5 tuiles associées font gagner 4 points.\n"
        	    + "  - Si une tuile est posée sur une case soleil, un bonus de 2 points est accordé.\n"
        	    + "  - Il n’y a pas de limite de points.\n\n"
        	    + "• Actions disponibles :\n"
        	    + "  1. Jouer une tuile.\n"
        	    + "  2. Acheter une action supplémentaire (permet de poser une tuile supplémentaire par action).\n"
        	    + "  3. Échanger tout le rack (l’action échange directement tout le rack, pas de choix possible).\n"
        	    + "  4. Passer son tour.\n\n"
        	    + "• Coût des actions :\n"
        	    + "  - L’achat d’une action supplémentaire coûte 2 points.\n\n"
        	    + "• Déroulement du tour :\n"
        	    + "  - À chaque tour, le joueur peut effectuer une des actions ci-dessus.\n"
        	    + "  - Le placement d’une tuile doit respecter les règles énoncé ci-dessus.\n"
        	    + "  - Les points sont gagnés selon où est posé la tuile et dépensés pour acheter des actions.\n\n"
        	    + "• Fin de partie :\n"
        	    + "  - La partie est terminée si un joueur vide son rack et sa pioche : il a alors gagné.\n"
        	    + "  - Sinon, au bout de 10 cycles = 20 tours (1 cycle = 2 tours), le gagnant est celui qui aura posé le plus de tuiles sur le plateau.\n"
        	);
        
        zoneRegles.setPrefSize(440, 320);
        ScrollPane scrollPane = new ScrollPane(zoneRegles);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Stage reglesStage = new Stage();
        Button boutonFermer = new Button("Fermer");
        boutonFermer.getStyleClass().add("btn-secondary");
        boutonFermer.setOnAction(e -> reglesStage.close());

        VBox racine = new VBox(14, scrollPane, boutonFermer);
        racine.setAlignment(javafx.geometry.Pos.CENTER);
        racine.setPadding(new Insets(20));
        racine.getStyleClass().add("background-ocean");

        Scene scene = new Scene(racine);
        scene.getStylesheets().add(ControleurParametres.class.getResource("/css/theme.css").toExternalForm());
        reglesStage.setTitle("Règles du jeu");
        reglesStage.getIcons().add(new Image(ControleurParametres.class.getResourceAsStream("/images/img_Latice/img/icone.png")));
        reglesStage.setScene(scene);
        reglesStage.setResizable(false);
        reglesStage.show();
    }

    @FXML
    private void fermerFenetre() {
        ((Stage) sliderVolume.getScene().getWindow()).close();
    }
}
