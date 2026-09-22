package main;

import java.util.*;

import latice.metier.Arbitre;
import latice.metier.Joueur;
import latice.metier.Plateau;
import latice.metier.Position;
import latice.metier.Tuile;

public class LaticeApplicationConsole {

    private final Scanner lecteur = new Scanner(System.in);
    private Arbitre arbitre;
    private Plateau plateau;
    private int nombreToursJoueur1 = 0;
    private int nombreToursJoueur2 = 0;
    private int tuilesPoseesJoueur1 = 0;
    private int tuilesPoseesJoueur2 = 0;
    private static final int NOMBRE_MAX_TOURS = 10;
    private static final String ROUGE = "\u001B[31m";
    private static final String RESET = "\u001B[0m";
    private static final String MESSAGE = "------------------------------------------------------------";

    public static void main(String[] args) {
        new LaticeApplicationConsole().lancer();
    }

    public void lancer() {
        dev();
        initialiserPartie();
        boucleDeJeu();
        afficherFinDePartie();
        lecteur.close();
    }

    private void dev() {
        System.out.println(MESSAGE);
        System.out.println("         Bienvenue dans le jeu Latice Console !");
        System.out.println(MESSAGE);
        System.out.println("Développé par Scotty, Shirine, Eloïse, Camille et Owen");
        System.out.println(MESSAGE);
    }

    private void initialiserPartie() {
        System.out.print("Nom du joueur 1: ");
        String nomJoueur1 = lecteur.nextLine().trim();
        System.out.print("Nom du joueur 2: ");
        String nomJoueur2 = lecteur.nextLine().trim();
        arbitre = new Arbitre(nomJoueur1, nomJoueur2);
        plateau = arbitre.plateau;
    }

    private void boucleDeJeu() {
        boolean partieEnCours = true;
        while (partieEnCours) {
            afficherPlateauEtRack();
            if (!gererTourJoueur()) {
                partieEnCours = false;
            }
        }
    }

    private void afficherPlateauEtRack() {
        Joueur joueur = arbitre.joueurCourant();
        System.out.println("\nPlateau :");
        plateau.afficher();
        System.out.println("\nTour de " + joueur.nom() + ", " + joueur.points() + " points, " + joueur.nbActionsSup + " action(s) supplémentaire(s)");
        System.out.print("Votre rack : ");
        List<Tuile> rack = joueur.rack().obtenirTuilesRack();
        for (int i = 0; i < rack.size(); i++) {
            System.out.print("[" + i + ": " + rack.get(i).convertirEmoji() + "] ");
        }
        System.out.println();
    }

    private int menuActions() {
        System.out.println("\n--- Menu ---");
        System.out.println("1. Jouer une tuile");
        System.out.println("2. Passer");
        System.out.println("3. Echanger des tuiles");
        System.out.println("4. Acheter une action supplémentaire");
        System.out.print("Votre choix (1-4): ");
        int choix = -1;
        while (choix < 1 || choix > 4) {
            if (lecteur.hasNextInt()) {
                choix = lecteur.nextInt();
                if (choix < 1 || choix > 4) {
                    System.out.print(ROUGE + "Choix invalide. Réessayez (1-4): " + RESET);
                }
            } else {
                lecteur.next();
                System.out.print(ROUGE + "Veuillez entrer un nombre (1-4): " + RESET);
            }
        }
        lecteur.nextLine();
        return choix;
    }

    private boolean gererTourJoueur() {
        Joueur joueur = arbitre.joueurCourant();

        // Fin de partie si plus de tuiles à jouer
        if (joueur.rack().obtenirTuilesRack().isEmpty() && joueur.pioche().estVide()) {
            return false;
        }

        int actionsDisponibles = joueur.nbActions + joueur.nbActionsSup;
        int tuilesPoseesCeTour = 0;
        boolean actionValide = false;

        while (!actionValide) {
            int choix = menuActions();
            switch (choix) {
                case 1:
                	// Jouer une ou plusieurs tuiles
                    boolean continuer = true;
                    while (tuilesPoseesCeTour < actionsDisponibles && !joueur.rack().obtenirTuilesRack().isEmpty() && continuer) {
                        jouerTuile(joueur);
                        tuilesPoseesCeTour++;
                        if (tuilesPoseesCeTour > joueur.nbActions && joueur.nbActionsSup > 0) {
                            joueur.nbActionsSup--;
                        }
                        if (tuilesPoseesCeTour < joueur.nbActions + joueur.nbActionsSup && !joueur.rack().obtenirTuilesRack().isEmpty()) {
                            System.out.print("Voulez-vous jouer une autre tuile ? (o/n): ");
                            String reponse = lecteur.nextLine().trim().toLowerCase();
                            if (!reponse.equals("o")) {
                                continuer = false;
                            } else {
                                afficherPlateauEtRack();
                                jouerTuile(joueur);
                            }
                        } else {
                            continuer = false;
                        }
                    }
                    actionValide = true;
                    break;
                case 2:
                	// Passer son tour
                    System.out.println("Vous passez votre tour.");
                    actionValide = true;
                    break;
                case 3:
                	// Echanger les tuiles du rack
                    echangerTuiles(joueur);
                    actionValide = true;
                    break;
                case 4:
                	// Acheter une action supplémentaire
                    if (joueur.points() < 2) {
                        System.out.println(ROUGE + "Pas assez de points pour acheter une action supplémentaire." + RESET);
                    } else {
                        acheterActionSupplementaire(joueur);
                        System.out.println("Vous avez " + joueur.nbActionsSup + " action(s) supplémentaire(s).");
                    }
                    actionValide = false;
                    break;
                default:
                    System.out.println(ROUGE + "Choix invalide. Veuillez réessayer." + RESET);
            }
        }

        // Gestion des tours
        if (joueur == arbitre.joueurCourant()) {
            if (nombreToursJoueur1 < NOMBRE_MAX_TOURS && joueur.nom().equals(arbitre.joueurCourant().nom())) {
                nombreToursJoueur1++;
            } else if (nombreToursJoueur2 < NOMBRE_MAX_TOURS) {
                nombreToursJoueur2++;
            }
        }
        arbitre.gererTours();

        // Conditions de fin de partie
        if ((arbitre.joueurCourant().rack().obtenirTuilesRack().isEmpty() && arbitre.joueurCourant().pioche().estVide()) ||
            (joueur.rack().obtenirTuilesRack().isEmpty() && joueur.pioche().estVide())) {
            return false;
        }
        if (nombreToursJoueur1 >= NOMBRE_MAX_TOURS && nombreToursJoueur2 >= NOMBRE_MAX_TOURS) {
            return false;
        }
        return true;
    }

    private void jouerTuile(Joueur joueur) {
        boolean tuilePlacee = false;
        while (!tuilePlacee) {
            int indice = demanderIndiceTuile(joueur);
            Tuile tuile = joueur.rack().obtenirTuilesRack().get(indice);
            Position pos = demanderPosition();
            if (!estPlacementValide(pos, tuile)) {
                System.out.println(ROUGE + "Placement invalide. Essayez à nouveau." + RESET);
                continue; 
            }
            if (plateau.placerTuile(pos, tuile)) {
                joueur.rack().retirerTuile(tuile);
                int pointsGagnes = gagnerPoints(pos);
                joueur.ajouterPoints(pointsGagnes);
                remplirRack(joueur);
                if (joueur == arbitre.joueur1) {
                    tuilesPoseesJoueur1++;
                } else if (joueur == arbitre.joueur2) {
                    tuilesPoseesJoueur2++;
                }
                tuilePlacee = true; 
            } else {
                System.out.println(ROUGE + "Case occupée. Choisissez une autre position." + RESET);
            }
        }
    }
    
    private int gagnerPoints(Position pos) {
        int[] directionX = {-1, 1, 0, 0};
        int[] directionY = {0, 0, -1, 1};
        int nbAdjacents = 0;
        for (int i = 0; i < 4; i++) {
            Position adjacent = new Position(pos.posX() + directionX[i], pos.posY() + directionY[i]);
            if (plateau.obtenirTuilesPlacees().containsKey(adjacent)) {
                nbAdjacents++;
            }
        }
        int points = 0;
        if (nbAdjacents == 2) points = 1;
        else if (nbAdjacents == 3) points = 2;
        else if (nbAdjacents == 4) points = 4;
        if (plateau.casePlateau(pos.posY(), pos.posX()) == Plateau.TypeCase.SOLEIL) {
            points += 2;
        }
        return points;
    }

    private void echangerTuiles(Joueur joueur) {

        if (joueur.pioche() == null) {
            System.err.println("Pioche est null pour le joueur courant");
            return;
        }

        joueur.pioche().avoirTuiles().addAll(joueur.rack().obtenirTuilesRack());
        joueur.rack().vider();

        while (!joueur.rack().estPlein() && !joueur.pioche().estVide()) {
            joueur.rack().ajouterTuile(joueur.pioche().piocher());
        }
        
    }
    
    
    private void acheterActionSupplementaire(Joueur joueur) {
        int cout = 2;
        if (joueur.points() < cout) {
            System.out.println(ROUGE + "Pas assez de points pour acheter une action supplémentaire." + RESET);
            try {
                Thread.sleep(3000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return;
        }
        joueur.ajouterPoints(-cout);
        joueur.nbActionsSup++;
    }

    
    private int demanderIndiceTuile(Joueur joueur) {
        int indice = -1;
        int taille = joueur.rack().obtenirTuilesRack().size();
        while (indice < 0 || indice >= taille) {
            System.out.print("Numéro de la tuile à jouer (0-" + (taille - 1) + "): ");
            if (lecteur.hasNextInt()) {
                indice = lecteur.nextInt();
                if (indice < 0 || indice >= taille) {
                    System.out.print(ROUGE + "Indice invalide. " + RESET);
                }
            } else {
                lecteur.next();
                System.out.print(ROUGE + "Veuillez entrer un nombre valide. " + RESET);
            }
        }
        lecteur.nextLine();
        return indice;
    }

    private Position demanderPosition() {
        int x = -1;
        int y = -1;
        boolean premiereTuile = plateau.obtenirTuilesPlacees().isEmpty();
        if (premiereTuile) {
            System.out.println("Première tuile : position centrale imposée (4,4).");
            return new Position(4, 4);
        }
        while (x < 0 || x >= plateau.tailleplateau()) {
            System.out.print("Colonne x (0-8): ");
            if (lecteur.hasNextInt()) {
                x = lecteur.nextInt();
                if (x < 0 || x >= plateau.tailleplateau()) {
                    System.out.println(ROUGE + "Veuillez entrer une valeur entre 0 et 8." + RESET);
                }
            } else {
                lecteur.next();
                System.out.println(ROUGE + "Veuillez entrer un nombre pour la colonne x." + RESET);
            }
        }
        while (y < 0 || y >= plateau.tailleplateau()) {
            System.out.print("Ligne y (0-8): ");
            if (lecteur.hasNextInt()) {
                y = lecteur.nextInt();
                if (y < 0 || y >= plateau.tailleplateau()) {
                    System.out.println(ROUGE + "Veuillez entrer une valeur entre 0 et 8." + RESET);
                }
            } else {
                lecteur.next();
                System.out.println(ROUGE + "Veuillez entrer un nombre pour la ligne y." + RESET);
            }
        }
        lecteur.nextLine();
        return new Position(x, y);
    }

    private boolean estPlacementValide(Position position, Tuile tuile) {
        if (plateau.obtenirTuilesPlacees().isEmpty())
            return position.posX() == 4 && position.posY() == 4;
        int[][] directions = { {0,1}, {1,0}, {0,-1}, {-1,0} };
        boolean adjacent = false;
        boolean estValide = false;
        for (int[] d : directions) {
            int adjacentX = position.posX() + d[0];
            int adjacentY = position.posY() + d[1];
            if (adjacentX >= 0 && adjacentX < plateau.tailleplateau() && adjacentY >= 0 && adjacentY < plateau.tailleplateau()) {
                Tuile voisine = plateau.obtenirTuile(new Position(adjacentX, adjacentY));
                if (voisine != null) {
                    adjacent = true;
                    if (voisine.obtenirCouleur() == tuile.obtenirCouleur() || voisine.obtenirSymbole() == tuile.obtenirSymbole()) {
                        estValide = true;
                    }
                }
            }
        }
        return adjacent && estValide;
    }

    private void remplirRack(Joueur joueur) {
        while (!joueur.pioche().estVide() && !joueur.rack().estPlein()) {
            Tuile nouvelleTuile = joueur.pioche().piocher();
            if (nouvelleTuile != null) {
                joueur.rack().ajouterTuile(nouvelleTuile);
            }
        }
    }

    
    private void afficherFinDePartie() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
        String[] banner = {

			"███████╗███████╗██╗░░░░░██╗░█████╗░██╗████████╗░█████╗░████████╗██╗░█████╗░███╗░░██╗░██████╗",
			"██╔════╝██╔════╝██║░░░░░██║██╔══██╗██║╚══██╔══╝██╔══██╗╚══██╔══╝██║██╔══██╗████╗░██║██╔════╝",
			"█████╗░░█████╗░░██║░░░░░██║██║░░╚═╝██║░░░██║░░░███████║░░░██║░░░██║██║░░██║██╔██╗██║╚█████╗░",
			"██╔══╝░░██╔══╝░░██║░░░░░██║██║░░██╗██║░░░██║░░░██╔══██║░░░██║░░░██║██║░░██║██║╚████║░╚═══██╗",
			"██║░░░░░███████╗███████╗██║╚█████╔╝██║░░░██║░░░██║░░██║░░░██║░░░██║╚█████╔╝██║░╚███║██████╔╝",
			"╚═╝░░░░░╚══════╝╚══════╝╚═╝░╚════╝░╚═╝░░░╚═╝░░░╚═╝░░╚═╝░░░╚═╝░░░╚═╝░╚════╝░╚═╝░░╚══╝╚═════╝░\n",
			
            "                                      FIN DE PARTIE !          			             "
        };
        for (String line : banner) {
            System.out.println(line);
            try { Thread.sleep(60); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        for (int i = 0; i < 10; i++) {
            System.out.println();
        }
        String resultat;
        if (tuilesPoseesJoueur1 > tuilesPoseesJoueur2) {
            resultat = arbitre.joueur1.nom() + " a gagné avec " + tuilesPoseesJoueur1 + " tuiles posées.";
        } else if (tuilesPoseesJoueur2 > tuilesPoseesJoueur1) {
            resultat = arbitre.joueur2.nom() + " a gagné avec " + tuilesPoseesJoueur2 + " tuiles posées.";
        } else {
            resultat = "Egalité : " + tuilesPoseesJoueur1 + " tuiles posées chacun.";
        }
        String message = "\n" + resultat + "\n";
        for (char c : message.toCharArray()) {
            System.out.print(c);
            try { Thread.sleep(30); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }

        String felicitation = "\nFÉLICITATIONS À TOUS LES JOUEURS !\n";
        for (char c : felicitation.toCharArray()) {
            System.out.print(c);
            try { Thread.sleep(30); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    
    }

}
