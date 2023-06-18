package flik;

import static org.junit.Assert.*;
import org.junit.Test;

public class FlikTest {
    @Test
    public void testNumber() {
        assertTrue(Flik.isSameNumber(Integer.valueOf(128), Integer.valueOf(128)));
    }
}