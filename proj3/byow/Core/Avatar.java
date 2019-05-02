package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Avatar {

    private static final int LIMIT = 4;
    private Position position;
    private TETile currTile;
    private boolean hasKey;
    private int worldsTraveled;

    public Avatar(Position p, TETile t) {
        position = p;
        currTile = t;
        hasKey = false;
        worldsTraveled = 0;
    }

    public Avatar(int x, int y, TETile t) {
        position = new Position(x, y);
        currTile = t;
        hasKey = false;
        worldsTraveled = 0;
    }

    public TETile currentTile() {
        return currTile;
    }

    public Position position() {
        return position;
    }

    public TETile[][] moveAvatar(TETile[][] world, int deltX, int deltY) {
        int newX = position.x() + deltX;
        int newY = position.y() + deltY;
        if (world[newX][newY].equals(Tileset.FLOOR)) {
            world[position.x()][position.y()] = currTile;
            currTile = world[newX][newY];
            position = new Position(newX, newY);
            world[newX][newY] = Tileset.AVATAR;
        } else if (world[newX][newY].equals(Tileset.KEY)) {
            world[position.x()][position.y()] = currTile;
            currTile = Tileset.FLOOR;
            position = new Position(newX, newY);
            world[newX][newY] = Tileset.AVATAR;
            hasKey = true;
            System.out.println("You picked up a key");
        } else if (world[newX][newY].equals(Tileset.LOCKED_DOOR) && hasKey) {
            if (worldsTraveled < LIMIT) {
                System.out.println("You open the door and enter");
                world = WorldGenerator.generateWorld(WorldGenerator.getRandomGen(world).nextLong());
                worldsTraveled++;
            }
        }
        return world;
    }

}
