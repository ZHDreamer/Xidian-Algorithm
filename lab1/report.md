# 实验一 渗透问题

班级1CS20510201 张明昊 20009200132

## 一、实验要求

1. 利用合并——查找结构编写程序通过蒙特卡罗模拟来估计渗透阈值
2. 比较 `QuickFindUF` 和 `WeightedQuickUnionUF` 性能差距

## 二、实验过程

### 1. 构建 Percolation 类

N\*N 个点组成的网络，每个点有状态 Open 或 Closed，用并查集来判断网格是否相连，以此判断网络是否渗透。

但是检查每一个底部的点和顶部的点是否相连需要$O\left( N^{2} \right)$的复杂度，这是不能接受的。我们可以在网格上下各添加一个虚拟节点，初始时与上下每一个格子都相连，在判断是否渗透时只需检查两个虚拟节点是否相连即可。

但是接口要求实现判断一个格子是否被渗透，如果整个系统是渗透的，那么上述方法会出现 back wash 问题，如下图红色的格子其实是空的，但因为底部虚拟节点，这个格子与顶部也是相连的，所以判断时候也会认为他是渗透的。

![percolates](https://raw.githubusercontent.com/ZHDreamer/markdown-images/master/images/2023/02/13/20-32-35-picgo.png)

一个解决方法是只保留顶部虚拟节点，这样判断一个格子是否被渗透只用查找其是否与顶部相连即可，额外维护一个是否链接到底部的布尔数组，如果有一个与顶部相连的点和底部也相连，那么整个系统是联通的。我们可以利用并查集 Union 时判断根节点是否与底部相连，如果至少有一个与底部相连，那么 Union 后整个树也是与底部相连的，我们把整个树的根节点也设为与底部相连即可，根节点与底部相连说明整个树的节点都与底部相连。一开始只有最底行与底部相连。

判断整个系统是否渗透只需验证顶部虚拟节点所在树的根节点是不是与底部相连即可，如果相连，整个系统就是渗透的，如果不相连，就不是渗透的。

```java
public class Percolation {
    private final int n;
    private int opensites = 0;

    private final boolean[] sites;
    private final boolean[] connected; // if a site is connected to bottom

    private final WeightedQuickUnionUF uf;

    // create n-by-n grid, with all sites blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException(
                    "n-by-n grid's n must be positive");
        }

        this.n = n;
        // Init the site, but add 1 top virtual site
        sites = new boolean[n * n + 1];
        sites[0] = true;
        connected = new boolean[n * n + 1];

        uf = new WeightedQuickUnionUF(n * n + 1);
    }

    private void checkIndex(int row, int col) {
        if (row < 1 || row > n || col < 1 || col > n) {
            throw new IllegalArgumentException("row or col is out of bounds");
        }
    }

    /**
     * calculate the index for the given site (row, col),
     * where the index starts at 1.
     */
    private int calcIndex(int row, int col) {
        checkIndex(row, col);
        return (row - 1) * n + col;
    }

    /*
     * connect 2 sites and update connection to bottom
     */
    private void connect(int pos1, int pos2) {
        if (connected[uf.find(pos1)] || connected[uf.find(pos2)]) {
            uf.union(pos1, pos2);
            connected[uf.find(pos1)] = true;
        } else {
            uf.union(pos1, pos2);
        }
    }

    // open site (row, col) if it is not open already
    public void open(int row, int col) {
        int pos = calcIndex(row, col);

        if (sites[pos]) {
            return;
        }
        // open this site
        sites[pos] = true;
        opensites++;

        // connect head or tail
        if (row == n) {
            connected[pos] = true;
        }
        if (row == 1) {
            connect(0, pos);
        }
        // connect 4 directions
        if (col > 1 && sites[pos - 1]) {
            connect(pos, pos - 1);
        }
        if (col < n && sites[pos + 1]) {
            connect(pos, pos + 1);
        }
        if (row > 1 && sites[pos - n]) {
            connect(pos, pos - n);
        }
        if (row < n && sites[pos + n]) {
            connect(pos, pos + n);
        }
    }

    /** is the site (row, col) open? */
    public boolean isOpen(int row, int col) {
        int pos = calcIndex(row, col);
        return sites[pos];
    }

    /** is the site (row, col) full? */
    public boolean isFull(int row, int col) {
        int pos = calcIndex(row, col);
        return uf.find(pos) == uf.find(0);
    }

    public int numberOfOpenSites() {
        return opensites;
    }

    /** does the system percolate? */
    public boolean percolates() {
        return connected[uf.find(0)];
    }
}
```

### 2.蒙特卡洛模拟

本实验通过蒙特卡洛算法，估算渗透阈值，具体做法为：

1. 初始化 n*n 全为 Blocked 的网格系统
2. 随机 Open 一个点，重复执行，直到整个系统变成渗透的为止
3. 上述过程重复 T 次，计算平均值、标准差、96% 置信区间

```java
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private int sites, trials;
    private double[] experiments;

    private double mean;
    private double stddev;
    private double conLo;
    private double conHi;

    private double confidence95 = 1.96;

    // perform trials independent experiments on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException("Illegal Argument");
        }

        this.sites = n;
        this.trials = trials;

        if (sites == 1) {
            mean = 1;
            stddev = Double.NaN;
            conLo = Double.NaN;
            conHi = Double.NaN;
        } else {
            experiments = new double[this.trials];

            for (int i = 0; i < this.trials; i++) {
                Percolation checkPerco = new Percolation(n);
                int count = 0;
                while (!checkPerco.percolates()) {
                    int row = StdRandom.uniformInt(n) + 1;
                    int col = StdRandom.uniformInt(n) + 1;
                    if (!checkPerco.isOpen(row, col)) {
                        checkPerco.open(row, col);
                        count++;
                    }
                }

                experiments[i] = (double) count / (sites * sites);
            }
        }

        mean = StdStats.mean(experiments);
        stddev = StdStats.stddev(experiments);
        conLo = mean - (confidence95 * stddev) / Math.sqrt(trials);
        conHi = mean + (confidence95 * stddev) / Math.sqrt(trials);
    }

    public double mean() {
        return mean;
    }

    public double stddev() {
        return stddev;
    }

    public double confidenceLo() {
        return conLo;
    }

    public double confidenceHi() {
        return conHi;
    }

    public static void main(String[] args) {
        int n = 200;
        int trials = 100;

        PercolationStats per = new PercolationStats(n, trials);

        System.out.println("mean                    = " + per.mean());
        System.out.println("stddev                  = " + per.stddev());
        System.out.println("95% confidence interval = [" + per.confidenceLo()
                + ", " + per.confidenceHi() + "]");
    }
}
```

## 三、实验结果

```bash
$ java-algs4 PercolationStats 200 100
mean                    = 0.5918397500000001
stddev                  = 0.009685582390852907
95% confidence interval = [0.589941375851393, 0.5937381241486073]
```

`QuickFindUF`

|    N |    T | Time(s) |
| ---: | ---: | ------: |
|   50 |  100 |   0.140 |
|  100 |  100 |   1.411 |
|  200 |  100 |  24.131 |
|  100 |   50 |   0.768 |
|  100 |  200 |   2.935 |

`WeightedQuickUnionUF`

|    N |    T | Time(s) |
| ---: | ---: | ------: |
|   50 |  100 |   0.029 |
|  100 |  100 |   0.070 |
|  200 |  100 |   0.188 |
|  100 |   50 |   0.044 |
|  100 |  200 |   0.110 |

由此可见，`WeightedQuickUnionUF` 比 `QuickFindUF` 快很多，运行时间大约与 N 为平方关系，与 T 为线性关系。
