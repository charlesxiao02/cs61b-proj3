package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;

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

        ter.initialize(WIDTH, HEIGHT);
        drawStartMenu();

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
        StdDraw.text(WIDTH / 2, HEIGHT / 4, "(Q)uit");
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

        //ter.initialize(WIDTH, HEIGHT);
        String firstKey = input.substring(0, 1);
        long seed = Long.parseLong(input.substring(1, input.length() - 1));
        String lastKey = input.substring(input.length() - 1);
        TETile[][] finalWorldFrame = WorldGenerator.generateWorld(seed);
        //ter.renderFrame(finalWorldFrame);
        return finalWorldFrame;
    }
}
