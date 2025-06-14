package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.event.GameObjectActionEvent;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.stream.TileRadius;
import org.sam.Constants;
import org.sam.Functions;
import org.sam.Task;
import org.sam.samInfernalShale;

import java.util.List;

public class Mining extends Task {
    samInfernalShale main;
    private final List<GameObjectActionEvent> selectedRocks;

    public Mining(samInfernalShale main, List selectedRocks) {
        super();
        super.name = "Mining Infernal Shale";
        this.main = main;
        this.selectedRocks = selectedRocks;
    }

    public boolean activate() {
        return !selectedRocks.isEmpty()
                && Constants.INFERNAL_SHALE_AREA.contains(Players.local())
                && !Inventory.isFull()
                && Functions.hasItem(" pickaxe")
                && Functions.hasItem(Constants.WET_CLOTH);
    }
    @Override
    public void execute() {
        Item wetCloth = Inventory.stream().name(Constants.WET_CLOTH).first();
        if (wetCloth == null) return;

        GameObjectActionEvent event = selectedRocks.get(0);
        if (!event.getName().contains("Infernal shale rocks")) return;

        GameObject targetRock = Objects.stream().at(event.getTile()).name(event.getName()).action("Mine").first();
        if (targetRock == null || !targetRock.valid()) return;

        TileRadius radius = new TileRadius(targetRock.tile(), 5);
        if (radius.getTile().distanceTo(Players.local().tile()) > 4) {
            event.getTile().matrix().interact("Walk here");
            Condition.wait(() -> radius.getTile().distanceTo(Players.local().tile()) < 1, 37, 20);
        }

        if (!wetCloth.interact("Wipe")) return;
        Condition.sleep(Random.nextInt(90, 104));
        targetRock.interact("Mine");
        Condition.wait(() -> Players.local().animation() == 12186, 15, 100);

        selectedRocks.remove(0);
        selectedRocks.add(event);

        Tile nextEventTile = selectedRocks.get(0).getTile();
        if (nextEventTile.distanceTo(Players.local().tile()) >= 1.1) {
            nextEventTile.matrix().interact("Walk here");
        }

        Condition.sleep(Random.nextInt(35, 42));
    }
}