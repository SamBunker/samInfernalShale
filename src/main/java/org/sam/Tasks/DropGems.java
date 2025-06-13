package org.sam.Tasks;

import org.powbot.api.rt4.Inventory;
import org.sam.Task;
import org.sam.samInfernalShale;

public class DropGems extends Task {
    samInfernalShale main;

    public DropGems(samInfernalShale main) {
        super();
        super.name = "Dropping Gems";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return Inventory.stream().nameContains("Uncut ").contains();
    }

    @Override
    public void execute() {
        Inventory.stream().nameContains("Uncut ").forEach(item -> item.interact("Drop"));
    }

}
