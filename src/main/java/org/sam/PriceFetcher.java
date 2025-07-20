package org.sam;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PriceFetcher {
    private static final String API_URL = "https://prices.runescape.wiki/api/v1/osrs/latest";
    private static final int INFERNAL_SHALE_ID = 30848;
    
    private int cachedPrice = 0;
    private long lastFetchTime = 0;
    private static final long CACHE_DURATION = 180000; // 3 minutes in milliseconds
    
    public int getInfernalShalePrice() {
        long currentTime = System.currentTimeMillis();
        
        // Return cached price if it's still valid
        if (cachedPrice > 0 && (currentTime - lastFetchTime) < CACHE_DURATION) {
            return cachedPrice;
        }
        
        try {
            // Fetch new price
            int newPrice = fetchPriceFromAPI();
            if (newPrice > 0) {
                cachedPrice = newPrice;
                lastFetchTime = currentTime;
                System.out.println("Updated Infernal Shale price: " + newPrice + " gp");
            }
        } catch (Exception e) {
            System.out.println("Failed to fetch price: " + e.getMessage());
            // If fetch fails and we have no cached price, use fallback
            if (cachedPrice == 0) {
                cachedPrice = 100; // Fallback price
                System.out.println("Using fallback price: " + cachedPrice + " gp");
            }
        }
        
        return cachedPrice;
    }
    
    private int fetchPriceFromAPI() throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "User");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("HTTP " + responseCode);
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();
        
        // Parse JSON response to find price for item ID 30848
        return parsePrice(response.toString());
    }
    
    private int parsePrice(String jsonResponse) {
        // Look for the pattern: "30848":{"high":XXX,"highTime":...,"low":YYY,"lowTime":...}
        Pattern pattern = Pattern.compile("\"" + INFERNAL_SHALE_ID + "\":\\{[^}]*\"low\":(\\d+)");
        Matcher matcher = pattern.matcher(jsonResponse);
        
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        
        // Fallback: try to find low price if high price not available
        pattern = Pattern.compile("\"" + INFERNAL_SHALE_ID + "\":\\{[^}]*\"low\":(\\d+)");
        matcher = pattern.matcher(jsonResponse);
        
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        
        throw new RuntimeException("Price not found in API response");
    }
    
    public String formatPrice(int price) {
        if (price >= 1000000) {
            return String.format("%.1fM", price / 1000000.0);
        } else if (price >= 1000) {
            return String.format("%.1fK", price / 1000.0);
        } else {
            return String.valueOf(price);
        }
    }
}