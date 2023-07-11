package byow.Core;

import byow.TileEngine.TETile;

public class Utils {
    /**
     * Load the world from the specified TETile[][] tiles.
     * All stuff of tiles will be simply copied to world.
     */
    public static void loadFrom(TETile[][] world, TETile[][] tiles) {
        for (int i = 0; i < world.length; ++i) {
            for (int j = 0; j < world[i].length; ++j) {
                world[i][j] = tiles[i][j];
            }
        }
    }
}
