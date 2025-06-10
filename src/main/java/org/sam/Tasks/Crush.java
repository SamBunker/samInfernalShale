package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.*;
import org.powbot.mobile.rlib.RPowBot;
import org.sam.Constants;
import org.sam.Task;
import org.sam.Functions;
important org.sam.Variables;
import org.sam.samInfernalShale;

import java.util.concurrent.ThreadLocalRandom;

public class Crush extends Task {
    samInfernalShale main;

    public Crush(samInfernalShale main) {
        super();
        super.name = "Crushing Infernal Shale";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return Inventory.isFull() && Functions.hasItem("Hammer") && Functions.hasItem("Chisel");
    }

    @Override
    public void execute() {
        Item hammer = Inventory.stream().name("Hammer").first();
        if (hammer.valid()) {
//            Inventory.stream().id(Constants.INFERNAL_SHALE).forEach(item -> hammer.useOn(item)); //may rewrite to attempt this method using more of the API instead
            for (int i = 0; i < Variables.maxCrushAttempts; i++) {
                Item shale = Inventory.stream().id(Constants.INFERNAL_SHALE).last();
                if (shale.inventoryActions().isEmpty()) break;
                hammer.useOn(shale);
                Condition.sleep(Random.nextInt(20, 80));
            }
        }
        Condition.sleep(Random.nextInt(80, 110));
    }
}
