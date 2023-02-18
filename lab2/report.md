# 实验二 排序算法性能比较

20009200132 张明昊

## 一、实验要求

1. 实现多种排序算法，包括插入排序（InsertionSort）、自顶向下归并排序（MergeSortTD）、自底向上归并排序（MergeSortBU）、随机快速排序（QuickSort）、3 路划分快速排序（QuickSort3P）。
2. 针对不同规模的数据，测试比较不同算法的时间空间性能。

## 二、实验过程

1.  设计了 Sort 类，用于实现一些 helper function 给所有的排序算法，所有排序算法都是 Sort 类的子类。

    ```java
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
    ```

2.  编写了生成随机和递增递减的 Array 的算法

    ```java
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

    public static boolean isSorted(int[] a) {
        for (int i = 0; i < a.length - 1; i++) {
            if (a[i] > a[i + 1]) {
                return false;
            }
        }
        return true;
    }
    ```

3.  编写了 testSort 方法，用于测试算法消耗的时间和空间，并且汇总输出

    ```java
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
        // 基本同上
    }
    
    public static Results testSort(String sortClassName, int a[], int n) {
        // 基本同上
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
        System.exit(0);
    }
    ```

## 三、实验结果

以下结果为 3 次 warm up run 后，10 次测试的平均值，-1 表示执行时间过长：

1.  随机数据：

    Time usage (ms):

    | Size    | IS      | TDM     | BUM     | RQ      | QD3P    |
    | ------- | ------- | ------- | ------- | ------- | ------- |
    | 10      | 0.179   | 0.180   | 0.183   | 0.202   | 0.151   |
    | 100     | 0.238   | 0.130   | 0.140   | 0.127   | 0.365   |
    | 1000    | 0.336   | 0.287   | 0.448   | 0.299   | 0.388   |
    | 10000   | 11.478  | 1.589   | 1.938   | 1.995   | 1.876   |
    | 100000  | 831.992 | 9.939   | 8.923   | 9.203   | 10.852  |
    | 1000000 | -1      | 108.498 | 106.836 | 103.695 | 121.782 |

    Space usage (kB):

    | Size    | IS  | TDM   | BUM   | RQ  | QD3P |
    | ------- | --- | ----- | ----- | --- | ---- |
    | 10      | 86  | 82    | 86    | 82  | 82   |
    | 100     | 82  | 82    | 74    | 68  | 74   |
    | 1000    | 86  | 82    | 82    | 78  | 82   |
    | 10000   | 70  | 90    | 86    | 86  | 82   |
    | 100000  | 82  | 477   | 468   | 82  | 78   |
    | 1000000 | -1  | 4,485 | 4,953 | 770 | 668  |

2.  有序递增数据：

    Time usage (ms):

    | Size    | IS    | TDM    | BUM    | RQ      | QD3P    |
    | ------- | ----- | ------ | ------ | ------- | ------- |
    | 10      | 0.146 | 0.174  | 0.180  | 0.186   | 0.153   |
    | 100     | 0.152 | 0.135  | 0.174  | 0.743   | 0.194   |
    | 1000    | 0.221 | 0.145  | 0.279  | 0.378   | 0.374   |
    | 10000   | 0.390 | 0.269  | 1.022  | 1.784   | 1.895   |
    | 100000  | 0.159 | 0.897  | 3.739  | 14.781  | 17.732  |
    | 1000000 | 0.741 | 11.452 | 45.526 | 176.125 | 217.089 |

    Space usage (kB):

    | Size    | IS  | TDM   | BUM   | RQ  | QD3P |
    | ------- | --- | ----- | ----- | --- | ---- |
    | 10      | 86  | 82    | 82    | 82  | 82   |
    | 100     | 74  | 78    | 78    | 80  | 82   |
    | 1000    | 82  | 82    | 78    | 78  | 82   |
    | 10000   | 78  | 82    | 78    | 86  | 78   |
    | 100000  | 82  | 468   | 473   | 82  | 82   |
    | 1000000 | 501 | 4,662 | 4,623 | 640 | 675  |

3.  基本有序数据 (10% swaps):

    Time usage (ms):

    | Size    | IS    | TDM    | BUM    | RQ      | QD3P    |
    | ------- | ----- | ------ | ------ | ------- | ------- |
    | 10      | 0.127 | 0.132  | 0.124  | 0.156   | 0.145   |
    | 100     | 0.127 | 0.110  | 0.159  | 0.201   | 0.186   |
    | 1000    | 0.180 | 0.131  | 0.269  | 0.327   | 0.400   |
    | 10000   | 0.418 | 0.183  | 0.999  | 1.769   | 1.804   |
    | 100000  | 0.175 | 0.985  | 3.942  | 15.750  | 21.524  |
    | 1000000 | 0.665 | 11.970 | 47.518 | 182.972 | 174.319 |

    Space usage (kB):

    | Size    | IS  | TDM   | BUM   | RQ  | QD3P |
    | ------- | --- | ----- | ----- | --- | ---- |
    | 10      | 82  | 82    | 86    | 82  | 82   |
    | 100     | 78  | 78    | 82    | 88  | 74   |
    | 1000    | 78  | 82    | 82    | 86  | 82   |
    | 10000   | 82  | 78    | 78    | 88  | 84   |
    | 100000  | 78  | 473   | 468   | 78  | 86   |
    | 1000000 | 488 | 4,657 | 4.716 | 690 | 690  |

4.  逆序数据：

    Time usage (ms):

    | Size    | IS     | TDM    | BUM    | RQ      | QD3P    |
    | ------- | ------ | ------ | ------ | ------- | ------- |
    | 10      | 0.164  | 0.220  | 0.203  | 0.186   | 0.212   |
    | 100     | 0.328  | 0.189  | 0.169  | 0.197   | 0.153   |
    | 1000    | 0.377  | 0.602  | 0.293  | 0.347   | 0.404   |
    | 10000   | 23.804 | 0.635  | 1.036  | 1.837   | 1.801   |
    | 100000  | -1     | 5.527  | 4.124  | 14.975  | 16.161  |
    | 1000000 | -1     | 35.349 | 40.640 | 114.107 | 118.659 |

    Space usage (kB):

    | Size    | IS  | TDM   | BUM   | RQ  | QD3P |
    | ------- | --- | ----- | ----- | --- | ---- |
    | 10      | 82  | 82    | 86    | 82  | 82   |
    | 100     | 78  | 78    | 82    | 88  | 74   |
    | 1000    | 78  | 82    | 82    | 86  | 82   |
    | 10000   | 82  | 78    | 78    | 88  | 84   |
    | 100000  | -1  | 471   | 473   | 82  | 82   |
    | 1000000 | -1  | 4,968 | 5,120 | 973 | 973  |

5.  大量重复随机数据（只能取 0\~9）：

    Time usage (ms):

    | Size    | IS      | TDM    | BUM    | RQ     | QD3P   |
    | ------- | ------- | ------ | ------ | ------ | ------ |
    | 10      | 0.165   | 0.180  | 0.152  | 0.148  | 0.142  |
    | 100     | 0.186   | 0.142  | 0.157  | 0.167  | 0.423  |
    | 1000    | 0.239   | 0.279  | 0.467  | 0.291  | 0.269  |
    | 10000   | 9.754   | 1.028  | 1.343  | 1.227  | 0.657  |
    | 100000  | 717.193 | 6.015  | 4.996  | 4.919  | 3.074  |
    | 1000000 | -1      | 66.070 | 57.885 | 51.773 | 29.807 |

    Space usage (kB):

    | Size    | IS  | TDM   | BUM   | RQ    | QD3P  |
    | ------- | --- | ----- | ----- | ----- | ----- |
    | 10      | 82  | 82    | 86    | 82    | 82    |
    | 100     | 78  | 78    | 82    | 88    | 74    |
    | 1000    | 78  | 82    | 82    | 86    | 82    |
    | 10000   | 82  | 78    | 78    | 88    | 84    |
    | 100000  | 86  | 460   | 468   | 78    | 78    |
    | 1000000 | -1  | 4,732 | 5,120 | 1,016 | 1,024 |

## 四、结果分析

1. 对于小规模数据，所有算法执行都很快，理论上来说插入排序在小规模数据上是最快的，因为创建递归函数的开销在小规模数据时比较明显。
2. 对于大规模数据，除了插入排序其他算法的运行时间都可以接受，因为其他算法的平均时间复杂度都是 $O(n\log n)$。
3. 对于递增数据，插入排序表现最好，归并排序表现也不错，两种快排并没有明显提升，因为在进行排序前会 Shuffle 整个数组。
4. 对于递减数据，归并排序表现较好，快排同样没有明显影响，插入排序表现最差，因为每次插入都需要和有序部分的所有元素进行一次比较。

## 五、回答问题

### 1.  哪种排序算法对于升序排序的数组表现最好？为什么？

插入排序最好，因为对于升序的数据，每次插入只需要比较一次，那么总体比较次数就是 n 次，要比其他算法至少 $n\log n$ 次要好很多。

### 2.  同样的算法对于基本有序的数组表现同样好吗？为什么？

同样表现很好，因为总体来说插入排序对于基本有序的数组比较次数还是明显比其他算法少的。

### 3.  一般来说，输入数据的顺序是否影响排序算法的性能？请参考表中的特定数据来回答这个问题

对于基本的快速排序，在数组为顺序时算法会退化为 quadratic time，但是由于进行了 shuffle，所以输入的顺序不影响快速排序的性能。插入排序在数组为逆序的时候会进行更多的比较，所以性能会被影响。所以输入数据的顺序确实会影响算法的性能。

### 4.  哪一类在较短的（即 n=1,000）数据集上表现最好？在较长的数据集（即 n=10,000）上，同样的算法一样好吗？为什么？

在小数据集上，所有排序算法表现差别不大，插入排序在 n=10 表现比较好。在较大数据集上插入排序表现比较差，因为插入排序是平方的复杂度，在 n 较小的时候是可能比其他 $n\log n$ 的算法快的，但是一旦 n 较大时，插入排序消耗的时间就会快速的增长。

### 5.  总的来说，哪种排序算法做的更好？给出关于为什么存在性能差异的假设

快速排序是最快的，同时消耗的额外空间也是比较小的。因为这里测试的数组是 `int[]`，`int` 是 Java 的原始类型，相对来说比较操作是比较快的。快排虽然用了更多的比较，但是移动数量比归并排序要少，所以总体来说是更快的。但是如果是引用类型数据，因为 `comparaTo` 接口并不保证性能，所以归并排序是更快的。同时排序稳定性对于引用类型来说也是有意义的，所以对于引用类型，归并排序是更好的。

### 6.  测试结果是否有不一致的情况？为什么发生了这样的事情？

对于 n 比较小的情况，因为总的运行时间比较小，所以很容易出现某次运行误差比较大的情况。对于这个问题，Java 基准测试 JMH 是对于每个测试都在一段时间内不断运行而不是只运行固定次数来解决这个问题。还有可能是因为方法装载或 JIT 优化等问题导致前几次运行速度明显较慢，解决方法是提前进行几次 warm up run，之后再开始真正的测试。
