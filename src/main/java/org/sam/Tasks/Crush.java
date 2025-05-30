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
        Item shale = Inventory.stream().id(Constants.INFERNAL_SHALE).last();

        if (hammer != null) {
            while (Inventory.stream().id(Constants.INFERNAL_SHALE).isNotEmpty()) {
                if (shale == null) break;
                if (hammer.interact("Use")) {
                    boolean selected = Condition.wait(() -> Inventory.selectedItem() != null && Inventory.selectedItem().name().equals("Hammer"), 50, 20);
                    if (selected && shale.interact("Use")) {
                        Condition.wait(() -> Inventory.stream().id(Constants.INFERNAL_SHALE).count() < Inventory.stream().id(Constants.INFERNAL_SHALE).count(),
                                40, 5);
                    }
                }
                Condition.sleep(ThreadLocalRandom.current().nextInt(48, 111));
            }
        }
    }
}
