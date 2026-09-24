package latice.metier;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Historique local des parties jouées, persisté dans une base SQLite embarquée
 * du dossier utilisateur (portable Windows/Linux/macOS via user.home) : une base
 * fichier unique, aucun serveur à installer, adaptée à une appli desktop mono-utilisateur.
 */
public final class HistoriqueParties {

    private static final Logger LOG = LoggerFactory.getLogger(HistoriqueParties.class);
    private static final DateTimeFormatter FORMAT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final int MAX_ENTREES = 50;

    private static final Path FICHIER_DB = Paths.get(System.getProperty("user.home"), ".latice", "historique.db");

    private HistoriqueParties() {
    }

    public record Partie(LocalDateTime date, String joueur1, int score1, String joueur2, int score2, String vainqueur) {

        public String resume() {
            return date.format(FORMAT_DATE) + "  —  " + joueur1 + " (" + score1 + ") vs "
                    + joueur2 + " (" + score2 + ")  →  " + vainqueur;
        }
    }

    public static void enregistrer(String joueur1, int score1, String joueur2, int score2, String vainqueur) {
        enregistrer(FICHIER_DB, joueur1, score1, joueur2, score2, vainqueur);
    }

    public static List<Partie> chargerHistorique() {
        return chargerHistorique(FICHIER_DB);
    }

    /** Visibilité package pour permettre aux tests de pointer vers une base temporaire plutôt que ~/.latice. */
    static void enregistrer(Path fichierDb, String joueur1, int score1, String joueur2, int score2, String vainqueur) {
        String sql = "INSERT INTO parties (date, joueur1, score1, joueur2, score2, vainqueur) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connexion = ouvrirConnexion(fichierDb);
                PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setString(1, LocalDateTime.now().format(FORMAT_DATE));
            stmt.setString(2, joueur1);
            stmt.setInt(3, score1);
            stmt.setString(4, joueur2);
            stmt.setInt(5, score2);
            stmt.setString(6, vainqueur);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Impossible d'enregistrer l'historique dans {}", fichierDb, e);
        }
    }

    /** Les parties les plus récentes en premier, au maximum MAX_ENTREES. */
    static List<Partie> chargerHistorique(Path fichierDb) {
        if (!Files.exists(fichierDb)) {
            return Collections.emptyList();
        }
        String sql = "SELECT date, joueur1, score1, joueur2, score2, vainqueur FROM parties ORDER BY id DESC LIMIT ?";
        List<Partie> parties = new ArrayList<>();
        try (Connection connexion = ouvrirConnexion(fichierDb);
                PreparedStatement stmt = connexion.prepareStatement(sql)) {
            stmt.setInt(1, MAX_ENTREES);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    parties.add(new Partie(
                            LocalDateTime.parse(rs.getString("date"), FORMAT_DATE),
                            rs.getString("joueur1"), rs.getInt("score1"),
                            rs.getString("joueur2"), rs.getInt("score2"),
                            rs.getString("vainqueur")));
                }
            }
        } catch (SQLException e) {
            LOG.error("Impossible de lire l'historique depuis {}", fichierDb, e);
        }
        return parties;
    }

    private static Connection ouvrirConnexion(Path fichierDb) throws SQLException {
        Path dossierParent = fichierDb.toAbsolutePath().getParent();
        if (dossierParent != null) {
            try {
                Files.createDirectories(dossierParent);
            } catch (Exception e) {
                LOG.warn("Impossible de créer le dossier de la base d'historique", e);
            }
        }
        Connection connexion = DriverManager.getConnection("jdbc:sqlite:" + fichierDb);
        try (Statement stmt = connexion.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS parties ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "date TEXT NOT NULL,"
                    + "joueur1 TEXT NOT NULL,"
                    + "score1 INTEGER NOT NULL,"
                    + "joueur2 TEXT NOT NULL,"
                    + "score2 INTEGER NOT NULL,"
                    + "vainqueur TEXT NOT NULL)");
        }
        return connexion;
    }
}
