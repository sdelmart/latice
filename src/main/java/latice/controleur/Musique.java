package latice.controleur;

import java.net.URL;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Musique {

    private MediaPlayer mediaPlayer;
    
    
    public MediaPlayer getMediaPlayer() { 
    	return mediaPlayer; 
    	}

    public void jouerMusique(String cheminFichier) {
        try {
            URL mediaUrl = getClass().getResource(cheminFichier);
            if (mediaUrl == null) {
                throw new IllegalArgumentException("Fichier audio introuvable : " + cheminFichier);
            }
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
            Media media = new Media(mediaUrl.toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(0.5);
            mediaPlayer.setOnError(() -> System.err.println("Erreur MediaPlayer : " + mediaPlayer.getError().getMessage()));

            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Erreur lors de la lecture de la musique : " + e.getMessage());
        }
    }

    public void arreterMusique() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }
    
    public void definirVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

    public double recupereVolume() {
        return mediaPlayer != null ? mediaPlayer.getVolume() : 0.5;
    }
}
