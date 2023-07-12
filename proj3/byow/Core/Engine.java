package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    private TETile[][] world;
    private Pair<Integer, Integer> location;
    private TERenderer render;

    public Engine() {
        world = new TETile[WIDTH][HEIGHT];
        location = new Pair<>(0, 0);
        render = new TERenderer();
    }

    private void displayWorld() {
        render.initialize(WIDTH, HEIGHT);
        TETile[][] showWorld = new TETile[WIDTH][HEIGHT];
        Utils.loadFrom(showWorld, world);
        showWorld[location.first][location.second] = Tileset.AVATAR;
        render.renderFrame(showWorld);
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
        while (in.possibleNextInput()) {
            String operand = String.valueOf(in.getNextKey()).toUpperCase();
            switch (operand) {
                case "N":
                    newWorld(in, display);
                    break;
                case "L":
                    break;
                case ":":
                    quitGame(in, display);
                    break;
                default:
                    break;
            }
        }
        return world;
    }

    private void newWorld(InputSource in, boolean display) {
        StringBuilder seed = new StringBuilder();
        String operand = String.valueOf(in.getNextKey()).toUpperCase();
        while (!operand.equals("S")) {
            seed.append(operand);
            operand = String.valueOf(in.getNextKey()).toUpperCase();
        }

        Generator gen = new Generator(WIDTH, HEIGHT, Long.parseLong(seed.toString()));
        Utils.loadFrom(world, gen.generate());

        if (display) {
            displayWorld();
        }
    }

    private void quitGame(InputSource in, boolean display) {
        String operand = String.valueOf(in.getNextKey()).toUpperCase();
        if (operand.equals("Q")) {
            // TODO: Fill in QuitGame
        }
    }

    private void move(Pair<Integer, Integer> d, boolean display) {
        location.first += d.first;
        location.second += d.second;

        if (display) {
            displayWorld();
        }
    }
}
