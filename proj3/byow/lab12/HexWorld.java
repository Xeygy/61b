package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 40;
    private static final int HEIGHT = 40;
    private static final int HEXSIZE = 3;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] hexTiles = new TETile[WIDTH][HEIGHT];
        fillWithNothingTiles(hexTiles);

        fillWithHexTiles(hexTiles);
        ter.renderFrame(hexTiles);
    }

    private static void fillWithNothingTiles(TETile[][] tiles) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }
    //automatically scales the hexTile picture based on width and height;
    private static void fillWithHexTiles(TETile[][] tiles) {
        int maxTileSize = 0;
        if ((WIDTH+6)/11 < HEIGHT/10) {
            maxTileSize = (WIDTH+6)/11;
        } else {
            maxTileSize = HEIGHT/10;
        }
        int x = (WIDTH/2) - (maxTileSize/2);
        int yOffset = (HEIGHT - maxTileSize * 10)/2;
        int y = HEIGHT - 1 - yOffset;
        fillWithHexTiles(tiles, maxTileSize, x, y);
    }
    //fills the screen with a sidelength 3 hexagon of hexTiles downward starting at (x, y)
    private static void fillWithHexTiles(TETile[][] tiles, int sidelength, int x, int y) {
        int x2 = x;
        int vertLineLength = 5;
        for (int i = 0; i < 3; i++) {
            vertHexTileLine(tiles, vertLineLength, sidelength, x, y);
            vertHexTileLine(tiles, vertLineLength, sidelength, x2, y);

            x = x + 2 * sidelength - 1;
            x2 = x2 - 2 * sidelength + 1;
            y = y - sidelength;
            vertLineLength--;
        }
    }

    //(x, y) is the top left corner of the first hexTile
    //draws a vertical line of hexTiles downward starting at (x, y)
    //sideLength is the sidelength of each individual tile
    private static void vertHexTileLine(TETile[][] tiles, int lineLength, int sidelength, int x, int y) {
        for (int i = 0; i < lineLength; i++) {
            TETile randomTile = randomTile();
            addHexTile(tiles, sidelength, x, y, randomTile);
            y = y - sidelength * 2;
        }
    }
    //adds a hexagon at (x, y), where (x, y) is the top left corner of the hexagon
    private static void addHexTile(TETile[][] tiles, int sidelength, int x, int y, TETile tileType) {
        int lineStart = x;
        int lineEnd = x + sidelength;
        //draw top half
        for (int i = 0; i < sidelength; i++) {
            addLine(tiles, lineStart, lineEnd, y, tileType);
            lineStart--;
            lineEnd++;
            y--;
        }
        //draw bottom half
        for (int i = 0; i < sidelength; i++) {
            lineStart++;
            lineEnd--;
            addLine(tiles, lineStart, lineEnd, y, tileType);
            y--;
        }
    }
    //draws a horizontal line of tiles starting at (xStart, y), start inclusive, end exclusive
    private static void addLine(TETile[][] tiles, int xStart, int xEnd, int y, TETile tileType) {
        if (y < 0 || y >= HEIGHT) {
            return;
        }
        if (xStart < 0) {
            xStart = 0;
        }
        for (int x = xStart; x < xEnd && x < WIDTH; x++) {
            tiles[x][y] = tileType;
        }
    }
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(5);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.SAND;
            case 3: return Tileset.MOUNTAIN;
            case 4: return Tileset.TREE;
            default: return Tileset.NOTHING;
        }
    }
}
