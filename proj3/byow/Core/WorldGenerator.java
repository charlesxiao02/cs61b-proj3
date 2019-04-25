package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class WorldGenerator {

    public static TETile[][] generateWorld(long seed) {

        Random randomGen = new Random(seed);
        int roomWidth = 120;
        int roomHeight = 60;
        TETile[][] world = new TETile[roomWidth][roomHeight];
        for (int r = 0; r < roomHeight; r++) {
            for (int c = 0; c < roomWidth; c++) {
                world[c][r] = Tileset.NOTHING;
            }
        }

        int numRooms = Math.floorMod(randomGen.nextInt(), ((roomHeight * roomWidth) / 75) - 4) + 4;
        int numHalls = randomGen.nextInt();
        while ((numHalls < numRooms) || (numHalls > numRooms * 2)) { //keeping number of hallways enough to connect all rooms but not too crazy
            numHalls = randomGen.nextInt();
        }

        for (int i = 0; i < numRooms; i++) { //rooms can have position from (1, 1) up to (rW - 3, rH - 3)
            int x = Math.floorMod(randomGen.nextInt(), roomWidth - 4) + 1;
            int y = Math.floorMod(randomGen.nextInt(), roomHeight - 4) + 1;
            while (!(world[x - 1][y - 1].equals(Tileset.NOTHING))
                    || !(world[x + 3][y + 3].equals(Tileset.NOTHING))) {
                x = Math.floorMod(randomGen.nextInt(), roomWidth - 4) + 1;
                y = Math.floorMod(randomGen.nextInt(), roomHeight - 4) + 1;
            }
            int w = Math.floorMod(randomGen.nextInt(), (roomWidth / 8) - 2) + 2;
            int h = Math.floorMod(randomGen.nextInt(), (roomHeight / 8) - 2) + 2;
            if (x + w > roomWidth - 2) {
                w = roomWidth - 2 - x;
            }
            if (y + h > roomHeight - 2) {
                h = roomHeight - 2 - y;
            }
            while (!(world[x - 1][y + h + 1].equals(Tileset.NOTHING))
                    || !(world[x + w + 1][y + h + 1].equals(Tileset.NOTHING))) {
                h--;
            }
            while (!(world[x + w + 1][y -1].equals(Tileset.NOTHING))
                    || !(world[x + w + 1][y + h + 1].equals(Tileset.NOTHING))) {
                w--;
            }
            makeRoom(world, new Position(x, y), w, h);
        }

        return world;
    }

    private static void makeRoom(TETile[][] world, Position p, int w, int h) {
        for (int r = 0; r < h; r++) {
            for (int c = 0; c < w; c++) {
                world[p.x() + c][p.y() + r] = Tileset.FLOOR;
            }
        } //setting top and bottom walls
        for (int c = -1; c < w + 1; c++) {
            world[p.x() + c][p.y() - 1] = Tileset.WALL;
            world[p.x() + c][p.y() + h] = Tileset.WALL;
        } //setting left right walls
        for (int r = 0; r < h; r++) {
            world[p.x() - 1][p.y() + r] = Tileset.WALL;
            world[p.x() + w][p.y() + r] = Tileset.WALL;
        }
    }

    private static void makeHallway(TETile[][] world, Position start, Position end) {
        if (start.x() == end.x()) { //vertical hallway
            int length = Math.abs(start.y() - end.y());
            for (int i = 0; i < length; i++) {
                world[start.x()][start.y() + i] = Tileset.FLOOR;
                world[start.x() - 1][start.y() + i] = Tileset.WALL;
                world[start.x() + 1][start.y() + i] = Tileset.WALL;
            }
        } else { //horizontal hallway
            int length = Math.abs(start.x() - end.x());
            for (int i = 0; i < length; i++) {
                world[start.x() + i][start.y()] = Tileset.FLOOR;
                world[start.x() + i][start.y() - 1] = Tileset.WALL;
                world[start.x() + i][start.y() + 1] = Tileset.WALL;
            }
        }
    }

     //testing purposes
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(120, 60);

        // initialize tiles
        TETile[][] world = new TETile[120][60];

        // fills in a block 14 tiles wide by 4 tiles tall


        world = generateWorld(78155778);

        // draws the world to the screen
        ter.renderFrame(world);
    }

}
