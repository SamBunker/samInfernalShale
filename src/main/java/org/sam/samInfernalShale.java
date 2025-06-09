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
        ),
        @ScriptConfiguration(
                name = "GemBag",
                description = "Deposit gems into your gem bag?",
                optionType = OptionType.BOOLEAN
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

    public GemBagManager gemBagManager = new GemBagManager();
    private String currentTask = "Idle";
    public static int rocksMined = 0;
    private int lastMiningXp = 0;
    Boolean GemBag;

    public boolean hasItem(String name) {
        return Inventory.stream().name(name).isNotEmpty() ||
                Equipment.stream().name(name).isNotEmpty();
    }

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
    public void onExperience(SkillExpGainedEvent event) {
        if (event.getSkill() == Skill.Mining) {
            int newXp = Skills.experience(Skill.Mining);
            if (newXp > lastMiningXp) {
                samInfernalShale.rocksMined++;
                lastMiningXp = newXp;
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

        if (msg.contains("sapphires") && msg.contains("emeralds") && msg.contains("rubies")) {
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
    }

    private final ArrayList<Task> taskList = new ArrayList<Task>();

    @Override
    public void onStart() {
        List<GameObjectActionEvent> selectedRocks = getOption("SelectedRocks");
        String miningMethod = getOption("Mining Method");
        GemBag = getOption("GemBag");

        switch (miningMethod) {
            case "3T Mining":
                taskList.add(new ThreeTick(this, selectedRocks));
                break;
            case "Mining":
                taskList.add(new Mining(this, selectedRocks));
                taskList.add(new Crush(this));
                break;
            case "AFK Mining":
                taskList.add(new AfkMine(this));
                taskList.add(new Crush(this));
                break;
            default:
                taskList.add(new AfkMine(this));
                taskList.add(new Crush(this));
                System.out.println("Unknown mining mode: " + miningMethod);
                break;
        }

        if (GemBag) {
            Item gemBag = Inventory.stream().name("Gem bag").first();
            if (gemBag != null) {
                gemBag.interact("Check");
            }
        }
        //Adding this by default to handle gems. If gem bag is disabled, gems will be dropped to the ground.
        taskList.add(new HandleGems(this, GemBag));

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