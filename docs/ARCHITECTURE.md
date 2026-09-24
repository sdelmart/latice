# Architecture

## Vue d'ensemble

Le code est séparé en deux packages qui ne dépendent jamais l'un de l'autre dans le mauvais sens :
`latice.metier` ne connaît rien de JavaFX (donc testable sans lancer d'interface graphique), et
`latice.controleur` orchestre l'UI en s'appuyant sur `latice.metier`.

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
        ARB[Arbitre]
        PLA[Plateau]
        JOU[Joueur]
        RAC[Rack]
        PIO[Pioche]
        IA[JoueurArtificiel]
        HIST[HistoriqueParties]
    end

    A --> CA
    L --> CJ
    P --> CP
    F --> CF

    CA --> NAV
    CF --> NAV
    NAV --> CJ

    CJ --> ARB
    CJ --> IA
    CJ --> HIST
    CJ --> MUS
    CJ --> SFX
    CJ --> THEME

    ARB --> JOU
    ARB --> PLA
    JOU --> RAC
    JOU --> PIO
    IA --> PLA
```

## Diagramme de classes (cœur du moteur de jeu)

```mermaid
classDiagram
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
        +ajouterPoints(Integer) void
    }
    class Plateau {
        -Map~Position,Tuile~ tuilesPlacees
        +casePlateau(int,int) TypeCase
        +placerTuile(Position,Tuile) boolean
    }
    class Rack {
        -List~Tuile~ tuiles
        +ajouterTuile(Tuile) boolean
        +retirerTuile(Tuile) boolean
    }
    class Pioche {
        -List~Tuile~ tuiles
        +piocher() Tuile
        +estVide() boolean
    }
    class Tuile {
        -Couleur couleur
        -Symbole symbole
    }
    class JoueurArtificiel {
        <<utility>>
        +choisirCoup(Plateau, List~Tuile~, Map, boolean) Optional~Coup~
    }
    class Coup {
        +Position position
        +Tuile tuile
        +int score
    }

    Arbitre "1" --> "2" Joueur
    Arbitre --> Plateau
    Joueur --> Rack
    Joueur --> Pioche
    Rack --> "0..5" Tuile
    JoueurArtificiel ..> Coup : produit
    JoueurArtificiel ..> Plateau : lit
    JoueurArtificiel ..> Tuile : évalue
```

## Séquence : tour de l'IA

Illustre comment le joueur humain (drag & drop) et l'IA convergent vers la même méthode
`placerTuile(...)`, garantissant qu'aucune des deux voies ne peut appliquer des règles différentes.

```mermaid
sequenceDiagram
    participant Arbitre
    participant ControleurDeJeu
    participant JoueurArtificiel
    participant Plateau

    ControleurDeJeu->>Arbitre: gererTours()
    ControleurDeJeu->>ControleurDeJeu: mettreAJourTour()
    Note over ControleurDeJeu: joueurCourant == IA
    ControleurDeJeu->>ControleurDeJeu: jouerTourIA() (PauseTransition ~700ms)
    ControleurDeJeu->>JoueurArtificiel: choisirCoup(plateau, rack, tuilesPlacees, premiereTuile)
    JoueurArtificiel->>Plateau: casePlateau(x,y) / évalue chaque position valide
    JoueurArtificiel-->>ControleurDeJeu: Optional<Coup>
    alt coup trouvé
        ControleurDeJeu->>ControleurDeJeu: placerTuile(joueur2, position, tuile)
        Note over ControleurDeJeu: même méthode que pour le joueur humain (drag & drop)
    else aucun coup valide
        Note over ControleurDeJeu: l'IA passe son tour
    end
    ControleurDeJeu->>Arbitre: gererTours()
    ControleurDeJeu->>ControleurDeJeu: mettreAJourTour()
```

## Pourquoi ces choix

- **`latice.metier` sans JavaFX** : `JoueurArtificiel` et `HistoriqueParties` sont testés par de simples tests JUnit, sans lancer de `Stage` ni de thread FX.
- **`Navigation` comme point d'entrée unique** : ouvrir le menu principal ou démarrer une partie passe toujours par la même classe, que ce soit au lancement de l'app, depuis "Nouvelle partie", ou depuis "Rejouer"/"Menu principal" en fin de partie — élimine la duplication de code de câblage FXML.
- **`placerTuile(...)` partagée** : le joueur humain (glisser-déposer) et l'IA appliquent la tuile via la même méthode, donc les mêmes règles de score/rack/repioche, sans jamais pouvoir diverger.
