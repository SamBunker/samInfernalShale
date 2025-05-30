package org.sam.Tasks;

import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Players;
import org.sam.Constants;
import org.sam.Task;
import org.sam.samInfernalShale;

public class Crush extends Task {
    samInfernalShale main;

    public Crush(samInfernalShale main) {
        super();
        super.name = "Crushing Infernal Shale";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return Inventory.isFull();
    }

    @Override
    public void execute() {

    }
}
