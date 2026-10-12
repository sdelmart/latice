package latice.metier;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import latice.enumeration.Couleur;
import latice.enumeration.Symbole;

public class Pioche {
    private List<Tuile> tuiles; // Liste des tuiles dans la pioche
    private int index; // Index pour suivre les tuiles piochées

    
    public Pioche() {
        creerPioche();
    }

    // Constructeur qui initialise une pioche avec une liste de tuiles
    public Pioche(List<Tuile> tuiles) {
        this.tuiles = new ArrayList<>(tuiles); 
        this.index = 0;
    }

    // Reconstitue une pioche en cours de partie (liste complète + nombre de tuiles déjà piochées)
    public Pioche(List<Tuile> tuiles, int index) {
        this.tuiles = new ArrayList<>(tuiles);
        this.index = Math.max(0, Math.min(index, tuiles.size()));
    }

    // Nombre de tuiles déjà piochées dans la liste
    public int indexCourant() {
        return index;
    }

    // Crée une pioche avec toutes les combinaisons de couleurs et symboles
    public void creerPioche() {
        tuiles = new ArrayList<>();
        Couleur[] couleurs = Couleur.values();
        Symbole[] symboles = Symbole.values();
        for (Symbole symbole : symboles) {
            for (Couleur couleur : couleurs) {
                if (tuiles.size() < 36) {
                    tuiles.add(new Tuile(couleur, symbole));
                }
            }
        }
        tuiles.addAll(new ArrayList<>(tuiles)); // Double les tuiles
        melanger();
    }

    // Mélange les tuiles de la pioche
    public void melanger() {
        Collections.shuffle(tuiles);
    }

    // Retourne la liste des tuiles
    public List<Tuile> avoirTuiles() {
        return tuiles;
    }

    // Pioche une tuile de la pioche
    public Tuile piocher() {
        if (estVide()) {
            return null;
        }
        return tuiles.get(index++);
    }

    // Vérifie si la pioche est vide
    public boolean estVide() {
        return index >= tuiles.size();
    }
    
    // Renvoie la taille de la pioche
    public int taille() {
        return tuiles.size();
    }
}
