package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;

public class MemoryGame {
    /**
     * The width of the window of this game.
     */
    private int width;
    /**
     * The height of the window of this game.
     */
    private int height;
    /**
     * The current round the user is on.
     */
    private int round;
    /**
     * The Random object used to randomly generate Strings.
     */
    private Random rand;
    /**
     * Whether or not the game is over.
     */
    private boolean gameOver;
    /**
     * Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'.
     */
    private boolean playerTurn;
    /**
     * The characters we generate random Strings from.
     */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /**
     * Encouraging phrases. Used in the last section of the spec, 'Helpful UI'.
     */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
            "You got this!", "You're a star!", "Go Bears!",
            "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
        game.startGame();
    }

    public MemoryGame(int width, int height, long seed) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        rand = new Random(seed);
    }

    public String generateRandomString(int n) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < n; ++i) {
            s.append(CHARACTERS[RandomUtils.uniform(rand, CHARACTERS.length)]);
        }
        return s.toString();
    }

    public void drawFrame(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setFont(new Font("Monaco", Font.BOLD, 30));
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(width * 0.5, height * 0.5, s);

        if (!gameOver) {
            int h = height - 1;
            String encourage = ENCOURAGEMENT[RandomUtils.uniform(rand, ENCOURAGEMENT.length)];

            StdDraw.setFont(new Font("Monaco", Font.BOLD, 18));
            StdDraw.textLeft(0, h, String.format("Round: %d", round));
            StdDraw.text(width * 0.5, h, playerTurn ? "Type!" : "Watch!");
            StdDraw.textRight(width, h, encourage);
        }
        StdDraw.show();
    }

    public void flashSequence(String letters) {
        for (int i = 0; i < letters.length(); ++i) {
            char c = letters.charAt(i);

            drawFrame(Character.toString(c));
            StdDraw.show();
            StdDraw.pause(1000);

            drawFrame("");
            StdDraw.show();
            StdDraw.pause(500);
        }
    }

    public String solicitNCharsInput(int n) {
        StringBuilder sb = new StringBuilder();
        drawFrame(sb.toString());
        while (true) {
            if (sb.length() == n) {
                break;
            }
            if (StdDraw.hasNextKeyTyped()) {
                sb.append(StdDraw.nextKeyTyped());
                drawFrame(sb.toString());
            }
            StdDraw.pause(10);
        }
        return sb.toString();
    }

    public void startGame() {
        round = 1;
        while (true) {
            playerTurn = false;
            drawFrame(String.format("Round: %d", round));
            String s = generateRandomString(round);
            flashSequence(s);

            playerTurn = true;
            if (s.equals(solicitNCharsInput(round))) {
                ++round;
            } else {
                gameOver = true;
                drawFrame(String.format("Game Over! You made it to round: %d", round));
                break;
            }
        }
        while (!StdDraw.hasNextKeyTyped()) {
            StdDraw.pause(10);
        }
    }

}
