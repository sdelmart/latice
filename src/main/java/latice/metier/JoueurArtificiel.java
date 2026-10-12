package latice.metier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Logique de décision de l'IA (aucune dépendance JavaFX) : pour chaque tuile de son rack
 * et chaque case du plateau, garde les coups autorisés par {@link Regles} (les mêmes règles que pour
 * un joueur humain) et retient celui qui rapporte le plus de points.
 */
public final class JoueurArtificiel {

    public record Coup(Position position, Tuile tuile, int score) {
    }

    private JoueurArtificiel() {
    }

    public static Optional<Coup> choisirCoup(Plateau plateau, List<Tuile> tuilesRack,
            Map<Position, Tuile> tuilesPlacees) {

        Coup meilleurCoup = null;
        int taille = plateau.tailleplateau();

        for (Tuile tuile : tuilesRack) {
            for (int ligne = 0; ligne < taille; ligne++) {
                for (int col = 0; col < taille; col++) {
                    Position pos = new Position(col, ligne);
                    if (!Regles.estPlacementValide(plateau, tuilesPlacees, pos, tuile)) {
                        continue;
                    }
                    int score = Regles.pointsGagnes(plateau, tuilesPlacees, pos);
                    if (meilleurCoup == null || score > meilleurCoup.score()) {
                        meilleurCoup = new Coup(pos, tuile, score);
                    }
                }
            }
        }
        return Optional.ofNullable(meilleurCoup);
    }
}
