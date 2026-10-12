package latice.metier;

import java.util.Objects;

public class Position {
    private final int x;	// Coordonnée horizontale
    private final int y;	// Coordonnée verticale

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    
    public int posX() { return x; }
    public int posY() { return y; }

    // Redéfinition de equals pour comparer deux positions
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    // Redéfinition de hashCode pour l'utilisation dans des collections
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
