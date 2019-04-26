package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Room {
    private Position location;
    private int width;
    private int height;

    public Room (Position place, int w, int h) {
        location = place;
        width = w;
        height = h;
    }

    public Position getStartPos() {
        return location;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public boolean isInRoom(Position interest) {
        int xMax = location.x() + width - 1;
        int yMax = location.y() + height - 1;

        if ((interest.x() < xMax) && (interest.x() > location.x())) {
            if ((interest.y() < yMax) && (interest.y() > location.y())) {
                return true;
            }
        }
        return false;
    }
}
