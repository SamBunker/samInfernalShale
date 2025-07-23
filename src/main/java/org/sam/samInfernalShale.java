package org.sam;
import com.google.common.eventbus.Subscribe;
import org.powbot.api.Color;
import org.powbot.api.event.InventoryChangeEvent;
import org.powbot.api.event.MessageEvent;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.walking.model.Skill;
import org.powbot.api.script.*;
import org.powbot.api.script.paint.PaintBuilder;
import org.powbot.mobile.script.ScriptManager;
import org.powbot.mobile.service.ScriptUploader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ScriptConfiguration.List({
        @ScriptConfiguration(
                name = "Mining Method",
                description = "How would you like to mine shale?",
                optionType = OptionType.STRING,
                allowedValues = {"3T Mining", "Mining", "AFK Mining"}
        ),
        @ScriptConfiguration(
                name = "SelectedRocks",
                description = "Select the rocks you'd like to interact with",
                optionType = OptionType.GAMEOBJECT_ACTIONS
        ),
        @ScriptConfiguration(
                name = "Hammer Type",
                description = "Select your preferred hammer type",
                optionType = OptionType.STRING,
                allowedValues = {"Regular Hammer", "Imcando Hammer (off-hand)"}
        )
})

@ScriptManifest(
        name = "Sam Infernal Shale",
        description = "3T, Tick Manipulation, Regular Mining, AFK Mining",
        author = "Sam",
        version = "1.2.2",
        category = ScriptCategory.Mining
)
public class samInfernalShale extends AbstractScript {
    public static void main(String[] args) {
        new ScriptUploader().uploadAndStart("Sam Infernal Shale", "", "R52T90A6VCM", true, false);
    }

    public MiningConfig config;
    public GemBagManager gemBagManager = new GemBagManager();
    //Constants constants = new Constants();
    public Variables vars = new Variables();
    TaskManager taskManager;
    PriceFetcher priceFetcher = new PriceFetcher();

    @ValueChanged(keyName = "Mining Method")
    public void methodChanged(String method) {
        switch (method) {
            case "3T Mining":
            case "Mining":
                updateVisibility("SelectedRocks", true);
                break;
            case "AFK Mining":
                updateVisibility("SelectedRocks", false);
                break;
        }
    }


    @Subscribe
    public void onInventoryChange(InventoryChangeEvent event) {
        // Simple approach: only count shale INCREASES (new shale gained)
        if (event.getItemId() == Constants.INFERNAL_SHALE && event.getQuantityChange() > 0) {
            vars.rocksMined += event.getQuantityChange();
            vars.consecutiveFailures = 0; // Reset failure counter on success
            trackSuccessfulMining(); // Track successful mining for timing adjustment
            System.out.println("Rock mined! Total: " + vars.rocksMined + ", Shale gained: " + event.getQuantityChange());
        }
        
        // Track crushed infernal shale changes
        if (event.getItemId() == Constants.CRUSHED_INFERNAL_SHALE) {
            if (event.getQuantityChange() > 0) {
                vars.crushedShaleObtained += event.getQuantityChange();
                
                // Track profit data for averaging
                long currentTime = System.currentTimeMillis();
                int profitGained = event.getQuantityChange() * priceFetcher.getInfernalShalePrice();
                
                vars.recentProfitTimestamps.add(currentTime);
                vars.recentProfitAmounts.add(profitGained);
                
                // Keep only last 20 profit events for rolling average (about 10-15 minutes of data)
                if (vars.recentProfitTimestamps.size() > 20) {
                    vars.recentProfitTimestamps.remove(0);
                    vars.recentProfitAmounts.remove(0);
                }
                
                System.out.println("Crushed shale obtained! Total: " + vars.crushedShaleObtained + ", Amount gained: " + event.getQuantityChange());
            } else if (event.getQuantityChange() < 0) {
                // Track when crushed shale is dropped/deposited (negative change)
                vars.crushedShaleObtained += Math.abs(event.getQuantityChange());
                System.out.println("Crushed shale dropped/deposited! Total tracked: " + vars.crushedShaleObtained + ", Amount lost: " + Math.abs(event.getQuantityChange()));
            }
        }
    }

    @Subscribe
    public void onMessageEvent(MessageEvent messageEvent) {
        if (!messageEvent.getSender().isEmpty()) {
            return;
        }
        String msg = messageEvent.getMessage().toLowerCase();
        System.out.println("msg: " + msg);

        // Detect timing failure message - only count as timing failure, not interaction failure
        if (msg.contains("you can't swing a pickaxe whilst wiping") || msg.contains("can't swing") || msg.contains("whilst wiping")) {
            vars.consecutiveFailures++;
            vars.recentTimingFailures++; // Track recent failures separately
            vars.totalMissedRocks++;
            
            // Subtract 1 from mining attempts since this wasn't a real mining attempt
            if (vars.miningAttempts > 0) {
                vars.miningAttempts--;
            }
            
            // Immediate timing adjustment after single failure
            if (vars.recentTimingFailures == 1) {
                System.out.println("Single timing failure detected - adjusting timing");
                adjustTimingAfterFailure();
            }
            
            System.out.println("Mining timing failure detected! Consecutive: " + vars.consecutiveFailures + ", Recent: " + vars.recentTimingFailures + ", Total missed: " + vars.totalMissedRocks);
        }

        Pattern pattern = Pattern.compile("(sapphires|emeralds|rubies|diamonds|dragonstones):\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(msg.toLowerCase());
        while (matcher.find()) {
            String gemType = matcher.group(1).toLowerCase();
            int count = Integer.parseInt(matcher.group(2));
            String formattedGem = "Uncut " + (gemType.endsWith("s") ? gemType.substring(0, gemType.length() - 1) : gemType);
            gemBagManager.updateGemCount(formattedGem, count);
            System.out.println("Updated " + formattedGem + " to " + count);
        }
    }

    @Override
    public void onStart() {
        config = new MiningConfig(
                getOption("SelectedRocks"),
                getOption("Mining Method"),
                getOption("Hammer Type")
        );

        taskManager = new TaskManager(this, config);
        
        // Initialize script start time for profit per hour calculation
        vars.scriptStartTime = System.currentTimeMillis();
        
        // Reset 3T mining initialization state on script start
        resetThreeTMiningState();
        
        // Initialize price fetcher with initial price lookup
        System.out.println("Fetching Infernal Shale price from RuneScape Wiki...");
        //priceFetcher.getInfernalShalePrice();
        
        // Set initial crushed shale count
        vars.initialCrushedShaleCount = Inventory.stream().id(Constants.CRUSHED_INFERNAL_SHALE).first().getStack();
        System.out.println("Initial crushed shale count: " + vars.initialCrushedShaleCount);
        int price = priceFetcher.getInfernalShalePrice();

        PaintBuilder paintBuilder = PaintBuilder.newBuilder()
                .minHeight(180)
                .minWidth(450)
                .backgroundColor(Color.argb(175, 0, 0, 0))
                .withTextSize(14F)
                .addString(() -> "Task: " + taskManager.getCurrentTaskName());
        
        // Only show Rock Success stats if not using AFK Mining
        if (!config.getMiningMethod().equals("AFK Mining")) {
            // Enhanced tracking for issue #5: mined / interaction-failed / wiped-missed rocks
            paintBuilder.addString(() -> {
                // Calculate successful interactions that failed to mine ore
                int successfulInteractionsFailed = Math.max(0, vars.miningAttempts - vars.rocksMined - vars.totalMissedRocks);
                int totalAttempts = vars.rocksMined + successfulInteractionsFailed + vars.totalMissedRocks;
                if (totalAttempts > 0) {
                    double successRate = (double) vars.rocksMined / totalAttempts * 100;
                    return "Rock Success: " + vars.rocksMined + "/" + successfulInteractionsFailed + "/" + vars.totalMissedRocks + " (" + String.format("%.1f", successRate) + "%)";
                }
                return "Rock Success: 0/0/0 (0.0%)";
            });
        }
        
        // Only show Timing Failures if not using AFK Mining
//        if (!config.getMiningMethod().equals("AFK Mining")) {
//            paintBuilder.addString(() -> "Timing Failures: " + vars.consecutiveFailures);
//        }
        
        
        paintBuilder.addString(() -> {
            int totalProfit = vars.crushedShaleObtained * price;
            
            // Calculate averaged profit per hour from recent profit events
            if (vars.recentProfitTimestamps.size() >= 2) {
                long oldestTime = vars.recentProfitTimestamps.get(0);
                long newestTime = vars.recentProfitTimestamps.get(vars.recentProfitTimestamps.size() - 1);
                long timeSpanMs = newestTime - oldestTime;
                
                if (timeSpanMs > 0) {
                    int totalRecentProfit = vars.recentProfitAmounts.stream().mapToInt(Integer::intValue).sum();
                    double timeSpanHours = timeSpanMs / (1000.0 * 60.0 * 60.0);
                    int averageProfitPerHour = (int) Math.round(totalRecentProfit / timeSpanHours);
                    
                    return "GE Profit: " + priceFetcher.formatPrice(totalProfit) + " gp (" + priceFetcher.formatPrice(averageProfitPerHour) + "/hr avg)";
                }
            }
            
            // Fallback to simple calculation if not enough data
            long runtimeMs = System.currentTimeMillis() - vars.scriptStartTime;
            double runtimeHours = runtimeMs / (1000.0 * 60.0 * 60.0);
            
            if (runtimeHours > 0.05) { // At least 3 minutes of runtime
                int profitPerHour = (int) Math.round(totalProfit / runtimeHours);
                return "GE Profit: " + priceFetcher.formatPrice(totalProfit) + " gp (" + priceFetcher.formatPrice(profitPerHour) + "/hr)";
            }
            
            return "GE Profit: " + priceFetcher.formatPrice(totalProfit) + " gp (calculating/hr)";
        })
                .trackSkill(Skill.Mining);
        
        addPaint(paintBuilder.build());
        vars.currentTask = "Idle";
    }

    @Override
    public void poll() {
        taskManager.executeTask();
        
        if (config.getMiningMethod().equals("3T Mining") || config.getMiningMethod().equals("Mining")) {
            if (config.getSelectedRocks() == null) {
                ScriptManager.INSTANCE.stop();
                return;
            }
        }
        if (!Functions.hasItem("pickaxe")) {
            ScriptManager.INSTANCE.stop();
            return;
        }
        if (ScriptManager.INSTANCE.isStopping()) {
            return;
        }
    }

    private void adjustTimingAfterFailure() {
        // Increase timing slightly for next attempt
        vars.averageClothWipeTime += 15; // Add 15ms to base timing
        
        // Ensure we don't exceed maximum
        vars.averageClothWipeTime = Math.min(vars.averageClothWipeTime, Constants.WET_CLOTH_MAX_WAIT);
    }

    public void trackSuccessfulMining() {
        vars.recentTimingFailures = Math.max(0, vars.recentTimingFailures - 1); // Gradually reduce failure count
        vars.lastSuccessfulClothDelay = vars.averageClothWipeTime; // Remember successful timing
    }

    private void resetThreeTMiningState() {
        // Reset all 3T mining initialization state on script start/restart
        vars.threeTMiningInitialized = false;
        vars.initialPositioningComplete = false;
        vars.runStateDisabledForMining = false;
        System.out.println("3T mining initialization state reset");
    }
}