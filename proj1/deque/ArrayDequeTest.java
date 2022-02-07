package deque;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayDequeTest {
    @Test
    public void addRemoveTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        assertTrue("ad1 should be empty upon initialization", ad1.isEmpty());

        ad1.addFirst(10);
        assertFalse("lld1 should contain 1 item", ad1.isEmpty());

        assertEquals(10, (int) ad1.removeFirst());
        assertTrue("lld1 should be empty after removal", ad1.isEmpty());

        ad1.addLast(12);
        assertFalse("lld1 should contain 1 item", ad1.isEmpty());

        assertEquals(12, (int) ad1.removeLast());
        assertTrue("lld1 should be empty after removal", ad1.isEmpty());
    }

    @Test
    public void getIndexTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        ad1.addFirst(1);
        ad1.addFirst(2);
        ad1.addLast(17);
        ad1.addFirst(3);
        ad1.addLast(18);
        // should be 3 2 1 17 18
        ad1.printDeque();
        assertEquals(2, (int) ad1.get(1));
        assertEquals(17, (int) ad1.get(3));
        assertEquals(null, ad1.get(8));
    }

    @Test
    public void printTest() {
        ArrayDeque<Integer> ad1 = new ArrayDeque<Integer>();
        ad1.addFirst(1);
        ad1.addFirst(2);
        ad1.addLast(17);
        ad1.addFirst(3);
        ad1.addLast(18);
        ad1.addLast(21);
        ad1.addFirst(4);
        ad1.addLast(25);
        // 4 3 2 1 17 18 21 25
        System.out.println("Printing out deque: ");
        ad1.printDeque();

        ad1.removeFirst();
        ad1.removeLast();
        // 3 2 1 17 18 21
        System.out.println("Printing out deque: ");
        ad1.printDeque();
    }
}
