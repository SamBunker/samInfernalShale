package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.rt4.*;
import org.sam.Constants;
import org.sam.Task;
import org.sam.samInfernalShale;

import java.util.concurrent.ThreadLocalRandom;

public class Crush extends Task {
    samInfernalShale main;

    public Crush(samInfernalShale main) {
        super();
        super.name = "Crushing Infernal Shale";
        this.main = main;
    }

    public boolean hasItem(String name) {
        return Inventory.stream().name(name).isNotEmpty() ||
                Equipment.stream().name(name).isNotEmpty();
    }


    @Override
    public boolean activate() {
        return Inventory.isFull() && hasItem("Hammer") && hasItem("Chisel");
    }

    @Override
    public void execute() {
        Item hammer = Inventory.stream().name("Hammer").first();
        int maxAttempts = 28;
        if (hammer.valid()) {
            for (int i = 0; i < maxAttempts; i++) {
                Item shale = Inventory.stream().id(Constants.INFERNAL_SHALE).last();
                if (shale == null) break;
                long previousCount = Inventory.stream().id(Constants.INFERNAL_SHALE).count();
                hammer.useOn(shale);
                Condition.wait(() -> Inventory.stream().id(Constants.INFERNAL_SHALE).count() < previousCount,
                        20, 10);
            }
        }
    }
}
