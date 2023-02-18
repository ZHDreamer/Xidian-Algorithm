public class Quick3string {
    private static final int CUTOFF = 5;

    private Quick3string() {
    }

    public static void sort(String s, int[] a) {
        int n = a.length;
        sort(s, a, 0, n - 1, 0);
    }

    private static int charAt(String s, int i) {
        assert i >= 0 && i <= s.length();
        if (i >= s.length())
            return -1;
        return s.charAt(i);
    }

    private static void sort(String s, int[] a, int lo, int hi, int d) {
        if (hi <= lo) {
            return;
        }

        int lt = lo, gt = hi;
        int v = charAt(s, a[lo] + d);
        int i = lo + 1;
        while (i <= gt) {
            int t = charAt(s, a[i] + d);
            if (t < v) {
                exch(a, lt++, i++);
            } else if (t > v) {
                exch(a, i, gt--);
            } else {
                i++;
            }
        }

        sort(s, a, lo, lt - 1, d);
        if (v >= 0) {
            sort(s, a, lt, gt, d + 1);
        }
        sort(s, a, gt + 1, hi, d);
    }

    public static void sort(Suffix[] a) {
        int n = a.length;
        sort(a, 0, n - 1, 0);
    }

    private static void sort(Suffix[] a, int lo, int hi, int d) {
        if (hi <= lo + CUTOFF) {
            insertion(a, lo, hi, d);
            return;
        }

        int lt = lo, gt = hi;
        int v = a[lo].charAt(d);
        int i = lo + 1;
        while (i <= gt) {
            int t = a[i].charAt(d);
            if (t < v) {
                exch(a, lt++, i++);
            } else if (t > v) {
                exch(a, i, gt--);
            } else {
                i++;
            }
        }

        sort(a, lo, lt - 1, d);
        if (v >= 0) {
            sort(a, lt, gt, d + 1);
        }
        sort(a, gt + 1, hi, d);
    }

    private static void insertion(Suffix[] a, int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(a[j], a[j - 1], d); j--)
                exch(a, j, j - 1);
    }

    // is v less than w, starting at character d
    private static boolean less(Suffix v, Suffix w, int d) {
        for (int i = d; i < Math.min(v.length(), w.length()); i++) {
            if (v.charAt(i) < w.charAt(i))
                return true;
            if (v.charAt(i) > w.charAt(i))
                return false;
        }
        return v.length() < w.length();
    }

    private static void exch(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    private static void exch(Object[] a, int i, int j) {
        Object temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}