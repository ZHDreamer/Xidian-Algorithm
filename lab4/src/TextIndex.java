import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextIndex {
    /** This class should not be instantiated */
    public TextIndex() {
    }

    /**
     * search corpus using the suffix method
     *
     * @param corpus The corpus file to search on
     * @param query  The query file contain each query by line
     * @return A string that show the search result of each query
     */
    public static String suffixSearch(String corpus, String query) {
        corpus = readCorpus(corpus);
        File file = new File(query);
        FileInputStream is = null;
        StringBuilder sb = new StringBuilder();

        int[] words = getWords(corpus);

        Suffix[] suffix = new Suffix[words.length];
        for (int i = 0; i < words.length; i++) {
            suffix[i] = new Suffix(corpus, i + 1, words[i]);
        }

        Quick3string.sort(suffix);

        try {
            if (file.length() == 0) {
                return "";
            }
            is = new FileInputStream(file);
            InputStreamReader streamReader = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(streamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                int[] indices = BinarySearch.search(suffix, line);

                Arrays.sort(indices);
                for (int i = 0; i < indices.length; i++) {
                    if (i == 0) {
                        sb.append(indices[i]);
                    } else {
                        sb.append(", ");
                        sb.append(indices[i]);
                    }
                    if (i == indices.length - 1) {
                        sb.append(" ");
                    }
                }
                sb.append("- ");
                sb.append(line);
                sb.append("\n");
            }
            reader.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * search corpus using the index method
     *
     * @param corpus The corpus file to search on
     * @param query  The query file contain each query by line
     * @return A string that show the search result of each query
     */
    public static String indexSearch(String corpus, String query) {
        corpus = readCorpus(corpus);
        File file = new File(query);
        FileInputStream is = null;
        StringBuilder sb = new StringBuilder();

        int[] words = getWords(corpus);
        Map<Integer, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < words.length; i++) {
            indexMap.put(words[i], i + 1);
        }
        Quick3string.sort(corpus, words);

        try {
            if (file.length() == 0) {
                return "";
            }
            is = new FileInputStream(file);
            InputStreamReader streamReader = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(streamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                int[] indices = BinarySearch.search(corpus, words, line);

                for (int i = 0; i < indices.length; i++) {
                    indices[i] = indexMap.get(indices[i]);
                }
                Arrays.sort(indices);
                for (int i = 0; i < indices.length; i++) {
                    if (i == 0) {
                        sb.append(indices[i]);
                    } else {
                        sb.append(", ");
                        sb.append(indices[i]);
                    }
                    if (i == indices.length - 1) {
                        sb.append(" ");
                    }
                }
                sb.append("- ");
                sb.append(line);
                sb.append("\n");
            }
            reader.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * search corpus using the brute-force method
     *
     * @param corpus The corpus string to search on
     * @param query  The query file contain each query by line
     * @return A string that show the search result of each query
     */
    public static String bruteSearch(String corpus, String query) {
        corpus = readCorpus(corpus);
        File file = new File(query);
        FileInputStream is = null;
        StringBuilder sb = new StringBuilder();
        try {
            if (file.length() == 0) {
                return "";
            }
            is = new FileInputStream(file);
            InputStreamReader streamReader = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(streamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                int[] indices = BruteSearch.search(corpus, line);

                for (int i = 0; i < indices.length; i++) {
                    if (i == 0) {
                        sb.append(indices[i]);
                    } else {
                        sb.append(", ");
                        sb.append(indices[i]);
                    }
                    if (i == indices.length - 1) {
                        sb.append(" ");
                    }
                }
                sb.append("- ");
                sb.append(line);
                sb.append("\n");
            }
            reader.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * read the corpus and replace \n by space
     *
     * @param corpusFileName The corpus file to be read in
     * @return A string of corpus
     */
    public static String readCorpus(String corpusFileName) {
        File file = new File(corpusFileName);
        FileInputStream fis = null;
        StringBuilder sb = new StringBuilder();
        try {
            if (file.length() == 0) {
                return "";
            }
            fis = new FileInputStream(file);
            InputStreamReader streamReader = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(streamReader);
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append(' ');
            }
            reader.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * generate indices for each words
     *
     * @param str A string to be processed
     * @return A int array that contain indices of every words in the input string
     */
    private static int[] getWords(String str) {
        Boolean isWord = true;
        List<Integer> words = new ArrayList<>();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == ' ') {
                isWord = true;
            } else if (isWord) {
                isWord = false;
                words.add(i);
            }
        }

        int[] res = new int[words.size()];

        for (int i = 0; i < res.length; i++) {
            res[i] = words.get(i);
        }

        return res;
    }

    public static void main(String[] args) {
        String corpus = args[0];
        String query = args[1];
        // int n = 20;
        // long start, end;

        switch (args[2]) {
            case "suffix":
                System.out.println(suffixSearch(corpus, query));
                break;
            case "index":
                System.out.println(indexSearch(corpus, query));
                break;
            case "brute":
                System.out.println(bruteSearch(corpus, query));
                break;
        }

        // start = System.nanoTime();
        // for (int i = 0; i < n; i++) {
        // suffixSearch(corpus, query);
        // // System.out.println(indexSerach(corpus, query));
        // }
        // end = System.nanoTime();
        // System.out.println("Time for suffix:" + (end - start) / 1000000);

        // start = System.nanoTime();
        // for (int i = 0; i < n; i++) {
        // indexSearch(corpus, query);
        // // System.out.println(indexSerach(corpus, query));
        // }
        // end = System.nanoTime();
        // System.out.println("Time for index:" + (end - start) / 1000000);

        // start = System.nanoTime();
        // for (int i = 0; i < n; i++) {
        // bruteSearch(corpus, query);
        // // System.out.println(bruteSearch(corpus, query));
        // }
        // end = System.nanoTime();
        // System.out.println("Time for brute force:" + (end - start) / 1000000);
    }
}