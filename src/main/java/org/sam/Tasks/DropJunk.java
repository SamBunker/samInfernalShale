package org.sam.Tasks;

import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Players;
import org.sam.Constants;
import org.sam.Task;
import org.sam.samInfernalShale;

public class DropJunk extends Task {
    samInfernalShale main;

    public DropJunk(samInfernalShale main) {
        super();
        super.name = "Dropping Junk";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return Constants.INFERNAL_SHALE_AREA.contains(Players.local()) && !Inventory.isFull();
    }

    @Override
    public void execute() {

    }
}
