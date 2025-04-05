package Aofa;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoogleMapsService {
    private final Map<String, Map<String, RouteResult>> mockRouteDatabase;

    public GoogleMapsService() {
        // Initialize with realistic mock data
        mockRouteDatabase = new HashMap<>();
        initializeMockRoutes();
    }

    private void initializeMockRoutes() {
        // Kingston routes
        addMockRoute("Kingston", "Morant Point", 
                   Arrays.asList("Kingston", "Port Antonio", "Morant Point"), 87, 105);
        addMockRoute("Kingston", "Montego Bay",
                   Arrays.asList("Kingston", "Spanish Town", "Montego Bay"), 120, 150);
        // Add more routes as needed...
    }

    private void addMockRoute(String origin, String destination, 
                            List<String> path, int distance, int time) {
        mockRouteDatabase.computeIfAbsent(origin, k -> new HashMap<>())
                       .put(destination, new RouteResult(path, distance, time));
    }

    public RouteResult getRoute(String origin, String destination, RouteCriteria criteria) {
        RouteResult result = mockRouteDatabase.getOrDefault(origin, Collections.emptyMap())
                                           .get(destination);
        
        if (result == null) {
            // Fallback for unknown routes
            result = new RouteResult(
                Arrays.asList(origin, destination),
                calculateFallbackDistance(origin, destination),
                calculateFallbackTime(origin, destination, criteria)
            );
        } else {
            // Apply criteria adjustments
            result = applyCriteriaEffects(result, criteria);
        }
        
        return result;
    }

    private RouteResult applyCriteriaEffects(RouteResult original, RouteCriteria criteria) {
        int timeAdjustment = 0;
        if (criteria.isAvoidTolls()) timeAdjustment += 15;
        if (criteria.isAvoidHighways()) timeAdjustment += 20;
        if (criteria.isAvoidHilly()) timeAdjustment += 10;
        
        return new RouteResult(
            original.path,
            original.totalDistance,
            original.totalTime + timeAdjustment
        );
    }

    private int calculateFallbackDistance(String origin, String destination) {
        // Simple heuristic based on town name lengths as fallback
        return 50 + (origin.length() + destination.length()) * 3;
    }

    private int calculateFallbackTime(String origin, String destination, RouteCriteria criteria) {
        int baseTime = calculateFallbackDistance(origin, destination) * 2;
        if (criteria.isAvoidTolls()) baseTime += 15;
        if (criteria.isAvoidHighways()) baseTime += 20;
        if (criteria.isAvoidHilly()) baseTime += 10;
        return baseTime;
    }
}