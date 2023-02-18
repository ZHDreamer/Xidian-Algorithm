import java.util.ArrayList;
import java.util.List;

class BruteSearch {
    public static int[] search(String str, String query) {
        int queryIdx = 1, corpusIdx = 0;
        List<Integer> indices = new ArrayList<>();
        while (corpusIdx < str.length()) {
            // skip all the spaces (there can be spaces at beginning of string
            // so we neet to skip the spaces at first place)
            while (corpusIdx < str.length() && str.charAt(corpusIdx) == ' ') {
                corpusIdx++;
            }
            if (str.startsWith(query, corpusIdx)) {
                indices.add(queryIdx);
            }
            while (corpusIdx < str.length() && str.charAt(corpusIdx) != ' ') {
                corpusIdx++;
            }
            queryIdx++;
        }
        int[] res = new int[indices.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = indices.get(i);
        }
        return res;
    }
}