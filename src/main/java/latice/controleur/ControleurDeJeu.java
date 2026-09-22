package latice.controleur;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import latice.metier.Joueur;
import latice.metier.Plateau;
import latice.metier.Position;
import latice.metier.Tuile;
import latice.metier.Arbitre;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;


public class ControleurDeJeu {

    @FXML
    private GridPane rack1;
    @FXML
    private GridPane rack2;
    @FXML
    private Button btnEchange;
    @FXML
    private Button btnPasse;
    @FXML
    private Button btnJouer;
    @FXML
    private Button btnAcheter;
    @FXML
    private Label labelJoueur1;
    @FXML
    private Label labelJoueur2;
    @FXML
    private GridPane gridPaneCentral;
    @FXML
    private Label labelToursJoueur1;
    @FXML
    private Label labelToursJoueur2;
    @FXML
    private Label labelPointsJoueur1;
    @FXML
    private Label labelPointsJoueur2;
    @FXML
    private Label labelActionsJoueur1;
    @FXML
    private Label labelActionsJoueur2;
    @FXML
    private Label labelTourActuel;
    @FXML
    private Button btnParametresLatice;

    private Joueur joueur1;
    private Joueur joueur2;
    private Arbitre arbitre;
    private Plateau plateau;
    private Map<Position, Integer> cellulesPlacement = new HashMap<>();
    private Map<Position, Tuile> placeTuiles = new HashMap<>();
    private boolean premiereTuile = false;
    private IntegerProperty toursRestantsJoueur1 = new SimpleIntegerProperty(10);
    private IntegerProperty toursRestantsJoueur2 = new SimpleIntegerProperty(10);
    private Musique musique;
    private Tuile tuileEnCoursDeplacement = null;
    private ImageView imageViewEnCoursDeplacement = null;
    private int tuilesPoseesCeTour = 0;
    private int tuilesPoseesJoueur1 = 0;
    private int tuilesPoseesJoueur2 = 0;

    public void setArbitre(Arbitre arbitre) {
        this.arbitre = arbitre;
    }

    public void initialiserPlateau(Plateau plateau) {
        this.plateau = plateau;
        if (gridPaneCentral == null) {
            throw new IllegalStateException("GridPane n'est pas initialisée. Vérifier le fichier FXML.");
        }
        gridPaneCentral.getChildren().clear();
        for (int ligne = 0; ligne < plateau.tailleplateau(); ligne++) {
            for (int col = 0; col < plateau.tailleplateau(); col++) {
                Plateau.TypeCase typeCase = plateau.casePlateau(ligne, col);
                ImageView imageView = new ImageView();
                switch (typeCase) {
                    case SOLEIL:
                        imageView.setImage(new Image(getClass().getResourceAsStream("/images/img_Latice/img/bg_sun.png")));
                        break;
                    case LUNE:
                        imageView.setImage(new Image(getClass().getResourceAsStream("/images/img_Latice/img/bg_moon.png")));
                        break;
                    case NORMALE:
                        imageView.setImage(new Image(getClass().getResourceAsStream("/images/img_Latice/img/bg_sea.png")));
                        break;
                }
                imageView.setFitWidth(68);
                imageView.setFitHeight(68);
                gridPaneCentral.add(imageView, col, ligne);
            }
        }
    }

    public void nomsJoueurs(String nomJoueur1, String nomJoueur2) {
        this.joueur1 = new Joueur(nomJoueur1);
        this.joueur2 = new Joueur(nomJoueur2);
        labelJoueur1.setText(nomJoueur1);
        labelJoueur2.setText(nomJoueur2);
    }

    public void initialiserRackJoueurs(Joueur joueur1, Joueur joueur2) {
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        if (rack1 == null || rack2 == null) {
            throw new IllegalStateException("Rack n'est pas initialisé. Vérifier le fichier FXML.");
        }
        remplirRack(rack1, joueur1.rack().obtenirTuilesRack());
        remplirRack(rack2, joueur2.rack().obtenirTuilesRack());
    }

    private void remplirRack(GridPane rack, List<Tuile> tuiles) {
        rack.getChildren().clear();
        for (int i = 0; i < tuiles.size(); i++) {
            Tuile tuile = tuiles.get(i);
            ImageView imageView = new ImageView(new Image(getClass().getResourceAsStream(tuile.cheminImage())));
            imageView.setFitWidth(115);
            imageView.setFitHeight(102);
            rack.add(imageView, 0, i);
            imageView.setUserData(tuile);
            imageView.setOnDragDetected(event -> {
                Joueur joueurActuel = arbitre.joueurCourant();
                GridPane rackActuel = joueurActuel == joueur1 ? rack1 : rack2;
                if (rack != rackActuel) {
                    event.consume();
                    return;
                }
                if (imageView.getImage() != null) {
                    Dragboard dragboard = imageView.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(imageView.getImage());
                    dragboard.setContent(content);
                    tuileEnCoursDeplacement = (Tuile) imageView.getUserData();
                    imageViewEnCoursDeplacement = imageView;
                }
                event.consume();
            });
        }
    }

    public void initialiserDragAndDrop() {
        dragAndDropPlateau();
    }

    private void dragAndDropPlateau() {
        gridPaneCentral.setOnDragOver(event -> {
            if (event.getGestureSource() != gridPaneCentral && event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        gridPaneCentral.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            if (dragboard.hasImage() && tuileEnCoursDeplacement != null) {
                int col = (int) (event.getX() / (gridPaneCentral.getWidth() / gridPaneCentral.getColumnConstraints().size()));
                int ligne = (int) (event.getY() / (gridPaneCentral.getHeight() / gridPaneCentral.getRowConstraints().size()));
                Position pos = new Position(col, ligne);
                Joueur joueurActuel = arbitre.joueurCourant();
                int actionsDisponibles = joueurActuel.nbActions + joueurActuel.nbActionsSup;

                if (tuilesPoseesCeTour >= actionsDisponibles) {
                    event.setDropCompleted(false);
                    event.consume();
                    return;
                }

                if (!premiereTuile) {
                    int centreCol = plateau.tailleplateau() / 2;
                    int centreLigne = plateau.tailleplateau() / 2;
                    if (col != centreCol || ligne != centreLigne) {
                        event.setDropCompleted(false);
                        event.consume();
                        return;
                    }
                    premiereTuile = true;
                } else {
                    if (!estPlacementValide(pos, tuileEnCoursDeplacement)) {
                        event.setDropCompleted(false);
                        event.consume();
                        return;
                    }
                }

                if (cellulesPlacement.getOrDefault(pos, 0) < 1) {
                    ImageView imageView = new ImageView(dragboard.getImage());
                    imageView.setFitWidth(67);
                    imageView.setFitHeight(67);
                    gridPaneCentral.add(imageView, col, ligne);
                    cellulesPlacement.put(pos, 1);
                    placeTuiles.put(pos, tuileEnCoursDeplacement);
                    if (imageViewEnCoursDeplacement != null) {
                        GridPane parentRack = (GridPane) imageViewEnCoursDeplacement.getParent();
                        parentRack.getChildren().remove(imageViewEnCoursDeplacement);
                        joueurActuel.rack().retirerTuile(tuileEnCoursDeplacement);
                    }
                    gagnerPoints(pos);
                    tuilesPoseesCeTour++;
                    btnEchange.setDisable(true);
                    btnAcheter.setDisable(true);
                    btnPasse.setDisable(true);
                    if (joueurActuel == joueur1) {
                        tuilesPoseesJoueur1++;
                    } else if (joueurActuel == joueur2) {
                        tuilesPoseesJoueur2++;
                    }

                    if (joueurActuel.nbActionsSup > 0 && tuilesPoseesCeTour > joueurActuel.nbActions) {
                        joueurActuel.nbActionsSup--;
                        mettreAJourActions(joueurActuel);
                    }
                    GridPane rackActuel = joueurActuel == joueur1 ? rack1 : rack2;
                    while (joueurActuel.rack().taille() < 5 && joueurActuel.pioche() != null && !joueurActuel.pioche().estVide()) {
                        joueurActuel.rack().ajouterTuile(joueurActuel.pioche().piocher());
                    }
                    remplirRack(rackActuel, joueurActuel.rack().obtenirTuilesRack());
                    mettreAJourPoints(joueurActuel);
                } else {
                    event.setDropCompleted(false);
                }
            } else {
                event.setDropCompleted(false);
            }
            tuileEnCoursDeplacement = null;
            imageViewEnCoursDeplacement = null;
            event.consume();
        });
    }

    private Tuile obtenirTuilePlateau(Position pos) {
        return placeTuiles.get(pos);
    }

    private boolean estPlacementValide(Position pos, Tuile tuile) {
        int[][] directions = { {0,1}, {1,0}, {0,-1}, {-1,0} };
        boolean adjacent = false;
        boolean estValide = false;
        for (int[] d : directions) {
            Position adjacentPos = new Position(pos.posX() + d[0], pos.posY() + d[1]);
            Tuile voisine = obtenirTuilePlateau(adjacentPos);
            if (voisine != null) {
                adjacent = true;
                if (voisine.obtenirCouleur() == tuile.obtenirCouleur() || voisine.obtenirSymbole() == tuile.obtenirSymbole()) {
                    estValide = true;
                }
            }
        }
        return adjacent && estValide;
    }

    private void gagnerPoints(Position pos) {
        Joueur joueurActuel = arbitre.joueurCourant();
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};
        int nbAdjacents = 0;
        for (int i = 0; i < 4; i++) {
            Position adjacent = new Position(pos.posX() + dx[i], pos.posY() + dy[i]);
            if (placeTuiles.containsKey(adjacent)) {
                nbAdjacents++;
            }
        }
        if (nbAdjacents == 2) {
            joueurActuel.ajouterPoints(1);
        } else if (nbAdjacents == 3) {
            joueurActuel.ajouterPoints(2);
        } else if (nbAdjacents == 4) {
            joueurActuel.ajouterPoints(4);
        }
        if (plateau.casePlateau(pos.posY(), pos.posX()) == Plateau.TypeCase.SOLEIL) {
            joueurActuel.ajouterPoints(2);
        }
    }

    private void mettreAJourPoints(Joueur joueur) {
        if (joueur == joueur1) {
            labelPointsJoueur1.setText("Points: " + joueur1.points());
        } else {
            labelPointsJoueur2.setText("Points: " + joueur2.points());
        }
    }

    private void mettreAJourActions(Joueur joueur) {
        if (joueur == joueur1) {
            labelActionsJoueur1.setText("Actions supplementaires: " + joueur1.nbActionsSup);
        } else {
            labelActionsJoueur2.setText("Actions supplementaires: " + joueur2.nbActionsSup);
        }
    }

    @FXML
    private void actionEchanger(ActionEvent event) {
        Joueur joueurActuel = arbitre.joueurCourant();
        GridPane rackActuel = joueurActuel == joueur1 ? rack1 : rack2;
        if (joueurActuel.pioche() == null) {
            System.err.println("Pioche est null pour le joueur courant");
            return;
        }
        joueurActuel.pioche().avoirTuiles().addAll(joueurActuel.rack().obtenirTuilesRack());
        joueurActuel.rack().vider();
        while (!joueurActuel.rack().estPlein() && !joueurActuel.pioche().estVide()) {
            joueurActuel.rack().ajouterTuile(joueurActuel.pioche().piocher());
        }
        remplirRack(rackActuel, joueurActuel.rack().obtenirTuilesRack());
        Joueur joueurCourant = arbitre.joueurCourant();
        if (joueurCourant == joueur1) {
            toursRestantsJoueur1.set(toursRestantsJoueur1.get() - 1);
        } else if (joueurCourant == joueur2) {
            toursRestantsJoueur2.set(toursRestantsJoueur2.get() - 1);
        }
        arbitre.gererTours();
        mettreAJourTour();
    }

    @FXML
    private void actionPasse(ActionEvent event) {
        if (arbitre == null) {
            System.err.println("Erreur: Arbitre est non initialisé");
            return;
        }
        Joueur joueurCourant = arbitre.joueurCourant();
        if (joueurCourant == joueur1) {
            toursRestantsJoueur1.set(toursRestantsJoueur1.get() - 1);
        } else if (joueurCourant == joueur2) {
            toursRestantsJoueur2.set(toursRestantsJoueur2.get() - 1);
        }
        arbitre.gererTours();
        mettreAJourTour();
    }

    @FXML
    private void actionJouer(ActionEvent event) {
        if (arbitre == null) {
            System.err.println("Erreur: Arbitre est non initialisé");
            return;
        }
        Joueur joueurActuel = arbitre.joueurCourant();
        GridPane rackActuel = joueurActuel == joueur1 ? rack1 : rack2;
        while (joueurActuel.rack().taille() < 5 && joueurActuel.pioche() != null && !joueurActuel.pioche().estVide()) {
            joueurActuel.rack().ajouterTuile(joueurActuel.pioche().piocher());
        }
        remplirRack(rackActuel, joueurActuel.rack().obtenirTuilesRack());
        if (joueurActuel == joueur1) {
            toursRestantsJoueur1.set(toursRestantsJoueur1.get() - 1);
        } else if (joueurActuel == joueur2) {
            toursRestantsJoueur2.set(toursRestantsJoueur2.get() - 1);
        }
        arbitre.gererTours();
        mettreAJourTour();
        mettreAJourPoints(joueurActuel);
    }

    public void mettreAJourTour() {
        tuilesPoseesCeTour = 0;
        btnEchange.setDisable(false);
        btnAcheter.setDisable(false);
        btnPasse.setDisable(false);
        Joueur joueurCourant = arbitre != null ? arbitre.joueurCourant() : null;
        if (toursRestantsJoueur1.get() == 0 && toursRestantsJoueur2.get() == 0) {
            String messageFin;
            if (tuilesPoseesJoueur1 > tuilesPoseesJoueur2) {
                messageFin = joueur1.nom() + " a gagné avec " + tuilesPoseesJoueur1 + " tuiles posées.";
            } else if (tuilesPoseesJoueur2 > tuilesPoseesJoueur1) {
                messageFin = joueur2.nom() + " a gagné avec " + tuilesPoseesJoueur2 + " tuiles posées.";
            } else {
                messageFin = "Egalité : " + tuilesPoseesJoueur1 + " tuiles posées chacun.";
            }
            afficherFinDePartie(messageFin);
            Stage stage = (Stage) labelTourActuel.getScene().getWindow();
            stage.close();
        } else {
            if (joueurCourant != null) {
                labelTourActuel.setText("Tour de " + joueurCourant.nom());
            }
        }
    }

    @FXML
    public void initialiserTours() {
        labelToursJoueur1.textProperty().bind(toursRestantsJoueur1.asString("Tours : %d"));
        labelToursJoueur2.textProperty().bind(toursRestantsJoueur2.asString("Tours : %d"));
    }

    public void mettreMusique(Musique musique) {
        this.musique = musique;
    }

    @FXML
    private void ouvrirParametresLatice() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ihm/Parametre.fxml"));
            Parent root = fxmlLoader.load();
            ControleurParametres controleur = fxmlLoader.getController();
            controleur.mettreMusique(musique);
            Stage stage = new Stage();
            stage.setTitle("Paramètres");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/img_latice/img/parametre.png")));
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void actionAcheter(ActionEvent event) {
        Joueur joueurActuel = arbitre.joueurCourant();
        if (joueurActuel.points() < 2) {
            System.out.println("Vous n'avez pas assez de points pour acheter une action supplementaire!");
        } else {
            joueurActuel.ajouterPoints(-2);
            joueurActuel.nbActionsSup += 1;
            System.out.println("Vous avez acheté une action supplementaire pour 2 points !");
        }
        mettreAJourActions(joueurActuel);
        mettreAJourPoints(joueurActuel);
    }
    
    private void afficherFinDePartie(String resultat) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ihm/FinDePartie.fxml"));
            Parent root = loader.load();
            ControleurFinDePartie controller = loader.getController();
            controller.definirResultat(resultat);
            Stage stage = new Stage();
            stage.setTitle("Fin de la partie");
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
