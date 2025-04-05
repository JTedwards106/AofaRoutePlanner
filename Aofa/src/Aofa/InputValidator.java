package Aofa;


import java.util.Map;
import java.util.Scanner;

public class InputValidator {
    public static boolean validateTowns(String start, String end, Map<String, Town> towns) {
        return towns.containsKey(start) && towns.containsKey(end);
    }
    
    public static boolean getYesNoInput(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) return true;
            if (input.equals("n")) return false;
            System.out.println("Please enter 'y' or 'n'");
        }
    }
}