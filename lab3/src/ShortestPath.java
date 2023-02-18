import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;

/*************************************************************************
 * Compilation: javac ShortestPath.java
 * Execution: java ShortestPath file < input.txt
 * Dependencies: EuclideanGraph.java Dijkstra.java In.java StdIn.java
 * Turtle.java
 *
 * Reads in a map from a file, and two integers s and d from standard input,
 * and plots the shortest path from s to d using turtle graphics.
 *
 * % java ShortestPath usa.txt
 * 0 5000
 *
 ****************************************************************************/

public class ShortestPath {
    public static void main(String[] args) {

        Turtle.create(1000, 700);

        String input = "res/usa.txt";
        // read in the graph from a file
        In graphin = new In(input);
        EuclideanGraph G = new EuclideanGraph(graphin);
        // System.err.println("Done reading the graph " + args[0]);
        G.draw();

        int s = 0;
        int d = 87428;
        Dijkstra dijkstra = new Dijkstra(G);
        // Dijkstra dijkstra = new Astar(G);
        // dijkstra.showPath(s, d);
        dijkstra.drawPath(s, d);
        Turtle.render();
    }
}
