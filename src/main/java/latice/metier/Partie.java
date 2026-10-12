package latice.metier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import latice.enumeration.Couleur;

/**
 * État et déroulement d'une partie à deux joueurs, sans aucune dépendance à JavaFX : l'interface
 * graphique, l'IA et les tests pilotent tous la partie par cette classe.
 *
 * <p>Un tour : le joueur peut acheter des actions, poser des tuiles (une par action disponible),
 * puis termine son tour ({@link #finirTour()}) ; il peut aussi passer ou échanger son rack, ce qui
 * termine directement le tour.
 */
public class Partie {

    private final Arbitre arbitre;
    private final Plateau plateau;
    private final int[] toursRestants = { Regles.TOURS_PAR_JOUEUR, Regles.TOURS_PAR_JOUEUR };
    private final int[] tuilesPosees = new int[2];
    private int tuilesPoseesCeTour;

    public Partie(String nomJoueur1, String nomJoueur2) {
        this(new Arbitre(new Joueur(nomJoueur1), new Joueur(nomJoueur2)));
    }

    public Partie(Arbitre arbitre) {
        this.arbitre = arbitre;
        this.plateau = arbitre.plateau;
    }

    // --- Consultation --------------------------------------------------------------------------

    public Plateau plateau() {
        return plateau;
    }

    public Map<Position, Tuile> tuilesPlacees() {
        return plateau.obtenirTuilesPlacees();
    }

    public Joueur joueur1() {
        return arbitre.joueur1;
    }

    public Joueur joueur2() {
        return arbitre.joueur2;
    }

    public Joueur joueurCourant() {
        return arbitre.joueurCourant();
    }

    public int toursRestants(Joueur joueur) {
        return toursRestants[indice(joueur)];
    }

    public int tuilesPosees(Joueur joueur) {
        return tuilesPosees[indice(joueur)];
    }

    public int tuilesPoseesCeTour() {
        return tuilesPoseesCeTour;
    }

    /** Nombre de tuiles que le joueur courant peut encore poser pendant ce tour. */
    public int actionsRestantes() {
        Joueur joueur = joueurCourant();
        return Math.max(0, joueur.nbActions + joueur.nbActionsSup - tuilesPoseesCeTour);
    }

    private int indice(Joueur joueur) {
        if (joueur == arbitre.joueur1) {
            return 0;
        }
        if (joueur == arbitre.joueur2) {
            return 1;
        }
        throw new IllegalArgumentException("Joueur inconnu : " + joueur);
    }

    // --- Actions du tour ------------------------------------------------------------------------

    public boolean peutPlacer(Position pos, Tuile tuile) {
        return !estTerminee()
                && actionsRestantes() > 0
                && Regles.estPlacementValide(plateau, plateau.obtenirTuilesPlacees(), pos, tuile);
    }

    /**
     * Pose une tuile du rack du joueur courant et renvoie les points gagnés.
     *
     * @throws IllegalStateException si le coup n'est pas autorisé (voir {@link #peutPlacer})
     * @throws IllegalArgumentException si la tuile n'est pas dans le rack du joueur courant
     */
    public int placer(Position pos, Tuile tuile) {
        Joueur joueur = joueurCourant();
        if (!joueur.rack().obtenirTuilesRack().contains(tuile)) {
            throw new IllegalArgumentException("La tuile n'est pas dans le rack de " + joueur.nom());
        }
        if (!peutPlacer(pos, tuile)) {
            throw new IllegalStateException("Placement interdit en " + pos.posX() + "," + pos.posY());
        }
        plateau.placerTuile(pos, tuile);
        joueur.rack().retirerTuile(tuile);
        int points = Regles.pointsGagnes(plateau, plateau.obtenirTuilesPlacees(), pos);
        joueur.ajouterPoints(points);

        tuilesPoseesCeTour++;
        tuilesPosees[indice(joueur)]++;
        if (joueur.nbActionsSup > 0 && tuilesPoseesCeTour > joueur.nbActions) {
            joueur.nbActionsSup--;
        }
        completerRack(joueur);
        return points;
    }

    /** Acheter et échanger ne sont possibles qu'avant d'avoir posé une tuile pendant ce tour. */
    public boolean peutAcheterOuEchanger() {
        return !estTerminee() && tuilesPoseesCeTour == 0;
    }

    public boolean acheterActionSupplementaire() {
        Joueur joueur = joueurCourant();
        if (!peutAcheterOuEchanger() || joueur.points() < Regles.COUT_ACTION_SUPPLEMENTAIRE) {
            return false;
        }
        joueur.ajouterPoints(-Regles.COUT_ACTION_SUPPLEMENTAIRE);
        joueur.nbActionsSup++;
        return true;
    }

    /** Remet tout le rack sous la pioche, en pioche un nouveau, et termine le tour. */
    public void echanger() {
        Joueur joueur = joueurCourant();
        if (!peutAcheterOuEchanger() || joueur.pioche() == null) {
            throw new IllegalStateException("Échange impossible");
        }
        joueur.pioche().remettreSousLaPioche(joueur.rack().obtenirTuilesRack());
        joueur.rack().vider();
        completerRack(joueur);
        finirTour();
    }

    /** Termine le tour du joueur courant (après avoir posé, ou pour passer) et donne la main à l'autre. */
    public void finirTour() {
        Joueur joueur = joueurCourant();
        int i = indice(joueur);
        toursRestants[i] = Math.max(0, toursRestants[i] - 1);
        completerRack(joueur);
        tuilesPoseesCeTour = 0;
        arbitre.gererTours();
    }

    private void completerRack(Joueur joueur) {
        while (!joueur.rack().estPlein() && joueur.pioche() != null && !joueur.pioche().estVide()) {
            joueur.rack().ajouterTuile(joueur.pioche().piocher());
        }
    }

    // --- Fin de partie -----------------------------------------------------------------------------

    /** Le joueur qui a vidé son rack et sa pioche gagne immédiatement. */
    public Optional<Joueur> joueurAyantEpuiseSesTuiles() {
        if (Regles.aVideSonRackEtSaPioche(arbitre.joueur1)) {
            return Optional.of(arbitre.joueur1);
        }
        if (Regles.aVideSonRackEtSaPioche(arbitre.joueur2)) {
            return Optional.of(arbitre.joueur2);
        }
        return Optional.empty();
    }

    public boolean estTerminee() {
        return joueurAyantEpuiseSesTuiles().isPresent()
                || (toursRestants[0] == 0 && toursRestants[1] == 0);
    }

    /** Vainqueur : celui qui a épuisé ses tuiles, sinon celui qui en a posé le plus ; vide en cas d'égalité. */
    public Optional<Joueur> vainqueur() {
        Optional<Joueur> epuise = joueurAyantEpuiseSesTuiles();
        if (epuise.isPresent()) {
            return epuise;
        }
        if (tuilesPosees[0] > tuilesPosees[1]) {
            return Optional.of(arbitre.joueur1);
        }
        if (tuilesPosees[1] > tuilesPosees[0]) {
            return Optional.of(arbitre.joueur2);
        }
        return Optional.empty();
    }

    // --- Sauvegarde / reprise ------------------------------------------------------------------------

    public Sauvegarde instantane(boolean solo, Couleur couleurJoueur1, Couleur couleurJoueur2) {
        List<Sauvegarde.EtatJoueur> joueurs = new ArrayList<>();
        Joueur[] ordre = { arbitre.joueur1, arbitre.joueur2 };
        Couleur[] couleurs = { couleurJoueur1, couleurJoueur2 };
        for (int i = 0; i < 2; i++) {
            Joueur j = ordre[i];
            joueurs.add(new Sauvegarde.EtatJoueur(j.nom(), couleurs[i], j.points(), j.nbActionsSup,
                    toursRestants[i], tuilesPosees[i], j.rack().obtenirTuilesRack(),
                    new ArrayList<>(j.pioche().avoirTuiles()), j.pioche().indexCourant()));
        }
        List<Sauvegarde.TuilePosee> posees = new ArrayList<>();
        plateau.obtenirTuilesPlacees().forEach((pos, tuile) -> posees.add(new Sauvegarde.TuilePosee(pos, tuile)));
        int courant = joueurCourant() == arbitre.joueur1 ? 0 : 1;
        return new Sauvegarde(solo, courant, joueurs, posees);
    }

    public static Partie restaurer(Sauvegarde sauvegarde) {
        Sauvegarde.EtatJoueur e1 = sauvegarde.joueurs().get(0);
        Sauvegarde.EtatJoueur e2 = sauvegarde.joueurs().get(1);
        Joueur j1 = reconstituer(e1);
        Joueur j2 = reconstituer(e2);
        Arbitre arbitre = new Arbitre(j1, j2, sauvegarde.joueurCourant() == 0 ? j1 : j2);
        Partie partie = new Partie(arbitre);
        partie.toursRestants[0] = e1.toursRestants();
        partie.toursRestants[1] = e2.toursRestants();
        partie.tuilesPosees[0] = e1.tuilesPosees();
        partie.tuilesPosees[1] = e2.tuilesPosees();
        for (Sauvegarde.TuilePosee t : sauvegarde.plateau()) {
            partie.plateau.placerTuile(t.position(), t.tuile());
        }
        return partie;
    }

    private static Joueur reconstituer(Sauvegarde.EtatJoueur etat) {
        Joueur joueur = new Joueur(etat.nom());
        joueur.definirPoints(etat.points());
        joueur.nbActionsSup = etat.actionsSup();
        etat.rack().forEach(joueur.rack()::ajouterTuile);
        joueur.fairePioche(new Pioche(etat.pioche(), etat.indexPioche()));
        return joueur;
    }
}
