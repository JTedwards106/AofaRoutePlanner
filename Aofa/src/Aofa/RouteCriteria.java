package Aofa;

public class RouteCriteria {
    private final boolean avoidTolls;
    private final boolean avoidHighways;
    private final boolean avoidHilly;
    private final boolean preferTime; // New field

    public RouteCriteria(boolean avoidTolls, boolean avoidHighways, 
                       boolean avoidHilly, boolean preferTime) {
        this.avoidTolls = avoidTolls;
        this.avoidHighways = avoidHighways;
        this.avoidHilly = avoidHilly;
        this.preferTime = preferTime;
    }

    // Getters
    public boolean isAvoidTolls() {
        return avoidTolls;
    }

    public boolean isAvoidHighways() {
        return avoidHighways;
    }

    public boolean isAvoidHilly() {
        return avoidHilly;
    }

    public boolean isPreferTime() {
        return preferTime;
    }

    public boolean shouldAvoid(Road.RoadType type) {
        return (avoidTolls && type == Road.RoadType.TOLL) ||
               (avoidHighways && type == Road.RoadType.HIGHWAY) ||
               (avoidHilly && type == Road.RoadType.HILLY);
    }
}