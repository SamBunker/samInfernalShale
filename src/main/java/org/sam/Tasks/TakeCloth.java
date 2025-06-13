package org.sam.Tasks;

import org.powbot.api.Condition;

import org.powbot.api.rt4.*;
import org.sam.Constants;
import org.sam.Functions;
import org.sam.Task;
import org.sam.samInfernalShale;

public class TakeCloth extends Task {
    samInfernalShale main;

    public TakeCloth(samInfernalShale main) {
        super();
        super.name = "Taking Cloth from Jim";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return !Functions.hasItem(Constants.WET_CLOTH) && !Inventory.isFull();
    }

    @Override
    public void execute() {
        Npc jim = Npcs.stream().id(Constants.JIM_ID).within(15).nearest().first();
        if (!jim.valid() && !jim.inViewport()) {
            Camera.turnTo(jim);
            Movement.step(jim);
            return;
        }
        if (jim.interact("Take-from")) {
            Condition.wait(() -> Functions.hasItem(Constants.WET_CLOTH), 120, 15);
        }
    }
}
