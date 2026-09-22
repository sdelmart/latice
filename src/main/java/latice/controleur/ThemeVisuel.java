package latice.controleur;

import java.util.prefs.Preferences;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Region;

/**
 * Fond d'écran choisi par l'utilisateur : sauvegardé entre les lancements et partagé
 * en direct entre toutes les fenêtres ouvertes (accueil, jeu, paramètres...).
 */
public final class ThemeVisuel {

    private static final Preferences PREFS = Preferences.userNodeForPackage(ThemeVisuel.class);
    private static final String CLE_FOND = "fond_ecran";
    private static final ObjectProperty<FondEcran> fondActuel = new SimpleObjectProperty<>(chargerFondSauvegarde());

    private ThemeVisuel() {
    }

    public static ObjectProperty<FondEcran> fondActuelProperty() {
        return fondActuel;
    }

    public static FondEcran fondActuel() {
        return fondActuel.get();
    }

    public static void definirFond(FondEcran fond) {
        fondActuel.set(fond);
        PREFS.put(CLE_FOND, fond.name());
    }

    /** Applique le fond actuel à une Region "calque de fond", et la tient à jour si le choix change ailleurs. */
    public static void lierFond(Region regionFond) {
        appliquer(regionFond, fondActuel());
        fondActuelProperty().addListener((obs, ancien, nouveau) -> appliquer(regionFond, nouveau));
    }

    private static void appliquer(Region regionFond, FondEcran fond) {
        for (FondEcran f : FondEcran.values()) {
            regionFond.getStyleClass().remove(f.classeCss());
        }
        regionFond.getStyleClass().add(fond.classeCss());
    }

    private static FondEcran chargerFondSauvegarde() {
        String nom = PREFS.get(CLE_FOND, FondEcran.parDefaut().name());
        try {
            return FondEcran.valueOf(nom);
        } catch (IllegalArgumentException e) {
            return FondEcran.parDefaut();
        }
    }
}
