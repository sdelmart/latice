package latice.metier;

import java.util.Random;

public class Arbitre {
    public final Joueur joueur1;
    public final Joueur joueur2;
    private Joueur joueurCourant;
    private final Pioche pioche;
    private final Random random = new Random();
    public Plateau plateau = new Plateau(9);

    public Arbitre(Joueur joueur1, Joueur joueur2) {
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        this.pioche = new Pioche();
        initialiserPartie();
    }

    public Arbitre(String nomJoueur1, String nomJoueur2) {
        this(new Joueur(nomJoueur1), new Joueur(nomJoueur2));
    }

    public void initialiserPartie() {
        diviserEtDonnerPioches();
        distribuerTuilesAuxRacks(joueur1);
        distribuerTuilesAuxRacks(joueur2);
        choisirJoueurAleatoire();
    }

    private void diviserEtDonnerPioches() {
        int taille = pioche.avoirTuiles().size() / 2;
        joueur1.fairePioche(new Pioche(pioche.avoirTuiles().subList(0, taille)));
        joueur2.fairePioche(new Pioche(pioche.avoirTuiles().subList(taille, pioche.avoirTuiles().size())));
    }

    private void distribuerTuilesAuxRacks(Joueur joueur) {
        while (!joueur.rack().estPlein() && !joueur.pioche().estVide()) {
            joueur.rack().ajouterTuile(joueur.pioche().piocher());
        }
    }

    private void choisirJoueurAleatoire() {
        if (random.nextBoolean()) {
            joueurCourant = joueur1;
        } else {
            joueurCourant = joueur2;
        }
        System.out.println("Le joueur qui commence est : " + joueurCourant.nom());
    }

    public Joueur joueurCourant() {
        return joueurCourant;
    }

    public void gererTours() {
        joueurCourant = (joueurCourant == joueur1) ? joueur2 : joueur1;
    }
}
