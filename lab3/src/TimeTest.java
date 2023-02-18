import edu.princeton.cs.algs4.In;

public class TimeTest {
    public static void main(String[] args) {
        String input = "res/usa.txt";
        // read in the graph from a file
        In graphin = new In(input);
        EuclideanGraph G = new EuclideanGraph(graphin);
        // Dijkstra dijkstra = new Dijkstra(G);
        Dijkstra dijkstra = new Astar(G);

        String test = "res/usa-5000short.txt";
        In testin = new In(test);
        long startTime, endTime;
        startTime = System.nanoTime();
        while (testin.hasNextChar()) {
            int s = testin.readInt();
            int d = testin.readInt();
            dijkstra.distance(s, d);
        }
        endTime = System.nanoTime();
        System.out.println((endTime - startTime) / 1000000);
    }
}
