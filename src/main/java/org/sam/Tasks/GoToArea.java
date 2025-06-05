package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.rt4.*;
import org.powbot.dax.api.models.RunescapeBank;
import org.sam.Constants;
import org.sam.GemBagManager;
import org.sam.Task;
import org.sam.samInfernalShale;

public class GoToArea extends Task {
    samInfernalShale main;

    public GoToArea(samInfernalShale main) {
        super();
        super.name = "Traveling to Infernal Shale Deposit";
        this.main = main;
    }

    public GemBagManager gemBagManager = new GemBagManager();

    @Override
    public boolean activate() {
        return !Constants.INFERNAL_SHALE_AREA.contains(Players.local()) && !gemBagManager.anyGemFull();
    }

    @Override
    public void execute() {
        Movement.builder(Constants.CHASM_OF_FIRE_WORLD.getRandomTile()).setUseTeleports(true).setAutoRun(true).move();
        if (Constants.CHASM_OF_FIRE_WORLD.contains(Players.local())) {
            Movement.moveTo(Constants.CHASM_OF_FIRE_ENTRANCE.getRandomTile());
            Condition.wait(() -> Constants.CHASM_OF_FIRE_ENTRANCE.contains(Players.local()), 80, 200);
            GameObject entrance = Objects.stream().name("Chasm").first();
            if (entrance != null) {
                if (entrance.interact("Enter")) {
                    Condition.wait(() -> Constants.INFERNAL_SHALE_AREA.contains(Players.local()), 80, 120);
                    // KEEP GOING
                }
            }
        }

        return;
    }
}
