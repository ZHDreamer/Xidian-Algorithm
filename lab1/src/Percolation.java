import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import edu.princeton.cs.algs4.QuickFindUF;

public class Percolation {
    private final int n;
    private int opensites = 0;

    private final boolean[] sites;
    private final boolean[] connected; // if a site is connected to bottom

    private final WeightedQuickUnionUF uf;
    // private final QuickFindUF uf;

    // create n-by-n grid, with all sites blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException(
                    "n-by-n grid's n must be positive");
        }

        this.n = n;

        // Init the site, but add 1 top virtual site
        sites = new boolean[n * n + 1];
        sites[0] = true;
        connected = new boolean[n * n + 1];

        uf = new WeightedQuickUnionUF(n * n + 1);
        // uf = new QuickFindUF(n * n + 1);
    }

    private void checkIndex(int row, int col) {
        if (row < 1 || row > n || col < 1 || col > n) {
            throw new IllegalArgumentException("row or col is out of bounds");
        }
    }

    /**
     * calculate the index for the given site (row, col),
     * where the index starts at 1.
     */
    private int calcIndex(int row, int col) {
        checkIndex(row, col);
        return (row - 1) * n + col;
    }

    /*
     * connect 2 sites and update connection to bottom
     */
    private void connect(int pos1, int pos2) {
        if (connected[uf.find(pos1)] || connected[uf.find(pos2)]) {
            uf.union(pos1, pos2);
            connected[uf.find(pos1)] = true;
        } else {
            uf.union(pos1, pos2);
        }
    }

    // open site (row, col) if it is not open already
    public void open(int row, int col) {
        int pos = calcIndex(row, col);

        if (sites[pos]) {
            return;
        }

        // open this site
        sites[pos] = true;
        opensites++;

        // connect head or tail
        if (row == n) {
            connected[pos] = true;
        }
        if (row == 1) {
            connect(0, pos);
        }

        // connect 4 directions
        if (col > 1 && sites[pos - 1]) {
            connect(pos, pos - 1);
        }
        if (col < n && sites[pos + 1]) {
            connect(pos, pos + 1);
        }
        if (row > 1 && sites[pos - n]) {
            connect(pos, pos - n);
        }
        if (row < n && sites[pos + n]) {
            connect(pos, pos + n);
        }
    }

    /** is the site (row, col) open? */
    public boolean isOpen(int row, int col) {
        int pos = calcIndex(row, col);
        return sites[pos];
    }

    /** is the site (row, col) full? */
    public boolean isFull(int row, int col) {
        int pos = calcIndex(row, col);
        return uf.find(pos) == uf.find(0);
    }

    public int numberOfOpenSites() {
        return opensites;
    }

    /** does the system percolate? */
    public boolean percolates() {
        return connected[uf.find(0)];
    }
}
