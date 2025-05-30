package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.rt4.*;
import org.sam.Constants;
import org.sam.Task;
import org.sam.samInfernalShale;

public class Mining extends Task {
    samInfernalShale main;
    private final Boolean tickManipulation;

    public Mining(samInfernalShale main, Boolean tickManipulation) {
        super();
        super.name = "Mining Infernal Shale";
        this.main = main;
        this.tickManipulation = tickManipulation;
    }

    public boolean hasItem(String name) {
        return Inventory.stream().name(name).isNotEmpty() ||
                Equipment.stream().name(name).isNotEmpty();
    }

    @Override
    public boolean activate() {
        return Constants.INFERNAL_SHALE_AREA.contains(Players.local()) && !Inventory.isFull();
    }

    @Override
    public void execute() {
        if (tickManipulation && !Inventory.isFull() && !hasItem(Constants.WET_CLOTH)) {
            Npc jim = Npcs.stream().id(Constants.JIM_ID).nearest().first();
            jim.interact("Take-from");
            Condition.wait(() -> hasItem(Constants.WET_CLOTH), 120, 15);
        }

        GameObject rock = Objects.stream().name(Constants.ORE_NAME).nearest().first();
        if (!tickManipulation) {
            if (rock != null) {
                Movement.step(rock);
                Condition.wait(() -> Players.local().tile().distanceTo(rock.tile()) == 1, 50, 40);
                if (rock.interact("Mine")) {
                    Condition.wait(() -> Players.local().animation() != -1, 50, 40);
                    Condition.wait(() -> Players.local().animation() == -1, 50, 500);
                }
            }
        }
        if (tickManipulation) {

        }
    }
}
