package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.event.GameObjectActionEvent;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.stream.TileRadius;
import org.sam.*;
import org.sam.Constants;

import java.util.List;

public class Mining extends Task {
    samInfernalShale main;
    public MiningConfig config;

    public Mining(samInfernalShale main, MiningConfig config) {
        super();
        super.name = "Mining Infernal Shale";
        this.main = main;
        this.config = config;
    }

    @Override
    public boolean activate() {
        return Constants.INFERNAL_SHALE_AREA.contains(Players.local())
                && !Inventory.isFull()
                && Functions.hasItem(" pickaxe")
                && Functions.hasItem(Constants.WET_CLOTH);
    }

    @Override
    public void execute() {
        GameObjectActionEvent event = config.getFirstSelectedRock();
        if (!event.getName().contains(Constants.ORE_NAME)) return;

        if (Functions.getTargetRock(event) == null || !Functions.getTargetRock(event).valid()) return;

        TileRadius radius = new TileRadius(Functions.getTargetRock(event).tile(), 5);
        if (radius.getTile().distanceTo(Players.local().tile()) > 4) {
            event.getTile().matrix().interact("Walk here");
            Condition.wait(() -> radius.getTile().distanceTo(Players.local().tile()) < 1, 37, 20);
        }

        if (!Functions.getFirstInventoryItemByID(Constants.WET_CLOTH_ID).interact("Wipe")) return;

        Condition.sleep(Random.nextInt(90, 104));
        if (Functions.getTargetRock(event).interact("Mine")) {
            Condition.wait(() -> Players.local().animation() == 12186, 15, 100);
            config.removeSelectedRock(0);
            config.addSelectedRock(event);

            Tile nextEventTile = config.getFirstSelectedRock().getTile();
            if (nextEventTile.distanceTo(Players.local().tile()) >= 1.1) {
                nextEventTile.matrix().interact("Walk here");
            }

            Condition.sleep(Random.nextInt(35, 42));
        }
    }
}