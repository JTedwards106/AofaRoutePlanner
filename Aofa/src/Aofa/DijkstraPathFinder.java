package Aofa;

import java.util.*;

public class DijkstraPathFinder {
    public RouteResult findShortestPath(Town start, Town end, RouteCriteria criteria) {
        Map<Town, Integer> distances = new HashMap<>();
        Map<Town, Integer> times = new HashMap<>();
        Map<Town, Town> previous = new HashMap<>();
        
        PriorityQueue<Town> queue = new PriorityQueue<>(
            Comparator.comparingInt(town -> 
                criteria.isPreferTime() ? times.get(town) : distances.get(town))
        );

        // Initialize all towns
        Set<Town> allTowns = getAllTowns(start);
        for (Town town : allTowns) {
            distances.put(town, Integer.MAX_VALUE);
            times.put(town, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        times.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            Town current = queue.poll();
            if (current.equals(end)) break;

            for (Road road : current.getConnections()) {
                if (criteria.shouldAvoid(road.getType())) continue;

                Town neighbor = road.getDestination();
                int newDist = distances.get(current) + road.getDistance();
                int newTime = times.get(current) + road.getAdjustedTime();

                int currentBest = criteria.isPreferTime() ? 
                    times.get(neighbor) : distances.get(neighbor);

                if ((criteria.isPreferTime() && newTime < currentBest) ||
                    (!criteria.isPreferTime() && newDist < currentBest)) {
                    
                    distances.put(neighbor, newDist);
                    times.put(neighbor, newTime);
                    previous.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        return buildResult(previous, end, distances.get(end), times.get(end));
    }

    private RouteResult buildResult(Map<Town, Town> previous, Town end, 
                                  Integer distance, Integer time) {
        if (distance == null || time == null) return null;

        List<String> path = new ArrayList<>();
        Town current = end;
        while (current != null) {
            path.add(0, current.getName());
            current = previous.get(current);
        }

        return new RouteResult(path, distance, time);
    }

    private Set<Town> getAllTowns(Town start) {
        Set<Town> visited = new HashSet<>();
        Queue<Town> queue = new LinkedList<>();
        queue.add(start);
        
        while (!queue.isEmpty()) {
            Town current = queue.poll();
            if (visited.contains(current)) continue;
            
            visited.add(current);
            for (Road road : current.getConnections()) {
                queue.add(road.getDestination());
            }
        }
        return visited;
    }
}