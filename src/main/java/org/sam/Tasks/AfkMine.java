package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.rt4.*;
import org.sam.Constants;
import org.sam.Functions;
import org.sam.Task;
import org.sam.samInfernalShale;

public class AfkMine extends Task {
    samInfernalShale main;

    public AfkMine(samInfernalShale main) {
        super();
        super.name = "AFK Mining Shale";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return Constants.INFERNAL_SHALE_AREA.contains(Players.local()) && !Inventory.isFull() && Functions.hasItem("pickaxe");
    }

    @Override
    public void execute() {
        if (Players.local().animation() != -1) return;

        GameObject activeShale = Objects.stream()
                .within(15)
                .id(Constants.INFERNAL_SHALE_DEPOSIT_ID)
                .action("Mine")
                .nearest()
                .first();
        if (activeShale == null) return;

        if (!activeShale.inViewport()) {
            Camera.turnTo(activeShale);
            Movement.step(activeShale);
            Condition.wait(() -> activeShale.getTile().distanceTo(Players.local().tile()) < 3, 60, 80);
        }

        if (activeShale.interact("Mine")) {
            main.vars.miningAttempts++;
            System.out.println("Mining attempt #" + main.vars.miningAttempts);
            
            // Track initial shale count to detect successful mining vs failed interaction
            long initialShaleCount = Inventory.stream().id(Constants.INFERNAL_SHALE).count();
            
            Condition.wait(() -> Players.local().animation() == 7139, 80, 100);
            
            // Check if mining actually succeeded by waiting briefly for inventory change
            boolean miningSucceeded = Condition.wait(() -> {
                long currentShaleCount = Inventory.stream().id(Constants.INFERNAL_SHALE).count();
                return currentShaleCount > initialShaleCount;
            }, 20, 100); // Wait up to 2 seconds for AFK mining success (slower than 3T)
            
            if (!miningSucceeded) {
                // Successful interaction but failed to mine ore
                main.vars.successfulInteractionsFailed++;
                System.out.println("Successful interaction failed to mine ore. Total: " + main.vars.successfulInteractionsFailed);
            }
        }
    }
}
