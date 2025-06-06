package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.Locatable;
import org.powbot.api.Notifications;
import org.powbot.api.rt4.*;
import org.sam.Constants;
import org.sam.GemBagManager;
import org.sam.Task;
import org.sam.samInfernalShale;

import java.util.HashMap;
import java.util.Map;
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

    public GemBagManager gemBagManager = new GemBagManager();

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

                Map<String, Integer> inventoryGemCounts = new HashMap<>();

                Inventory.stream()
                        .filter(i -> i.name().startsWith("Uncut sapphire") ||
                                i.name().startsWith("Uncut emerald") ||
                                i.name().startsWith("Uncut ruby") ||
                                i.name().startsWith("Uncut diamond"))
                        .forEach(item -> inventoryGemCounts.put(item.name(), item.stackSize()));

                if (gemBagItem.interact("Fill")) {
                    boolean cleared = Condition.wait(() -> Inventory.stream().name("Uncut sapphire", "Uncut emerald", "Uncut ruby", "Uncut diamond").isEmpty(), 160, 20);
                    if (cleared) {
                        inventoryGemCounts.forEach((gemName, amountAdded) -> {
                            int previousCount = gemBagManager.getCount(gemName);
                            int newCount = previousCount + amountAdded;
                            gemBagManager.updateGemCount(gemName, newCount);
                            System.out.println("Added " + amountAdded + " " + gemName + " to gem bag. New total: " + newCount);
                        });
                    }
                    return;
                }

                if (gemBagManager.anyGemFull()) {
                    Locatable nearestBank = Bank.nearest();
                    Movement.builder(nearestBank).setAutoRun(true).setUseTeleports(true).move();
                    boolean bankIsOpen = Bank.opened();
                    if (Bank.inViewport()) {
                        Condition.wait(() -> Bank.open(), 50, 100);
                        Condition.wait(() -> bankIsOpen, 20, 80);
                        if (gemBagItem.interact("Empty")) {
                            gemBagManager.reset();
                        }
                        Condition.sleep(ThreadLocalRandom.current().nextInt(28, 67));
                        Bank.close(true);
                        Condition.wait(() -> !bankIsOpen, 150, 20);
                        return;
                    } else {
                        Camera.turnTo(nearestBank);
                        Movement.moveTo(nearestBank);
                        Condition.wait(() -> Players.local().tile().distanceTo(nearestBank) < 5, 80, 150);
                    }
                    return;
                }
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
