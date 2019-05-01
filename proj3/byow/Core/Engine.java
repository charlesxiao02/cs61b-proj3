package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;
    private Avatar player;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {

        //draw start menu
        //wait for key
        //if n, prompt seed
        //gen world
        //while next keys not :Q
        //act accordingly
        //if l, load from last thing
        //using a txt file??? To track all previous moves?
        //also could help implement a replay for ambition points

        ter.initialize(WIDTH, HEIGHT + 2);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        drawStartMenu();
        String allKeysPressed = ""; //track keys pressed, either for world loading or ambition points of replay
        boolean play = true;
        while (play) {
            char key = ' ';
            if (StdDraw.hasNextKeyTyped()) {
                key = StdDraw.nextKeyTyped();
                allKeysPressed += key;
            }
            switch (key) {
                case ':':
                    if (StdDraw.hasNextKeyTyped()) {
                        key = StdDraw.nextKeyTyped();
                        allKeysPressed += key;
                    }
                    if (key == 'Q') {
                        allKeysPressed += key;
                        play = false;
                    } else {
                        allKeysPressed = allKeysPressed.substring(0, allKeysPressed.length() - 1);
                    }
                    break;
                case 'N':
                    String seed = "";
                    if (StdDraw.hasNextKeyTyped()) {
                        key = StdDraw.nextKeyTyped();
                        allKeysPressed += key;
                    }
                    while (key != 'S') {
                        seed += key;
                        if (StdDraw.hasNextKeyTyped()) {
                            key = StdDraw.nextKeyTyped();
                            allKeysPressed += key;
                        }
                    }
                    world = (WorldGenerator.generateWorld(Long.parseLong(seed)));
                    player = placeAvatar(world);
                case 'E':
                    play = false;
                case 'W':
                    player.moveAvatar(world, 0, 1);
                    break;
                case 'A':
                    player.moveAvatar(world, -1, 0);
                    break;
                case 'S':
                    player.moveAvatar(world, 0, -1);
                    break;
                case 'D':
                    player.moveAvatar(world, 1, 0);
                    break;
                case 'Q':
                    player.moveAvatar(world, -1, 1);
                    break;
                case 'E':
                    player.moveAvatar(world, 1, 1);
                    break;
                case 'Z':
                    player.moveAvatar(world, -1, -1);
                    break;
                case 'X':
                    player.moveAvatar(world, 1, -1);
                    break;
            }
        }
        File savefile = new File("savefile.txt");
        try {
            savefile.createNewFile();
        } catch (IOException e) {
            System.out.println("Error occured.");
        }
    }

    private Avatar placeAvatar(TETile[][] world) {
        Random r = new Random();
        int x = r.nextInt(WIDTH - 2) + 1;
        int y = r.nextInt(HEIGHT - 2) + 1;
        while (!world[x][y].equals(Tileset.FLOOR)) {
            x = r.nextInt(WIDTH - 2) + 1;
            y = r.nextInt(HEIGHT - 2) + 1;
        }
        world[x][y] = Tileset.AVATAR;
        return new Avatar(x, y, world[x][y]);
    }

    private void drawStartMenu() {
        StdDraw.clear(StdDraw.BLACK);
        Font title = new Font("Monaco", Font.BOLD, 32);
        Font subtitle = new Font("Monaco", Font.PLAIN, 20);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(title);
        StdDraw.text(WIDTH / 2, HEIGHT * 3 / 4, "CS61B Project");
        StdDraw.setFont(subtitle);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "(N)ew Game");
        StdDraw.text(WIDTH / 2, HEIGHT * 3 / 8, "(L)oad Game");
        StdDraw.text(WIDTH / 2, HEIGHT / 4, "(E)xit");
        StdDraw.show();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        ter.initialize(WIDTH, HEIGHT + 2);
        String firstKey = input.substring(0, 1);
        long seed = Long.parseLong(input.substring(1, input.length() - 1));
        String lastKey = input.substring(input.length() - 1);
        TETile[][] finalWorldFrame = WorldGenerator.generateWorld(seed);
        ter.renderFrame(finalWorldFrame);
        return finalWorldFrame;
    }
}
