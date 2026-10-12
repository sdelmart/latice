package latice.metier;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import latice.enumeration.Couleur;
import latice.enumeration.Symbole;

/**
 * Instantané complet d'une partie en cours, et sa persistance dans un fichier texte
 * (~/.latice/partie_en_cours.properties). La partie est sauvegardée au début de chaque tour
 * et le fichier est supprimé quand la partie se termine.
 */
public record Sauvegarde(boolean solo, int joueurCourant, List<EtatJoueur> joueurs, List<TuilePosee> plateau) {

    public Sauvegarde {
        joueurs = Collections.unmodifiableList(new ArrayList<>(joueurs));
        plateau = Collections.unmodifiableList(new ArrayList<>(plateau));
    }

    public record EtatJoueur(String nom, Couleur couleur, int points, int actionsSup, int toursRestants,
            int tuilesPosees, List<Tuile> rack, List<Tuile> pioche, int indexPioche) {

        public EtatJoueur {
            rack = List.copyOf(rack);
            pioche = List.copyOf(pioche);
        }
    }

    public record TuilePosee(Position position, Tuile tuile) {
    }

    private static final Logger LOG = LoggerFactory.getLogger(Sauvegarde.class);
    private static final int VERSION = 1;
    private static Path fichierParDefaut() {
        return Donnees.fichier("partie_en_cours.properties");
    }

    // --- API par défaut (fichier de l'utilisateur) -----------------------------------------

    public static boolean existe() {
        return Files.isRegularFile(fichierParDefaut());
    }

    public static void enregistrer(Sauvegarde sauvegarde) {
        enregistrer(fichierParDefaut(), sauvegarde);
    }

    public static Optional<Sauvegarde> charger() {
        return charger(fichierParDefaut());
    }

    public static void supprimer() {
        supprimer(fichierParDefaut());
    }

    // --- Variantes avec chemin explicite (utilisées par les tests) ---------------------------

    static void enregistrer(Path fichier, Sauvegarde s) {
        Properties p = new Properties();
        p.setProperty("version", String.valueOf(VERSION));
        p.setProperty("solo", String.valueOf(s.solo()));
        p.setProperty("courant", String.valueOf(s.joueurCourant()));
        for (int i = 0; i < s.joueurs().size(); i++) {
            EtatJoueur j = s.joueurs().get(i);
            String cle = "joueur." + i + ".";
            p.setProperty(cle + "nom", j.nom());
            p.setProperty(cle + "couleur", j.couleur().name());
            p.setProperty(cle + "points", String.valueOf(j.points()));
            p.setProperty(cle + "actionsSup", String.valueOf(j.actionsSup()));
            p.setProperty(cle + "tours", String.valueOf(j.toursRestants()));
            p.setProperty(cle + "tuilesPosees", String.valueOf(j.tuilesPosees()));
            p.setProperty(cle + "rack", encoderTuiles(j.rack()));
            p.setProperty(cle + "pioche", encoderTuiles(j.pioche()));
            p.setProperty(cle + "indexPioche", String.valueOf(j.indexPioche()));
        }
        StringBuilder plateau = new StringBuilder();
        for (TuilePosee t : s.plateau()) {
            if (plateau.length() > 0) {
                plateau.append(';');
            }
            plateau.append(t.position().posX()).append(':').append(t.position().posY())
                    .append(':').append(encoderTuile(t.tuile()));
        }
        p.setProperty("plateau", plateau.toString());

        Path nomDuFichier = fichier.getFileName();
        if (nomDuFichier == null) {
            throw new IllegalArgumentException("Chemin de sauvegarde invalide : " + fichier);
        }
        try {
            Path dossier = fichier.toAbsolutePath().getParent();
            if (dossier != null) {
                Files.createDirectories(dossier);
            }
            Path temporaire = fichier.resolveSibling(nomDuFichier + ".tmp");
            try (Writer ecrivain = Files.newBufferedWriter(temporaire, StandardCharsets.UTF_8)) {
                p.store(ecrivain, "Latice - partie en cours");
            }
            Files.move(temporaire, fichier, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOG.error("Impossible de sauvegarder la partie dans {}", fichier, e);
        }
    }

    /** Renvoie la partie sauvegardée, ou vide si le fichier est absent, illisible ou d'une autre version. */
    static Optional<Sauvegarde> charger(Path fichier) {
        if (!Files.isRegularFile(fichier)) {
            return Optional.empty();
        }
        Properties p = new Properties();
        try (Reader lecteur = Files.newBufferedReader(fichier, StandardCharsets.UTF_8)) {
            p.load(lecteur);
        } catch (IOException e) {
            LOG.warn("Sauvegarde illisible : {}", fichier, e);
            return Optional.empty();
        }
        try {
            if (Integer.parseInt(p.getProperty("version", "0")) != VERSION) {
                LOG.warn("Version de sauvegarde non supportée dans {}", fichier);
                return Optional.empty();
            }
            List<EtatJoueur> joueurs = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                String cle = "joueur." + i + ".";
                joueurs.add(new EtatJoueur(
                        obligatoire(p, cle + "nom"),
                        Couleur.valueOf(obligatoire(p, cle + "couleur")),
                        Integer.parseInt(obligatoire(p, cle + "points")),
                        Integer.parseInt(obligatoire(p, cle + "actionsSup")),
                        Integer.parseInt(obligatoire(p, cle + "tours")),
                        Integer.parseInt(obligatoire(p, cle + "tuilesPosees")),
                        decoderTuiles(obligatoire(p, cle + "rack")),
                        decoderTuiles(obligatoire(p, cle + "pioche")),
                        Integer.parseInt(obligatoire(p, cle + "indexPioche"))));
            }
            List<TuilePosee> plateau = new ArrayList<>();
            String brut = p.getProperty("plateau", "");
            if (!brut.isEmpty()) {
                for (String entree : brut.split(";")) {
                    String[] champs = entree.split(":", 3);
                    plateau.add(new TuilePosee(
                            new Position(Integer.parseInt(champs[0]), Integer.parseInt(champs[1])),
                            decoderTuile(champs[2])));
                }
            }
            int courant = Integer.parseInt(obligatoire(p, "courant"));
            if (courant < 0 || courant > 1) {
                throw new IllegalArgumentException("joueur courant invalide : " + courant);
            }
            return Optional.of(new Sauvegarde(Boolean.parseBoolean(p.getProperty("solo")), courant, joueurs, plateau));
        } catch (RuntimeException e) {
            LOG.warn("Sauvegarde corrompue ignorée : {} ({})", fichier, e.toString());
            return Optional.empty();
        }
    }

    static void supprimer(Path fichier) {
        try {
            Files.deleteIfExists(fichier);
        } catch (IOException e) {
            LOG.warn("Impossible de supprimer la sauvegarde {}", fichier, e);
        }
    }

    // --- Encodage des tuiles ------------------------------------------------------------------

    private static String obligatoire(Properties p, String cle) {
        String valeur = p.getProperty(cle);
        if (valeur == null) {
            throw new IllegalArgumentException("clé manquante : " + cle);
        }
        return valeur;
    }

    private static String encoderTuile(Tuile t) {
        return t.obtenirCouleur().name() + "/" + t.obtenirSymbole().name();
    }

    private static Tuile decoderTuile(String texte) {
        String[] champs = texte.split("/");
        return new Tuile(Couleur.valueOf(champs[0]), Symbole.valueOf(champs[1]));
    }

    private static String encoderTuiles(List<Tuile> tuiles) {
        StringBuilder sb = new StringBuilder();
        for (Tuile t : tuiles) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(encoderTuile(t));
        }
        return sb.toString();
    }

    private static List<Tuile> decoderTuiles(String texte) {
        List<Tuile> tuiles = new ArrayList<>();
        if (!texte.isEmpty()) {
            for (String t : texte.split(",")) {
                tuiles.add(decoderTuile(t));
            }
        }
        return tuiles;
    }
}
