package latice.controleur;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Region;

public class ControleurChargement {
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Region fondRegion;

    private Runnable chargementTermine;

    public void initialize() {
        ThemeVisuel.lierFond(fondRegion);
        // Animation de la progress bar
        AnimationTimer temps = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (progressBar.getProgress() < 1.0) {
                    progressBar.setProgress(progressBar.getProgress() + 0.01);
                } else {
                    this.stop();
                    if (chargementTermine != null) {
                        chargementTermine.run();
                    }
                }
            }
        };
        temps.start();
    }
    
    public void mettreSurChargementTermine(Runnable callback) {
        this.chargementTermine = callback;
    }
}