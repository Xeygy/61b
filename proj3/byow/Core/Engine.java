package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    /** random inputs */
    int seed = 0; //modified by input
    Random random = new Random(seed);

    //TEMP TESTING MAIN CLASS
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        Engine engine = new Engine();
        ter.renderFrame(engine.interactWithInputString("N12345S"));
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
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
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        fillWithNothingTiles(finalWorldFrame);

        /** read input */
        int nIndex = input.indexOf("n");
        int sIndex = input.indexOf("s");
        if (nIndex != -1 && sIndex != -1) {
            seed = Integer.parseInt(input.substring(nIndex + 1, sIndex));
            random = new Random(seed);
        }
        //generateRooms(finalWorldFrame, 40);
        generatePath(finalWorldFrame, 0, 0, 15, 15);
        generateWalls(finalWorldFrame);
        return finalWorldFrame;
    }
    /** adds n rectangular rooms with possible sidelengths between 2 and 6, inclusive */
    private void generateRooms(TETile[][] tiles, int n) {
        for (int i = 0; i < n; i++) {
            int roomX = random.nextInt(WIDTH);
            int roomY = random.nextInt(HEIGHT);
            int roomWidth = random.nextInt(5) + 2; //0 to 4, plus 2
            int roomHeight = random.nextInt(5) + 2;
            generateRoom(tiles, roomX, roomY, roomWidth, roomHeight);
        }
    }
    /** generates one rectangular room, bottom left corner (x, y) with specified width and height */
    private static void generateRoom(TETile[][] tiles, int x, int y, int width, int height) {
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                addPoint(tiles, Tileset.FLOOR, i, j);
            }
        }
    }
    /** adds walls to existing floor blocks */
    private static void generateWalls(TETile[][] tiles) {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                if (isValidWallSpace(tiles, x, y)) {
                    tiles[x][y] = Tileset.WALL;
                }
            }
        }
    }
    /** checks if point is valid wall location */
    private static boolean isValidWallSpace(TETile[][] tiles, int x, int y) {
        if (tiles[x][y].equals(Tileset.NOTHING)) {
            //scans tiles around the tile to see if it has a floor tile near it
            for (int i = x - 1; i <= x + 1; i++) {
                for (int j = y - 1; j <= y + 1; j++) {
                    if (i >= 0 && i < WIDTH && j >= 0 && j < HEIGHT &&
                        tiles[i][j].equals(Tileset.FLOOR)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /** generates a path between point x1 y1 and x2 y2
     * looks like this:
     *              ||==========X
     *              || ^random distance
     *              ||
     *    X=========||
     *      ^random distance
     * TODO: Make distance random
     **/
    private static void generatePath(TETile[][] tiles, int x1, int y1, int x2, int y2) {
        int verticalHallwayLoc = x1 - (x1-x2) / 2;
        hLine(tiles, Tileset.FLOOR, y1, x1, verticalHallwayLoc);
        hLine(tiles, Tileset.FLOOR, y2, verticalHallwayLoc, x2 + 1);
        vLine(tiles, Tileset.FLOOR, verticalHallwayLoc, y1, y2);
    }
    /** exception handler */
    private static void addPoint(TETile[][] tiles, TETile tileType, int x, int y) {
        if (y < 0 || y >= HEIGHT) {
            return;
        }
        if (x < 0 || x >= WIDTH) {
            return;
        }
        tiles[x][y] = tileType;
    }
    /** draws horizontal line, at y, starting at xStart (inclusive) and ending at xEnd (exclusive), start < end */
    private static void hLine(TETile tiles[][], TETile tileType, int y, int xStart, int xEnd) {
        for (int x = xStart; x < xEnd; x++) {
            addPoint(tiles, tileType, x, y);
        }
    }
    /** draws vertical line, at x, starting at yStart (inclusive) and ending at yEnd (exclusive), start < end */
    private static void vLine(TETile tiles[][], TETile tileType, int x, int yStart, int yEnd) {
        for (int y = yStart; y < yEnd; y++) {
            addPoint(tiles, tileType, x, y);
        }
    }
    private static void fillWithNothingTiles(TETile[][] tiles) {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

}
