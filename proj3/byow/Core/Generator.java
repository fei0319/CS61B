package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Generator {
    public static final double RETRY_FACTOR_MULTIPLIER = 0.94;
    public static final double POSSIBILITY_FOR_EXTRA_SIZE = 0.7;
    public static final int EXTRA_SIZE = 4;
    public static final int IGNORE_DISTANCE_LIMIT = 100;
    public static final double BERNOULLIAN_SIZE_PARAMETER = 0.12;
    public static final int GENERATION_COUNT = 32;
    public static final int TEST_WIDTH = 80;
    public static final int TEST_HEIGHT = 30;
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
        int result = RandomUtils.bernoulli(rng, POSSIBILITY_FOR_EXTRA_SIZE) ? 0 : EXTRA_SIZE;
        for (int i = 0; i < n; ++i) {
            if (RandomUtils.bernoulli(rng, BERNOULLIAN_SIZE_PARAMETER)) {
                result += 1;
            }
        }
        result = Math.max(result, 2);
        return result;
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

    int distance(Pair<Integer, Integer> a, Pair<Integer, Integer> b) {
        return Math.abs(a.first - b.first) + Math.abs(a.second - b.second);
    }

    /**
     * Check whether there is a {@link Tileset#FLOOR} tile in
     * the specified direction. If there is one, return the
     * coordination of the certain tile.
     *
     * @return a {@link Pair} of the coordination or null indicating
     * no {@link Tileset#FLOOR} being in the direction
     */
    private Pair<Integer, Integer> floorInDirection(TETile[][] tiles, int x, int y, int dirX, int dirY) {
        while (inbound(x, y)) {
            if (tiles[x][y] == Tileset.FLOOR) {
                return new Pair<>(x, y);
            }
            x += dirX;
            y += dirY;
        }
        return null;
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
    private void tryCreateHallway(
            TETile[][] tiles, int x, int y, UnionFindSet ufs, boolean ignoreDistance) {
        if (tiles[x][y] == Tileset.FLOOR) {
            return;
        }

        ArrayList<Pair<Integer, Integer>> directions = new ArrayList<>();
        directions.add(new Pair<>(1, 0));
        directions.add(new Pair<>(-1, 0));
        directions.add(new Pair<>(0, 1));
        directions.add(new Pair<>(0, -1));

        int root = -1;
        ArrayList<Pair<Integer, Integer>> available = new ArrayList<>();
        for (Pair<Integer, Integer> direction : directions) {
            if (available.size() >= 2) {
                break;
            }
            Pair<Integer, Integer> coord = floorInDirection(tiles, x, y, direction.first, direction.second);
            if (coord != null && ufs.getRoot(coord) != root
                    && (ignoreDistance || distance(coord, new Pair<>(x, y)) <= 5)) {
                available.add(direction);
                if (root == -1) {
                    root = ufs.getRoot(coord);
                }
            }
        }

        if (available.size() >= 2) {
            for (Pair<Integer, Integer> direction : available) {
                createHallway(tiles, x, y, direction.first, direction.second);
            }
        }
    }

    private void addHallways(TETile[][] tiles) {
        UnionFindSet ufs = new UnionFindSet(width, height);
        ufs.update(tiles);

        int cnt = 0;
        while (ufs.count(tiles) > 1) {
            cnt += 1;
            int x = RandomUtils.uniform(rng, width), y = RandomUtils.uniform(rng, height);
            tryCreateHallway(tiles, x, y, ufs, cnt > IGNORE_DISTANCE_LIMIT);
            ufs.update(tiles);
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

        for (int i = 0; i < GENERATION_COUNT; ++i) {
            generateRoom(tiles, factor);
            factor *= RETRY_FACTOR_MULTIPLIER;
        }

        addHallways(tiles);

        addBrim(tiles, Tileset.WALL);
        return tiles;
    }

    public static void main(String[] args) {
        int w = TEST_WIDTH, h = TEST_HEIGHT;
        var gen = new Generator(w, h, new Random().nextInt());
        var tiles = gen.generate();

        var render = new TERenderer();
        render.initialize(w, h);
        render.renderFrame(tiles);
    }
}