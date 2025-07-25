package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.*;
import org.sam.*;
import org.sam.Constants;
import org.sam.Variables;

public class Crush extends Task {
    samInfernalShale main;

    public Crush(samInfernalShale main) {
        super();
        super.name = "Crushing Infernal Shale";
        this.main = main;
    }

    @Override
    public boolean activate() {
        if (main.config != null && "Imcando Hammer (off-hand)".equals(main.config.getHammerType())) {
            return Inventory.isFull() && Functions.hasHammer(main);
        }
        return Inventory.isFull() && Functions.hasItem(Constants.HAMMER) && Functions.hasItem(Constants.CHISEL);
    }

    @Override
    public void execute() {
        Item chisel = Inventory.stream().name(Constants.CHISEL).first();
        for (int i = 0; i < Variables.maxCrushAttempts; i++) {
            Item shale = Inventory.stream().id(Constants.INFERNAL_SHALE).last();
            if (shale.inventoryActions().isEmpty()) break;
            if (chisel.useOn(shale)) {
                Condition.sleep(Random.nextInt(20, 80));
            }
        }
    }
}
