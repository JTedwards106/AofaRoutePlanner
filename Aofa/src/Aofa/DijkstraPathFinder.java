package Aofa;

import java.util.*;

public class DijkstraPathFinder {
    public RouteResult findShortestPath(Town start, Town end, RouteCriteria criteria) {
         /* Prevents NullPointerException by validating all input parameters */
       if (start == null || end == null || criteria == null) {
            throw new IllegalArgumentException("Start town, end town, and criteria cannot be null");
        }
         /* Ensures start/end towns exist in the graph */
        Set<Town> allTowns = getAllTowns(start);
        if (!allTowns.contains(start) || !allTowns.contains(end)) {
            throw new RouteCalculationException("Start or end town not found in road network");
        }

        /* Prevents unnecessary computation when start == end */
        if (start.equals(end)) {
            return new RouteResult(Collections.singletonList(start.getName()), 0, 0);
        } Map<Town, Integer> distances = new HashMap<>();
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
                /* Skips invalid roads and prevents NullPointerException */
                if (road == null || road.getDestination() == null) continue;
                if (criteria.shouldAvoid(road.getType())) continue;

                Town neighbor = road.getDestination();
                /* Detects invalid negative distances/times */
                 if (road.getDistance() < 0 || road.getAdjustedTime() < 0) {
                    throw new RouteCalculationException(
                        "Invalid road weights between " + current.getName() + 
                        " and " + neighbor.getName());
                }
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
         /* Handles cases where no valid route exists */
        if (!previous.containsKey(end)) {
            throw new RouteCalculationException(
                "No valid path found between " + start.getName() + 
                " and " + end.getName() + " with current criteria");
        }

        return buildResult(previous, end, distances.get(end), times.get(end));
    }

    private RouteResult buildResult(Map<Town, Town> previous, Town end, 
                                  Integer distance, Integer time) {
        
       /* Ensures the computed path has valid values */
        if (distance == null || time == null || distance == Integer.MAX_VALUE) {
            throw new RouteCalculationException("Invalid route calculation result");
        }

        List<String> path = new ArrayList<>();
        Town current = end;
        while (current != null) {
            path.add(0, current.getName());
            current = previous.get(current);
        }

        return new RouteResult(path, distance, time);
    }

    private Set<Town> getAllTowns(Town start) {
       /* Prevents infinite loops with cycle detection */
        Set<Town> visited = new HashSet<>();
        Queue<Town> queue = new LinkedList<>();
        queue.add(start);
        
        while (!queue.isEmpty()) {
            Town current = queue.poll();
            if (visited.contains(current)) continue;
            
            visited.add(current);
            for (Road road : current.getConnections()) {
                if (road != null && road.getDestination() != null) {
                    queue.add(road.getDestination());
                }
            }
        }
        return visited;
    }
}
