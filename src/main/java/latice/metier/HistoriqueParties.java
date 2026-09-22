package latice.metier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Historique local des parties jouées, stocké dans un fichier texte du dossier utilisateur
 * (portable Windows/Linux/macOS via user.home) : une ligne par partie, format CSV simple.
 */
public final class HistoriqueParties {

    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final int MAX_ENTREES = 50;
    private static final String SEPARATEUR = ";";

    private static final Path FICHIER = Paths.get(System.getProperty("user.home"), ".latice", "historique.csv");

    private HistoriqueParties() {
    }

    public record Partie(LocalDateTime date, String joueur1, int score1, String joueur2, int score2, String vainqueur) {

        String versLigne() {
            return date.format(FORMAT_DATE) + SEPARATEUR + joueur1 + SEPARATEUR + score1
                    + SEPARATEUR + joueur2 + SEPARATEUR + score2 + SEPARATEUR + vainqueur;
        }

        static Partie depuisLigne(String ligne) {
            String[] champs = ligne.split(SEPARATEUR, -1);
            if (champs.length != 6) {
                return null;
            }
            try {
                return new Partie(
                        LocalDateTime.parse(champs[0], FORMAT_DATE),
                        champs[1], Integer.parseInt(champs[2]),
                        champs[3], Integer.parseInt(champs[4]),
                        champs[5]);
            } catch (Exception e) {
                return null;
            }
        }

        public String resume() {
            return date.format(FORMAT_DATE) + "  —  " + joueur1 + " (" + score1 + ") vs "
                    + joueur2 + " (" + score2 + ")  →  " + vainqueur;
        }
    }

    public static void enregistrer(String joueur1, int score1, String joueur2, int score2, String vainqueur) {
        Partie partie = new Partie(LocalDateTime.now(), joueur1, score1, joueur2, score2, vainqueur);
        try {
            Files.createDirectories(FICHIER.getParent());
            Files.write(FICHIER, (partie.versLigne() + System.lineSeparator()).getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Impossible d'enregistrer l'historique : " + e.getMessage());
        }
    }

    /** Les parties les plus récentes en premier, au maximum MAX_ENTREES. */
    public static List<Partie> chargerHistorique() {
        if (!Files.exists(FICHIER)) {
            return Collections.emptyList();
        }
        List<Partie> parties = new ArrayList<>();
        try (BufferedReader lecteur = Files.newBufferedReader(FICHIER, StandardCharsets.UTF_8)) {
            String ligne;
            while ((ligne = lecteur.readLine()) != null) {
                Partie partie = Partie.depuisLigne(ligne);
                if (partie != null) {
                    parties.add(partie);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        Collections.reverse(parties);
        return parties.size() > MAX_ENTREES ? parties.subList(0, MAX_ENTREES) : parties;
    }
}
