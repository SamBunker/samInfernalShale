package org.sam;
import org.powbot.api.Condition;
import org.powbot.api.rt4.Chat;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Item;
import org.powbot.api.rt4.walking.model.Skill;
import org.powbot.api.script.*;
import org.powbot.api.script.paint.PaintBuilder;
import org.powbot.mobile.script.ScriptManager;
import org.powbot.mobile.service.ScriptUploader;
import org.sam.Tasks.Crush;
import org.sam.Tasks.HandleGems;
import org.sam.Tasks.GoToArea;
import org.sam.Tasks.Mining;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ScriptConfiguration.List({
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
        description = "Begin inside the infernal shale mining area.",
        author = "Sam",
        version = "1.0",
        category = ScriptCategory.Mining
)
public class samInfernalShale extends AbstractScript {
    public GemBagManager gemBagManager = new GemBagManager();
    private String currentTask = "Idle";
    Boolean TickManipulation;
    Boolean GemBag;

    private final ArrayList<Task> taskList = new ArrayList<Task>();

    public static void main(String[] args) {
        new ScriptUploader().uploadAndStart("Sam Infernal Shale", "", "R52T90A6VCM", true, false);
    }

    @Override
    public void onStart() {

        TickManipulation = getOption("TickManipulation");
        GemBag = getOption("GemBag");

        if (GemBag) {
            Item gemBag = Inventory.stream().name("Gem bag").first();

            if (gemBag != null) {
                if (gemBag.interact("Check")) {
                    //Condition.sleep(ThreadLocalRandom.current().nextInt(20, 50));
                    String bagContents = Chat.getChatMessage();
                    if (bagContents != null && bagContents.toLowerCase().startsWith("your gem bag contains")) {
                        Pattern pattern = Pattern.compile("(\\d+)\\s+(sapphires|emeralds|rubies|diamonds)");
                        Matcher matcher = pattern.matcher(bagContents.toLowerCase());

                        while (matcher.find()) {
                            int count = Integer.parseInt(matcher.group(1));
                            String gemType = matcher.group(2);
                            String formattedGem = "Uncut " + gemType.substring(0, gemType.length() - 1); // Remove plural
                            gemBagManager.updateGemCount(formattedGem, count);
                            System.out.println("Updated " + formattedGem + " to " + count);
                        }
                    }

                }
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
        taskList.add(new GoToArea(this));
        taskList.add(new HandleGems(this, GemBag));
        taskList.add(new Crush(this));
        taskList.add(new Mining(this, TickManipulation));
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