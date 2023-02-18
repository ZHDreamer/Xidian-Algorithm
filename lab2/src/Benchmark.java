import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SuppressWarnings("unchecked")
public class Benchmark {
    private static final int TEST_INTERVAL = 10;
    private static final List<String> sortMethods = new ArrayList<>() {
        {
            add("InsertionSort");
            add("MergeSortTD");
            add("MergeSortBU");
            add("QuickSort");
            add("QuickSort3P");
        }
    };

    private static class Result {
        int time;
        int memory;

        Result(int time, int memory) {
            this.time = time;
            this.memory = memory;
        }
    }

    private static class Results {
        List<Result> results;

        Results() {
            results = new ArrayList<>();
        }

        public void add(Result e) {
            results.add(e);
        }

        public double avgTime() {
            double avg = 0;
            for (Result result : results) {
                avg += result.time / results.size();
            }
            return avg;
        }

        public double avgMemo() {
            double avg = 0;
            for (Result result : results) {
                avg += result.memory / results.size();
            }
            return avg;
        }

    }

    /* generate a random array in [low, high) */
    public static int[] generateRandomArray(int n, int low, int high) {
        assert low < high;

        int[] a = new int[n];

        for (int i = 0; i < n; i++) {
            a[i] = (int) (Math.random() * (high - low) + low);
        }

        return a;
    }

    /* generate a random array in [low, high) */
    public static Integer[] generateRandomIntegerArray(int n, int low, int high) {
        assert low < high;

        Integer[] a = new Integer[n];

        for (int i = 0; i < n; i++) {
            a[i] = (Integer) (int) (Math.random() * (high - low) + low);
        }

        return a;
    }

    /* generate a almost sorted array */
    public static int[] generateOrderArray(int n, int swaps) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
        }

        for (int i = 0; i < swaps; i++) {
            Sort.swap(a, (int) Math.random() * n, (int) Math.random() * n);
        }

        return a;
    }

    public static int[] generateReverseArray(int n, int swaps) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = n - i - 1;
        }

        for (int i = 0; i < swaps; i++) {
            Sort.swap(a, (int) Math.random() * n, (int) Math.random() * n);
        }

        return a;
    }

    public static boolean isSorted(Object[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            if (Sort.compare(a[i], a[i + 1]) > 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSorted(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            if (a[i] > a[i + 1]) {
                return false;
            }
        }
        return true;
    }

    public static Result testSort(String sortClassName, Object[] a) {
        Class sortClass;
        Method sortMethod;
        int time = -1;
        int memory = -1;

        try {
            sortClass = Class.forName(sortClassName);
            sortMethod = sortClass.getMethod("sort", Object[].class);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(-2, -2);
        }

        Object[] params = new Object[a.length];
        for (int i = 0; i < a.length; i++) {
            params[i] = a[i];
        }

        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                sortMethod.invoke(null, (Object) params);
                return "success";
            }
        };
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<String> future = executorService.submit(task);

        try {
            Runtime run = Runtime.getRuntime();
            run.gc();
            long startMemo = run.totalMemory() - run.freeMemory();
            long startTime = System.nanoTime();
            future.get(5000, TimeUnit.MILLISECONDS);
            // sortMethod.invoke(null, (Object) params);
            long endTime = System.nanoTime();
            long endMemo = run.totalMemory() - run.freeMemory();
            time = (int) (endTime - startTime);
            memory = (int) (endMemo - startMemo);

            assert isSorted(params);
        } catch (TimeoutException e) {
            time = -1;
        } catch (Exception e) {
            e.printStackTrace();
            time = -2;
        } finally {
            executorService.shutdownNow();
        }
        return new Result(time, memory);
    }

    public static Results testSort(String sortClassName, Object a[], int n) {
        Results ret = new Results();
        for (int i = 0; i < n; i++) {
            Result r = testSort(sortClassName, a);
            ret.add(r);
            if (r.time < 0) {
                break;
            }
        }
        return ret;
    }

    public static Result testSort(String sortClassName, int[] a) {
        Class sortClass;
        Method sortMethod;
        int time = -1;
        int memory = -1;

        try {
            sortClass = Class.forName(sortClassName);
            sortMethod = sortClass.getMethod("sort", int[].class);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(-2, -2);
        }

        int[] params = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            params[i] = a[i];
        }

        Callable<String> task = new Callable<String>() {
            @Override
            public String call() throws Exception {
                sortMethod.invoke(null, (Object) params);
                return "success";
            }
        };
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<String> future = executorService.submit(task);

        try {
            Runtime run = Runtime.getRuntime();
            run.gc();
            long startMemo = run.totalMemory() - run.freeMemory();
            long startTime = System.nanoTime();
            future.get(5000, TimeUnit.MILLISECONDS);
            // sortMethod.invoke(null, (Object) params);
            long endTime = System.nanoTime();
            long endMemo = run.totalMemory() - run.freeMemory();
            time = (int) (endTime - startTime);
            memory = (int) (endMemo - startMemo);

            assert (isSorted(params));
        } catch (TimeoutException e) {
            time = -1;
        } catch (Exception e) {
            e.printStackTrace();
            time = -2;
        } finally {
            executorService.shutdownNow();
        }
        return new Result(time, memory);
    }

    public static Results testSort(String sortClassName, int a[], int n) {
        Results ret = new Results();
        for (int i = 0; i < 3; i++) {
            Result r = testSort(sortClassName, a);
            if (r.time < 0) {
                break;
            }
        }

        for (int i = 0; i < n; i++) {
            Result r = testSort(sortClassName, a);
            ret.add(r);
            if (r.time < 0) {
                break;
            }
        }
        return ret;
    }

    public static void printResults(Map<Integer, Map<String, Results>> results, String arrayType) {
        DecimalFormat df = new DecimalFormat("#,###");
        System.out.println("Test array:" + arrayType);
        System.out.println("Time usage");

        System.out.printf("%10s", "n");
        for (String s : sortMethods) {
            System.out.printf("%16s", s);
        }
        System.out.println();

        for (int n : results.keySet()) {
            System.out.printf("%10d", n);
            Map<String, Results> resultN = results.get(n);
            for (String s : sortMethods) {
                Results result = resultN.get(s);
                System.out.printf("%16s", df.format(result.avgTime() / 1000) + " us");
            }
            System.out.println();
        }

        System.out.println();
        System.out.println("Memory usage");

        System.out.printf("%10s", "n");
        for (String s : sortMethods) {
            System.out.printf("%16s", s);
        }

        System.out.println();
        for (int n : results.keySet()) {
            System.out.printf("%10d", n);
            Map<String, Results> resultN = results.get(n);
            for (String s : sortMethods) {
                Results result = resultN.get(s);
                System.out.printf("%16s", df.format(result.avgMemo() / 1024) + " kB");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Map<Integer, Map<String, Results>> result = new TreeMap<>();

        for (int n = 10; n <= 100000; n *= 10) {
            // Integer[] a = generateRandomIntegerArray(n, 0, n);
            int[] a = generateRandomArray(n, 0, n);
            result.put(n, new HashMap<>());
            for (String sortMethod : sortMethods) {
                result.get(n).put(sortMethod, testSort(sortMethod, a, TEST_INTERVAL));
            }
        }

        printResults(result, "random");

        // System.out.println("InsertionSort\t QuickSort");
        // System.out.println(testSort("MergeSortBU", a) + "\t " +
        // testSort("MergeSortTD", a) + "\t " + testSort("MergeSortTDOptimized", a));
        // System.out.println("InsertionSort: " + testSort("InsertionSort", a));
        // System.out.println("QuickSort: " + testSort("QuickSort", a));

        System.exit(0);
    }
}