package latice.controleur;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ControleurParametres {

	@FXML
    private Slider sliderVolume;

    private Musique musique;
    
    public void mettreMusique(Musique musique) {
        this.musique = musique;
        if (sliderVolume != null && musique != null) {
            definirSlider();
        }
    }

    @FXML
    private void initialize() {
        if (musique != null && sliderVolume != null) {
            definirSlider();
        }
    }

    private void definirSlider() {
        sliderVolume.setValue(musique.recupereVolume());
        sliderVolume.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (this.musique != null) {
                this.musique.definirVolume(newVal.doubleValue());
            }
        });
    }
    
    @FXML
    private void ouvrirRegles() {
        TextArea zoneRegles = new TextArea();
        zoneRegles.setWrapText(true);
        zoneRegles.setEditable(false);
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
        
        zoneRegles.setPrefSize(400, 300);
        ScrollPane scrollPane = new ScrollPane(zoneRegles);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Scene scene = new Scene(scrollPane);
        Stage reglesStage = new Stage();
        reglesStage.setTitle("Règles du jeu");
        reglesStage.setScene(scene);
        reglesStage.show();
    }

    @FXML
    private void fermerFenetre() {
        ((Stage) sliderVolume.getScene().getWindow()).close();
    }
}
