package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Player {
    public int x;
    public int y;
    //the current floor the player is standing on
    public TETile currFloor;
    public int width;
    public int height;

    public Player(int x, int y, TETile[][] tiles) {
        currFloor = tiles[x][y];
        tiles[x][y] = Tileset.AVATAR;

        System.out.println(x + " " + y);
        this.x = x;
        this.y = y;
        width = tiles.length;
        height = tiles[0].length;
    }

    public void moveLeft(TETile[][] tiles) {
        if (x > 0 && !tiles[x-1][y].equals(Tileset.WALL)) {
            teleport(x-1, y, tiles);
        }
    }
    public void moveRight(TETile[][] tiles) {
        if (x < width - 1 && !tiles[x+1][y].equals(Tileset.WALL)) {
            teleport(x+1, y, tiles);
        }
    }
    public void moveUp(TETile[][] tiles) {
        if (y < height - 1 && !tiles[x][y+1].equals(Tileset.WALL)) {
            teleport(x, y+1, tiles);
        }
    }
    public void moveDown(TETile[][] tiles) {
        if (y > 0 && !tiles[x][y-1].equals(Tileset.WALL)) {
            teleport(x, y-1, tiles);
        }
    }

    //assumes x and y are in bounds of tiles[][]
    //teleports the player to (newX, newY)
    private void teleport(int newX, int newY, TETile[][] tiles) {
        tiles[x][y] = currFloor;
        currFloor = tiles[newX][newY];
        tiles[newX][newY] = Tileset.AVATAR;
        x = newX;
        y = newY;
    }
}
