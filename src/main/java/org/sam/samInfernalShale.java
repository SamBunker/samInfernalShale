package org.sam;
import com.google.common.eventbus.Subscribe;
import org.powbot.api.event.GameObjectActionEvent;
import org.powbot.api.event.MessageEvent;
import org.powbot.api.event.SkillExpGainedEvent;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.walking.model.Skill;
import org.powbot.api.script.*;
import org.powbot.api.script.paint.PaintBuilder;
import org.powbot.mobile.script.ScriptManager;
import org.powbot.mobile.service.ScriptUploader;
import org.sam.Tasks.*;
import org.sam.Variables;
import org.sam.Functions;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

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

    //private final ArrayList<Task> taskList = new ArrayList<Task>(); //moved to constants.java
    public GemBagManager gemBagManager = new GemBagManager();
    //private String currentTask = "Idle"; //moved to variables.java
    //public static int rocksMined = 0; //moved to variables.java
    //private int lastMiningXp = 0; //removed
    //private List<GameObjectActionEvent> selectedRocks = getOption("SelectedRocks"); //moved to variables.java
    //private String miningMethod = getOption("Mining Method"); //moved to variables.java

    // public boolean hasItem(String name) {
    //     return Inventory.stream().name(name).isNotEmpty() ||
    //             Equipment.stream().name(name).isNotEmpty();
    // } // Moved to Functions.java

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

    // @Subscribe
    // public void onExperience(SkillExpGainedEvent event) {
    //     if (event.getSkill() == Skill.Mining) {
    //         int newXp = Skills.experience(Skill.Mining);
    //         if (newXp > lastMiningXp) {
    //             samInfernalShale.rocksMined++;
    //             lastMiningXp = newXp;
    //         }
    //     }
    // }

    @Subscribe
    public void onInventoryChange(InventoryChangeEvent event) {
        for (Item added : event.getAddedItems()) {
            if (added.id() == Constants.INFERNAL_SHALE) {
                samInfernalShale.rocksMined++;
            }
        }

    }

    @Subscribe
    public void onMessageEvent(MessageEvent messageEvent) {
        if (messageEvent.getSender() != null && !messageEvent.getSender().isEmpty()) {
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
        //Item gemBag = Inventory.stream().id(Constants.GEM_BAG_ID).first(); //Moved to Functions.java

        switch (Variables.miningMethod) {
            case "3T Mining":
                Constants.TASK_LIST.add(new ThreeTick(this, Variables.selectedRocks));
                break;
            case "Mining":
                Constants.TASK_LIST.add(new Mining(this, Variables.selectedRocks));
                Constants.TASK_LIST.add(new Crush(this));
                break;
            case "AFK Mining":
                Constants.TASK_LIST.add(new AfkMine(this));
                Constants.TASK_LIST.add(new Crush(this));
                break;
            default:
                Constants.TASK_LIST.add(new AfkMine(this));
                Constants.TASK_LIST.add(new Crush(this));
                System.out.println("Unknown mining mode: " + Variables.miningMethod);
                break;
        }

        if (Functions.getGemBag() != null) {
            gemBag.interact("Check");
        }

        Constants.TASK_LIST.add(new HandleGems(this, GemBag));

        addPaint(
                PaintBuilder.newBuilder()
                        .minHeight(150)
                        .minWidth(450)
                        .backgroundColor(2)
                        .withTextSize(14F)
                        .addString(() -> "Task: " + currentTask)
                        .addString(() -> "Rocks Mined: " + rocksMined)
                        .trackSkill(Skill.Mining)
                        .build()
        );
    }

    @Override
    public void poll() {
        for (Task task : taskList) {
            if (task.activate()) {
                currentTask = task.name;
                task.execute();
                if (ScriptManager.INSTANCE.isStopping()) {
                    break;
                }
            }
        }
        currentTask = "Idle";
    }
}