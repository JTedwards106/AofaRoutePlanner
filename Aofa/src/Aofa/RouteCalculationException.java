package Aofa;

@SuppressWarnings("serial")
public class RouteCalculationException extends RuntimeException {
    // Constructor with message
    public RouteCalculationException(String message) {
        super(message);
    }

    // Constructor with message and cause
    public RouteCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}