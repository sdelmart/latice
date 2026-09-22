package latice.controleur;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javafx.scene.media.AudioClip;

/**
 * Effets sonores courts (clic, pose de tuile...), indépendants de la musique de fond
 * qui reste gérée par {@link Musique} (AudioClip est adapté aux sons brefs, contrairement
 * à MediaPlayer qui est fait pour une piste qui boucle).
 */
public final class EffetsSonores {

    public enum Effet {
        CLIC("/sons/clic.wav"),
        POSE_TUILE("/sons/pose_tuile.wav"),
        VICTOIRE("/sons/victoire.wav"),
        ERREUR("/sons/erreur.wav"),
        CHANGEMENT_TOUR("/sons/changement_tour.wav");

        private final String chemin;

        Effet(String chemin) {
            this.chemin = chemin;
        }
    }

    private static final Preferences PREFS = Preferences.userNodeForPackage(EffetsSonores.class);
    private static final String CLE_ACTIFS = "effets_sonores_actifs";

    private static final Map<Effet, AudioClip> CLIPS = new EnumMap<>(Effet.class);
    private static boolean actifs = PREFS.getBoolean(CLE_ACTIFS, true);

    private EffetsSonores() {
    }

    public static void jouer(Effet effet) {
        if (!actifs) {
            return;
        }
        AudioClip clip = CLIPS.computeIfAbsent(effet, EffetsSonores::charger);
        if (clip != null) {
            clip.play();
        }
    }

    public static void definirActifs(boolean valeur) {
        actifs = valeur;
        PREFS.putBoolean(CLE_ACTIFS, valeur);
    }

    public static boolean sontActifs() {
        return actifs;
    }

    private static AudioClip charger(Effet effet) {
        URL url = EffetsSonores.class.getResource(effet.chemin);
        if (url == null) {
            System.err.println("Effet sonore introuvable : " + effet.chemin);
            return null;
        }
        return new AudioClip(url.toExternalForm());
    }
}
