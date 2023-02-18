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
