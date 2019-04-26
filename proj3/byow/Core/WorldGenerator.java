package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.util.HashMap;
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
        WeightedQuickUnionUF rooms = new WeightedQuickUnionUF(numRooms);
        HashMap<Integer, Room> roomNumbers = new HashMap<>();
        //adding rooms oh man
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
                Room newRoom = new Room(new Position(x, y), w, h);
                roomNumbers.put(roomsGenerated, newRoom);
                roomsGenerated++;
                ter.renderFrame(world);
            }

            /*
            maybe make a hashmap with each room's pos, w, h, connected???
            to help with the connectedness and hallways
             */

            //adding hallways oh boy
            /*
            pick a point
            if floor, good
            if not, pick again
            go along horizontally or vertically until find more floor NOT IN SAME ROOM HOW TO DO THAT
            make hallway???
            And for more fun:
            pick two points that AREN'T floor
            connect!
             */
        }


        int hallsGenerated = 0;

        while (!allConnected(rooms, numRooms)) {
            int room1 = randomGen.nextInt(numRooms);
            int room2 = randomGen.nextInt(numRooms);
            while (rooms.connected(room1, room2)) {
                room2 = randomGen.nextInt(numRooms);
            }
            Position spot1 = roomNumbers.get(room1).getRandomSpotInRoom();
            Position spot2 = roomNumbers.get(room2).getRandomSpotInRoom();
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

        while (hallsGenerated < numHalls) {
            int x = randomGen.nextInt(worldWidth); //pick random int in world for x
            int y = randomGen.nextInt(worldHeight); //same for y
            while (!world[x][y].equals(Tileset.FLOOR)) {
                x = randomGen.nextInt(worldWidth);
                y = randomGen.nextInt(worldHeight);
            }
            boolean genHorizontalHall = randomGen.nextInt() % 2 == 0;
            if (genHorizontalHall) {
                int x2 = randomGen.nextInt(worldWidth / 4) + x;
                if (x2 > worldWidth) {
                    x2 = worldWidth - 1;
                }
                makeHallway(world, new Position(x, y), new Position(x2, y));
                hallsGenerated++;
            } else {
                int y2 = randomGen.nextInt(worldHeight / 4) + y;
                if (y2 > worldHeight) {
                    y2 = worldHeight - 1;
                }
                makeHallway(world, new Position(x, y), new Position(x, y2));
                hallsGenerated++;
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

    private static boolean allConnected(WeightedQuickUnionUF x, int size) {
        int parent1 = x.find(0);
        for (int i = 1; i < size; i++) {
            if (x.find(i) != parent1) {
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
            if (start.y() > end.y()) {
                Position temp = start;
                start = end;
                end = temp;
            }
            int length = Math.abs(start.y() - end.y());
            for (int i = 0; i < length; i++) {
                world[start.x()][start.y() + i] = Tileset.FLOOR;
                if (!world[start.x() - 1][start.y() + i].equals(Tileset.FLOOR)) {
                    world[start.x() - 1][start.y() + i] = Tileset.WALL;
                }
                if (!world[start.x() + 1][start.y() + i].equals(Tileset.FLOOR)) {
                    world[start.x() + 1][start.y() + i] = Tileset.WALL;
                }
            }
            /*
            if (!world[end.x()][end.y()].equals(Tileset.FLOOR)) {
                if (end.y() == worldHeight - 1) {
                    if (!world[end.x() - 1][end.y()].equals(Tileset.FLOOR)) {
                        world[end.x() - 1][end.y()] = Tileset.WALL;
                    }
                    if (!world[end.x()][end.y()].equals(Tileset.FLOOR)) {
                        world[end.x()][end.y()] = Tileset.WALL;
                    }
                    if (!world[end.x() + 1][end.y()].equals(Tileset.FLOOR)) {
                        world[end.x() + 1][end.y()] = Tileset.WALL;
                    }
                } else {
                    if (!world[end.x() - 1][end.y() + 1].equals(Tileset.FLOOR)) {
                        world[end.x() - 1][end.y() + 1] = Tileset.WALL;
                    }
                    if (!world[end.x()][end.y() + 1].equals(Tileset.FLOOR)) {
                        world[end.x()][end.y() + 1] = Tileset.WALL;
                    }
                    if (!world[end.x() + 1][end.y() + 1].equals(Tileset.FLOOR)) {
                        world[end.x() + 1][end.y() + 1] = Tileset.WALL;
                    }
                }
            }
            if (!world[start.x()][start.y()].equals(Tileset.FLOOR)) {
                if (start.y() == 0) {
                    if (!world[start.x() - 1][start.y()].equals(Tileset.FLOOR)) {
                        world[start.x() - 1][start.y()] = Tileset.WALL;
                    }
                    if (!world[start.x()][start.y()].equals(Tileset.FLOOR)) {
                        world[start.x()][start.y()] = Tileset.WALL;
                    }
                    if (!world[start.x() + 1][start.y()].equals(Tileset.FLOOR)) {
                        world[start.x() + 1][start.y()] = Tileset.WALL;
                    }
                } else {
                    if (!world[start.x() - 1][start.y() - 1].equals(Tileset.FLOOR)) {
                        world[start.x() - 1][start.y() - 1] = Tileset.WALL;
                    }
                    if (!world[start.x()][start.y() - 1].equals(Tileset.FLOOR)) {
                        world[start.x()][start.y() - 1] = Tileset.WALL;
                    }
                    if (!world[start.x() + 1][start.y() - 1].equals(Tileset.FLOOR)) {
                        world[start.x() + 1][start.y() - 1] = Tileset.WALL;
                    }
                }
            }
            */
        } else { //horizontal hallway
            if (start.x() > end.x()) {
                Position temp = start;
                start = end;
                end = temp;
            }
            int length = Math.abs(start.x() - end.x());
            for (int i = 0; i < length; i++) {
                world[start.x() + i][start.y()] = Tileset.FLOOR;
                if (!world[start.x() + i][start.y() - 1].equals(Tileset.FLOOR)) {
                    world[start.x() + i][start.y() - 1] = Tileset.WALL;
                }
                if (!world[start.x() + i][start.y() + 1].equals(Tileset.FLOOR)) {
                    world[start.x() + i][start.y() + 1] = Tileset.WALL;
                }
            }
            /*
            if (!world[end.x()][end.y()].equals(Tileset.FLOOR)) {
                if (end.x() == worldWidth - 1) {
                    if (!world[end.x()][end.y() - 1].equals(Tileset.FLOOR)) {
                        world[end.x()][end.y() - 1] = Tileset.WALL;
                    }
                    if (!world[end.x()][end.y()].equals(Tileset.FLOOR)) {
                        world[end.x()][end.y()] = Tileset.WALL;
                    }
                    if (!world[end.x()][end.y() + 1].equals(Tileset.FLOOR)) {
                        world[end.x()][end.y() + 1] = Tileset.WALL;
                    }
                } else {
                    if (!world[end.x() + 1][end.y() - 1].equals(Tileset.FLOOR)) {
                        world[end.x() + 1][end.y() - 1] = Tileset.WALL;
                    }
                    if (!world[end.x() + 1][end.y()].equals(Tileset.FLOOR)) {
                        world[end.x() + 1][end.y()] = Tileset.WALL;
                    }
                    if (!world[end.x() + 1][end.y() + 1].equals(Tileset.FLOOR)) {
                        world[end.x() + 1][end.y() + 1] = Tileset.WALL;
                    }
                }
            }
            if (!world[start.x()][start.y()].equals(Tileset.FLOOR)) {
                if (start.x() == 0) {
                    if (!world[start.x()][start.y() - 1].equals(Tileset.FLOOR)) {
                        world[start.x()][start.y() - 1] = Tileset.WALL;
                    }
                    if (!world[start.x()][start.y()].equals(Tileset.FLOOR)) {
                        world[start.x()][start.y()] = Tileset.WALL;
                    }
                    if (!world[start.x()][start.y() + 1].equals(Tileset.FLOOR)) {
                        world[start.x()][start.y() + 1] = Tileset.WALL;
                    }
                } else {
                    if (!world[start.x() - 1][start.y() - 1].equals(Tileset.FLOOR)) {
                        world[start.x() - 1][start.y() - 1] = Tileset.WALL;
                    }
                    if (!world[start.x() - 1][start.y()].equals(Tileset.FLOOR)) {
                        world[start.x() - 1][start.y()] = Tileset.WALL;
                    }
                    if (!world[start.x() - 1][start.y() + 1].equals(Tileset.FLOOR)) {
                        world[start.x() - 1][start.y() + 1] = Tileset.WALL;
                    }
                }
            }
            */
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


        world = generateWorld(712545728);

        //makeRoom(world, new Position(worldWidth - 20, worldHeight - 14), 7, 7);
        //makeHallway(world, new Position(worldWidth - 28, worldHeight - 12), new Position(worldWidth - 10, worldHeight - 12));

        // draws the world to the screen
        ter.renderFrame(world);
    }
}
