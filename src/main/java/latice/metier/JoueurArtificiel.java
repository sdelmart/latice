package latice.metier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Logique de décision de l'IA (aucune dépendance JavaFX) : pour chaque tuile de son rack
 * et chaque position valide du plateau, calcule le score obtenu et retient le meilleur coup.
 */
public final class JoueurArtificiel {

    public record Coup(Position position, Tuile tuile, int score) {
    }

    private JoueurArtificiel() {
    }

    public static Optional<Coup> choisirCoup(Plateau plateau, List<Tuile> tuilesRack,
            Map<Position, Tuile> tuilesPlacees, boolean premiereTuile) {

        Coup meilleurCoup = null;
        int taille = plateau.tailleplateau();

        for (Tuile tuile : tuilesRack) {
            for (int ligne = 0; ligne < taille; ligne++) {
                for (int col = 0; col < taille; col++) {
                    Position pos = new Position(col, ligne);
                    if (tuilesPlacees.containsKey(pos)) {
                        continue;
                    }
                    boolean valide;
                    if (!premiereTuile) {
                        valide = col == taille / 2 && ligne == taille / 2;
                    } else {
                        valide = estPlacementValide(pos, tuile, tuilesPlacees);
                    }
                    if (!valide) {
                        continue;
                    }
                    int score = calculerScore(plateau, pos, tuilesPlacees);
                    if (meilleurCoup == null || score > meilleurCoup.score()) {
                        meilleurCoup = new Coup(pos, tuile, score);
                    }
                }
            }
        }
        return Optional.ofNullable(meilleurCoup);
    }

    private static boolean estPlacementValide(Position pos, Tuile tuile, Map<Position, Tuile> tuilesPlacees) {
        int[][] directions = { {0, 1}, {1, 0}, {0, -1}, {-1, 0} };
        boolean adjacent = false;
        boolean valide = false;
        for (int[] d : directions) {
            Tuile voisine = tuilesPlacees.get(new Position(pos.posX() + d[0], pos.posY() + d[1]));
            if (voisine != null) {
                adjacent = true;
                if (voisine.obtenirCouleur() == tuile.obtenirCouleur() || voisine.obtenirSymbole() == tuile.obtenirSymbole()) {
                    valide = true;
                }
            }
        }
        return adjacent && valide;
    }

    private static int calculerScore(Plateau plateau, Position pos, Map<Position, Tuile> tuilesPlacees) {
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        int nbAdjacents = 0;
        for (int i = 0; i < 4; i++) {
            if (tuilesPlacees.containsKey(new Position(pos.posX() + dx[i], pos.posY() + dy[i]))) {
                nbAdjacents++;
            }
        }
        int score = switch (nbAdjacents) {
            case 2 -> 1;
            case 3 -> 2;
            case 4 -> 4;
            default -> 0;
        };
        if (plateau.casePlateau(pos.posY(), pos.posX()) == Plateau.TypeCase.SOLEIL) {
            score += 2;
        }
        return score;
    }
}
