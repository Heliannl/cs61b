package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> al = new AListNoResizing<>();
        BuggyAList<Integer> bl = new BuggyAList<>();
        al.addLast(4);
        bl.addLast(4);
        al.addLast(5);
        bl.addLast(5);
        al.addLast(6);
        bl.addLast(6);
        assertEquals(al.size(), bl.size());
        assertEquals(al.removeLast(), bl.removeLast());
        assertEquals(al.removeLast(), bl.removeLast());
        assertEquals(al.removeLast(), bl.removeLast());
    }

    @Test
    public void randomSizedTest() {
        AListNoResizing<Integer> al = new AListNoResizing<>();
        BuggyAList<Integer> bl = new BuggyAList<>();
        int N = 5000;
        for (int i = 0; i < N; i++) {
            int operationNumber = StdRandom.uniform(0,4);
            if (operationNumber == 0) {
                int randVal = StdRandom.uniform(0,100);
                al.addLast(randVal);
                bl.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                int size = al.size();
                System.out.println("size: " + size);
                assertEquals(al.size(), bl.size());
            } else if (operationNumber == 2) {
                if (al.size() > 0) {
                    assertEquals(al.getLast(), bl.getLast());
                }
            } else if (operationNumber == 3) {
                if (al.size() > 0) {
                    assertEquals(al.removeLast(), bl.removeLast());
                }
            }
        }
    }
}
