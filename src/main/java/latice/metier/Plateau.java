package latice.metier;
import java.util.HashMap;
import java.util.Map;


public class Plateau {

    // Enumération des types de cases possibles : NORMALE, SOLEIL, LUNE.
    public enum TypeCase {
        NORMALE(),
        SOLEIL(),
        LUNE();
    }
    
    private Map<Position, Tuile> tuilesPlacees = new HashMap<>();
    private int taille; // Taille du plateau (nombre de cases par côté).
    private TypeCase[][] cases; // Tableau 2D représentant les cases du plateau.

    
    public Plateau(int taille) {
        this.taille = taille;
        this.cases = new TypeCase[taille][taille];
        initialiserPlateau(); // Initialise les cases du plateau.
    }

    // Initialise les cases du plateau avec des types prédéfinis.
    private void initialiserPlateau() {
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                cases[i][j] = TypeCase.NORMALE; // Par défaut, toutes les cases sont NORMALE.
            }
        }
        // Définition des cases spéciales (SOLEIL et LUNE).
        cases[0][0] = TypeCase.SOLEIL;
        cases[0][4] = TypeCase.SOLEIL;
        cases[0][8] = TypeCase.SOLEIL;
        cases[1][1] = TypeCase.SOLEIL;
        cases[1][7] = TypeCase.SOLEIL;
        cases[2][2] = TypeCase.SOLEIL;
        cases[2][6] = TypeCase.SOLEIL;
        cases[4][0] = TypeCase.SOLEIL;
        cases[4][4] = TypeCase.LUNE;
        cases[4][8] = TypeCase.SOLEIL;
        cases[6][2] = TypeCase.SOLEIL;
        cases[6][6] = TypeCase.SOLEIL;
        cases[7][1] = TypeCase.SOLEIL;
        cases[7][7] = TypeCase.SOLEIL;
        cases[8][0] = TypeCase.SOLEIL;
        cases[8][4] = TypeCase.SOLEIL;
        cases[8][8] = TypeCase.SOLEIL;
    }

    // Retourne la taille du plateau.
    public int tailleplateau() {
        return taille;
    }

    // Retourne le type de case à une position donnée.
    public TypeCase casePlateau(int i, int j) {
        return cases[i][j];
    }
    
    public boolean placerTuile(Position pos, Tuile tuile) {
        if (tuilesPlacees.containsKey(pos)) return false; // Already occupied
        tuilesPlacees.put(pos, tuile);
        return true;
    }
    
    public Tuile obtenirTuile(Position pos) {
        return tuilesPlacees.get(pos);
    }
    
    public Map<Position, Tuile> obtenirTuilesPlacees() {
        return tuilesPlacees;
    }


    // Affiche le plateau dans la console les symboles associés
    public void afficher() {
        System.out.print("    ");
        for (int col = 0; col < taille; col++) {
            System.out.printf(" %-3d", col);
        }
        System.out.println();

        System.out.print("   ");
        for (int col = 0; col < taille; col++) {
            System.out.print("----");
        }
        System.out.println("-");

        for (int ligne = 0; ligne < taille; ligne++) {
            System.out.printf(" %-2d ", ligne);
            for (int col = 0; col < taille; col++) {
                Position pos = new Position(col, ligne);
                Tuile tuile = obtenirTuile(pos);
                String contenu;
                if (tuile != null) {
                    contenu = tuile.convertirEmoji();
                } else if (cases[ligne][col] == TypeCase.SOLEIL) {
                    contenu = "☀️";
                } else if (cases[ligne][col] == TypeCase.LUNE) {
                    contenu = "🌘";
                } else {
                    contenu = "  ";
                }
                System.out.printf("|%-3s", contenu);
            }
            System.out.println("|");

            System.out.print("   ");
            for (int col = 0; col < taille; col++) {
                System.out.print("----");
            }
            System.out.println("-");
        }
    }



}
