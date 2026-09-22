package latice.controleur;

import java.util.EnumMap;
import java.util.Map;

import latice.enumeration.Couleur;

/** Correspondance entre les couleurs de jeu (tuiles) et leurs teintes à l'écran. */
public final class CouleurUI {

    private static final Map<Couleur, String> HEX = new EnumMap<>(Couleur.class);
    static {
        HEX.put(Couleur.ROUGE, "#e0463f");
        HEX.put(Couleur.JAUNE, "#e8c14b");
        HEX.put(Couleur.VERT, "#4caf6d");
        HEX.put(Couleur.BLEU, "#3d6fe0");
        HEX.put(Couleur.CYAN, "#3ec7c2");
        HEX.put(Couleur.MAGENTA, "#c750b0");
    }

    private CouleurUI() {
    }

    public static String hex(Couleur couleur) {
        return HEX.getOrDefault(couleur, "#f3ecd9");
    }
}
