package main;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import latice.controleur.EffetsSonores;
import latice.controleur.PisteMusicale;
import latice.enumeration.Couleur;
import latice.enumeration.Symbole;
import latice.metier.Tuile;

/**
 * Vérifie qu'un build contient bien tout ce dont le jeu a besoin : images, sons, musiques, écrans FXML,
 * feuille de style, pilote SQLite natif (et, pour un build empaqueté, bibliothèques natives JavaFX).
 * Lancé par la CI sur chaque plateforme via {@code Latice --verifier-ressources}, car une ressource
 * manquante ou mal orthographiée (casse !) ne se voit pas toujours quand on lance depuis Maven.
 */
public final class VerificationRessources {

    private static final String[] RESSOURCES_FIXES = {
        "/images/img_Latice/img/bg_sun.png", "/images/img_Latice/img/bg_moon.png",
        "/images/img_Latice/img/bg_sea.png", "/images/img_Latice/img/fond.png",
        "/images/img_Latice/img/icone.png", "/images/img_Latice/img/parametre.png",
        "/images/fonds/etoile_cote.jpg", "/images/fonds/palmiers_etoiles.jpg",
        "/ihm/Accueil.fxml", "/ihm/Chargement.fxml", "/ihm/Latice.fxml", "/ihm/Parametre.fxml",
        "/ihm/FinDePartie.fxml", "/css/theme.css", "/video/Confetis.mp4", "/logback.xml"
    };

    private VerificationRessources() {
    }

    /** Ressources du jeu + pilote SQLite : doit être vide sur n'importe quel build correct. */
    public static List<String> problemesRessources() {
        List<String> problemes = new ArrayList<>();
        for (Couleur couleur : Couleur.values()) {
            for (Symbole symbole : Symbole.values()) {
                exiger(problemes, new Tuile(couleur, symbole).cheminImage());
            }
        }
        for (String chemin : RESSOURCES_FIXES) {
            exiger(problemes, chemin);
        }
        for (EffetsSonores.Effet effet : EffetsSonores.Effet.values()) {
            exiger(problemes, effet.chemin());
        }
        for (PisteMusicale piste : PisteMusicale.values()) {
            exiger(problemes, piste.chemin());
        }
        try (Connection connexion = DriverManager.getConnection("jdbc:sqlite::memory:")) {
            if (!connexion.createStatement().executeQuery("SELECT 1").next()) {
                problemes.add("SQLite : requête de test sans résultat");
            }
        } catch (Exception e) {
            problemes.add("SQLite : pilote natif inutilisable (" + e + ")");
        }
        return problemes;
    }

    /** Bibliothèque native du toolkit JavaFX pour le système courant (présente dans un jar/une app empaquetée). */
    public static List<String> problemesNatifsJavaFx() {
        String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        String natif;
        if (os.contains("win")) {
            natif = "/glass.dll";
        } else if (os.contains("mac")) {
            natif = "/libglass.dylib";
        } else {
            natif = "/libglass.so";
        }
        List<String> problemes = new ArrayList<>();
        exiger(problemes, natif);
        return problemes;
    }

    private static void exiger(List<String> problemes, String chemin) {
        if (VerificationRessources.class.getResource(chemin) == null) {
            problemes.add("Ressource introuvable : " + chemin);
        }
    }

    /** Écrit le rapport (sortie standard + fichier optionnel) et renvoie le code de sortie : 0 si tout va bien. */
    public static int executer(Path fichierRapport) {
        List<String> problemes = new ArrayList<>(problemesRessources());
        problemes.addAll(problemesNatifsJavaFx());
        StringBuilder rapport = new StringBuilder();
        rapport.append("Latice - vérification des ressources\n")
                .append("java ").append(System.getProperty("java.version"))
                .append(" / ").append(System.getProperty("os.name"))
                .append(' ').append(System.getProperty("os.arch")).append('\n');
        if (problemes.isEmpty()) {
            rapport.append("OK : toutes les ressources sont présentes.\n");
        } else {
            problemes.forEach(p -> rapport.append("ERREUR : ").append(p).append('\n'));
        }
        System.out.print(rapport);
        if (fichierRapport != null) {
            try {
                Files.writeString(fichierRapport, rapport.toString(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                System.err.println("Impossible d'écrire le rapport : " + e.getMessage());
            }
        }
        return problemes.isEmpty() ? 0 : 1;
    }
}
