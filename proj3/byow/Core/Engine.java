package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.File;
import java.io.Serializable;

public class Engine implements Serializable {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final File SAVE_FILE = new File("savefile.txt");
    private TETile[][] world;
    private Pair<Integer, Integer> location;
    private TERenderer render;
    private Generator gen;
    private boolean inGame;

    public Engine() {
        world = new TETile[WIDTH][HEIGHT];
        location = new Pair<>(0, 0);
        render = new TERenderer();
        inGame = false;
    }

    private TETile[][] worldWithAvatar() {
        TETile[][] showWorld = new TETile[WIDTH][HEIGHT];
        Utils.loadFrom(showWorld, world);
        showWorld[location.first][location.second] = Tileset.AVATAR;
        return showWorld;
    }

    private void displayWorld() {
        inGame = true;

        render.renderFrame(worldWithAvatar());
    }

    private void displayTitle() {
        inGame = false;

        render.drawTitle();
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        InputSource in = new KeyboardInputSource();

        interact(in, true);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
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
        InputSource in = new StringInputDevice(input);

        return interact(in, false);
    }

    /**
     * Interact with the engine with a {@link InputSource}.
     *
     * @param in      the input source
     * @param display whether to display the UI when interacting
     * @return the final status of the world
     */
    private TETile[][] interact(InputSource in, boolean display) {
        if (display) {
            render.initialize(WIDTH, HEIGHT);
            displayTitle();
        }

        while (in.possibleNextInput()) {
            String operand = String.valueOf(in.getNextKey()).toUpperCase();
            switch (operand) {
                case "N" -> newWorld(in, display);
                case "L" -> loadWorld(display);
                case ":" -> quitGame(in, display);
                case "Q" -> quitProgram();
                case "W" -> move(new Pair<>(0, 1), display);
                case "S" -> move(new Pair<>(0, -1), display);
                case "A" -> move(new Pair<>(-1, 0), display);
                case "D" -> move(new Pair<>(1, 0), display);
                default -> {
                }
            }
        }
        return worldWithAvatar();
    }

    private void newWorld(InputSource in, boolean display) {
        StringBuilder seed = new StringBuilder();
        String operand = String.valueOf(in.getNextKey()).toUpperCase();
        while (!operand.equals("S")) {
            seed.append(operand);
            operand = String.valueOf(in.getNextKey()).toUpperCase();
        }

        gen = new Generator(WIDTH, HEIGHT, Long.parseLong(seed.toString()));
        Utils.loadFrom(world, gen.generate());

        if (display) {
            displayWorld();
        }
    }

    private void loadWorld(boolean display) {
        Engine status = Utils.readObject(SAVE_FILE, this.getClass());
        this.world = status.world;
        this.location = status.location;
        this.render = status.render;
        this.gen = status.gen;
        this.inGame = status.inGame;

        if (display) {
            displayWorld();
        }
    }

    private void quitGame(InputSource in, boolean display) {
        String operand = String.valueOf(in.getNextKey()).toUpperCase();
        if (operand.equals("Q")) {
            if (inGame) {
                Utils.writeObject(SAVE_FILE, this);
            }
            if (display) {
                displayTitle();
            }
        }
    }

    private void quitProgram() {
        System.exit(0);
    }

    private void move(Pair<Integer, Integer> d, boolean display) {
        if (inGame) {
            location.first += d.first;
            location.second += d.second;
        }

        if (display) {
            displayWorld();
        }
    }
}
