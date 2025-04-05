package Aofa;

import java.util.ArrayList;
import java.util.List;

public class Town {
    private final String name;
    private final List<Road> connections;

    public Town(String name) {
        this.name = name;
        this.connections = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Road> getConnections() {
        return new ArrayList<>(connections); // Return defensive copy
    }

    public void addConnection(Road road) {
        connections.add(road);
    }
}