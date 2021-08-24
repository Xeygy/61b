package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.InputDemo.StringInputDevice;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

    /** random inputs */
    int seed = 0; //modified by input
    Random random;
    Player player;
    InputSource iP;
    TETile[][] world;
    //TEMP TESTING MAIN CLASS
    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        Engine engine = new Engine();
        //multiRender(10, engine, 5);
        //ter.renderFrame(engine.interactWithInputString("n8990sWAAAAAASSDD"));
        engine.interactWithKeyboard();
    }
    //helper to run multiple tests, secs is the amount of time in between renders
    public static void multiRender(int n, Engine engine, int secs) {
        TERenderer ter = new TERenderer();
        for (int i = 0; i < n; i++) {
            int seed = (int) (Math.random() * 10000); //cool seed 8602, 6254; 5195 has a thruline; 8990 wall bug
            ter.renderFrame(engine.interactWithInputString("n" + seed + "s"));
            System.out.println(seed);
            try {
                TimeUnit.SECONDS.sleep(secs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void interactWithInputSource() {
        //assumes input source is created
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        boolean nextScreen = false;
        InputSource inputSource = iP;
        StdDraw.text(WIDTH/2, HEIGHT/2, "press N for new game");
        while (!nextScreen && inputSource.possibleNextInput()) {
            switch (inputSource.getNextKey()) {
                case 'N':
                    //new game
                    nextScreen = true;
                case 'L':
                    //load game, do last
                case 'Q':
                    //quit
            }
        }
        nextScreen = false;
        String seed = "";
        String ints = "1234567890";
        while (!nextScreen  && inputSource.possibleNextInput()) {
            char c = inputSource.getNextKey();
            if (ints.indexOf(c) == -1) {
                nextScreen = true;
            } else {
                seed = seed + c;
            }
        }
        this.seed = Integer.parseInt(seed);
        random = new Random(this.seed);
        roomGen(world, 2, 6, 20);
        generateWalls(world);
        ter.renderFrame(world);
    }
    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        boolean nextScreen = false;
        InputSource inputSource = new KeyboardInputSource();
        StdDraw.text(WIDTH/2, HEIGHT/2, "press N for new game");
        while (!nextScreen && inputSource.possibleNextInput()) {
            switch (inputSource.getNextKey()) {
                case 'N':
                    //new game
                    nextScreen = true;
                case 'L':
                    //load game, do last
                case 'Q':
                    //quit
            }
        }
        nextScreen = false;
        String seed = "";
        String ints = "1234567890";
        while (!nextScreen  && inputSource.possibleNextInput()) {
            char c = inputSource.getNextKey();
            if (ints.indexOf(c) == -1) {
                nextScreen = true;
            } else {
                seed = seed + c;
            }
        }
        this.seed = Integer.parseInt(seed);
        random = new Random(this.seed);


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

        /** generate world */
        int nIndex = input.indexOf("n");
        int sIndex = input.indexOf("s");
        if (nIndex != -1 && sIndex != -1) {
            seed = Integer.parseInt(input.substring(nIndex + 1, sIndex)); //default 0
        }
        random = new Random(seed);
        roomGen(finalWorldFrame, 2, 6, 20);
        generateWalls(finalWorldFrame);

        //TODO: make modifiable
        InputSource inputSource = new StringInputDevice(input.substring(sIndex + 1));
        /** register movement */
        while (inputSource.possibleNextInput()) {
            char c = inputSource.getNextKey();
            switch (c) {
                case 'W':
                    player.moveUp(finalWorldFrame);
                    continue;
                case 'A':
                    player.moveLeft(finalWorldFrame);
                    continue;
                case 'S':
                    player.moveDown(finalWorldFrame);
                    continue;
                case 'D':
                    player.moveRight(finalWorldFrame);
                    continue;
            }
        }
        return finalWorldFrame;
    }

    //TODO: clean code, make numRooms easily modifiable
    private void roomGen(TETile[][] tiles, int minDim, int maxDim, int numRooms) {
        int maxMinDiff = maxDim - minDim;
        int prevX = 0;
        int prevY = 0;
        for (int i = 0; i < 18; i++) {
            int width = random.nextInt(maxMinDiff) + minDim;
            int height = random.nextInt(maxMinDiff) + minDim;
            if (i == 0) {
                prevX = random.nextInt(WIDTH - 2) + 1; //spacing on the border so the path doesn't generate on an edge
                prevY = random.nextInt(HEIGHT - 2) + 1;
                drawRect(tiles, Tileset.FLOOR, prevX - width/2, prevY - height/2, prevX + width/2, prevY + height/2);
                player = new Player(prevX, prevY, tiles);
            }
            int randomX = random.nextInt(WIDTH / 10) + i * WIDTH / 20;
            int randomY = random.nextInt(HEIGHT / 2 ) + ((HEIGHT - 6) / 2) * (i % 2); // -6 is to match the roomwidth gen
            if (i == 1 || i == 19) {
                generateVertPath(tiles, prevX, prevY, randomX, randomY);
            } else {
                generatePath(tiles, prevX, prevY, randomX, randomY);
            }
            drawRect(tiles, Tileset.FLOOR, randomX - width/2, randomY - height/2, randomX + width/2, randomY + height/2);
            prevX = randomX;
            prevY = randomY;
        }
    }
    /** x1 < x2, y1 < y2 */
    private void drawRect(TETile[][] tiles, TETile tileType, int x1, int y1, int x2, int y2) {
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                addPoint(tiles, tileType, x, y);
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
        } else if (x == 0 || y == 0 || x == WIDTH - 1 || y == HEIGHT - 1) {
            return true;
        }
        return false;
    }

    /** generates a path between point x1 y1 and x2 y2
     * looks like this:
     *              ||==========X
     *              ||
     *              ||
     *    X=========||
     *              ^ halfway point
     **/
    /** randomly generates either a path sticking out from the side of a room or from the top/bottom of the room */
    private void generatePath(TETile[][] tiles, int x1, int y1, int x2, int y2) {
        int rand = random.nextInt(2);
        switch (rand) {
            case 0:
                generateHorizPath(tiles,x1,y1,x2,y2);
            case 1:
                generateVertPath(tiles,x1,y1,x2,y2);
        }
    }
    private static void generateHorizPath(TETile[][] tiles, int x1, int y1, int x2, int y2) {
        int verticalHallwayLoc = x1 - (x1-x2) / 2;
        hLine(tiles, Tileset.FLOOR, y1, x1, verticalHallwayLoc);
        hLine(tiles, Tileset.FLOOR, y2, verticalHallwayLoc, x2);
        vLine(tiles, Tileset.FLOOR, verticalHallwayLoc, y1, y2);
    }
    private static void generateVertPath(TETile[][] tiles, int x1, int y1, int x2, int y2) {
        int horizHallwayLoc = y1 - (y1-y2) / 2;
        vLine(tiles, Tileset.FLOOR, x1, y1, horizHallwayLoc);
        vLine(tiles, Tileset.FLOOR, x2, horizHallwayLoc, y2);
        hLine(tiles, Tileset.FLOOR, horizHallwayLoc, x1, x2);
    }

    /** exception handler, allows calls to draw outside the bounds without error */
    private static void addPoint(TETile[][] tiles, TETile tileType, int x, int y) {
        if (y < 0 || y >= HEIGHT) {
            return;
        }
        if (x < 0 || x >= WIDTH) {
            return;
        }
        if (tiles[x][y].equals(Tileset.NOTHING)) {
            tiles[x][y] = tileType;
        }
    }
    /** draws horizontal line, at y, starting at xStart (inclusive) and ending at xEnd (inclusive) */
    private static void hLine(TETile tiles[][], TETile tileType, int y, int xStart, int xEnd) {
        if (xEnd < xStart) {
            int temp = xEnd;
            xEnd = xStart;
            xStart = temp;
        }
        for (int x = xStart; x <= xEnd; x++) {
            addPoint(tiles, tileType, x, y);
        }
    }
    /** draws vertical line, at x, starting at yStart (inclusive) and ending at yEnd (inclusive) */
    private static void vLine(TETile tiles[][], TETile tileType, int x, int yStart, int yEnd) {
        if (yStart > yEnd) {
            int temp = yEnd;
            yEnd = yStart;
            yStart = temp;
        }
        for (int y = yStart; y <= yEnd; y++) {
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
