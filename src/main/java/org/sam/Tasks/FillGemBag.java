package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.rt4.*;
import org.sam.*;

import java.util.HashMap;
import java.util.Map;

public class FillGemBag extends Task {
    samInfernalShale main;
    private final Boolean gemBag;

    public FillGemBag(samInfernalShale main, Boolean gemBag) {
        super();
        super.name = "Filling Gem Bag";
        this.main = main;
        this.gemBag = gemBag;
    }

    public GemBagManager gemBagManager = new GemBagManager();

    @Override
    public boolean activate() {
        return gemBag && Inventory.stream().nameContains("Uncut ").contains();
    }

    @Override
    public void execute() {
        if (Functions.getGemBag() != null) {
            Map<String, Integer> inventoryGemCounts = new HashMap<>();
            Inventory.stream().nameContains("Uncut ").forEach(item -> inventoryGemCounts.put(item.name(), item.stackSize()));

            if (Functions.getGemBag().interact("Fill")) {
                boolean cleared = Condition.wait(() -> Inventory.stream().nameContains("Uncut ").isEmpty(), 20, 20);
                if (cleared) {
                    inventoryGemCounts.forEach((gemName, amountAdded) -> {
                        int previousCount = gemBagManager.getCount(gemName);
                        int newCount = previousCount + amountAdded;
                        gemBagManager.updateGemCount(gemName, newCount);
                        System.out.println("Added " + amountAdded + " " + gemName + " to gem bag. New total: " + newCount);
                    });
                }
            }
        }
    }
}
