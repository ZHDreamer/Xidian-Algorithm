
/*************************************************************************
 *  Dijkstra's algorithm.
 *
 *************************************************************************/

import java.awt.Color;

public class Dijkstra {
    protected static double INFINITY = Double.MAX_VALUE;
    protected static double EPSILON = 0.000001;

    protected EuclideanGraph G;
    protected double[] dist;
    protected int[] pred;
    protected IndexPQ pq;
    // private MultiwayHeapIndexMinPQ<Double> pq;

    public Dijkstra(EuclideanGraph G) {
        this.G = G;
    }

    // return shortest path distance from s to d
    public double distance(int s, int d) {
        dijkstra(s, d, false);
        return dist[d];
    }

    // print shortest path from s to d (interchange s and d to print in right order)
    public void showPath(int d, int s) {
        dijkstra(s, d, false);
        if (pred[d] == -1) {
            System.out.println(d + " is unreachable from " + s);
            return;
        }
        for (int v = d; v != s; v = pred[v])
            System.out.print(v + "-");
        System.out.println(s);
    }

    // plot shortest path from s to d
    public void drawPath(int s, int d) {
        dijkstra(s, d, true);
        if (pred[d] == -1)
            return;
        Turtle.setColor(Color.red);
        for (int v = d; v != s; v = pred[v])
            G.point(v).drawTo(G.point(pred[v]));
        Turtle.render();
    }

    // Dijkstra's algorithm to find shortest path from s to d
    protected void dijkstra(int s, int d, boolean draw) {
        int V = G.V();

        // initialize
        dist = new double[V];
        pred = new int[V];
        for (int v = 0; v < V; v++)
            dist[v] = INFINITY;
        for (int v = 0; v < V; v++)
            pred[v] = -1;

        // priority queue
        IndexPQ pq = new IndexPQ(V);
        // pq = new MultiwayHeapIndexMinPQ<>(V);
        for (int v = 0; v < V; v++)
            pq.insert(v, dist[v]);

        // set distance of source
        dist[s] = 0.0;
        pred[s] = s;
        pq.change(s, dist[s]);

        // run Dijkstra's algorithm
        while (!pq.isEmpty()) {
            int v = pq.delMin();
            //// System.out.println("process " + v + " " + dist[v]);

            // v not reachable from s so stop
            if (pred[v] == d || pred[v] == -1)
                break;

            // scan through all nodes w adjacent to v
            IntIterator i = G.neighbors(v);
            while (i.hasNext()) {
                int w = i.next();
                if (draw) {
                    Turtle.setColor(Color.yellow);
                    G.point(v).drawTo(G.point(w));
                    Turtle.render();
                }
                double newPath = dist[v] + G.distance(v, w);
                if (newPath < dist[w] - EPSILON) {
                    dist[w] = newPath;
                    pq.change(w, dist[w]);
                    pred[w] = v;
                    //// System.out.println(" lower " + w + " to " + dist[w]);
                }
            }
        }
    }

}
