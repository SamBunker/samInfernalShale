package org.sam.Tasks;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.event.GameObjectActionEvent;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.stream.TileRadius;
import org.sam.*;
import org.sam.Constants;


public class ThreeTick extends Task {
    samInfernalShale main;
    public MiningConfig config;
    Variables vars = new Variables();

    public ThreeTick(samInfernalShale main, MiningConfig config) {
        super();
        super.name = "3T Infernal Shale";
        this.main = main;
        this.config = config;
    }

    @Override
    public boolean activate() {
        return Constants.INFERNAL_SHALE_AREA.contains(Players.local())
                && !Inventory.isFull()
                && Functions.hasItem(" pickaxe")
                && Functions.hasItem(Constants.HAMMER)
                && Functions.hasItem(Constants.CHISEL)
                && Functions.hasItem(Constants.WET_CLOTH);
    }

    @Override
    public void execute() {
        GameObjectActionEvent event = config.getFirstSelectedRock();
        if (!event.getName().contains(Constants.ORE_NAME)) return;

        Functions.getTargetRock(event);
        if (!Functions.getTargetRock(event).valid()) return;

        TileRadius radius = new TileRadius(Functions.getTargetRock(event).tile(), 5);
        if (radius.getTile().distanceTo(Players.local().tile()) > 4) {
            event.getTile().matrix().interact("Walk here");
            Condition.wait(() -> radius.getTile().distanceTo(Players.local().tile()) < 1, 37, 20);
        }

        if (!Functions.getFirstInventoryItemByID(Constants.WET_CLOTH_ID).interact("Wipe")) return;

        Condition.sleep(Random.nextInt(96, 101));

        if (Functions.getTargetRock(event).interact("Mine")) {
            vars.awaitingShale = true;
            //vars.rocksMined++;
            Condition.wait(() -> Players.local().animation() == 12186, 15, 100); //What animation is this?
            config.removeSelectedRock(0);
            config.addSelectedRock(event);

            Tile nextEventTile = config.getFirstSelectedRock().getTile();
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