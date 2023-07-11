package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;

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

    private TETile[][] interact(InputSource in, boolean display) {
        TERenderer render = new TERenderer();
        render.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];

        while (in.possibleNextInput()) {
            String operand = String.valueOf(in.getNextKey()).toUpperCase();
            switch (operand) {
                case "N":
                    newWorld(in, display, render, world);
                    break;
                case "L":
                    break;
                case "Q":
                    break;
                default:
                    break;
            }
        }
        return null;
    }

    private void newWorld(InputSource in, boolean display, TERenderer render, TETile[][] world) {
        StringBuilder seed = new StringBuilder();
        String operand = String.valueOf(in.getNextKey()).toUpperCase();
        while (!operand.equals("S")) {
            seed.append(operand);
            operand = String.valueOf(in.getNextKey()).toUpperCase();
        }

        Generator gen = new Generator(WIDTH, HEIGHT, Long.parseLong(seed.toString()));
        Utils.loadFrom(world, gen.generate());

        if (display) {
            render.renderFrame(world);
        }
    }
}
