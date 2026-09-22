package latice.metier;


public class Joueur {
    private final String nom; 
    private final Rack rack;
    private Pioche pioche; 
    public Integer points = 0; // Points du joueur, initialisés à 0
    public int nbActionsSup = 0; // Nombre d'actions supplémentaires achetées
	public int nbActions = 1; // Nombre d'actions par tour, initialisé à 1

    public Joueur(String nom) {
        this.nom = nom;
        this.rack = new Rack();
    }

    // Retourne le nom du joueur
    public String nom() {
        return nom;
    }

    // Retourne le rack du joueur
    public Rack rack() {
        return rack;
    }

    // Retourne la pioche du joueur
    public Pioche pioche() {
        return pioche;
    }

    // Attribue une pioche au joueur
    public void fairePioche(Pioche pioche) {
        this.pioche = pioche;
    }
    
    // Retourne les points du joueur
    public Integer points() {
        return points;
    }

    // Ajoute des points au joueur
    public void ajouterPoints(Integer points) {
        this.points += points;
    }

    
}
