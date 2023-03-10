public class QuickSort extends Sort {
    public static void sort(Object[] a) {
        shuffle(a);
        sort(a, 0, a.length - 1);
    }

    private static void sort(Object[] a, int lo, int hi) {
        if (hi <= lo) {
            return;
        }
        int j = partition(a, lo, hi);
        sort(a, lo, j - 1);
        sort(a, j + 1, hi);
    }

    private static int partition(Object[] a, int lo, int hi) {
        int i = lo, j = hi + 1;
        while (true) {
            while (less(a[++i], a[lo])) {
                if (i == hi) {
                    break;
                }
            }

            while (less(a[lo], a[--j])) {
                if (j == lo) {
                    break;
                }
            }

            if (i >= j) {
                break;
            }

            swap(a, i, j);
        }

        swap(a, lo, j);
        return j;
    }

    public static void sort(int[] a) {
        shuffle(a);
        sort(a, 0, a.length - 1);
    }

    private static void sort(int[] a, int lo, int hi) {
        if (hi <= lo) {
            return;
        }
        int j = partition(a, lo, hi);
        sort(a, lo, j - 1);
        sort(a, j + 1, hi);
    }

    private static int partition(int[] a, int lo, int hi) {
        int i = lo, j = hi + 1;
        while (true) {
            while (a[++i] < a[lo]) {
                if (i == hi) {
                    break;
                }
            }

            while (a[lo] < a[--j]) {
                if (j == lo) {
                    break;
                }
            }

            if (i >= j) {
                break;
            }

            swap(a, i, j);
        }

        swap(a, lo, j);
        return j;
    }
}
