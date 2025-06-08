package org.sam;
import com.google.common.eventbus.Subscribe;
import org.powbot.api.Condition;
import org.powbot.api.Events;
import org.powbot.api.Random;
import org.powbot.api.event.GameObjectActionEvent;
import org.powbot.api.event.MessageEvent;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.walking.model.Skill;
import org.powbot.api.script.*;
import org.powbot.api.script.paint.PaintBuilder;
import org.powbot.mobile.script.ScriptManager;
import org.powbot.mobile.service.ScriptUploader;
import org.sam.Tasks.*;
import org.w3c.dom.events.EventListener;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

@ScriptConfiguration.List({
        @ScriptConfiguration(
                name = "SelectedRocks",
                description = "Select the rocks you'd like to interact with",
                optionType = OptionType.GAMEOBJECT_ACTIONS
        ),
        @ScriptConfiguration(
                name = "TickManipulation",
                description = "Do you want to use Jim's Wet Rag for tick manipulation?",
                optionType = OptionType.BOOLEAN
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
    public GemBagManager gemBagManager = new GemBagManager();
    private String currentTask = "Idle";
    Boolean TickManipulation;
    Boolean GemBag;

    public boolean hasItem(String name) {
        return Inventory.stream().name(name).isNotEmpty() ||
                Equipment.stream().name(name).isNotEmpty();
    }

    private final ArrayList<Task> taskList = new ArrayList<Task>();

    public static void main(String[] args) {
        new ScriptUploader().uploadAndStart("Sam Infernal Shale", "", "R52T90A6VCM", true, false);
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

    @Override
    public void onStart() {
        List<GameObjectActionEvent> selectedRocks = getOption("SelectedRocks");
        TickManipulation = getOption("TickManipulation");
        GemBag = getOption("GemBag");


        if (GemBag) {
            Item gemBag = Inventory.stream().name("Gem bag").first();

            if (gemBag != null) {
                gemBag.interact("Check");
            }
        }

        addPaint(
                PaintBuilder.newBuilder()
                        .minHeight(150)
                        .minWidth(450)
                        .backgroundColor(2)
                        .withTextSize(14F)
                        .addString(() -> "Task: " + currentTask)
                        .trackSkill(Skill.Mining)
                        .build()
        );
        taskList.add(new HandleGemBag(this, gemBagManager));
        taskList.add(new HandleGems(this, GemBag));
        if (TickManipulation != null) {
            taskList.add(new ThreeTick(this, selectedRocks));
        } else {
            taskList.add(new Mining(this, selectedRocks));
            taskList.add(new Crush(this));
        }
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