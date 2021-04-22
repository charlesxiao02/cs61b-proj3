
package byow.Core;

import java.io.Serializable;
import java.util.Random;

public class Room implements Serializable {
    private Position location;
    private int width;
    private int height;

    public Room(Position place, int w, int h) {
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

    public Position getRandomSpotInRoom(Random randomGen) {
        int x = randomGen.nextInt(width - 2) + location.x() + 1;
        int y = randomGen.nextInt(height - 2) + location.y() + 1;
        return new Position(x, y);
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Room)) {
            throw new IllegalArgumentException("Must compare Room to Room");
        }
        Room other = (Room) o;
        return this.location.equals(other.location)
                && this.width == other.width
                && this.height == other.height;
    }

    @Override
    public int hashCode() {
        return (location.x() * 53 * 53 * 53) + (location.y() * 53 * 53) + (width * 53) + height;
    }

}
