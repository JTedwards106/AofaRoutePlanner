package Aofa;

import java.util.List;

public class RouteResult {
    public List<String> path;
    public int totalDistance;
    public int totalTime;

    public RouteResult(List<String> path, int totalDistance, int totalTime) {
        this.path = path;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
    }
}