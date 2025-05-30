package org.sam;
import org.powbot.api.script.*;
import org.powbot.mobile.script.ScriptManager;
import org.powbot.mobile.service.ScriptUploader;

import java.util.ArrayList;

@ScriptConfiguration.List({
        @ScriptConfiguration(
                name = "Harpoon",
                allowedValues = {"Merfolk trident", "Trident of the seas", "Trident of the swamp", "Dragon harpoon", "Harpoon"},
                defaultValue = "Dragon harpoon",
                description = "Which trident or harpoon are you using?",
                optionType = OptionType.STRING
        ),
        @ScriptConfiguration(
                name = "Stamina",
                description = "Use Stamina Potions?",
                optionType = OptionType.BOOLEAN
        ),
        @ScriptConfiguration(
                name = "NumuliteUnlock",
                description = "Did you pay 20,000 numulite for permanent access to drift net fishing? If not, grab Numulite from bank.",
                optionType = OptionType.BOOLEAN
        )
})

@ScriptManifest(
        name = "Sam Template Script",
        description = "Begins WHERE?",
        author = "Sam",
        version = "1",
        category = ScriptCategory.Fishing
)
public class samInfernalShale extends AbstractScript {
    Boolean NumuliteUnlock;
    String Harpoon = "";
    Boolean Stamina;

    private final ArrayList<Task> taskList = new ArrayList<Task>();

    public static void main(String[] args) {
        new ScriptUploader().uploadAndStart("Sam Template Script", "", "R52T90A6VCM", true, false);
    }

    @Override
    public void onStart() {
        NumuliteUnlock = getOption("NumuliteUnlock");
        Harpoon = getOption("Harpoon");
        Stamina = getOption("Stamina");
//        taskList.add(new Banking(this, NumuliteUnlock, Harpoon, Stamina));
//        taskList.add(new ReturnToArea(this));
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