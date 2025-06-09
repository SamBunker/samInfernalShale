package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.event.GameObjectActionEvent;
import org.powbot.api.rt4.*;
import org.sam.Constants;
import org.sam.Task;
import org.sam.samInfernalShale;

import java.util.List;

public class AfkMine extends Task {
    samInfernalShale main;

    public AfkMine(samInfernalShale main) {
        super();
        super.name = "AFK Mining Shale";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return Constants.INFERNAL_SHALE_AREA.contains(Players.local()) && !Inventory.isFull();
    }

    @Override
    public void execute() {
        if (Movement.running(false)) {
            Movement.running(true);
            Condition.wait(() -> Movement.running(true), 60, 80);
        }

        if (Combat.specialAttack() && Combat.specialPercentage() == 100) {
            Combat.specialAttack(true);
            Condition.sleep(Random.nextInt(120, 210));
        }

        if (Players.local().animation() != -1) return;

        GameObject activeShale = Objects.stream().id(Constants.INFERNAL_SHALE_DEPOSIT_ID).action("Mine").first();
        if (activeShale == null) return;

        if (!activeShale.inViewport()) {
            Camera.turnTo(activeShale);
            Movement.step(activeShale);
            Condition.wait(() -> activeShale.getTile().distanceTo(Players.local().tile()) < 3, 60, 80);
        }

        activeShale.interact("Mine");
        Condition.wait(() -> Players.local().animation() == 7139, 80, 100);
    }
}
