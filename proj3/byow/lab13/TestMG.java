package byow.lab13;

import org.junit.Assert;
import org.junit.Test;

public class TestMG {
    @Test
    public void testGenerateRandomString() {
        final int len = 12;
        String s = new MemoryGame(40, 40, 0).generateRandomString(len);
        System.out.println(s);
        Assert.assertEquals(s.length(), len);
    }

    @Test
    public void testDrawFrame() {
        var game = new MemoryGame(40, 40, 0);
        game.drawFrame(game.generateRandomString(8));
    }

    @Test
    public void testFlashSequence() {
        var game = new MemoryGame(40, 40, 0);
        game.flashSequence("Hello");
    }

    @Test
    public void testSolicitNCharsInput() {
        var game = new MemoryGame(40, 40, 0);
        game.solicitNCharsInput(10);
    }

    @Test
    public void testGame() {
        MemoryGame.main(new String[]{"114514"});
    }
}