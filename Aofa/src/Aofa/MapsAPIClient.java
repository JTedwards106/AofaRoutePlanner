package Aofa;

import java.io.*;
import java.net.*;
import java.util.*;
import com.google.gson.*;

public class MapsAPIClient {
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/json";
    
    public RouteResult getRoute(String origin, String destination, RouteCriteria criteria) {
        System.out.println("\n=== DEBUG: Starting Google Maps Request ===");
        System.out.println("From: " + origin + " to " + destination);
        System.out.println("Avoid Tolls: " + criteria.isAvoidTolls());
        System.out.println("Avoid Highways: " + criteria.isAvoidHighways());
        
        try {
            // 1. Build URL with proper encoding
            String url = buildUrl(origin, destination, criteria);
            System.out.println("API URL: " + url);
            
            // 2. Make the API call
            String jsonResponse = makeHttpRequest(url);
            System.out.println("Raw Response:\n" + jsonResponse);
            
            // 3. Parse the actual response
            RouteResult result = parseResponse(jsonResponse);
            System.out.println("Parsed Result: " + result.totalDistance + " km, " + result.totalTime + " mins");
            return result;
            
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
            return new RouteResult(Arrays.asList("Error: Check API Key"), -1, -1);
        }
    }

    private String buildUrl(String origin, String destination, RouteCriteria criteria) 
        throws UnsupportedEncodingException {
        
        String fullOrigin = URLEncoder.encode(origin + ", Jamaica", "UTF-8");
        String fullDest = URLEncoder.encode(destination + ", Jamaica", "UTF-8");
        
        StringBuilder url = new StringBuilder(BASE_URL)
            .append("?origin=").append(fullOrigin)
            .append("&destination=").append(fullDest)
            .append("&key=").append(ConfigManager.getApiKey())
            .append("&departure_time=now")
            .append("&traffic_model=pessimistic")
            .append("&units=metric");
        
        if (criteria.isAvoidTolls()) url.append("&avoid=tolls");
        if (criteria.isAvoidHighways()) url.append("&avoid=highways");
        
        return url.toString();
    }

    private String makeHttpRequest(String urlString) throws IOException {
        @SuppressWarnings("deprecation")
		HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);  // 5-second timeout
        conn.setReadTimeout(10000);    // 10-second timeout
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("API request failed with status: " + conn.getResponseCode());
            }

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
            
        } catch (IOException e) {
            // Try to read error stream if available
            try (InputStream errorStream = conn.getErrorStream();
                 BufferedReader errorReader = new BufferedReader(
                     new InputStreamReader(errorStream))) {
                
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine);
                }
                throw new IOException("API Error: " + errorResponse.toString(), e);
                
            } catch (NullPointerException | IOException ex) {
                throw new IOException("Failed to read error stream: " + e.getMessage(), e);
            }
        } finally {
            conn.disconnect();
        }
    }

    private RouteResult parseResponse(String jsonResponse) {
        JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
        JsonArray routes = json.getAsJsonArray("routes");
        JsonObject route = routes.get(0).getAsJsonObject();
        JsonArray legs = route.getAsJsonArray("legs");
        JsonObject leg = legs.get(0).getAsJsonObject();
        
        // Extract path
        List<String> path = new ArrayList<>();
        path.add(leg.get("start_address").getAsString());
        JsonArray steps = leg.getAsJsonArray("steps");
        for (JsonElement step : steps) {
            path.add(step.getAsJsonObject().get("end_address").getAsString());
        }
        
        // Extract metrics
        int distance = leg.getAsJsonObject("distance").get("value").getAsInt() / 1000; // meters to km
        int duration = leg.getAsJsonObject("duration").get("value").getAsInt() / 60; // seconds to minutes
        
        return new RouteResult(path, distance, duration);
    }
    
    public boolean testApiConnection() {
        try {
            String testUrl = BASE_URL + "?origin=Kingston,Jamaica" +
                            "&destination=Ocho+Rios,Jamaica" +
                            "&key=" + ConfigManager.getApiKey();
            
            String response = makeHttpRequest(testUrl);
            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            
            if (json.get("status").getAsString().equals("OK")) {
                System.out.println("API is working! Status: OK");
                return true;
            } else {
                System.err.println("API Error: " + json.get("status").getAsString());
                if (json.has("error_message")) {
                    System.err.println("Message: " + json.get("error_message").getAsString());
                }
                return false;
            }
        } catch (Exception e) {
            System.err.println("API Test Failed: " + e.getMessage());
            return false;
        }
    }
}