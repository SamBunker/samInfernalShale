package org.sam;
import com.google.common.eventbus.Subscribe;
import org.powbot.api.Color;
import org.powbot.api.event.MessageEvent;
import org.powbot.api.event.SkillExpGainedEvent;
import org.powbot.api.rt4.Skills;
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
        version = "1.1",
        category = ScriptCategory.Mining
)
public class samInfernalShale extends AbstractScript {
    public static void main(String[] args) {
        new ScriptUploader().uploadAndStart("Sam Infernal Shale", "", "R52T90A6VCM", true, false);
    }

    public MiningConfig config;
    public GemBagManager gemBagManager = new GemBagManager();
    Constants constants = new Constants();
    Variables vars = new Variables();
    TaskManager taskManager;

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

    int lastMiningXp;
     @Subscribe
     public void onExperience(SkillExpGainedEvent event) {
         if (event.getSkill() == Skill.Mining) {
             int newXp = Skills.experience(Skill.Mining);
             if (newXp > lastMiningXp) {
                 vars.rocksMined++;
                 lastMiningXp = newXp;
             }
         }
     }

//    @Subscribe
//    public void onInventoryChange(InventoryChangeEvent event) {
//        if (!vars.awaitingShale) return;
//        if (event.getItemId() == Constants.INFERNAL_SHALE && event.getQuantityChange() > 0) {
//            vars.rocksMined += event.getQuantityChange();
//            vars.awaitingShale = false;
//        }
//    }

    @Subscribe
    public void onMessageEvent(MessageEvent messageEvent) {
        if (!messageEvent.getSender().isEmpty()) {
            return;
        }
        String msg = messageEvent.getMessage().toLowerCase();
        System.out.println("msg: " + msg);

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

        addPaint(
                PaintBuilder.newBuilder()
                        .minHeight(150)
                        .minWidth(450)
                        .backgroundColor(Color.argb(175, 0, 0, 0))
                        .withTextSize(14F)
                        .addString(() -> "Task: " + taskManager.getCurrentTaskName())
                        .addString(() -> "Rocks Mined: " + vars.rocksMined)
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