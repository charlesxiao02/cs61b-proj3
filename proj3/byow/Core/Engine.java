package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.awt.*;
import java.io.*;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;
    private Avatar player;
    private int worldsTraveled;
    private static final int LIMIT = 4;
    private String allKeys;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        worldsTraveled = 0;
        ter.initialize(WIDTH, HEIGHT + 4);
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        drawStartMenu();
        int mouseX = 0;
        int mouseY = 0;
        Random r;
        boolean play = true;
        startMusic();
        allKeys = "";
        InputSource inputSource = new KeyboardInputSource();
        while (play) {
            if (!StdDraw.hasNextKeyTyped()) {
                Position pointer = updateHUD(mouseX, mouseY, world);
                mouseX = pointer.x();
                mouseY = pointer.y();
            } else {
                char key = Character.toUpperCase(StdDraw.nextKeyTyped());
                allKeys += key;
                switch (key) {
                    case ':':
                        if (inputSource.possibleNextInput()) {
                            key = inputSource.getNextKey();
                        }
                        if (key == 'Q') {
                            allKeys += key;
                            saveToFile(world, player, allKeys);
                            System.exit(0);
                        }
                        break;
                    case 'L':
                        Object[] loaded = loadFromFile();
                        world = (TETile[][]) loaded[0];
                        player = (Avatar) loaded[1];
                        allKeys = (String) loaded[2] + key;
                        r = new Random(20);
                        break;
                    case 'N':
                        String seed = "";
                        if (inputSource.possibleNextInput()) {
                            key = inputSource.getNextKey();
                        }
                        while (key != 'S') {
                            seed += key;
                            allKeys += key;
                            if (inputSource.possibleNextInput()) {
                                key = inputSource.getNextKey();
                            }
                        }
                        world = (WorldGenerator.generateWorld(Long.parseLong(seed)));
                        player = placeAvatar(world);
                        break;
                    case 'R':
                        loaded = loadFromFile();
                        allKeys = (String) loaded[2];
                        world = interactWithInputString(allKeys.substring(0, allKeys.length() - 2));
                    case 'E':
                        System.exit(0);
                        break;
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
                    default:
                        break;
                }
                if (player.enteredDoor()) {
                    doorSound();
                    Long newSeed;
                    try {
                        newSeed = WorldGenerator.getRandomGen(world).nextLong();
                    } catch (NullPointerException e) {
                        r = new Random(player.position().x() * player.position().y());
                        newSeed = r.nextLong();
                    }
                    world = WorldGenerator.generateWorld(newSeed);
                    player = placeAvatar(world);
                    worldsTraveled++;
                    if (worldsTraveled > LIMIT) {
                        System.exit(0);
                    }
                }
                Position pointer = updateHUD(mouseX, mouseY, world);
                mouseX = pointer.x();
                mouseY = pointer.y();
            }
        }
    }

    private Avatar placeAvatar(TETile[][] world) {
        Random r = new Random(10);
        int x = r.nextInt(WIDTH - 2) + 1;
        int y = r.nextInt(HEIGHT - 2) + 1;
        while (!world[x][y].equals(Tileset.FLOOR)) {
            x = r.nextInt(WIDTH - 2) + 1;
            y = r.nextInt(HEIGHT - 2) + 1;
        }
        Avatar avatar = new Avatar(x, y, world[x][y]);
        world[x][y] = Tileset.AVATAR;
        return avatar;
    }

    private void saveToFile(TETile[][] world, Avatar player, String keys) {
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(new FileOutputStream("savefile.txt"));
            os.writeObject(world);
            os.writeObject(player);
            os.writeObject(keys);
            System.out.println("Save successful");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Object[] loadFromFile() {
        Object[] input = new Object[3];
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream("savefile.txt"));
            input[0] = is.readObject();
            input[1] = is.readObject();
            input[2] = is.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return input;
    }

    private Position updateHUD(int mousex, int mousey, TETile[][] worldinput) {
        StdDraw.enableDoubleBuffering();
        int newMouseX = (int) StdDraw.mouseX();
        int newMouseY = (int) StdDraw.mouseY();
        if (newMouseX >= WIDTH) {
            newMouseX = WIDTH - 1;
        } else if (newMouseY >= HEIGHT) {
            newMouseY = HEIGHT - 1;
        } else if (newMouseX <= 0) {
            newMouseX = 0;
        } else if (newMouseY <= 0) {
            newMouseY = 0;
        }
        if (newMouseX != mousex) {
            mousex = newMouseX;
        }
        if (newMouseY != mousey) {
            mousey = newMouseY;
        }
        if (worldinput[0][0] != null && worldinput[mousex][mousey] != null) {
            StdDraw.clear();
            ter.renderFrame(worldinput);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.textLeft(1, HEIGHT + 2, "You see a " + worldinput[mousex][mousey].description() + " here");
            if (player.hasKey()) {
                StdDraw.setPenColor(StdDraw.YELLOW);
                StdDraw.text(78, HEIGHT + 2, "⚷");
            }
            StdDraw.enableDoubleBuffering();
            StdDraw.show();
        }
        StdDraw.pause(20);

        return new Position(mousex, mousey);
    }
    private void startMusic() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("Magic-Clock-Shop.au").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }

    private void doorSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("glitchedtones_Door-Bedroom-Open-01.au").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }
    private void drawStartMenu() {
        StdDraw.clear(StdDraw.BLACK);
        Font title = new Font(Font.MONOSPACED, Font.BOLD, 32);
        Font subtitle = new Font(Font.MONOSPACED, Font.BOLD, 16);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setFont(title);
        StdDraw.text(WIDTH / 2, HEIGHT * 3 / 4, "CS61B Project");
        StdDraw.setFont(subtitle);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "(N)ew Game");
        StdDraw.text(WIDTH / 2, HEIGHT * 3 / 8, "(L)oad Game");
        StdDraw.text(WIDTH / 2, HEIGHT / 4, "(R)eplay Last Save");
        StdDraw.text(WIDTH / 2, HEIGHT / 8, "(E)xit");
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

        ter.initialize(WIDTH, HEIGHT + 4);
        if (input.length() == 0) {
            return null;
        }
        char firstKey = Character.toUpperCase(input.charAt(0));
        int index = 1;
        TETile[][] finalWorldFrame = null;
        if (firstKey != 'L') {
            String seedString = "";
            while (input.charAt(index) > 47 && input.charAt(index) < 58) {
                seedString += input.charAt(index);
                index++;
            }
            Long seed = Long.parseLong(seedString);
            index++;
            finalWorldFrame = WorldGenerator.generateWorld(seed);
            player = placeAvatar(finalWorldFrame);
        } else if (firstKey == 'L') {
            Object[] loaded = loadFromFile();
            finalWorldFrame = (TETile[][]) loaded[0];
            player = (Avatar) loaded[1];
        }
        ter.renderFrame(finalWorldFrame);
        while (index < input.length() + 1) {
            char key = input.charAt(index - 1);
            key = Character.toUpperCase(key);
            switch (key) {
                case ':':
                    key = input.charAt(index);
                    key = Character.toUpperCase(key);
                    if (key == 'Q') {
                        saveToFile(finalWorldFrame, player, allKeys);
                        System.exit(0);
                    }
                    break;
                case 'L':
                    Object[] loaded = loadFromFile();
                    finalWorldFrame = (TETile[][]) loaded[0];
                    player = (Avatar) loaded[1];
                    allKeys = (String) loaded[2] + key;
                    break;
                case 'E':
                    System.exit(0);
                    break;
                case 'W':
                    player.moveAvatar(finalWorldFrame, 0, 1);
                    break;
                case 'A':
                    player.moveAvatar(finalWorldFrame, -1, 0);
                    break;
                case 'S':
                    player.moveAvatar(finalWorldFrame, 0, -1);
                    break;
                case 'D':
                    player.moveAvatar(finalWorldFrame, 1, 0);
                    break;
                default:
                    break;
            }
            if (player.enteredDoor()) {
                Long newSeed;
                try {
                    newSeed = WorldGenerator.getRandomGen(finalWorldFrame).nextLong();
                } catch (NullPointerException e) {
                    Random r = new Random(player.position().x() * player.position().y());
                    newSeed = r.nextLong();
                }
                finalWorldFrame = WorldGenerator.generateWorld(newSeed);
                player = placeAvatar(finalWorldFrame);
                worldsTraveled++;
                if (worldsTraveled > LIMIT) {
                    System.exit(0);
                }
            }
            index++;
            ter.renderFrame(finalWorldFrame);
            StdDraw.pause(20);
        }
        ter.renderFrame(finalWorldFrame);
        return finalWorldFrame;
    }
}
