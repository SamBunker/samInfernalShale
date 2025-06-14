package org.sam.Tasks;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.event.GameObjectActionEvent;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.stream.TileRadius;
import org.sam.*;
import org.sam.Constants;
import org.sam.Tasks.Config.MiningConfig;

import java.util.List;

public class ThreeTick extends Task {
    samInfernalShale main;
    private final List<GameObjectActionEvent> selectedRocks;

    public ThreeTick(samInfernalShale main, List selectedRocks) {
        super();
        super.name = "3T Infernal Shale";
        this.main = main;
        this.selectedRocks = selectedRocks;
    }

    @Override
    public boolean activate() {
        return selectedRocks != null
                && Constants.INFERNAL_SHALE_AREA.contains(Players.local())
                && !Inventory.isFull()
                && Functions.hasItem(" pickaxe")
                && Functions.hasItem(Constants.HAMMER)
                && Functions.hasItem(Constants.CHISEL)
                && Functions.hasItem(Constants.WET_CLOTH);
    }

    @Override
    public void execute() {
        GameObjectActionEvent event = selectedRocks.get(0);
        if (!event.getName().contains(Constants.ORE_NAME)) return;

        if (Functions.getTargetRock(event) == null || !Functions.getTargetRock(event).valid()) return;

        TileRadius radius = new TileRadius(Functions.getTargetRock(event).tile(), 5);
        if (radius.getTile().distanceTo(Players.local().tile()) > 4) {
            event.getTile().matrix().interact("Walk here");
            Condition.wait(() -> radius.getTile().distanceTo(Players.local().tile()) < 1, 37, 20);
        }

        if (!Functions.getWetCloth().interact("Wipe")) return;

        Condition.sleep(Random.nextInt(96, 101));

        if (Functions.getTargetRock(event).interact("Mine")) {
            Condition.wait(() -> Players.local().animation() == 12186, 15, 100); //What animation is this?
            selectedRocks.remove(0);
            selectedRocks.add(event);
//            config.removeSelectedRock(0);
//            config.addSelectedRock(event);

            Tile nextEventTile = selectedRocks.get(0).getTile();
            if (nextEventTile.distanceTo(Players.local().tile()) >= 1.1) {
                nextEventTile.matrix().interact("Walk here");
                long initialCount = Inventory.stream().id(Constants.INFERNAL_SHALE).count();

                if (initialCount >= 2 && nextEventTile.matrix().distanceTo(Players.local().tile()) < 2.1) {
                    Item shale = Inventory.stream().id(Constants.INFERNAL_SHALE).last();

                    if (initialCount >= Random.nextInt(6, 8)) {
                        Condition.sleep(Random.nextInt(6, 9));
                        for (int i = 0; i < 2; i++) {
                            if (Functions.getHammer().useOn(shale)) {
                                Condition.wait(() ->
                                                Inventory.stream().id(Constants.INFERNAL_SHALE).count() <= initialCount - 1,
                                        80, 10
                                );
                            }
                        }
                    } else {
                        Condition.sleep(Random.nextInt(8, 21));
                        if (Functions.getHammer().useOn(shale)) {
                            Condition.wait(() ->
                                            Inventory.stream().id(Constants.INFERNAL_SHALE).count() <= initialCount - 1,
                                    2, 5
                            );
                        }
                    }
                }
            }
        }
        Condition.sleep(Random.nextInt(30, 40));
    }
}