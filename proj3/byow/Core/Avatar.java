package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Avatar {

    private Position position;
    private TETile currTile;

    public Avatar(Position p, TETile t) {
        position = p;
        currTile = t;
    }

    public Avatar(int x, int y, TETile t) {
        position = new Position(x, y);
        currTile = t;
    }

    public TETile currentTile() {
        return currTile;
    }

    public Position position() {
        return position;
    }

    public void moveAvatar(TETile[][] world, int deltX, int deltY) {
        int newX = position.x() + deltX;
        int newY = position.y() + deltY;
        if (world[newX][newY].equals(Tileset.FLOOR)) {
            world[position.x()][position.y()] = currTile;
            currTile = world[newX][newY];
            position = new Position(newX, newY);
            world[newX][newY] = Tileset.AVATAR;
        }
    }

}
