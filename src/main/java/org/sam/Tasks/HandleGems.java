package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.Locatable;
import org.powbot.api.Notifications;
import org.powbot.api.rt4.Bank;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Item;
import org.powbot.api.rt4.Movement;
import org.powbot.dax.api.models.RunescapeBank;
import org.sam.Constants;
import org.sam.Task;
import org.sam.samInfernalShale;

import java.util.concurrent.ThreadLocalRandom;

public class HandleGems extends Task {
    samInfernalShale main;
    private final Boolean gemBag;

    public HandleGems(samInfernalShale main, Boolean gemBag) {
        super();
        super.name = "Handling Gems";
        this.main = main;
        this.gemBag = gemBag;
    }

    private final String[] GEMS = {
            "Uncut sapphire",
            "Uncut emerald",
            "Uncut ruby",
            "Uncut diamond"
    };

    @Override
    public boolean activate() {
        for (String gem : GEMS) {
            if (Inventory.stream().name(gem).isNotEmpty()) {
                return true; // Gems found
            }
        }
        return false; // No gems were found
    }

    @Override
    public void execute() {
        if (gemBag) {
            Item gemBagItem = Inventory.stream().id(Constants.GEM_BAG_ID).first();
            if (gemBagItem != null) {
                gemBagItem.interact("Fill");
                boolean cleared = Condition.wait(() -> Inventory.stream().name("Uncut sapphire", "Uncut emerald", "Uncut ruby", "Uncut diamond").isEmpty(),
                        160, 20);
                if (!cleared) {
                    Locatable nearestBank = Bank.nearest();
                    Movement.builder(nearestBank).setAutoRun(true).setUseTeleports(true).move();
                    Boolean bankIsOpen = Bank.opened();
                    if (Bank.inViewport()) {
                        Condition.wait(() -> Bank.open(), 50, 100);
                        Condition.wait(() -> bankIsOpen, 20, 80);
                        gemBagItem.interact("Empty");
                        Condition.sleep(ThreadLocalRandom.current().nextInt(28, 67));
                        Bank.close(true);
                        Condition.wait(() -> !bankIsOpen, 150, 20);
                        return;
                    }
                }
                return;
            } else {
                Notifications.showNotification("No gem bag found! Dropping gems.");
            }
            for (String gem : GEMS) {
                while (Inventory.stream().name(gem).isNotEmpty()) {
                    Item gemItem = Inventory.stream().name(gem).first();
                    if (gemItem != null && gemItem.interact("Drop")) {
                        Condition.wait(() -> Inventory.stream().name(gem).isNotEmpty(), 50, 20);
                    }
                    Condition.sleep(ThreadLocalRandom.current().nextInt(424, 706));
                }
            }
        }
    }
}
