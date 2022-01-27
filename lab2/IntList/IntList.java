package IntList;

public class IntList {
    public int first;
    public IntList rest;

    public IntList(int f, IntList r) {
        first = f;
        rest = r;
    }

    /** Return the size of the list using... recursion! */
    public int size() {
        if (rest == null) {
            return 1;
        }
        return 1 + this.rest.size();
    }

    /** Return the size of the list using no recursion! */
    public int iterativeSize() {
        IntList p = this;
        int totalSize = 0;
        while (p != null) {
            totalSize += 1;
            p = p.rest;
        }
        return totalSize;
    }

    /** Returns the ith item of this IntList. */
    public int get(int i) {
        if (i == 0) {
            return first;
        }
        return rest.get(i - 1);
    }

    /** Method to return a string representation of an IntList */
    public String toString() {
        if (rest == null) {
            // Converts an Integer to a String!
            return String.valueOf(first);
        } else {
            return first + " -> " + rest.toString();
        }
    }

    /** Modifies the list so that all of its elements are squared. */
    public static void dSquareList(IntList L){
        while (L != null) {
            L.first = L.first * L.first;
            L = L.rest;
        }
    }

    /** Returns a version of the list with all elements squared, using iteration.
     * This List is not modified. */
    public static IntList squareListIterative(IntList L){
        if(L == null){
            return null;
        }
        IntList p = new IntList(L.first*L.first, null);
        IntList ptr = p;
        L = L.rest;
        while (L != null) {
            ptr.rest = new IntList(L.first*L.first, null);
            ptr = ptr.rest;
            L = L.rest;
        }
        return p;
    }

    /** Returns a version of the list with all elements squared, using recursion.
     * This List is not modified.*/
    public static IntList squareListRecursion(IntList L){
        if(L == null){
            return null;
        }
        return new IntList(L.first*L.first, squareListRecursion(L.rest));
    }

    /** Returns a list consisting of all elements of A, followed by all elements of B.
     *  May modify A, to be completed by you. Don't use new. */
    public static IntList dcatenate(IntList A, IntList B){
        if(A == null){
            return B;
        }
        while(A.rest != null){
            A = A.rest;
        }
        A.rest = B;
        return A;
    }

    /** Return a list consisting all elements of A, followed by all elements of B.
     *  May not modify A, to be completed by you. Use new. */
    public static IntList catenate(IntList A, IntList B){
        if(A == null){
           return B;
        }
        IntList p = new IntList(A.first, null);
        IntList ptr = p;
        A = A.rest;
        while(A != null){
            ptr.rest = new IntList(A.first, null);
            ptr = ptr.rest;
            A = A.rest;
        }
        while(B != null){
            ptr.rest = new IntList(B.first, null);
            ptr = ptr.rest;
            B = B.rest;
        }
        return p;
    }

    /**
     * Method to create an IntList from an argument list.
     * You don't have to understand this code. We have it here
     * because it's convenient with testing. It's used like this:
     *
     * IntList myList = IntList.of(1, 2, 3, 4, 5);
     * will create an IntList 1 -> 2 -> 3 -> 4 -> 5 -> null.
     *
     * You can pass in any number of arguments to IntList.of and it will work:
     * IntList mySmallerList = IntList.of(1, 4, 9);
     */
    public static IntList of(int ...argList) {
        if (argList.length == 0)
            return null;
        int[] restList = new int[argList.length - 1];
        System.arraycopy(argList, 1, restList, 0, argList.length - 1);
        return new IntList(argList[0], IntList.of(restList));
    }
}
