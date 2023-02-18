import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.Stopwatch;

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
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);

        Stopwatch stopwatch = new Stopwatch();
        PercolationStats per = new PercolationStats(n, trials);

        System.out.println("time                    = " + stopwatch.elapsedTime());
        System.out.println("mean                    = " + per.mean());
        System.out.println("stddev                  = " + per.stddev());
        System.out.println("95% confidence interval = [" + per.confidenceLo()
                + ", " + per.confidenceHi() + "]");
    }
}
