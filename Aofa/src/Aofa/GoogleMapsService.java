package Aofa;

import java.util.*;

public class GoogleMapsService {
    private final Map<String, Map<String, RouteResult>> mockRouteDatabase;

    /*Ensures mock data is always available and prevents null database */
    public GoogleMapsService() {
        this.mockRouteDatabase = new HashMap<>();
        try {
            initializeMockRoutes();
        } catch (Exception e) {
            throw new MapServiceException("Failed to initialize route database", e);
        }
    }

    /*Validates all mock routes before adding to database */
    private void initializeMockRoutes() {
        try {
            // Kingston routes
            addMockRoute("Kingston", "Morant Point", 
                       Arrays.asList("Kingston", "Port Antonio", "Morant Point"), 87, 105);
            addMockRoute("Kingston", "Montego Bay",
                       Arrays.asList("Kingston", "Spanish Town", "Montego Bay"), 120, 150);
            // Add more routes...
        } catch (IllegalArgumentException e) {
            throw new MapServiceException("Invalid mock route configuration", e);
        }
    }

    /* Prevents invalid routes from being added to the database */
    private void addMockRoute(String origin, String destination, 
                            List<String> path, int distance, int time) {
        if (origin == null || destination == null) {
            throw new IllegalArgumentException("Origin and destination cannot be null");
        }
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (distance <= 0 || time <= 0) {
            throw new IllegalArgumentException("Distance and time must be positive");
        }
        if (!path.get(0).equals(origin) || !path.get(path.size()-1).equals(destination)) {
            throw new IllegalArgumentException("Path must start and end with specified towns");
        }

        mockRouteDatabase.computeIfAbsent(origin, k -> new HashMap<>())
                       .put(destination, new RouteResult(path, distance, time));
    }

    /* Validates all user-provided parameters */
    public RouteResult getRoute(String origin, String destination, RouteCriteria criteria) {
        if (origin == null || destination == null || criteria == null) {
            throw new IllegalArgumentException("Origin, destination and criteria cannot be null");
        }
        if (origin.trim().isEmpty() || destination.trim().isEmpty()) {
            throw new IllegalArgumentException("Town names cannot be empty");
        }

        /* Prevents NPE during map lookups */
        RouteResult result;
        try {
            result = mockRouteDatabase.getOrDefault(origin, Collections.emptyMap())
                                   .get(destination);
        } catch (NullPointerException e) {
            throw new MapServiceException("Route database corruption detected", e);
        }

        /*Provides graceful degradation for unknown routes */
        if (result == null) {
            try {
                result = generateFallbackRoute(origin, destination, criteria);
            } catch (Exception e) {
                throw new MapServiceException("Failed to generate fallback route", e);
            }
        } else {
            /*Safely applies user preferences to the route */
            try {
                result = applyCriteriaEffects(result, criteria);
            } catch (Exception e) {
                throw new MapServiceException("Failed to apply route criteria", e);
            }
        }

        return result;
    }

    /*Validates criteria effects before application */
    private RouteResult applyCriteriaEffects(RouteResult original, RouteCriteria criteria) {
        if (original == null || criteria == null) {
            throw new IllegalArgumentException("Original result and criteria cannot be null");
        }

        int timeAdjustment = 0;
        try {
            if (criteria.isAvoidTolls()) timeAdjustment += 15;
            if (criteria.isAvoidHighways()) timeAdjustment += 20;
            if (criteria.isAvoidHilly()) timeAdjustment += 10;
        } catch (Exception e) {
            throw new MapServiceException("Invalid criteria configuration", e);
        }

        return new RouteResult(
            original.path,
            original.totalDistance,
            Math.max(0, original.totalTime + timeAdjustment) // Prevent negative time
        );
    }

    private RouteResult generateFallbackRoute(String origin, String destination, 
                                           RouteCriteria criteria) {
        /* ===== Ensures fallback values are always valid */
        try {
            int distance = calculateFallbackDistance(origin, destination);
            int time = calculateFallbackTime(origin, destination, criteria);
            
            if (distance <= 0 || time <= 0) {
                throw new MapServiceException("Invalid fallback calculation");
            }
            
            return new RouteResult(
                Arrays.asList(origin, destination),
                distance,
                time
            );
        } catch (Exception e) {
            // Ultimate fallback if all else fails
            return new RouteResult(
                Arrays.asList(origin, destination),
                100,
                120
            );
        }
    }

    private int calculateFallbackDistance(String origin, String destination) {
        return Math.max(10, 50 + (origin.length() + destination.length()) * 3);
    }

    private int calculateFallbackTime(String origin, String destination, RouteCriteria criteria) {
        int baseTime = calculateFallbackDistance(origin, destination) * 2;
        try {
            if (criteria.isAvoidTolls()) baseTime += 15;
            if (criteria.isAvoidHighways()) baseTime += 20;
            if (criteria.isAvoidHilly()) baseTime += 10;
        } catch (Exception e) {
            // If criteria evaluation fails, use base time
        }
        return Math.max(15, baseTime);
    }

    /* ========== CUSTOM EXCEPTION CLASS ========== */
    public static class MapServiceException extends RuntimeException {
        public MapServiceException(String message) {
            super(message);
        }
        public MapServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
