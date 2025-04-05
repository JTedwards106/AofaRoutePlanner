package Aofa;

import java.util.*;

public class ShortestPathFinder {
    private final Graph graph;
    private final boolean useDistance; // true for distance, false for time

    public ShortestPathFinder(Graph graph, boolean useDistance) {
        this.graph = Objects.requireNonNull(graph, "Graph cannot be null");
        this.useDistance = useDistance;
    }

    public RouteResult findShortestPath(Town start, Town end) throws RouteCalculationException {
        // Input validation
        Objects.requireNonNull(start, "Start town cannot be null");
        Objects.requireNonNull(end, "End town cannot be null");
        if (!graph.containsTown(start) || !graph.containsTown(end)) {
            throw new RouteCalculationException("Start or end town not found in graph");
        }

        // Dijkstra's algorithm implementation
        Map<Town, Double> distances = new HashMap<>();
        Map<Town, Town> previousTowns = new HashMap<>();
        PriorityQueue<Town> queue = new PriorityQueue<>(
            Comparator.comparingDouble(town -> distances.getOrDefault(town, Double.MAX_VALUE))
        );

        // Initialize distances
        for (Town town : graph.getAllTowns()) {
            distances.put(town, Double.MAX_VALUE);
        }
        distances.put(start, 0.0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Town current = queue.poll();
            if (current.equals(end)) break;

            for (Road road : graph.getRoadsFrom(current)) {
                Town neighbor = road.getDestination();
                double edgeWeight = useDistance ? road.getDistance() : road.getTime();
                double newDistance = distances.get(current) + edgeWeight;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previousTowns.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        // Check if path exists
        if (distances.get(end) == Double.MAX_VALUE) {
            throw new RouteCalculationException("No path exists between " + start.getName() + " and " + end.getName());
        }

        // Build path
        List<Town> path = new LinkedList<>();
        for (Town current = end; current != null; current = previousTowns.get(current)) {
            path.add(0, current); // Insert at beginning to reverse order
        }

        double total = distances.get(end);
        return new RouteResult(path, total, useDistance);
    }
}
