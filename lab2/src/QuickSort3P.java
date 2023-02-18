public class QuickSort3P extends Sort {
    public static void sort(Object[] a) {
        shuffle(a);
        sort(a, 0, a.length - 1);
    }

    private static void sort(Object[] a, int lo, int hi) {
        if (hi <= lo) {
            return;
        }

        Object v = a[lo];
        int lt = lo, gt = hi;
        int i = lo;

        while (i <= gt) {
            int cmp = compare(a[i], v);
            if (cmp < 0) {
                swap(a, lt++, i++);
            } else if (cmp > 0) {
                swap(a, i, gt--);
            } else {
                i++;
            }
        }

        sort(a, lo, lt - 1);
        sort(a, gt + 1, hi);
    }

    public static void sort(int[] a) {
        shuffle(a);
        sort(a, 0, a.length - 1);
    }

    private static void sort(int[] a, int lo, int hi) {
        if (hi <= lo) {
            return;
        }

        int v = a[lo];
        int lt = lo, gt = hi;
        int i = lo;

        while (i <= gt) {
            int cmp = a[i] - v;
            if (cmp < 0) {
                swap(a, lt++, i++);
            } else if (cmp > 0) {
                swap(a, i, gt--);
            } else {
                i++;
            }
        }

        sort(a, lo, lt - 1);
        sort(a, gt + 1, hi);
    }
}
