package org.sam;
import org.powbot.api.script.*;
import org.powbot.mobile.script.ScriptManager;
import org.powbot.mobile.service.ScriptUploader;
import org.sam.Tasks.Banking;
import org.sam.Tasks.GoToArea;
import org.sam.Tasks.Mining;

import java.util.ArrayList;

@ScriptConfiguration.List({
        @ScriptConfiguration(
                name = "TickManipulation",
                description = "Do you want to use Jim's Wet Rag for tick manipulation?",
                optionType = OptionType.BOOLEAN
        )
})

@ScriptManifest(
        name = "Sam Infernal Shale",
        description = "Begins WHERE?",
        author = "Sam",
        version = "1",
        category = ScriptCategory.Mining
)
public class samInfernalShale extends AbstractScript {
    Boolean TickManipulation;

    private final ArrayList<Task> taskList = new ArrayList<Task>();

    public static void main(String[] args) {
        new ScriptUploader().uploadAndStart("Sam Infernal Shale Script", "", "R52T90A6VCM", true, false);
    }

    @Override
    public void onStart() {
        TickManipulation = getOption("TickManipulation");
        taskList.add(new Mining(this, TickManipulation));
        taskList.add(new Banking(this));
        taskList.add(new GoToArea(this));

    }

    @Override
    public void poll() {
        for (Task task : taskList) {
            if (task.activate()) {
                task.execute();
                if (ScriptManager.INSTANCE.isStopping()) {
                    break;
                }
            }
        }
    }
}