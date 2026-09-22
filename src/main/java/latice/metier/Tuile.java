package latice.metier;

import latice.enumeration.Couleur;
import latice.enumeration.Symbole;

public class Tuile {
	
    private Couleur couleur;
    private Symbole symbole;

    public Tuile(Couleur couleur, Symbole symbole) {
        this.couleur = couleur;
        this.symbole = symbole;
    }

    public String cheminImage() {
        return "/images/img_Latice/img/" + symbole.name().toLowerCase()  + "_" + couleur.name().toLowerCase()  + ".png";
    }

    // Retourne une représentation textuelle de la tuile
    @Override
    public String toString() {
        return symbole + " " + couleur;
    }
    
    public Couleur obtenirCouleur() {
    	return couleur;
	}
    
	public Symbole obtenirSymbole() {
	    return symbole;
	}
	
	public String convertirEmoji() {
	    String emoji = "";
	    switch (symbole) {
	        case FLEUR: emoji = "\u2740"; break;
	        case OISEAU: emoji = "\uD83D\uDC26"; break;
	        case TORTUE: emoji = "\uD80C\uDD89"; break;
	        case DAUPHIN: emoji = "\uD83D\uDC2C"; break;
	        case PLUME: emoji = "\uD83E\uDEB6"; break;
	        case LEZARD: emoji = "\uD80C\uDD88"; break;
	    }
	    String codeCouleur = "";
	    switch (couleur) {
	        case ROUGE: codeCouleur = "\u001B[31m"; break;  
	        case JAUNE: codeCouleur = "\u001B[33m"; break;  
	        case VERT: codeCouleur = "\u001B[32m"; break;  
	        case BLEU: codeCouleur = "\u001B[34m"; break;    
	        case CYAN: codeCouleur = "\u001B[36m"; break;   
	        case MAGENTA: codeCouleur = "\u001B[35m"; break; 
	    }
	    String reset = "\u001B[0m";
	    return " " + codeCouleur + emoji + reset +" ";
	}
}
