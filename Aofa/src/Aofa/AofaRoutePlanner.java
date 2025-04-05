package Aofa;

import java.util.*;


public class AofaRoutePlanner {
    private Map<String, Town> towns = new HashMap<>();
    private DijkstraPathFinder pathFinder = new DijkstraPathFinder();
    private GoogleMapsService mapsService = new GoogleMapsService();

    public static void main(String[] args) {
        AofaRoutePlanner planner = new AofaRoutePlanner();
        planner.initializeTowns();
        planner.start();
        String port = System.getenv("PORT");
        if (port == null) {
            port = "8080";
        }
        System.setProperty("server.port", port);
        
    }

    // Add this method to create and store towns
    private Town addTown(String name) {
        Town town = new Town(name);
        towns.put(name, town);
        return town;
    }

    // Add this method to create bidirectional routes
    private void addRoute(Town source, Town destination, int distanceKm, int timeMinutes, Road.RoadType type) {
        source.addConnection(new Road(destination, distanceKm, timeMinutes, type));
        destination.addConnection(new Road(source, distanceKm, timeMinutes, type));
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("AofA Route Planner\nAvailable Towns:");
        towns.keySet().forEach(t -> System.out.print(t + ", "));
        
        System.out.print("\n\nEnter start town: ");
        String start = scanner.nextLine();
        
        System.out.print("Enter destination: ");
        String end = scanner.nextLine();

        if (!towns.containsKey(start) || !towns.containsKey(end)) {
            System.err.println("Invalid town names!");
            return;
        }

     // In your start() method:
        System.out.println("\nRoute Preferences:");
        boolean avoidTolls = getYesNo(scanner, "Avoid tolls? (y/n): ");
        boolean avoidHighways = getYesNo(scanner, "Avoid highways? (y/n): ");
        boolean avoidHilly = getYesNo(scanner, "Avoid hilly roads? (y/n): ");
        boolean preferTime = getYesNo(scanner, "Optimize for time instead of distance? (y/n): ");

        RouteCriteria criteria = new RouteCriteria(avoidTolls, avoidHighways, avoidHilly, preferTime);
        
        RouteResult ourDistance = pathFinder.findShortestPath(towns.get(start), towns.get(end), criteria);
        RouteResult googleRoute = mapsService.getRoute(start, end, criteria);

        System.out.println("\n=== Algorithm ===");
        printRoute(ourDistance);
        
        System.out.println("\n=== Google Maps ===");
        printRoute(googleRoute);
        
        scanner.close();
    }

    private boolean getYesNo(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().toLowerCase();
            if (input.equals("y")) return true;
            if (input.equals("n")) return false;
            System.out.println("Please enter 'y' or 'n'");
        }
    }

    private void printRoute(RouteResult result) {
        if (result == null) {
            System.out.println("No route found!");
            return;
        }
        System.out.println("Route: " + String.join(" -> ", result.path));
        System.out.println("Distance: " + result.totalDistance + " km");
        System.out.println("Time: " + result.totalTime + " minutes");
    }

    private void initializeTowns() {
        // Create all towns
        Town kingston = addTown("Kingston");
        Town morantPoint = addTown("Morant Point");
        Town portAntonio = addTown("Port Antonio");
        Town annotoBay = addTown("Annoto Bay");
        Town spanishTown = addTown("Spanish Town");
        Town portRoyal = addTown("Port Royal");
        Town portMaria = addTown("Port Maria");
        Town ochoRios = addTown("Ocho Rios");
        Town stAnnsBay = addTown("St. Ann's Bay");
        Town falmouth = addTown("Falmouth");
        Town montegoBay = addTown("Montego Bay");
        Town lucca = addTown("Lucea");
        Town negril = addTown("Negril");
        Town alley = addTown("Alley");
        Town santaCruz = addTown("Santa Cruz");
        Town blackRiver = addTown("Black River");
        Town savannaLaMar = addTown("Savanna La Mar");
        Town ewarton = addTown("Ewarton");
        Town mayPen = addTown("May Pen");
        Town mandeville = addTown("Mandeville");
        Town christiana = addTown("Christiana");

        // Add all routes with distances and times (bidirectional)
        addRoute(kingston, morantPoint, 87, 105, Road.RoadType.STANDARD);
        addRoute(kingston, portAntonio, 92, 124, Road.RoadType.HIGHWAY);
        addRoute(kingston, annotoBay, 47, 73, Road.RoadType.HIGHWAY);
        addRoute(kingston, spanishTown, 20, 28, Road.RoadType.HIGHWAY);
        addRoute(kingston, portRoyal, 26, 32, Road.RoadType.INNER_CITY);
        addRoute(morantPoint, portAntonio, 68, 83, Road.RoadType.HILLY);
        addRoute(portAntonio, annotoBay, 46, 52, Road.RoadType.HIGHWAY);
        addRoute(annotoBay, portMaria, 25, 27, Road.RoadType.HILLY);
        addRoute(portMaria, ochoRios, 31, 32, Road.RoadType.STANDARD);
        addRoute(ochoRios, stAnnsBay, 12, 14, Road.RoadType.INNER_CITY);
        addRoute(stAnnsBay, falmouth, 57, 57, Road.RoadType.HIGHWAY);
        addRoute(falmouth, montegoBay, 35, 36, Road.RoadType.HIGHWAY);
        addRoute(montegoBay, lucca, 37, 37, Road.RoadType.TOLL);
        addRoute(lucca, negril, 40, 37, Road.RoadType.STANDARD);
        addRoute(spanishTown, alley, 55, 68, Road.RoadType.HIGHWAY);
        addRoute(alley, santaCruz, 93, 106, Road.RoadType.HILLY);
        addRoute(santaCruz, blackRiver, 29, 28, Road.RoadType.STANDARD);
        addRoute(blackRiver, savannaLaMar, 47, 47, Road.RoadType.STANDARD);
        addRoute(savannaLaMar, negril, 28, 29, Road.RoadType.STANDARD);
        addRoute(spanishTown, ewarton, 31, 43, Road.RoadType.STANDARD);
        addRoute(ewarton, ochoRios, 47, 48, Road.RoadType.HILLY);
        addRoute(ewarton, christiana, 78, 117, Road.RoadType.HILLY);
        addRoute(spanishTown, mayPen, 35, 37, Road.RoadType.HIGHWAY);
        addRoute(mayPen, alley, 25, 45, Road.RoadType.STANDARD);
        addRoute(mayPen, mandeville, 39, 44, Road.RoadType.HIGHWAY);
        addRoute(mandeville, christiana, 21, 32, Road.RoadType.HILLY);
        addRoute(christiana, falmouth, 70, 93, Road.RoadType.HILLY);
        addRoute(christiana, santaCruz, 51, 54, Road.RoadType.HILLY);
        addRoute(savannaLaMar, montegoBay, 50, 59, Road.RoadType.STANDARD);
    }
}