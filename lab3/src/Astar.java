import java.util.LinkedList;

import java.awt.Color;

public class Astar extends Dijkstra {
    private LinkedList<Integer> changed = new LinkedList<>();

    public Astar(EuclideanGraph G) {
        super(G);
        int V = G.V();

        dist = new double[V];
        pred = new int[V];
        for (int v = 0; v < V; v++)
            dist[v] = INFINITY;
        for (int v = 0; v < V; v++)
            pred[v] = -1;

    }

    @Override
    // return shortest path distance from s to d
    public double distance(int s, int d) {
        dijkstra(s, d, false);
        return dist[d] + G.distance(s, d);
    }

    @Override
    protected void dijkstra(int s, int d, boolean draw) {
        int V = G.V();

        while (!changed.isEmpty()) {
            int v = changed.poll();
            dist[v] = Double.POSITIVE_INFINITY;
            pred[v] = -1;
        }

        MultiwayHeapIndexMinPQ<Double> pq = new MultiwayHeapIndexMinPQ<>(8, V);
        // IndexPQ pq = new IndexPQ(V);

        dist[s] = 0.0;
        pred[s] = s;
        pq.insert(s, dist[s]);

        while (!pq.isEmpty()) {
            int v = pq.delMin();
            if (v == d || pred[v] == -1) {
                break;
            }
            IntIterator i = G.neighbors(v);
            while (i.hasNext()) {
                int w = i.next();
                if (draw) {
                    Turtle.setColor(Color.yellow);
                    G.point(v).drawTo(G.point(w));
                    Turtle.render();
                }
                double newPath = dist[v] + G.distance(v, w) + G.distance(w, d) - G.distance(v, d);
                // double newPath = dist[v] + G.distance(v, w);
                if (newPath < dist[w] - EPSILON) {
                    if (dist[w] < INFINITY) {
                        pq.change(w, newPath);
                    } else {
                        pq.insert(w, newPath);
                        changed.add(w);
                    }
                    dist[w] = newPath;
                    pred[w] = v;
                }
            }
        }
    }
}
