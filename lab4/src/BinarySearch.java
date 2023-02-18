import java.util.ArrayList;
import java.util.List;

public class BinarySearch {
    public static int[] search(String str, int[] arr, String query) {
        List<Integer> indices = search(str, arr, query, 0, arr.length);
        if (indices == null) {
            return new int[0];
        }
        int[] res = new int[indices.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = indices.get(i);
        }
        return res;
    }

    private static List<Integer> search(String str, int[] arr, String query, int left, int right) {
        if (left > right) {
            return null;
        }

        int mid = (left + right) / 2;
        int cmp = compare(str, arr[mid], query);

        if (cmp < 0) {
            return search(str, arr, query, mid + 1, right);
        } else if (cmp > 0) {
            return search(str, arr, query, left, mid - 1);
        } else {
            List<Integer> leftSearch = search(str, arr, query, left, mid - 1);
            List<Integer> rightSearch = search(str, arr, query, mid + 1, right);

            List<Integer> res = new ArrayList<>();
            if (leftSearch != null) {
                res.addAll(leftSearch);
            }
            res.add(arr[mid]);
            if (rightSearch != null) {
                res.addAll(rightSearch);
            }
            return res;
        }
    }

    public static int[] search(Suffix[] arr, String query) {
        List<Integer> indices = search(arr, query, 0, arr.length);
        if (indices == null) {
            return new int[0];
        }
        int[] res = new int[indices.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = indices.get(i);
        }
        return res;
    }

    private static List<Integer> search(Suffix[] arr, String query, int left, int right) {
        if (left > right) {
            return null;
        }

        int mid = (left + right) / 2;
        int cmp = compare(arr[mid], query);

        if (cmp < 0) {
            return search(arr, query, mid + 1, right);
        } else if (cmp > 0) {
            return search(arr, query, left, mid - 1);
        } else {
            List<Integer> leftSearch = search(arr, query, left, mid - 1);
            List<Integer> rightSearch = search(arr, query, mid + 1, right);

            List<Integer> res = new ArrayList<>();
            if (leftSearch != null) {
                res.addAll(leftSearch);
            }
            res.add(arr[mid].index());
            if (rightSearch != null) {
                res.addAll(rightSearch);
            }
            return res;
        }
    }

    private static int compare(String str, int idx, String query) {
        if (str == null || query == null) {
            throw new IllegalArgumentException();
        }
        int queryIdx = 0;
        while (true) {
            if (query.length() == queryIdx) {
                return 0;
            } else if (str.length() == idx) {
                return -1;
            }
            if (str.charAt(idx) > query.charAt(queryIdx)) {
                return 1;
            } else if (str.charAt(idx) < query.charAt(queryIdx)) {
                return -1;
            }
            idx++;
            queryIdx++;
        }
    }

    private static int compare(Suffix corpus, String query) {
        if (corpus == null || query == null) {
            throw new IllegalArgumentException();
        }
        int i = 0;
        while (true) {
            if (query.length() == i) {
                return 0;
            } else if (corpus.length() == i) {
                return -1;
            }
            if (corpus.charAt(i) > query.charAt(i)) {
                return 1;
            } else if (corpus.charAt(i) < query.charAt(i)) {
                return -1;
            }
            i++;
        }
    }
}
