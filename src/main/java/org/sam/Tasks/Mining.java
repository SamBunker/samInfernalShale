package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.event.GameObjectActionEvent;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.stream.TileRadius;
import org.sam.Constants;
import org.sam.Task;
import org.sam.samInfernalShale;

import java.util.List;

public class Mining extends Task {
    samInfernalShale main;
    private final Boolean tickManipulation;
    private final List<GameObjectActionEvent> selectedRocks;

    public Mining(samInfernalShale main, Boolean tickManipulation, List selectedRocks) {
        super();
        super.name = "Mining Infernal Shale";
        this.main = main;
        this.tickManipulation = tickManipulation;
        this.selectedRocks = selectedRocks;
    }

    public boolean hasItem(String name) {
        return Inventory.stream().name(name).isNotEmpty() ||
                Equipment.stream().name(name).isNotEmpty();
    }

    @Override
    public boolean activate() {
        return Constants.INFERNAL_SHALE_AREA.contains(Players.local()) && !Inventory.isFull();
    }

    @Override
    public void execute() {
        if (Combat.specialAttack() && Combat.specialPercentage() == 100) {
            Combat.specialAttack(true);
            Condition.sleep(Random.nextInt(120, 210));
        }

        if (tickManipulation && !Inventory.isFull() && !hasItem(Constants.WET_CLOTH)) {
            Npc jim = Npcs.stream().id(Constants.JIM_ID).nearest().first();
            jim.interact("Take-from");
            Condition.wait(() -> hasItem(Constants.WET_CLOTH), 120, 15);
        }

        if (!tickManipulation || selectedRocks == null) return;
        Item wetCloth = Inventory.stream().name(Constants.WET_CLOTH).first();
        if (wetCloth == null) return;

        GameObjectActionEvent event = selectedRocks.get(0);
        if (!event.getName().contains("Infernal shale rocks")) return;

        GameObject targetRock = Objects.stream().at(event.getTile()).name(event.getName()).action("Mine").first();
        if (targetRock == null || !targetRock.valid()) return;

        TileRadius radius = new TileRadius(targetRock.tile(), 2);
        if (radius.getTile().reachable() && radius.getTile().distanceTo(Players.local().tile()) > 1) {
            if (radius.getTile().matrix().interact("Walk here")) {
                Condition.wait(() -> radius.getTile().distanceTo(Players.local().tile()) < 1, 50, 20);
            }
        }
        if (!wetCloth.interact("Wipe")) return;
        Condition.sleep(Random.nextInt(89, 109));
        targetRock.interact("Mine");
        boolean mined = Condition.wait(() -> {
                    GameObject rockNow = Objects.stream().at(event.getTile()).name("Rocks").first();
                    return rockNow != null && rockNow.valid();
                },
                5, 250);
        if (mined) {
            selectedRocks.remove(0);
            selectedRocks.add(event);
            return;
        }
        Condition.sleep(Random.nextInt(90, 120));
    }
}