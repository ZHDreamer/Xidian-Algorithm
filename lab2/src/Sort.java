public class Sort {
    protected static boolean less(Object v, Object w) {
        return ((Comparable) v).compareTo(w) < 0;
    }

    protected static int compare(Object v, Object w) {
        return ((Comparable) v).compareTo(w);
    }

    protected static void swap(Object a[], int i, int j) {
        Object temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    protected static void shuffle(Object[] a) {
        for (int i = a.length - 1; i >= 0; i--) {
            swap(a, i, (int) (Math.random() * (i + 1)));
        }
    }

    protected static void swap(int a[], int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    protected static void shuffle(int[] a) {
        for (int i = a.length - 1; i >= 0; i--) {
            swap(a, i, (int) (Math.random() * (i + 1)));
        }
    }
}
