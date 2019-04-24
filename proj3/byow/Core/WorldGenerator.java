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
                && ((h < 30) || (h > 90))) {
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


        return world;
    }

}
