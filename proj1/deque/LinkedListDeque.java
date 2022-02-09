package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    /**
     * Request:
     * 1.add and remove must not involve any looping or recursion, and take "constant time"
     * 2.get must use iteration, not recursion
     * 3.size must take constant time
     * 4.deque resize (do not maintain references to items that are no longer in the deque)
     */

    private static class TNode<T> {
        private TNode prev;
        private T item;
        private TNode next;

        TNode(TNode p, T i, TNode n) {
            this.prev = p;
            this.item = i;
            this.next = n;
        }
    }

    private TNode sentinel;
    private int size;

    /**
     * Creates ab empty linked list deque. */
    public LinkedListDeque() {
        size = 0;
        sentinel = new TNode(sentinel, (T) new Object(), sentinel);
    }

    /**
     * Creates a deep copy of the other.
    public LinkedListDeque(LinkedListDeque other) {
        size = 0;
        sentinel = new TNode(sentinel, 63, sentinel);
        for (int i = 0; i < other.size; i++) {
            addLast((T) other.get(i));
        }
    }
     */

    /**
     * Adds an item of type T to the front of the deque. */
    @Override
    public void addFirst(T item) {
        TNode tempNode = new TNode(sentinel, item, sentinel.next);
        if (size == 0) {
            sentinel.prev = tempNode;
            sentinel.next = tempNode;
            tempNode.next = sentinel;
        } else {
            sentinel.next.prev = tempNode;
            sentinel.next = tempNode;
        }
        size++;
    }

    /**
     * Adds an item of type T to the back of the deque. */
    @Override
    public void addLast(T item) {
        TNode tempNode = new TNode(sentinel.prev, item, sentinel);
        if (size == 0) {
            sentinel.prev = tempNode;
            sentinel.next = tempNode;
            tempNode.prev = sentinel;
        } else {
            sentinel.prev.next = tempNode;
            sentinel.prev = tempNode;
        }
        size++;
    }

    /**
     * Returns true if deque is empty, false otherwise.
     */
    /**
    public boolean isEmpty() {
        if (size == 0) {
            return true;
        } else {
            return false;
        }
    }*/

    /**
     * Returns the number of the items in the deque. */
    @Override
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line. */
    @Override
    public void printDeque() {
        TNode prevNode = sentinel.next;
        while (prevNode != sentinel) {
            System.out.print((T) prevNode.item);
            prevNode = prevNode.next;
            if (prevNode != sentinel) {
                System.out.print(" ");
            } else {
                System.out.println();
            }
        }
    }

    /**
     * Removes and returns the item at the front of the deque.
     * If no such item exists, returns null. */
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T returnItem = (T) sentinel.next.item;
        if (size == 1) {
            sentinel.next = sentinel;
            sentinel.prev = sentinel;
        } else {
            sentinel.next.next.prev = sentinel;
            sentinel.next = sentinel.next.next;
        }
        size--;
        return returnItem;
    }

    /**
     * Removes and returns the item at the back of the deque.
     * If no such item exists, returns null. */
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        T returnItem = (T) sentinel.prev.item;
        if (size == 1) {
            sentinel.next = sentinel;
            sentinel.prev = sentinel;
        } else {
            sentinel.prev.prev.next = sentinel;
            sentinel.prev = sentinel.prev.prev;
        }
        size--;
        return returnItem;
    }

    /**
     * Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
     * If no such item exists, returns null. Must not alter the deque. */
    @Override
    public T get(int index) {
        if (index > size - 1) {
            return null;
        }
        TNode tempNode = sentinel.next;
        for (int i = 0; i < index; i++) {
            tempNode = tempNode.next;
        }
        return (T) tempNode.item;
    }

    /**
     * Same as get, but uses recursion. */
    private T getRecursiveHelp(int index, TNode currNode) {
        if (index == 0) {
            return (T) currNode.item;
        }
        return getRecursiveHelp(index - 1, currNode.next);
    }

    public T getRecursive(int index) {
        if (index < 0 || index > size - 1) {
            return null;
        }
        return getRecursiveHelp(index, sentinel.next);
    }

    /** The deque object we'll make are iterable, so we must provide this method
     * to return an iterator. */
    private class TIterator implements Iterator<T> {
        private int wizPos;

        TIterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            return wizPos < size;
        }

        public T next() {
            T returnItem = get(wizPos);
            wizPos++;
            return returnItem;
        }
    }

    public Iterator<T> iterator() {
        return new TIterator();
    }

    /** Returns whether the parameter o is equal to the Deque. o is considered equal
     * if it is a deque, and it contains the same contents in the same order. */

    /** My solution
     * @Override
    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque<?>) || size == ((LinkedListDeque<?>) o).size()) {
            return false;
        }
        TNode prevNode = sentinel.next;
        TNode oNode = ((LinkedListDeque<?>) o).sentinel.next;
        while (prevNode != null) {
            if (prevNode.item != oNode.item) {
                return false;
            }
            prevNode = prevNode.next;
            oNode = oNode.next;
        }
        return true;
    }
     */

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque<?>)) {
            return false;
        }
        Deque<T> other = (Deque<T>) o;
        if (other.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); i++) {
            if (this.get(i) != other.get(i)) {
                return false;
            }
        }
        return true;
    }
}
