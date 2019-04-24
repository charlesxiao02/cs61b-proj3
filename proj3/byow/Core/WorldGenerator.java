package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class WorldGenerator {

    public static TETile[][] generateWorld(long seed) {

        Random randomGen = new Random(seed);
        int roomWidth = 80;
        int roomHeight = 60;
        /*
        // remove w, h, initialize rW, rH in place. set number for testing purposes.
        int w = randomGen.nextInt();
        int h = randomGen.nextInt();
        while (((w < 50) || (w > 120))
                && ((h < 20) || (h > 90))) {
            w = randomGen.nextInt();
            h = randomGen.nextInt();
        }
        roomWidth = w;
        roomHeight = h;*/
        TETile[][] world = new TETile[roomHeight][roomWidth];
        for (int r = 0; r < roomHeight; r++) {
            for (int c = 0; c < roomWidth; c++) {
                world[r][c] = Tileset.NOTHING;
            }
        }

        int numRooms = randomGen.nextInt();
        while (numRooms / (roomHeight * roomWidth) < 75) { //attempts to keep number of rooms reasonable relative to world size
            numRooms = randomGen.nextInt();
        }
        int numHalls = randomGen.nextInt();
        while ((numHalls < numRooms) || (numHalls > numRooms * 2)) { //keeping number of hallways enough to connect all rooms but not too crazy
            numHalls = randomGen.nextInt();
        }

        return world;
    }

    private static void makeRoom(TETile[][] world, Position p, int w, int h) {
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                world[p.y() + r][p.x() + c] = Tileset.FLOOR;
            }
        } //setting top and bottom walls
        for (int c = -1; c < w + 1; c++) {
            world[p.y() - 1][p.x() + c] = Tileset.WALL;
            world[p.y() + h][p.x() + c] = Tileset.WALL;
        } //setting left right walls
        for (int r = 0; r < w; r++) {
            world[p.y() + r][p.x() - 1] = Tileset.WALL;
            world[p.y() + r][p.x() + w] = Tileset.WALL;
        }
    }

}
