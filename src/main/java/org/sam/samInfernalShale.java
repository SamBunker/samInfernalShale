package org.sam;
import com.google.common.eventbus.Subscribe;
import org.powbot.api.event.InventoryChangeEvent;
import org.powbot.api.event.MessageEvent;
import org.powbot.api.rt4.walking.model.Skill;
import org.powbot.api.script.*;
import org.powbot.api.script.paint.PaintBuilder;
import org.powbot.mobile.script.ScriptManager;
import org.powbot.mobile.service.ScriptUploader;
import org.sam.Tasks.*;
import org.sam.Tasks.Config.MiningConfig;
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

    private final MiningConfig config = new MiningConfig(
            getOption("SelectedRocks"),
            getOption("Mining Method")
    );

    Constants constants = new Constants();
    Variables vars = new Variables();
    public GemBagManager gemBagManager = new GemBagManager();

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
        if (event.getItemId() == Constants.INFERNAL_SHALE) {
            Variables.rocksMined++;
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

        switch (config.getMiningMethod()) {
            case "3T Mining":
                constants.TASK_LIST.add(new TakeCloth(this));
                constants.TASK_LIST.add(new ThreeTick(this, config));
                break;
            case "Mining":
                constants.TASK_LIST.add(new TakeCloth(this));
                constants.TASK_LIST.add(new Mining(this, config));
                constants.TASK_LIST.add(new Crush(this));
                break;
            case "AFK Mining":
                constants.TASK_LIST.add(new AfkMine(this));
                constants.TASK_LIST.add(new Crush(this));
                break;
            default:
                constants.TASK_LIST.add(new AfkMine(this));
                constants.TASK_LIST.add(new Crush(this));
                System.out.println("Unknown mining mode: " + config.getMiningMethod());
                break;
        }

        if (Functions.getGemBag() != null) {
            Functions.getGemBag().interact("Check");
            constants.TASK_LIST.add(new FillGemBag(this, Functions.getGemBag().valid()));
        } else {
            constants.TASK_LIST.add(new DropGems(this));
        }

        constants.TASK_LIST.add(new FillGemBag(this, Functions.getGemBag().valid()));
        constants.TASK_LIST.add(new Running(this));
        constants.TASK_LIST.add(new SpecialAttack(this));


        addPaint(
                PaintBuilder.newBuilder()
                        .minHeight(150)
                        .minWidth(450)
                        .backgroundColor(2)
                        .withTextSize(14F)
                        .addString(() -> "Task: " + vars.currentTask)
                        .addString(() -> "Rocks Mined: " + Variables.rocksMined)
                        .trackSkill(Skill.Mining)
                        .build()
        );
    }

    @Override
    public void poll() {
        for (Task task : constants.TASK_LIST) {
            if (task.activate()) {
                if (config.getMiningMethod().equals("3T Mining") || config.getMiningMethod().equals("Mining")) {
                    if (config.getSelectedRocks() == null) {
                        ScriptManager.INSTANCE.stop();
                        break;
                    }
                }
                if (Functions.hasItem("pickaxe")) {
                    ScriptManager.INSTANCE.stop();
                    break;
                }
                if (ScriptManager.INSTANCE.isStopping()) {
                    break;
                }
                vars.currentTask = task.name;
                task.execute();
            }
        }
        vars.currentTask = "Idle";
    }
}