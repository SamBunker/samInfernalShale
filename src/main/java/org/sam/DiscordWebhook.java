package org.sam;

import org.powbot.api.rt4.Players;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordWebhook {
    private final String webhookUrl;
    
    public DiscordWebhook(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
    
    public boolean sendStartMessage(String username, int infernalShalePrice) {
        if (webhookUrl == null || webhookUrl.trim().isEmpty()) {
            return false; // No webhook configured
        }
        
        try {
            String message = String.format(
                "🚀 **Script Started** 🚀\n" +
                "**Player:** %s\n" +
                "**Current Infernal Shale Price:** %s gp\n" +
                "**Script:** Sam Infernal Shale Mining\n" +
                "**Time:** <t:%d:f>",
                username,
                formatPrice(infernalShalePrice),
                System.currentTimeMillis() / 1000
            );
            
            return sendWebhook(message);
        } catch (Exception e) {
            System.out.println("Failed to send Discord start message: " + e.getMessage());
            return false;
        }
    }
    
    public boolean sendStopMessage(int averageGpPerHour, int totalProfit, int shaleCollected, long totalTimeMs, String powbotUsername) {
        if (webhookUrl == null || webhookUrl.trim().isEmpty()) {
            return false; // No webhook configured
        }
        
        try {
            long totalTimeMinutes = totalTimeMs / (1000 * 60);
            long hours = totalTimeMinutes / 60;
            long minutes = totalTimeMinutes % 60;
            
            String timeStr = hours > 0 ? 
                String.format("%dh %dm", hours, minutes) : 
                String.format("%dm", minutes);
            
            String message = String.format(
                "🛑 **Script Stopped** 🛑\n" +
                "**Player:** %s\n" +
                "**Average GP/hr:** %s gp\n" +
                "**Total Profit:** %s gp\n" +
                "**Shale Collected:** %,d\n" +
                "**Total Runtime:** %s\n" +
                "**Script:** Sam Infernal Shale Mining\n" +
                "**Time:** <t:%d:f>",
                powbotUsername,
                formatPrice(averageGpPerHour),
                formatPrice(totalProfit),
                shaleCollected,
                timeStr,
                System.currentTimeMillis() / 1000
            );
            
            return sendWebhook(message);
        } catch (Exception e) {
            System.out.println("Failed to send Discord stop message: " + e.getMessage());
            return false;
        }
    }
    
    private boolean sendWebhook(String content) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Sam-Infernal-Shale-Bot");
            connection.setDoOutput(true);
            
            String jsonPayload = String.format("{\"content\":\"%s\"}", 
                content.replace("\"", "\\\"").replace("\n", "\\n"));
            
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                System.out.println("Discord webhook sent successfully");
                return true;
            } else {
                System.out.println("Discord webhook failed with response code: " + responseCode);
                return false;
            }
            
        } catch (IOException e) {
            System.out.println("Discord webhook error: " + e.getMessage());
            return false;
        }
    }
    
    private String formatPrice(int price) {
        if (price >= 1_000_000) {
            return String.format("%.1fM", price / 1_000_000.0);
        } else if (price >= 1_000) {
            return String.format("%.1fK", price / 1_000.0);
        } else {
            return String.format("%,d", price);
        }
    }
}