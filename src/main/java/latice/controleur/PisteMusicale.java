package latice.controleur;

/**
 * Catalogue des musiques disponibles pour habiller le jeu.
 * Ajouter un fichier .mp3 dans resources/musique puis une entrée ici suffit à l'exposer dans les paramètres.
 */
public enum PisteMusicale {

    ILE("/musique/MusLatice.mp3", "Mélodie de l'île"),
    VAGUES("/musique/WaitMus.mp3", "Vagues nocturnes");

    private final String chemin;
    private final String nomAffiche;

    PisteMusicale(String chemin, String nomAffiche) {
        this.chemin = chemin;
        this.nomAffiche = nomAffiche;
    }

    public String chemin() {
        return chemin;
    }

    public String nomAffiche() {
        return nomAffiche;
    }

    public static PisteMusicale parDefaut() {
        return ILE;
    }

    public static PisteMusicale depuisChemin(String chemin) {
        for (PisteMusicale piste : values()) {
            if (piste.chemin.equals(chemin)) {
                return piste;
            }
        }
        return parDefaut();
    }

    @Override
    public String toString() {
        return nomAffiche;
    }
}
