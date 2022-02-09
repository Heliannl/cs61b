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

        ad1.addFirst(12);
        ad1.removeLast();
        assertTrue(ad1.isEmpty());

        ad1.addLast(12);
        ad1.removeFirst();
        assertTrue(ad1.isEmpty());
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

        ArrayDeque<Integer> ad2 = new ArrayDeque<>();
        for (int i = 0; i < 8; i++) {
            ad2.addLast(i);
        }
        assertEquals(0, (int) ad2.get(0));
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

    @Test
    public void iteratorTest() {
        ArrayDeque<String> lld1 = new ArrayDeque<String>();
        lld1.addFirst("aaa");
        lld1.addFirst("bbb");
        lld1.addFirst("ccc");
        lld1.addFirst("ddd");

        lld1.printDeque();

        for (String s : lld1) {
            System.out.println(s);
        }
    }

    @Test
    public void equalsTest() {
        ArrayDeque<String> lld1 = new ArrayDeque<String>();
        lld1.addFirst("aaa");
        lld1.addFirst("bbb");
        lld1.addFirst("ccc");
        lld1.addFirst("ddd");

        ArrayDeque<String> lld2 = new ArrayDeque<String>();
        lld2.addFirst("aaa");
        lld2.addFirst("bbb");
        lld2.addFirst("ccc");
        lld2.addFirst("ddd");

        assertEquals(true, lld1.equals(lld2));

        lld2.removeFirst();
        assertEquals(false, lld1.equals(lld2));
    }
}
