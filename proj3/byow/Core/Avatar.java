package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.io.Serializable;
import java.util.Random;

public class Avatar implements Serializable {

    private Position position;
    private TETile currTile;
    private boolean hasKey;
    private boolean enteredDoor;

    public Avatar(Position p, TETile t) {
        position = p;
        currTile = t;
        hasKey = false;
    }

    public Avatar(int x, int y, TETile t) {
        position = new Position(x, y);
        currTile = t;
        hasKey = false;
    }

    public TETile currentTile() {
        return currTile;
    }

    public Position position() {
        return position;
    }

    public boolean hasKey() {
        return hasKey;
    }

    public TETile[][] moveAvatar(TETile[][] world, int deltX, int deltY) {
        int newX = position.x() + deltX;
        int newY = position.y() + deltY;
        if (world[newX][newY].equals(Tileset.FLOOR)) {
            Random r = new Random();
            if (r.nextBoolean()) {
                stepping();
            }
            world[position.x()][position.y()] = Tileset.FLOOR;
            currTile = world[newX][newY];
            position = new Position(newX, newY);
            world[newX][newY] = Tileset.AVATAR;
        } else if (world[newX][newY].equals(Tileset.KEY)) {
            pickedUpKey();
            world[position.x()][position.y()] = currTile;
            currTile = Tileset.FLOOR;
            position = new Position(newX, newY);
            world[newX][newY] = Tileset.AVATAR;
            hasKey = true;
            System.out.println("You picked up a key");
        } else if (world[newX][newY].equals(Tileset.LOCKED_DOOR) && hasKey) {
            System.out.println("You open the door and enter");
            enteredDoor = true;
        }
        return world;
    }
    private void pickedUpKey() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("household_key_car_electronic_pick_up_from_table_002.au").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }
    private void stepping() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("zapsplat_foley_footstep_single_trainer_on_wooden_step_011_27751.au").getAbsoluteFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }

    public boolean enteredDoor() {
        return enteredDoor;
    }

}
