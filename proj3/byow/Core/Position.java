package byow.Core;

public class Position {

    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Position)) {
            throw new IllegalArgumentException("Must compare Position to Position");
        }
        Position other = (Position) o;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return this.x * 103 + this.y;
    }

}
