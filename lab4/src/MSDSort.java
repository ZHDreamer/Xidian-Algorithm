public class MSDSort {
    private static final int R = 256;

    private MSDSort() {
    }

    public static void sort(String str, int[] arr) {
        int n = arr.length;
        int[] aux = new int[n];
        sort(str, arr, 0, n - 1, 0, aux);
    }

    private static int charAt(String str, int idx) {
        assert idx >= 0 && idx <= str.length();
        if (idx >= str.length())
            return -1;
        return str.charAt(idx);
    }

    private static void sort(String str, int[] arr, int lo, int hi, int d, int[] aux) {
        if (hi <= lo) {
            return;
        }

        // compute frequency counts
        int[] count = new int[R + 2];
        for (int i = lo; i <= hi; i++) {
            int c = charAt(str, d + arr[i]);
            count[c + 2]++;
        }

        // transform counts to indices
        for (int r = 0; r < R + 1; r++)
            count[r + 1] += count[r];

        // distribute
        for (int i = lo; i <= hi; i++) {
            int c = charAt(str, d + arr[i]);
            aux[count[c + 1]++] = arr[i];
        }

        // copy back
        for (int i = lo; i <= hi; i++)
            arr[i] = aux[i - lo];

        // recursively sort for each character (excludes sentinel -1)
        for (int r = 0; r < R; r++)
            sort(str, arr, lo + count[r], lo + count[r + 1] - 1, d + 1, aux);
    }
}
