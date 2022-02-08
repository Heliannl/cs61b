package deque;

import org.junit.Test;

import java.util.Comparator;

import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest {
    @Test
    public void testInt() {
        /** from little to big. */
        Comparator<Integer> intC = (o1, o2) -> o1 - o2;
        MaxArrayDeque<Integer> intD = new MaxArrayDeque<Integer>(intC);
        assertEquals(null, intD.max());
        intD.addFirst(12);
        intD.addFirst(15);
        intD.addFirst(2);
        /* 2 15 12 */
        assertEquals(15, (int) intD.max());
        intD.addFirst(19);
        /* 19 2 15 12 */
        assertEquals(19, (int) intD.max());
        intD.addLast(32);
        /* 19 2 15 12 32 */
        assertEquals(32, (int) intD.max());
        /** from big to litter. */
        Comparator<Integer> intCr = (o1, o2) -> o2 - o1;
        assertEquals(2, (int) intD.max(intCr));
    }
}
