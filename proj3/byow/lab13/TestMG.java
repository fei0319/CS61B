package byow.lab13;

import org.junit.Assert;
import org.junit.Test;

public class TestMG {
    @Test
    public void testGenerateRandomString() {
        final int len = 12;
        String s = new MemoryGame(30, 30, 0).generateRandomString(len);
        System.out.println(s);
        Assert.assertEquals(s.length(), len);
    }

    @Test
    public void testDrawFrame() {
        var game = new MemoryGame(30, 30, 0);
        game.drawFrame(game.generateRandomString(8));
    }
}