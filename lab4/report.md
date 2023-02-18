# 实验四 文本索引

20009200132 张明昊

## 一、实验要求

编写一个构建大块文本索引的程序，然后进行快速搜索，来查找某个字符串在该文本中的出现位置。

## 二、实验过程

### 方法一：暴力搜索

遍历搜索每个词出现的位置，记录到列表中，最后返回一个数组。

```java
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
```

### 方法二：后缀数组二分查找

构建后缀类，因为 `subString()` 现在并不是常数时间的复杂度了，所以我们需要单独构建一个后缀类来减小创建后缀数组的开销。

```java
public class Suffix implements Comparable<Suffix> {
    private final String text;
    private final int offset;
    private final int index;

    public Suffix(String text, int index) {
        this(text, index, 0);
    }

    public Suffix(String text, int index, int offset) {
        this.text = text;
        this.index = index;
        this.offset = offset;
    }

    public int length() {
        return text.length() - offset;
    }

    public char charAt(int i) {
        if (i + offset >= text.length())
            return 0;
        return text.charAt(offset + i);
    }

    public int index() {
        return index;
    }

    public int compareTo(Suffix that) {
        int len1 = this.length();
        int len2 = that.length();
        int n = Math.min(len1, len2);

        for (int i = 0; i < n; i++) {
            char c1 = this.charAt(i);
            char c2 = that.charAt(i);
            if (c1 != c2) {
                return c1 - c2;
            }
        }

        return len1 - len2;
    }
}
```

之后用排序算法将后缀数组排序，我选择使用的是字符串三路快排：

```java
public class Quick3string {
    private static final int CUTOFF = 5;

    private Quick3string() {
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

    private static void exch(Object[] a, int i, int j) {
        Object temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}
```

再用二分查找找到目标单词：

```java
public class BinarySearch {
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
```

### 方法三：指针排序

用哈希表储存实际在字符串位置和词索引的对应，然后对字符串位置也就是索引进行排序，创建索引：

```java
    int[] words = getWords(corpus);
    Map<Integer, Integer> indexMap = new HashMap<>();
    for (int i = 0; i < words.length; i++) {
        indexMap.put(words[i], i + 1);
    }
    Quick3string.sort(corpus, words);
```

增加对于字符串索引的排序算法：

```java
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

    private static void exch(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}
```

增加对于字符串索引的二分查找算法：

```java
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
}
```

### 搜索函数

```java
public class TextIndex {
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
            if (file.length() == 0) { return ""; }
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
        …
        int[] words = getWords(corpus);
        Map<Integer, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < words.length; i++) {
            indexMap.put(words[i], i + 1);
        }
        Quick3string.sort(corpus, words);

        try {
            …
            while ((line = reader.readLine()) != null) {
                int[] indices = BinarySearch.search(corpus, words, line);

                for (int i = 0; i < indices.length; i++) {
                    indices[i] = indexMap.get(indices[i]);
                }
        …
    }

    /**
     * search corpus using the brute-force method
     *
     * @param corpus The corpus string to search on
     * @param query  The query file contain each query by line
     * @return A string that show the search result of each query
     */
    public static String bruteSearch(String corpus, String query) {
        …
        try {
            …
            while ((line = reader.readLine()) != null) {
                int[] indices = BruteSearch.search(corpus, line);
        …
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
    }
}
```

### 测试运行时间

用 `hyperfine` 来测试运行时间：

```bash
hyperfine --warmup 3\
        'java -cp bin TextIndex res/alice29.txt res/alice_query.txt suffix'\
        'java -cp bin TextIndex res/alice29.txt res/alice_query.txt index'\
        'java -cp bin TextIndex res/alice29.txt res/alice_query.txt brute'
```

## 三、实验结果

```bash
$ java TextIndex res/small_corpus.txt res/small_query.txt brute
18 - wisdom
40, 46 - season
22 - age of foolishness
- age of fools

$ java TextIndex res/small_corpus.txt res/small_query.txt suffix
18 - wisdom
40, 46 - season
22 - age of foolishness
- age of fools

$ java TextIndex res/small_corpus.txt res/small_query.txt index
18 - wisdom
40, 46 - season
22 - age of foolishness
- age of fools
```

三种方法均和样例输出一致，证明算法正确

```text
Benchmark 1: java -cp bin TextIndex res/alice29.txt res/alice_query.txt suffix
  Time (mean ± σ):     205.3 ms ±  10.4 ms    [User: 586.8 ms, System: 66.6 ms]
  Range (min … max):   191.6 ms … 232.3 ms    15 runs

Benchmark 2: java -cp bin TextIndex res/alice29.txt res/alice_query.txt index
  Time (mean ± σ):     211.6 ms ±  17.9 ms    [User: 582.3 ms, System: 80.7 ms]
  Range (min … max):   191.1 ms … 242.4 ms    14 runs

Benchmark 3: java -cp bin TextIndex res/alice29.txt res/alice_query.txt brute
  Time (mean ± σ):     560.0 ms ±  16.4 ms    [User: 682.7 ms, System: 44.3 ms]
  Range (min … max):   531.4 ms … 587.7 ms    10 runs

Summary
  'java -cp bin TextIndex res/alice29.txt res/alice_query.txt suffix' ran
    1.03 ± 0.10 times faster than 'java -cp bin TextIndex res/alice29.txt res/alice_query.txt index'
    2.73 ± 0.16 times faster than 'java -cp bin TextIndex res/alice29.txt res/alice_query.txt brute'
```

可以看到后缀和指针排序两种方法相差不多，暴力算法明显比两种要慢，但是慢的不多，一部分原因是构建索引本身就需要一定时间，在查找次数不够多的情况下，建立索引并不一定有优势，如果增大查找次数，这个差距会更加明显（测试使用 n=1000）。

