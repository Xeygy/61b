//package byow.Core;
//
//import byow.TileEngine.TETile;
//import byow.TileEngine.Tileset;
//
//public class DrawUtils {
//    /** draws a rectangle, bottom left corner (x, y) with specified width and height */
//    public static void drawRectangle(TETile[][] tiles, TETile tileType,int x, int y, int width, int height) {
//        for (int i = x; i < x + width; i++) {
//            for (int j = y; j < y + height; j++) {
//                addPoint(tiles, tileType, i, j);
//            }
//        }
//    }
//    /** adds walls to existing floor blocks */
//    private static void generateWalls(TETile[][] tiles) {
//        for (int x = 0; x < WIDTH; x++) {
//            for (int y = 0; y < HEIGHT; y++) {
//                if (isValidWallSpace(tiles, x, y)) {
//                    tiles[x][y] = Tileset.WALL;
//                }
//            }
//        }
//    }
//    /** checks if point is valid wall location */
//    private static boolean isValidWallSpace(TETile[][] tiles, int x, int y) {
//        if (tiles[x][y].equals(Tileset.NOTHING)) {
//            //scans tiles around the tile to see if it has a floor tile near it
//            for (int i = x - 1; i <= x + 1; i++) {
//                for (int j = y - 1; j <= y + 1; j++) {
//                    if (i >= 0 && i < WIDTH && j >= 0 && j < HEIGHT &&
//                            tiles[i][j].equals(Tileset.FLOOR)) {
//                        return true;
//                    }
//                }
//            }
//        }
//        return false;
//    }
//    /** generates a path between point x1 y1 and x2 y2 */
//    private static void generatePath(TETile[][] tiles, int x1, int y1, int x2, int y2) {
//
//    }
//    /** exception handler */
//    private static void addPoint(TETile[][] tiles, TETile tileType, int x, int y) {
//        if (y < 0 || y >= HEIGHT) {
//            return;
//        }
//        if (x < 0 || x >= WIDTH) {
//            return;
//        }
//        tiles[x][y] = tileType;
//    }
//}
