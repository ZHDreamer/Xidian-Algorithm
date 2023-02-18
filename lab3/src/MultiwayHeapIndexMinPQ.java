/*
 *  Compilation:  javac PowerOf2IndexMinPQ.java
 *  Execution:    java PowerOf2IndexMinPQ
 *
 *  Minimum-oriented, indexed PQ implementation using a power-of-2 heap.
 */

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The <tt>PowerOf2IndexMinPQ</tt> class uses a power-of-2 width heap to
 * implement a minimum-oriented, indexed priority queue of generic keys. It
 * supports the usual <em>insert</em> and <em>delete-the-minimum</em>
 * operations, along with <em>delete</em> and <em>change-the-key</em>
 * methods. In order to let the client refer to keys on the priority queue, an
 * integer between 0 and NMAX-1 is associated with each key&mdash;the client
 * uses this integer to specify which key to delete or change. It also supports
 * methods for peeking at the minimum key, testing if the priority queue is
 * empty, and iterating through the keys.
 * <p>
 * This implementation uses a 2-to-the-power-p heap along with an array to
 * associate keys with integers in the given range. The <em>insert</em>,
 * <em>delete-the-minimum</em>, <em>delete</em>,
 * <em>change-key</em>, <em>decrease-key</em>, and <em>increase-key</em>
 * operations take logarithmic time. The <em>is-empty</em>, <em>size</em>,
 * <em>min-index</em>, <em>min-key</em>, and <em>key-of</em> operations take
 * constant time. Construction takes time proportional to the specified
 * capacity.
 * <p>
 * For additional documentation, see
 * <a href="http://algs4.cs.princeton.edu/24pq">Section 2.4</a> of
 * <i>Algorithms, 4th Edition</i> by Robert Sedgewick and Kevin Wayne.
 *
 * @author Dean Elzinga
 */
public class MultiwayHeapIndexMinPQ<Key extends Comparable<Key>>
        implements Iterable<Integer> {
    static private final int P_DEFAULT = 3; // Default to 8-heap.
    private int p; // 2-to-the-p heap. Each parent has 1<<p children.
    private int NMAX; // Maximum number of elements on PQ.
    private int N; // Number of elements on PQ.
    private int[] pq; // Binary heap using 0-based indexing.
    private int[] qp; // Inverse of pq - qp[pq[i]] = pq[qp[i]] = i.
    private Key[] keys; // keys[i] = priority of i

    /**
     * Initializes an empty 2^^p-ary indexed priority queue with indices between 0
     * and NMAX-1.
     *
     * @param NMAX the keys on the priority queue are index from 0 to NMAX-1
     * @throws java.lang.IllegalArgumentException if NMAX < 0
     */
    public MultiwayHeapIndexMinPQ(int p, int NMAX) {
        this.p = p;
        if (NMAX < 0) {
            throw new IllegalArgumentException();
        }
        this.NMAX = NMAX;
        keys = (Key[]) new Comparable[NMAX]; // Make this of length NMAX??
        pq = new int[NMAX];
        qp = new int[NMAX]; // Make this of length NMAX??
        for (int i = 0; i <= NMAX - 1; i++) {
            qp[i] = -1;
        }
    }

    /**
     * Initializes an empty d-ary indexed priority queue with indices between 0
     * and NMAX-1.
     *
     * @param NMAX the keys on the priority queue are index from 0 to NMAX-1
     * @throws java.lang.IllegalArgumentException if NMAX < 0
     */
    public MultiwayHeapIndexMinPQ(int NMAX) {
        this(P_DEFAULT, NMAX);
    }

    /**
     * Is the priority queue empty?
     *
     * @return true if the priority queue is empty; false otherwise
     */
    public boolean isEmpty() {
        return N == 0;
    }

    /**
     * Is i an index on the priority queue?
     *
     * @param i an index
     * @throws java.lang.IndexOutOfBoundsException unless (0 &le; i < NMAX)
     */
    public boolean contains(int i) {
        if (i < 0 || i >= NMAX) {
            throw new IndexOutOfBoundsException();
        }
        return qp[i] != -1;
    }

    /**
     * Returns the number of keys on the priority queue.
     *
     * @return the number of keys on the priority queue
     */
    public int size() {
        return N;
    }

    /**
     * Associates key with index i.
     *
     * @param i   an index
     * @param key the key to associate with index i
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; i < NMAX
     * @throws java.util.IllegalArgumentException  if there already is an item
     *                                             associated with index i
     */
    public void insert(int i, Key key) {
        if (i < 0 || i >= NMAX) {
            throw new IndexOutOfBoundsException();
        }
        if (contains(i)) {
            throw new IllegalArgumentException("Index already in the priority queue.");
        }
        qp[i] = N;
        pq[N] = i;
        keys[i] = key;
        swim(N++);
    }

    /**
     * Returns an index associated with a minimum key.
     *
     * @return an index associated with a minimum key
     * @throws java.util.NoSuchElementException if priority queue is empty
     */
    public int minIndex() {
        if (N == 0) {
            throw new NoSuchElementException("Priority queue underflow");
        }
        return pq[0];
    }

    /**
     * Returns a minimum key.
     *
     * @return a minimum key
     * @throws java.util.NoSuchElementException if priority queue is empty
     */
    public Key minKey() {
        if (N == 0) {
            throw new NoSuchElementException("Priority queue underflow");
        }
        return keys[pq[0]];
    }

    /**
     * Removes a minimum key and returns its associated index.
     *
     * @return an index associated with a minimum key
     * @throws java.util.NoSuchElementException if priority queue is empty
     */
    public int delMin() {
        if (N == 0) {
            throw new NoSuchElementException("Priority queue underflow");
        }
        int min = pq[0];
        exch(0, --N);
        sink(0);
        qp[min] = -1; // delete
        keys[pq[N]] = null; // to help with garbage collection
        pq[N] = -1; // not needed
        return min;
    }

    /**
     * Returns the key associated with index i.
     *
     * @param i the index of the key to return
     * @return the key associated with index i
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; i < NMAX
     * @throws java.util.NoSuchElementException    no key is associated with index i
     */
    public Key keyOf(int i) {
        if (i < 0 || i >= NMAX) {
            throw new IndexOutOfBoundsException();
        }
        if (!contains(i)) {
            throw new NoSuchElementException("Index not in priority queue.");
        } else {
            return keys[i];
        }
    }

    /**
     * Change the key associated with index i to the specified value.
     *
     * @param i   the index of the key to change
     * @param key change the key assocated with index i to this key
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; i < NMAX
     */
    public void change(int i, Key key) {
        if (i < 0 || i >= NMAX) {
            throw new IndexOutOfBoundsException();
        }
        if (!contains(i)) {
            throw new NoSuchElementException("Index not in priority queue.");
        }
        keys[i] = key;
        swim(qp[i]);
        sink(qp[i]);
    }

    /**
     * Decrease the key associated with index i to the specified value.
     *
     * @param i   the index of the key to decrease
     * @param key decrease the key assocated with index i to this key
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; i < NMAX
     * @throws java.lang.IllegalArgumentException  if key &ge; key associated with
     *                                             index i
     * @throws java.util.NoSuchElementException    no key is associated with index i
     */
    public void decreaseKey(int i, Key key) {
        if (i < 0 || i >= NMAX) {
            throw new IndexOutOfBoundsException();
        }
        if (!contains(i)) {
            throw new NoSuchElementException("Index is not in the priority queue.");
        }
        if (keys[i].compareTo(key) <= 0) {
            throw new IllegalArgumentException(
                    "decreaseKey() called with argument that does not strictly decrease the key.");
        }
        keys[i] = key;
        swim(qp[i]);
    }

    /**
     * Increase the key associated with index i to the specified value.
     *
     * @param i   the index of the key to increase
     * @param key increase the key assocated with index i to this key
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; i < NMAX
     * @throws java.lang.IllegalArgumentException  if key &le; key associated with
     *                                             index i
     * @throws java.util.NoSuchElementException    no key is associated with index i
     */
    public void increaseKey(int i, Key key) {
        if (i < 0 || i >= NMAX) {
            throw new IndexOutOfBoundsException();
        }
        if (!contains(i)) {
            throw new NoSuchElementException("Index not in priority queue.");
        }
        if (keys[i].compareTo(key) >= 0) {
            throw new IllegalArgumentException(
                    "increaseKey() called with argument that does not strictly increase the key.");
        }
        keys[i] = key;
        sink(qp[i]);
    }

    /**
     * Remove the key associated with index i.
     *
     * @param i the index of the key to remove
     * @throws java.lang.IndexOutOfBoundsException unless 0 &le; i < NMAX
     * @throws java.util.NoSuchElementException    no key is associated with index i
     */
    public void delete(int i) {
        if (i < 0 || i >= NMAX) {
            throw new IndexOutOfBoundsException();
        }
        if (!contains(i)) {
            throw new NoSuchElementException("Index not in priority queue.");
        }
        int index = qp[i];
        exch(index, --N);
        swim(index);
        sink(index);
        keys[i] = null;
        qp[i] = -1;
    }

    /**
     * ************************************************************
     * General helper functions
     *************************************************************
     */
    private boolean greater(int i, int j) {
        return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
    }

    private void exch(int i, int j) {
        int swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
        qp[pq[i]] = i;
        qp[pq[j]] = j;
    }

    /**
     * ************************************************************
     * Heap helper functions
     *************************************************************
     */
    /*
     * Parent of k in this 0-based, (1<<p)-heap is at (k-1)>>p instead
     * of at k/2 in the 1-based, binary case.
     */
    private void swim(int k) {
        while (k > 0 && greater((k - 1) >> p, k)) {
            exch(k, (k - 1) >> p);
            k = (k - 1) >> p;
        }
    }

    // First child of item at k is at (k<<p)+1, rather than at 2*k,
    // as in the 0-based, binary case.
    private void sink(int k) {
        while ((k << p) + 1 < N) {
            int next = (k << p) + 1;
            int last = next + (1 << p) - 1;
            // lastChild = minimum(lastChild, N-1):
            last = last <= N - 1 ? last : N - 1;
            // firstChild gets index of max item among (1<<p) sibs.
            for (int sib = next + 1; sib <= last; sib++) {
                if (greater(next, sib)) {
                    next = sib;
                }
            }
            if (!greater(k, next)) {
                break;
            }
            exch(k, next);
            k = next;
        }
    }

    /**
     * *********************************************************************
     * Iterators
     *********************************************************************
     */
    /**
     * Returns an iterator that iterates over the keys on the priority queue in
     * ascending order. The iterator doesn't implement <tt>remove()</tt> since
     * it's optional.
     *
     * @return an iterator that iterates over the keys in ascending order
     */
    public Iterator<Integer> iterator() {
        return new HeapIterator();
    }

    private class HeapIterator implements Iterator<Integer> {
        // Create a new pq.
        private MultiwayHeapIndexMinPQ<Key> copy;

        // Add all elements to the copy of the heap.
        // This takes linear time; already in heap order, so no keys move.
        public HeapIterator() {
            copy = new MultiwayHeapIndexMinPQ<Key>(pq.length);
            for (int i = 0; i <= N - 1; i++) {
                copy.insert(pq[i], keys[pq[i]]);
            }
        }

        public boolean hasNext() {
            return !copy.isEmpty();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Integer next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return copy.delMin();
        }
    }

}
