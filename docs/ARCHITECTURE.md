# Architecture

## Vue d'ensemble

Le code est séparé en deux packages qui ne dépendent jamais l'un de l'autre dans le mauvais sens :
`latice.metier` ne connaît rien de JavaFX (donc testable sans lancer d'interface graphique), et
`latice.controleur` affiche l'état du jeu et traduit les gestes du joueur en appels à `latice.metier`.

```mermaid
flowchart LR
    subgraph UI["src/main/resources/ihm/*.fxml"]
        A[Accueil.fxml]
        L[Latice.fxml]
        P[Parametre.fxml]
        F[FinDePartie.fxml]
    end

    subgraph Controleur["latice.controleur"]
        CA[ControleurAccueil]
        CJ[ControleurDeJeu]
        CP[ControleurParametres]
        CF[ControleurFinDePartie]
        NAV[Navigation]
        MUS[Musique]
        SFX[EffetsSonores]
        THEME[ThemeVisuel]
    end

    subgraph Metier["latice.metier (zéro dépendance JavaFX)"]
        PAR[Partie]
        REG[Regles]
        SAV[Sauvegarde]
        IA[JoueurArtificiel]
        HIST[HistoriqueParties]
        ARB[Arbitre]
        PLA[Plateau]
        JOU[Joueur]
        RAC[Rack]
        PIO[Pioche]
    end

    A --> CA
    L --> CJ
    P --> CP
    F --> CF

    CA --> NAV
    CF --> NAV
    NAV --> CJ

    CJ --> PAR
    CJ --> IA
    CJ --> SAV
    CJ --> HIST
    CJ --> MUS
    CJ --> SFX
    CJ --> THEME

    PAR --> REG
    PAR --> ARB
    PAR --> SAV
    IA --> REG
    ARB --> JOU
    ARB --> PLA
    JOU --> RAC
    JOU --> PIO
```

## Les règles : un seul endroit

`Regles` contient les règles de Latice (placement, points, fin de partie par épuisement des tuiles) et les constantes
(plateau 9×9, rack de 5, 10 tours par joueur, 2 points l'action supplémentaire). La partie graphique, l'IA et la
version console l'appellent toutes : il n'existe plus de copie à maintenir en parallèle (un défaut de règle s'était
justement retrouvé identique dans les trois copies).

Placement valide : case libre ; la toute première tuile au centre ; ensuite la tuile touche au moins une tuile posée et
est compatible (même couleur **ou** même symbole) avec **chacune** des tuiles qu'elle touche.

## Diagramme de classes (cœur du moteur de jeu)

```mermaid
classDiagram
    class Partie {
        -Arbitre arbitre
        -int[] toursRestants
        -int tuilesPoseesCeTour
        +peutPlacer(Position, Tuile) boolean
        +placer(Position, Tuile) int
        +acheterActionSupplementaire() boolean
        +echanger() void
        +finirTour() void
        +estTerminee() boolean
        +vainqueur() Optional~Joueur~
        +instantane(boolean, Couleur, Couleur) Sauvegarde
        +restaurer(Sauvegarde)$ Partie
    }
    class Regles {
        <<utility>>
        +estPlacementValide(Plateau, Map, Position, Tuile)$ boolean
        +pointsGagnes(Plateau, Map, Position)$ int
        +aVideSonRackEtSaPioche(Joueur)$ boolean
    }
    class Sauvegarde {
        <<record>>
        +enregistrer(Sauvegarde)$ void
        +charger()$ Optional~Sauvegarde~
        +supprimer()$ void
    }
    class Arbitre {
        +Joueur joueur1
        +Joueur joueur2
        +Plateau plateau
        +joueurCourant() Joueur
        +gererTours() void
    }
    class Joueur {
        -String nom
        -Rack rack
        -Pioche pioche
        +Integer points
        +int nbActionsSup
    }
    class Plateau {
        -Map~Position,Tuile~ tuilesPlacees
        +casePlateau(int,int) TypeCase
        +placerTuile(Position,Tuile) boolean
    }
    class Rack
    class Pioche
    class Tuile {
        -Couleur couleur
        -Symbole symbole
    }
    class JoueurArtificiel {
        <<utility>>
        +choisirCoup(Plateau, List~Tuile~, Map)$ Optional~Coup~
    }

    Partie --> Arbitre
    Partie ..> Regles : applique
    Partie ..> Sauvegarde : instantané / reprise
    Arbitre "1" --> "2" Joueur
    Arbitre --> Plateau
    Joueur --> Rack
    Joueur --> Pioche
    Rack --> "0..5" Tuile
    JoueurArtificiel ..> Regles : mêmes règles que le joueur humain
```

## Séquence : tour de l'IA

Le joueur humain (glisser-déposer) et l'IA passent par la même méthode `Partie.placer(...)` : mêmes règles, mêmes
points, même pioche, sans qu'aucune des deux voies puisse diverger.

```mermaid
sequenceDiagram
    participant UI as ControleurDeJeu
    participant Partie
    participant IA as JoueurArtificiel
    participant Regles

    UI->>Partie: finirTour()
    UI->>UI: demarrerTour() : sauvegarde automatique
    Note over UI: joueur courant == IA
    UI->>UI: jouerTourIA() (pause ~700 ms)
    UI->>IA: choisirCoup(plateau, rack, tuilesPlacees)
    IA->>Regles: estPlacementValide / pointsGagnes (chaque case, chaque tuile)
    IA-->>UI: Optional<Coup>
    alt coup trouvé
        UI->>Partie: placer(position, tuile)
        Partie->>Regles: estPlacementValide, pointsGagnes
    else aucun coup légal
        Note over UI: l'IA passe son tour
    end
    UI->>Partie: estTerminee() ?
    UI->>Partie: finirTour()
```

## Sauvegarde et reprise

À chaque début de tour, `Partie.instantane(...)` produit un `Sauvegarde` (joueurs, scores, racks, pioches et leur
position, plateau, joueur courant, mode solo, couleurs) écrit atomiquement dans `~/.latice/partie_en_cours.properties`.
Le menu principal affiche *Continuer* si un fichier valide existe ; `Partie.restaurer(...)` reconstruit la partie à
l'identique (les pioches reprennent exactement où elles en étaient). Le fichier est supprimé à la fin de la partie ; un
fichier absent, corrompu ou d'une autre version est simplement ignoré.

## Pourquoi ces choix

- **`latice.metier` sans JavaFX** : l'IA, les règles, la partie et la sauvegarde sont testés par de simples tests JUnit
  (parties IA contre IA complètes, allers-retours de sauvegarde), sans fenêtre ni thread FX.
- **`Partie` possède l'état, `ControleurDeJeu` l'affiche** : le contrôleur ne contient plus de règle de jeu.
- **`Navigation` comme point d'entrée unique** : ouvrir le menu, démarrer ou reprendre une partie passe toujours par la
  même classe (lancement, « Continuer », « Rejouer », « Menu principal »).
- **Données redirigeables** (`-Dlatice.donnees=...`) : les tests ne touchent jamais à `~/.latice`.
