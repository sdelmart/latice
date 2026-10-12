package latice.metier;

import java.util.Map;

/**
 * Règles de Latice, en un seul endroit : l'interface graphique, l'IA et la version console
 * s'appuient toutes sur ces méthodes au lieu d'en maintenir chacune une copie.
 */
public final class Regles {

    public static final int TAILLE_PLATEAU = 9;
    public static final int TAILLE_RACK = 5;
    public static final int TOURS_PAR_JOUEUR = 10;
    public static final int COUT_ACTION_SUPPLEMENTAIRE = 2;

    private static final int[][] VOISINS = { {0, 1}, {1, 0}, {0, -1}, {-1, 0} };

    private Regles() {
    }

    public static Position centre(Plateau plateau) {
        return new Position(plateau.tailleplateau() / 2, plateau.tailleplateau() / 2);
    }

    public static boolean estDansLePlateau(Plateau plateau, Position pos) {
        return pos.posX() >= 0 && pos.posX() < plateau.tailleplateau()
                && pos.posY() >= 0 && pos.posY() < plateau.tailleplateau();
    }

    public static boolean sontCompatibles(Tuile a, Tuile b) {
        return a.obtenirCouleur() == b.obtenirCouleur() || a.obtenirSymbole() == b.obtenirSymbole();
    }

    /**
     * Une tuile peut être posée sur une case libre du plateau si :
     * <ul>
     *   <li>c'est la toute première tuile, et elle est posée au centre ;</li>
     *   <li>sinon, elle touche (par un côté) au moins une tuile déjà posée, et elle est compatible
     *       (même couleur OU même symbole) avec <b>chacune</b> des tuiles qu'elle touche.</li>
     * </ul>
     */
    public static boolean estPlacementValide(Plateau plateau, Map<Position, Tuile> tuilesPlacees,
            Position pos, Tuile tuile) {
        if (!estDansLePlateau(plateau, pos) || tuilesPlacees.containsKey(pos)) {
            return false;
        }
        if (tuilesPlacees.isEmpty()) {
            return pos.equals(centre(plateau));
        }
        boolean toucheUneTuile = false;
        for (int[] d : VOISINS) {
            Tuile voisine = tuilesPlacees.get(new Position(pos.posX() + d[0], pos.posY() + d[1]));
            if (voisine != null) {
                toucheUneTuile = true;
                if (!sontCompatibles(voisine, tuile)) {
                    return false;
                }
            }
        }
        return toucheUneTuile;
    }

    /**
     * Points gagnés en posant une tuile en {@code pos} : 1 point pour 2 voisines (3 tuiles associées),
     * 2 pour 3 voisines, 4 pour 4 voisines, plus 2 points si la case est un soleil.
     */
    public static int pointsGagnes(Plateau plateau, Map<Position, Tuile> tuilesPlacees, Position pos) {
        int voisines = 0;
        for (int[] d : VOISINS) {
            if (tuilesPlacees.containsKey(new Position(pos.posX() + d[0], pos.posY() + d[1]))) {
                voisines++;
            }
        }
        int points = switch (voisines) {
            case 2 -> 1;
            case 3 -> 2;
            case 4 -> 4;
            default -> 0;
        };
        if (plateau.casePlateau(pos.posY(), pos.posX()) == Plateau.TypeCase.SOLEIL) {
            points += 2;
        }
        return points;
    }

    public static boolean aVideSonRackEtSaPioche(Joueur joueur) {
        return joueur.rack().taille() == 0 && (joueur.pioche() == null || joueur.pioche().estVide());
    }
}
