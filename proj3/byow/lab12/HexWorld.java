package byow.lab12;

import org.junit.Test;

import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Arrays;
import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    /**
     * Draws a hexagon of specified tile the square who is in
     * is located at (x, y).
     *
     * @param tiles tiles to draw hexagon in
     * @param x     position specifier
     * @param y     position specifier
     * @param size  size of the hexagon
     * @param tile  tile type to fill in the hexagon
     */
    private static void addHexagon(TETile[][] tiles, int x, int y, int size, TETile tile) {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size + i * 2; ++j) {
                int xpos = x + size - 1 - i + j;
                tiles[xpos][y + i] = tile;
                tiles[xpos][y + size * 2 - i - 1] = tile;
            }
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] tiles = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; ++i) {
            Arrays.fill(tiles[i], Tileset.NOTHING);
        }
        addHexagon(tiles, 0, 0, 5, Tileset.FLOWER);

        ter.renderFrame(tiles);
    }
}
