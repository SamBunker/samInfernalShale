package org.sam;
import com.google.common.eventbus.Subscribe;
import org.powbot.api.Color;
import org.powbot.api.event.InventoryChangeEvent;
import org.powbot.api.event.MessageEvent;
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
        )
})

@ScriptManifest(
        name = "Sam Infernal Shale",
        description = "3T, Tick Manipulation, Regular Mining, AFK Mining",
        author = "Sam",
        version = "1.2",
        category = ScriptCategory.Mining
)
public class samInfernalShale extends AbstractScript {
    public static void main(String[] args) {
        new ScriptUploader().uploadAndStart("Sam Infernal Shale", "", "R52T90A6VCM", true, false);
    }

    public MiningConfig config;
    public GemBagManager gemBagManager = new GemBagManager();
    Constants constants = new Constants();
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
            System.out.println("Rock mined! Total: " + vars.rocksMined + ", Shale gained: " + event.getQuantityChange());
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
            vars.totalMissedRocks++;
            // Subtract 1 from mining attempts since this wasn't a real mining attempt
            if (vars.miningAttempts > 0) {
                vars.miningAttempts--;
            }
            System.out.println("Mining timing failure detected! Consecutive: " + vars.consecutiveFailures + ", Total missed: " + vars.totalMissedRocks);
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
                getOption("Mining Method")
        );

        taskManager = new TaskManager(this, config);
        
        // Initialize price fetcher with initial price lookup
        System.out.println("Fetching Infernal Shale price from RuneScape Wiki...");
        priceFetcher.getInfernalShalePrice();

        addPaint(
                PaintBuilder.newBuilder()
                        .minHeight(190)
                        .minWidth(450)
                        .backgroundColor(Color.argb(175, 0, 0, 0))
                        .withTextSize(14F)
                        .addString(() -> "Task: " + taskManager.getCurrentTaskName())
                        .addString(() -> {
                            if (vars.miningAttempts > 0) {
                                double successRate = (double) vars.rocksMined / vars.miningAttempts * 100;
                                return "Mining: " + vars.rocksMined + "/" + vars.miningAttempts + " (" + String.format("%.1f", successRate) + "%)";
                            }
                            return "Mining: " + vars.rocksMined + "/0 (0.0%)";
                        })
                        .addString(() -> "Timing Failures: " + vars.consecutiveFailures)
                        .addString(() -> {
                            int price = priceFetcher.getInfernalShalePrice();
                            int totalProfit = vars.rocksMined * price;
                            return "GE Profit: " + priceFetcher.formatPrice(totalProfit) + " gp (" + price + " ea)";
                        })
                        .addString(() -> {
                            int price = priceFetcher.getInfernalShalePrice();
                            int failedAttempts = vars.miningAttempts - vars.rocksMined;
                            int totalFailures = vars.totalMissedRocks + failedAttempts;
                            int totalLoss = totalFailures * price;
                            return "Total Loss: " + priceFetcher.formatPrice(totalLoss) + " gp (" + totalFailures + " failed)";
                        })
                        .trackSkill(Skill.Mining)
                        .build()
        );
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
}