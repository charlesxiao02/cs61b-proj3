package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class WorldGenerator {

    private static final int worldWidth = 80;
    private static final int worldHeight = 40;

    public static TETile[][] generateWorld(long seed) {

        TERenderer ter = new TERenderer();
        ter.initialize(worldWidth, worldHeight);

        Random randomGen = new Random(seed);
        TETile[][] world = new TETile[worldWidth][worldHeight];
        for (int r = 0; r < worldHeight; r++) {
            for (int c = 0; c < worldWidth; c++) {
                world[c][r] = Tileset.NOTHING;
            }
        }

        int numRooms = Math.floorMod(randomGen.nextInt(), ((worldHeight * worldWidth) / 75) - 4) + 4;
        int numHalls = randomGen.nextInt();
        while ((numHalls < numRooms) || (numHalls > numRooms * 2)) { //keeping number of hallways enough to connect all rooms but not too crazy
            numHalls = randomGen.nextInt();
        }

        int roomsGenerated = 0;

        while (roomsGenerated < numRooms) { //rooms can have position from (0, 0) up to (rW - 4, rH - 4)
            int x = Math.floorMod(randomGen.nextInt(), worldWidth - 4);
            int y = Math.floorMod(randomGen.nextInt(), worldHeight - 4);
            while (!(world[x][y].equals(Tileset.NOTHING))
                    || !(world[x + 4][y + 4].equals(Tileset.NOTHING))) {
                x = Math.floorMod(randomGen.nextInt(), worldWidth - 4);
                y = Math.floorMod(randomGen.nextInt(), worldHeight - 4);
            }
            int w = Math.floorMod(randomGen.nextInt(), (worldWidth / 5) - 4) + 4;
            int h = Math.floorMod(randomGen.nextInt(), (worldHeight / 5) - 4) + 4;
            if (x + w > worldWidth) {
                w = worldWidth - 1 - x;
            }
            if (y + h > worldHeight) {
                h = worldHeight - 1 - y;
            }

            //prevent overlap properly
            if (!checkEdgesUsed(world, new Position(x, y), w, h)) {
                continue;
            }
            if ((h > 3) && (w > 3)) {
                makeRoom(world, new Position(x, y), w, h);
                roomsGenerated++;
            }

            ter.renderFrame(world);
        }

        return world;
    }
   private static boolean checkEdgesUsed(TETile[][] world, Position p, int w, int h){
        for (int c = 0; c < w; c++) {
            if (!(world[p.x() + c][p.y()]).equals(Tileset.NOTHING)
                    || !(world[p.x() + c][p.y() + h - 1]).equals(Tileset.NOTHING)) {
                return false;
            }
        }
        for (int r = 1; r < h - 1; r++) {
            if (!(world[p.x()][p.y() + r]).equals(Tileset.NOTHING)
                    || !(world[p.x() + w - 1][p.y() + r]).equals(Tileset.NOTHING)) {
                return false;
            }
        }
        return true;

    }
    private static void makeRoom(TETile[][] world, Position p, int w, int h) { //w and h include walls
        for (int r = 1; r < h - 1; r++) {
            for (int c = 1; c < w - 1; c++) {
                world[p.x() + c][p.y() + r] = Tileset.FLOOR;
            }
        } //setting top and bottom walls
        for (int c = 0; c < w; c++) {
            world[p.x() + c][p.y()] = Tileset.WALL;
            world[p.x() + c][p.y() + h - 1] = Tileset.WALL;
        } //setting left right walls
        for (int r = 1; r < h - 1; r++) {
            world[p.x()][p.y() + r] = Tileset.WALL;
            world[p.x() + w - 1][p.y() + r] = Tileset.WALL;
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
        ter.initialize(worldWidth, worldHeight);

        // initialize tiles
        TETile[][] world = new TETile[worldWidth][worldHeight];

        for (int r = 0; r < worldWidth; r++) {
            for (int c = 0; c < worldHeight; c++) {
                world[r][c] = Tileset.NOTHING;
            }
        }

        // fills in a block 14 tiles wide by 4 tiles tall


        world = generateWorld(71556728);

        //makeRoom(world, new Position(worldWidth - 4, worldHeight - 4), 4, 4);

        // draws the world to the screen
        ter.renderFrame(world);
    }

}
