package byow.Core;

public class Avatar {

    private Position position;

    public Avatar(Position p) {
        position = p;
    }

    public Avatar(int x, int y) {
        position = new Position(x, y);
    }

}
