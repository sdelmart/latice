package latice.metier;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Emplacement des données de l'utilisateur (historique, partie sauvegardée) : ~/.latice par défaut,
 * redirigeable avec -Dlatice.donnees=... (utilisé par les tests pour ne jamais toucher aux vraies données).
 */
public final class Donnees {

    private Donnees() {
    }

    public static Path dossier() {
        String personnalise = System.getProperty("latice.donnees");
        return personnalise != null
                ? Paths.get(personnalise)
                : Paths.get(System.getProperty("user.home"), ".latice");
    }

    public static Path fichier(String nom) {
        return dossier().resolve(nom);
    }
}
