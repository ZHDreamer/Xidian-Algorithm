class InsertionSort extends Sort {
    public static void sort(Object[] array) {
        // for index 1 because index 0 as one element is already sorted
        for (int i = 1; i < array.length; i++) {

            // store the current element
            Object tmp = array[i];

            // search and swap the element for right to left
            int j = i;
            while (j > 0 && less(tmp, array[j - 1])) {
                array[j] = array[j - 1];
                j--;
            }

            array[j] = tmp;
        }
    }

    public static void sort(int[] array) {
        // for index 1 because index 0 as one element is already sorted
        for (int i = 1; i < array.length; i++) {

            // store the current element
            int tmp = array[i];

            // search and swap the element for right to left
            int j = i;
            while (j > 0 && tmp < array[j - 1]) {
                array[j] = array[j - 1];
                j--;
            }

            array[j] = tmp;
        }
    }
}