package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class UnionFindSet {
    private int width;
    private int height;
    private int[] belong;

    private int code(int x, int y) {
        return x * height + y;
    }

    private int code(Pair<Integer, Integer> a) {
        return code(a.first, a.second);
    }

    public UnionFindSet(int width, int height) {
        this.width = width;
        this.height = height;
        this.belong = new int[width * height];
        for (int i = 0; i < belong.length; ++i) {
            belong[i] = i;
        }
    }

    public int getRoot(int x) {
        if (belong[x] == x) {
            return x;
        }
        belong[x] = getRoot(belong[x]);
        return belong[x];
    }

    public int getRoot(Pair<Integer, Integer> x) {
        return getRoot(code(x));
    }

    public void connect(Pair<Integer, Integer> a, Pair<Integer, Integer> b) {
        int x = getRoot(code(a)), y = getRoot(code(b));
        if (x != y) {
            belong[x] = y;
        }
    }

    private boolean isRoot(int x, int y) {
        return getRoot(code(x, y)) == code(x, y);
    }

    public void update(TETile[][] tiles) {
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (tiles[i][j] == Tileset.FLOOR) {
                    if (i > 0 && tiles[i - 1][j] == Tileset.FLOOR) {
                        connect(new Pair<>(i, j), new Pair<>(i - 1, j));
                    }
                    if (j > 0 && tiles[i][j - 1] == Tileset.FLOOR) {
                        connect(new Pair<>(i, j), new Pair<>(i, j - 1));
                    }
                }
            }
        }
    }

    public int count(TETile[][] tiles) {
        int result = 0;
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                if (tiles[i][j] == Tileset.FLOOR && isRoot(i, j)) {
                    result += 1;
                }
            }
        }
        return result;
    }
}
