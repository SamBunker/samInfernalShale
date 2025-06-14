package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.rt4.*;
import org.sam.Constants;
import org.sam.Functions;
import org.sam.Task;
import org.sam.samInfernalShale;

public class AfkMine extends Task {
    samInfernalShale main;

    public AfkMine(samInfernalShale main) {
        super();
        super.name = "AFK Mining Shale";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return Constants.INFERNAL_SHALE_AREA.contains(Players.local()) && !Inventory.isFull() && Functions.hasItem("pickaxe");
    }

    @Override
    public void execute() {
        if (Players.local().animation() != -1) return;

        GameObject activeShale = Objects.stream()
                .within(15)
                .id(Constants.INFERNAL_SHALE_DEPOSIT_ID)
                .action("Mine")
                .nearest()
                .first();
        if (activeShale == null) return;

        if (!activeShale.inViewport()) {
            Camera.turnTo(activeShale);
            Movement.step(activeShale);
            Condition.wait(() -> activeShale.getTile().distanceTo(Players.local().tile()) < 3, 60, 80);
        }

        if (activeShale.interact("Mine")) {
            Condition.wait(() -> Players.local().animation() == 7139, 80, 100);
        }
    }
}
