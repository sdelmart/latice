package latice.metier;


import java.util.ArrayList;
import java.util.List;

public class Rack {
	
    private final List<Tuile> tuiles = new ArrayList<>(); // Liste des tuiles dans le rack
    private static final int NBTUILES = 5; // Nombre maximum de tuiles dans le rack

    // Ajoute une tuile au rack si ce dernier n'est pas plein
    public boolean ajouterTuile(Tuile tuile) {
        if (tuiles.size() < NBTUILES) {
            tuiles.add(tuile);
            return true;
        }
        return false;
    }

    // Retourne les tuiles du rack
    public List<Tuile> obtenirTuilesRack() {
        return new ArrayList<>(tuiles);
    }

    // Vérifie si le rack est plein
    public boolean estPlein() {
        return tuiles.size() == NBTUILES;
    }

    // Vide le rack
    public void vider() {
        tuiles.clear();
    }
    
    public int taille() {
        return tuiles.size();
    }
    
    public boolean retirerTuile(Tuile tuile) {
        return tuiles.remove(tuile);
    }
}
