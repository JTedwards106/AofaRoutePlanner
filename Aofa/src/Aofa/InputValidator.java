package Aofa;

import java.util.Map;
import java.util.Scanner;
import java.util.NoSuchElementException;

public class InputValidator {
    /* Prevents NullPointerException by validating all parameters */
    public static boolean validateTowns(String start, String end, Map<String, Town> towns) {
        if (start == null || end == null || towns == null) {
            throw new IllegalArgumentException("Start town, end town, and towns map cannot be null");
        }
        
        /* Ensures town names aren't blank */
        if (start.trim().isEmpty() || end.trim().isEmpty()) {
            throw new IllegalArgumentException("Town names cannot be empty");
        }
        
        /* Verifies both towns exist in the system */
        boolean isValid = towns.containsKey(start) && towns.containsKey(end);
        
        if (!isValid) {
            System.err.println("Error: One or both towns not found in our database");
        }
        return isValid;
    }

    /*  Handles scanner failures and invalid inputs */
    public static boolean getYesNoInput(Scanner scanner, String prompt) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner cannot be null");
        }
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new IllegalArgumentException("Prompt cannot be null or empty");
        }

        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim().toLowerCase();
                
                /*  Ensures only valid yes/no responses are accepted */
                if (input.equals("y") || input.equals("yes")) {
                    return true;
                }
                if (input.equals("n") || input.equals("no")) {
                    return false;
                }
                
                System.out.println("Invalid input. Please enter 'y'/'yes' or 'n'/'no'");
                
            } catch (NoSuchElementException e) {
                /* Handles cases where input stream is closed */
                System.err.println("Input error: " + e.getMessage());
                throw new IllegalStateException("Scanner input stream failed", e);
            } catch (IllegalStateException e) {
                /* Handles cases where scanner was closed prematurely */
                System.err.println("Scanner is closed. Please restart the application");
                throw e;
            }
        }
    }
    
    /*  Additional utility method for number inputs */
    public static int getNumberInput(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.printf("Please enter a number between %d and %d%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number format. Please try again.");
            } catch (NoSuchElementException | IllegalStateException e) {
                System.err.println("Input error: " + e.getMessage());
                throw new IllegalStateException("Failed to read input", e);
            }
        }
    }
}
