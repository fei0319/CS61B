package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Generator {
    private int width;
    private int height;
    private long seed;
    private Random rng;

    public Generator(int w, int h, long seed) {
        this.width = w;
        this.height = h;
        this.seed = seed;
        this.rng = new Random(seed);
    }


    /**
     * Get a random size for the room.
     * The distribution of the size is
     * defined by implementation.
     *
     * @return size
     */
    private int randomSize() {
        int n = Math.min(width, height);
        int result = RandomUtils.bernoulli(rng, 0.7) ? 0 : 4;
        for (int i = 0; i < n; ++i) {
            if (RandomUtils.bernoulli(rng, 0.12)) {
                result += 1;
            }
        }
        result = Math.max(result, 2);
        return result;
    }

    /**
     * C++ style pair.
     */
    private class Pair<V1, V2> {
        public V1 first;
        public V2 second;

        public Pair(V1 v1, V2 v2) {
            this.first = v1;
            this.second = v2;
        }
    }

    /**
     * Determine whether a room of size (w, h)
     * can be placed in (x, y).
     *
     * @param tiles the tiles
     * @return true iff a room of size (w, h)
     * can be placed in (x, y)
     */
    private boolean canHold(TETile[][] tiles, int x, int y, int w, int h) {
        if (x <= 0 || y <= 0) {
            return false;
        }
        if (x + w >= width || y + h >= height) {
            return false;
        }
        for (int i = x; i < x + w; ++i) {
            for (int j = y; j < y + h; ++j) {
                if (tiles[i][j] != Tileset.NOTHING) {
                    return false;
                }
                if (hasNeighbor(tiles, i, j, Tileset.FLOOR)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Return all positions in which a room of
     * size (w, h) can be placed.
     *
     * @param tiles the tiles
     * @return positions in which the room can
     * be placed
     */
    private ArrayList<Pair<Integer, Integer>> positions(TETile[][] tiles, int w, int h) {
        ArrayList<Pair<Integer, Integer>> result = new ArrayList<>();
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (canHold(tiles, i, j, w, h)) {
                    result.add(new Pair<Integer, Integer>(i, j));
                }
            }
        }
        return result;
    }

    private boolean inbound(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private boolean hasNeighbor(TETile[][] tiles, int x, int y, TETile tile) {
        ArrayList<Pair<Integer, Integer>> neighbors = new ArrayList<>();
        neighbors.add(new Pair<>(1, 1));
        neighbors.add(new Pair<>(-1, 1));
        neighbors.add(new Pair<>(1, 0));
        neighbors.add(new Pair<>(-1, 0));
        neighbors.add(new Pair<>(1, -1));
        neighbors.add(new Pair<>(-1, -1));
        neighbors.add(new Pair<>(0, 1));
        neighbors.add(new Pair<>(0, -1));

        for (Pair<Integer, Integer> neighbor : neighbors) {
            int dx = neighbor.first, dy = neighbor.second;
            if (inbound(x + dx, y + dy) && tiles[x + dx][y + dy] == tile) {
                return true;
            }
        }
        return false;
    }

    private void addRoom(TETile[][] tiles, int x, int y, int w, int h) {
        for (int i = x; i < x + w; ++i) {
            for (int j = y; j < y + h; ++j) {
                tiles[i][j] = Tileset.FLOOR;
            }
        }
    }

    private void addBrim(TETile[][] tiles, TETile tile) {
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (tiles[i][j] != Tileset.FLOOR && hasNeighbor(tiles, i, j, Tileset.FLOOR)) {
                    tiles[i][j] = tile;
                }
            }
        }
    }

    /**
     * Try to generate a rectangle room in random position
     * and of random size.
     * This method will try to randomly generate width and
     * height for the room and randomly pick a position
     * that can place a room of such scale. If no positions
     * fulfill the requirement, will retry with a fixed
     * possibility.
     *
     * @param factor possibility for a retry upon failure
     * @return true iff the generation is successful
     */
    private boolean generateRoom(TETile[][] tiles, double factor) {
        do {
            int w = randomSize(), h = randomSize();
            ArrayList<Pair<Integer, Integer>> poss = positions(tiles, w, h);
            if (poss.isEmpty()) {
                continue;
            }
            int index = RandomUtils.uniform(rng, poss.size());
            Pair<Integer, Integer> pos = poss.get(index);
            addRoom(tiles, pos.first, pos.second, w, h);
            return true;

        } while (RandomUtils.bernoulli(rng, factor));
        return false;
    }

    /**
     * Check whether there is a {@link Tileset#FLOOR} tile in
     * the specified direction.
     *
     * @return true iff there is a {@link Tileset#FLOOR} tile
     * in the direction
     */
    private boolean hasFloor(TETile[][] tiles, int x, int y, int dirX, int dirY) {
        while (inbound(x, y)) {
            if (tiles[x][y] == Tileset.FLOOR) {
                return true;
            }
            x += dirX;
            y += dirY;
        }
        return false;
    }

    /**
     * Create a hallway of specified direction from the
     * specified location to the nearest {@link Tileset#FLOOR}.
     */
    private void createHallway(TETile[][] tiles, int x, int y, int dirX, int dirY) {
        do {
            tiles[x][y] = Tileset.FLOOR;
            x += dirX;
            y += dirY;
        } while (inbound(x, y) && tiles[x][y] != Tileset.FLOOR);
    }

    /**
     * Try to create several hallways starting from (x, y).
     */
    private void tryCreateHallway(TETile[][] tiles, int x, int y) {
        ArrayList<Pair<Integer, Integer>> directions = new ArrayList<>();
        directions.add(new Pair<>(1, 0));
        directions.add(new Pair<>(-1, 0));
        directions.add(new Pair<>(0, 1));
        directions.add(new Pair<>(0, -1));

        int cnt = 0;
        for (Pair<Integer, Integer> direction : directions) {
            if (hasFloor(tiles, x, y, direction.first, direction.second)) {
                createHallway(tiles, x, y, direction.first, direction.second);
                cnt += 1;
            }
            if (cnt >= 2) {
                break;
            }
        }
    }

    /**
     * Generate a pseudorandom world.
     *
     * @return the world
     */
    public TETile[][] generate() {
        TETile[][] tiles = new TETile[width][height];
        for (TETile[] tile : tiles) {
            Arrays.fill(tile, Tileset.NOTHING);
        }

        double factor = 1.0;

        for (int i = 0; i < 32; ++i) {
            generateRoom(tiles, factor);
            factor *= 0.94;
        }

        for (int i = 0; i < 16; ++i) {
            int x = RandomUtils.uniform(rng, width), y = RandomUtils.uniform(rng, height);

            tryCreateHallway(tiles, x, y);
        }

        addBrim(tiles, Tileset.WALL);
        return tiles;
    }

    public static void main(String args[]) {
        int w = 80, h = 30;
        var gen = new Generator(w, h, new Random().nextInt());
        var tiles = gen.generate();

        var render = new TERenderer();
        render.initialize(w, h);
        render.renderFrame(tiles);
    }
}
