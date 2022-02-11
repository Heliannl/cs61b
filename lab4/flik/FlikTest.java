package flik;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FlikTest {
    @Test
    public void flikTest() {
        assertTrue("2 == 2", Flik.isSameNumber(2, 2));
        assertFalse("2 !=3 ", Flik.isSameNumber(2, 3));
        assertTrue("128 == 128", Flik.isSameNumber(128, 128));
    }
}
