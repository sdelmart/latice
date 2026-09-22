package latice.controleur;

import java.net.URL;
import java.util.prefs.Preferences;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Musique {

    private static final Preferences PREFS = Preferences.userNodeForPackage(Musique.class);
    private static final String CLE_PISTE = "piste_selectionnee";
    private static final String CLE_VOLUME = "volume";

    private MediaPlayer mediaPlayer;
    private PisteMusicale pisteActuelle = PisteMusicale.parDefaut();

    public MediaPlayer getMediaPlayer() {
    	return mediaPlayer;
    	}

    /** Relance la dernière musique/volume choisis par l'utilisateur (ou les valeurs par défaut au premier lancement). */
    public void demarrerAvecPreferences() {
        String cheminSauvegarde = PREFS.get(CLE_PISTE, PisteMusicale.parDefaut().chemin());
        double volumeSauvegarde = PREFS.getDouble(CLE_VOLUME, 0.5);
        pisteActuelle = PisteMusicale.depuisChemin(cheminSauvegarde);
        jouerMusique(pisteActuelle.chemin());
        definirVolume(volumeSauvegarde);
    }

    /** Change la piste en cours sans interrompre le volume choisi, et retient le choix pour la prochaine fois. */
    public void changerPiste(PisteMusicale piste) {
        double volumeActuel = recupereVolume();
        this.pisteActuelle = piste;
        jouerMusique(piste.chemin());
        definirVolume(volumeActuel);
        PREFS.put(CLE_PISTE, piste.chemin());
    }

    public PisteMusicale pisteActuelle() {
        return pisteActuelle;
    }

    public void jouerMusique(String cheminFichier) {
        try {
            URL mediaUrl = getClass().getResource(cheminFichier);
            if (mediaUrl == null) {
                throw new IllegalArgumentException("Fichier audio introuvable : " + cheminFichier);
            }
            double volumePrecedent = recupereVolume();
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
            Media media = new Media(mediaUrl.toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.setVolume(volumePrecedent);
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
        PREFS.putDouble(CLE_VOLUME, volume);
    }

    public double recupereVolume() {
        return mediaPlayer != null ? mediaPlayer.getVolume() : PREFS.getDouble(CLE_VOLUME, 0.5);
    }
}
