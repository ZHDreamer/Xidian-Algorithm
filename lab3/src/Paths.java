import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;

/*************************************************************************
 * Compilation: javac Paths.java
 * Execution: java Paths file < input.txt
 * Dependencies: EuclideanGraph.java Dijkstra.java In.java StdIn.java
 *
 * Reads in a map from a file, and repeatedly reads in two integers s
 * and d from standard input, and prints the shortest path from s
 * to d to standard output.
 *
 ****************************************************************************/

public class Paths {
    public static void main(String[] args) {
        String input = "res/usa.txt";
        // read in the graph from a file
        In graphin = new In(input);
        EuclideanGraph G = new EuclideanGraph(graphin);
        System.err.println("Done reading the graph " + input);
        System.err.println("Enter query pairs from stdin");

        // read in the s-d pairs from standard input
        // Dijkstra dijkstra = new Dijkstra(G);
        Dijkstra dijkstra = new Astar(G);
        while (!StdIn.isEmpty()) {
            int s = StdIn.readInt();
            int d = StdIn.readInt();
            // dijkstra.showPath(s, d);
            System.out.println(dijkstra.distance(s, d));
        }
    }
}
