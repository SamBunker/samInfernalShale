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

public class ThreeTick extends Task {
    samInfernalShale main;
    private final MiningConfig config;

    public ThreeTick(samInfernalShale main, MiningConfig config) {
        super();
        super.name = "3T Infernal Shale";
        this.main = main;
        this.config = config;
    }

    @Override
    public boolean activate() {
        return !config.getSelectedRocks().isEmpty() && Constants.INFERNAL_SHALE_AREA.contains(Players.local()) && !Inventory.isFull() && Functions.hasItem(" pickaxe", Constants.HAMMER, Constants.CHISEL, Constants.WET_CLOTH);
    }

    @Override
    public void execute() {
        GameObjectActionEvent event = Functions.getFirstSelectedRock();
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
            config.getSelectedRocks().remove(0);
            config.getSelectedRocks().add(event);

            Tile nextEventTile = Functions.getFirstSelectedRock().getTile();
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