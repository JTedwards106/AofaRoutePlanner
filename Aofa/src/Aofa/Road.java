package Aofa;

public class Road {
    public enum RoadType {
        HIGHWAY(0.9),  // 10% faster
        TOLL(1.0),
        HILLY(1.3),    // 30% slower 
        INNER_CITY(1.2), // 20% slower
        STANDARD(1.0);

        private final double timeFactor;

        RoadType(double factor) {
            this.timeFactor = factor;
        }

        public int getAdjustedTime(int baseTime) {
            return (int) Math.round(baseTime * timeFactor);
        }
    }

    private final Town destination;
    private final int distance;
    private final int baseTime;
    private final RoadType type;

    public Road(Town destination, int distance, int baseTime, RoadType type) {
        this.destination = destination;
        this.distance = distance;
        this.baseTime = baseTime;
        this.type = type;
    }

    public int getAdjustedTime() {
        return type.getAdjustedTime(baseTime);
    }

    // Getters
    public Town getDestination() { return destination; }
    public int getDistance() { return distance; }
    public RoadType getType() { return type; }
}