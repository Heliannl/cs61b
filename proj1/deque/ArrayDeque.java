package deque;

public class ArrayDeque<T> implements Deque<T> {
    /**
     * Request:
     * 1.add and remove take constant time, except during resizing operations
     * 2.get and size take constant time
     * 3.the starting size of the array should be 8
     * 4.resize size: for arrays length 16 or more, usage factor should always be at least 25%,
     * for smaller arrays, usage factor can be arbitrarily low
     */

    private T[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }

    public ArrayDeque(ArrayDeque other) {
        size = 0;
        for (int i = 0; i < other.size; i++) {
            addLast((T) other.get(i));
        }
    }

    private void resize(int capacity) {
        T[] resizeItems = (T[]) new Object[capacity];
        System.arraycopy(items, 0, resizeItems, 0, size);
        items = resizeItems;
    }

    @Override
    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextFirst] = item;
        size++;
        nextFirst = (nextFirst + items.length - 1) % items.length;
    }

    @Override
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextLast] = item;
        size++;
        nextLast = (nextLast + 1) % items.length;
    }

    /**
    public boolean isEmpty() {
        if (size == 0) {
            return true;
        } else {
            return false;
        }
    }*/

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(items[(nextFirst + 1 + i) % items.length] + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        T returnItem = (T) items[(nextFirst + 1) % items.length];
        nextFirst = (nextFirst + 1) % items.length;
        size--;
        if (items.length >= 16 && (double) size / items.length < 0.25) {
            resize(items.length / 2);
        }
        return returnItem;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        T returnItem = (T) items[(nextLast + items.length - 1) % items.length];
        nextLast = (nextLast + items.length - 1) % items.length;
        size--;
        return returnItem;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index > size - 1) {
            return null;
        }
        return items[(nextFirst + 1 + index) % items.length];
    }

    /** error
    public T getRecursiveHelp(int index, int currFirst){
        if(index==0){
            return items[(currFirst+1)% items.length];
        }
        return getRecursiveHelp(index-1, (currFirst+ items.length-1)% items.length);
    }

    public T getRecursive(int index) {
        if (index < 0 || index > size - 1) {
            return null;
        }
        return getRecursiveHelp(index, nextFirst);
    }
     */
}
