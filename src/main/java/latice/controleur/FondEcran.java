package latice.controleur;

/**
 * Catalogue des fonds d'écran disponibles. Chaque entrée pointe vers une classe CSS
 * définie dans theme.css ; en ajouter un revient à ajouter une classe .background-xxx + une entrée ici.
 */
public enum FondEcran {

    ARDOISE("background-ardoise", "Minimaliste"),
    PROFOND("background-profond", "Bleu abyssal"),
    AUBE("background-aube", "Aube dorée"),
    ETOILE("background-ocean", "Nuit étoilée");

    private final String classeCss;
    private final String nomAffiche;

    FondEcran(String classeCss, String nomAffiche) {
        this.classeCss = classeCss;
        this.nomAffiche = nomAffiche;
    }

    public String classeCss() {
        return classeCss;
    }

    public static FondEcran parDefaut() {
        return ARDOISE;
    }

    @Override
    public String toString() {
        return nomAffiche;
    }
}
