package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.HashMap;
import java.util.Random;

public class WorldGenerator {

    private static final int WORLD_WIDTH = 80;
    private static final int WORLD_HEIGHT = 40;

    public static TETile[][] generateWorld(long seed) {
        Random randomGen = new Random(seed);
        TETile[][] world = new TETile[WORLD_WIDTH][WORLD_HEIGHT];
        world = initializeWorld(world, WORLD_WIDTH, WORLD_HEIGHT);
        int numRooms = randomGen.nextInt((WORLD_HEIGHT * WORLD_WIDTH / 75) - 4) + 4;
        int numHalls = randomGen.nextInt(numRooms) + numRooms + 2;
        WeightedQuickUnionUF rooms = new WeightedQuickUnionUF(numRooms);
        HashMap<Integer, Room> roomNumbers = new HashMap<>();
        int roomsGenerated = 0;
        while (roomsGenerated < numRooms) {
            int x = Math.floorMod(randomGen.nextInt(), WORLD_WIDTH - 4);
            int y = Math.floorMod(randomGen.nextInt(), WORLD_HEIGHT - 4);
            while (!(world[x][y].equals(Tileset.NOTHING))
                    || !(world[x + 4][y + 4].equals(Tileset.NOTHING))) {
                x = Math.floorMod(randomGen.nextInt(), WORLD_WIDTH - 4);
                y = Math.floorMod(randomGen.nextInt(), WORLD_HEIGHT - 4);
            }
            int w = Math.floorMod(randomGen.nextInt(), (WORLD_WIDTH / 5) - 4) + 4;
            int h = Math.floorMod(randomGen.nextInt(), (WORLD_HEIGHT / 5) - 4) + 4;
            if (x + w > WORLD_WIDTH) {
                w = WORLD_WIDTH - 1 - x;
            }
            if (y + h > WORLD_HEIGHT) {
                h = WORLD_HEIGHT - 1 - y;
            } //prevent overlap properly
            if (!checkEdgesUsed(world, new Position(x, y), w, h)) {
                continue;
            }
            if ((h > 3) && (w > 3)) {
                makeRoom(world, new Position(x, y), w, h);
                Room newRoom = new Room(new Position(x, y), w, h);
                roomNumbers.put(roomsGenerated, newRoom);
                roomsGenerated++;
            }
        }
        int hallsGenerated = 0;
        while (!allConnected(rooms, numRooms)) {
            int room1 = randomGen.nextInt(numRooms);
            int room2 = randomGen.nextInt(numRooms);
            while (rooms.connected(room1, room2)) {
                room2 = randomGen.nextInt(numRooms);
            }
            Position spot1 = roomNumbers.get(room1).getRandomSpotInRoom(randomGen);
            Position spot2 = roomNumbers.get(room2).getRandomSpotInRoom(randomGen);
            if ((spot1.x() == spot2.x()) || (spot1.y() == spot2.y())) {
                makeHallway(world, spot1, spot2);
            } else {
                boolean oneWayOrAnother = randomGen.nextInt() % 2 == 0;
                Position spot3;
                if (oneWayOrAnother) {
                    spot3 = new Position(spot1.x(), spot2.y());
                } else {
                    spot3 = new Position(spot2.x(), spot1.y());
                }
                makeHallway(world, spot1, spot3);
                makeHallway(world, spot2, spot3);
            }
            rooms.union(room1, room2);
            hallsGenerated++;
        }
        genRandomHalls(hallsGenerated, numHalls, randomGen, world);
        return world;
    }

    private static void genRandomHalls(int hallsGenerated, int numHalls,
                                       Random randomGen, TETile[][] world) {
        while (hallsGenerated < numHalls) {
            int x = randomGen.nextInt(WORLD_WIDTH - 3) + 1; //pick random int in world for x
            int y = randomGen.nextInt(WORLD_HEIGHT - 3) + 1; //same for y
            while (!world[x][y].equals(Tileset.FLOOR)) {
                x = randomGen.nextInt(WORLD_WIDTH - 3) + 1;
                y = randomGen.nextInt(WORLD_HEIGHT - 3) + 1;
            }
            boolean genHorizontalHall = randomGen.nextInt() % 2 == 0;
            if (genHorizontalHall) {
                int x2 = randomGen.nextInt(WORLD_WIDTH / 4) + x;
                if (x2 > WORLD_WIDTH - 2) {
                    x2 = WORLD_WIDTH - 2;
                }
                makeHallway(world, new Position(x, y), new Position(x2, y));
                hallsGenerated++;
            } else {
                int y2 = randomGen.nextInt(WORLD_HEIGHT / 4) + y;
                if (y2 > WORLD_HEIGHT - 2) {
                    y2 = WORLD_HEIGHT - 2;
                }
                makeHallway(world, new Position(x, y), new Position(x, y2));
                hallsGenerated++;
            }
        }
    }

    private static TETile[][] initializeWorld(TETile[][] world, int w, int h) {
        world = new TETile[w][h];
        for (int r = 0; r < w; r++) {
            for (int c = 0; c < h; c++) {
                world[r][c] = Tileset.NOTHING;
            }
        }
        return world;
    }

    private static boolean checkEdgesUsed(TETile[][] world, Position p, int w, int h) {
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

    private static boolean allConnected(WeightedQuickUnionUF x, int size) {
        int parent1 = x.find(0);
        for (int i = 1; i < size; i++) {
            if (x.find(i) != parent1) {
                return false;
            }
        }
        return true;
    }

    private static void makeRoom(TETile[][] world, Position p, int w, int h) {
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
            if (start.y() > end.y()) {
                Position temp = start;
                start = end;
                end = temp;
            }
            if (!world[end.x()][end.y()].equals(Tileset.FLOOR)) { //cap top side
                for (int i = -1; i < 2; i++) {
                    if (!world[end.x() + i][end.y() + 1].equals(Tileset.FLOOR)) {
                        world[end.x() + i][end.y() + 1] = Tileset.WALL;
                    }
                }
            }
            if (!world[start.x()][start.y()].equals(Tileset.FLOOR)) { //cap bottom side
                for (int i = -1; i < 2; i++) {
                    if (!world[start.x() + i][start.y() - 1].equals(Tileset.FLOOR)) {
                        world[start.x() + i][start.y() - 1] = Tileset.WALL;
                    }
                }
            }
            int length = Math.abs(start.y() - end.y()) + 1;
            for (int i = 0; i < length; i++) {
                world[start.x()][start.y() + i] = Tileset.FLOOR;
                if (!world[start.x() - 1][start.y() + i].equals(Tileset.FLOOR)) {
                    world[start.x() - 1][start.y() + i] = Tileset.WALL;
                }
                if (!world[start.x() + 1][start.y() + i].equals(Tileset.FLOOR)) {
                    world[start.x() + 1][start.y() + i] = Tileset.WALL;
                }
            }

        } else { //horizontal hallway
            if (start.x() > end.x()) {
                Position temp = start;
                start = end;
                end = temp;
            }
            if (!world[end.x()][end.y()].equals(Tileset.FLOOR)) { //cap left side
                for (int i = -1; i < 2; i++) {
                    if (!world[end.x() + 1][end.y() + i].equals(Tileset.FLOOR)) {
                        world[end.x() + 1][end.y() + i] = Tileset.WALL;
                    }
                }
            }
            if (!world[start.x()][start.y()].equals(Tileset.FLOOR)) { //cap right side
                for (int i = -1; i < 2; i++) {
                    if (!world[start.x() - 1][start.y() + i].equals(Tileset.FLOOR)) {
                        world[start.x() - 1][start.y() + i] = Tileset.WALL;
                    }
                }
            }
            int length = Math.abs(start.x() - end.x()) + 1;
            for (int i = 0; i < length; i++) {
                world[start.x() + i][start.y()] = Tileset.FLOOR;
                if (!world[start.x() + i][start.y() - 1].equals(Tileset.FLOOR)) {
                    world[start.x() + i][start.y() - 1] = Tileset.WALL;
                }
                if (!world[start.x() + i][start.y() + 1].equals(Tileset.FLOOR)) {
                    world[start.x() + i][start.y() + 1] = Tileset.WALL;
                }
            }
        }
    }


    /*
    //testing purposes
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WORLD_WIDTH, WORLD_HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WORLD_WIDTH][WORLD_HEIGHT];

        for (int r = 0; r < WORLD_WIDTH; r++) {
            for (int c = 0; c < WORLD_HEIGHT; c++) {
                world[r][c] = Tileset.NOTHING;
            }
        }

        // fills in a block 14 tiles wide by 4 tiles tall

        world = generateWorld(8963186996212760330L);

        //makeRoom(world, new Position(WORLD_WIDTH- 20, WORLD_HEIGHT - 14), 7, 7);
        //makeHallway(world, new Position(25, 1), new Position(25, WORLD_HEIGHT - 2));
        //makeHallway(world, new Position(3, 7), new Position(11, 7));
        //makeHallway(world, new Position(11, 7), new Position(11, 15));
        //makeHallway(world, new Position(3, 7), new Position(3, 15));
        //makeHallway(world, new Position(3, 15), new Position(11, 15));

        // draws the world to the screen
        ter.renderFrame(world);
    }*/

}
