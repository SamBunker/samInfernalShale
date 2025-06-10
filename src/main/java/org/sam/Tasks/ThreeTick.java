package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.event.GameObjectActionEvent;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.stream.TileRadius;
import org.sam.Constants;
import org.sam.Task;
import org.sam.Functions;
import org.sam.samInfernalShale;

import java.util.List;

public class ThreeTick extends Task {
    samInfernalShale main;
    private final List<GameObjectActionEvent> selectedRocks;
    Item hammer = Inventory.stream().name("Hammer").first();

    public ThreeTick(samInfernalShale main, List selectedRocks) {
        super();
        super.name = "3T Infernal Shale";
        this.main = main;
        this.selectedRocks = selectedRocks;
    }

    @Override
    public boolean activate() {
        return Constants.INFERNAL_SHALE_AREA.contains(Players.local()) && !Inventory.isFull();
    }

    @Override
    public void execute() {
        if (Movement.running()) {
            Movement.running(false);
            Condition.sleep(Random.nextInt(40, 80));
        }
        if (Combat.specialPercentage() == 100 && Combat.specialAttack(true)) {
            Condition.sleep(Random.nextInt(40, 80));
        }

        if (!Inventory.isFull() && !Functions.hasItem(Constants.WET_CLOTH)) {
            Npc jim = Npcs.stream().id(Constants.JIM_ID).nearest().first();
            jim.interact("Take-from");
            Condition.wait(() -> main.hasItem(Constants.WET_CLOTH), 120, 15);
        }

        if (selectedRocks == null) return; // causes to sit idle
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

        Condition.sleep(Random.nextInt(96, 101));

        targetRock.interact("Mine");
        Condition.wait(() -> Players.local().animation() == 12186, 15, 100); //What animation is this?

        selectedRocks.remove(0);
        selectedRocks.add(event);

        Tile nextEventTile = selectedRocks.get(0).getTile();
        if (nextEventTile.distanceTo(Players.local().tile()) >= 1.1) {
            nextEventTile.matrix().interact("Walk here");
            long initialCount = Inventory.stream().id(Constants.INFERNAL_SHALE).count();

            if (initialCount >= 2 && nextEventTile.matrix().distanceTo(Players.local().tile()) < 2.1) {
                Item shale = Inventory.stream().id(Constants.INFERNAL_SHALE).last();

                if (initialCount >= Random.nextInt(6, 8)) {
                    Condition.sleep(Random.nextInt(6, 9));
                    for (int i = 0; i < 2; i++) {
                        if (hammer.useOn(shale)) {
                            Condition.wait(() ->
                                            Inventory.stream().id(Constants.INFERNAL_SHALE).count() <= initialCount - 1,
                                    2, 5
                            );
                        }
                    }
                } else {
                    Condition.sleep(Random.nextInt(8, 21));
                    if (hammer.useOn(shale)) {
                        Condition.wait(() ->
                                        Inventory.stream().id(Constants.INFERNAL_SHALE).count() <= initialCount - 1,
                                2, 5
                        );
                    }
                }
            }
        }

        Condition.sleep(Random.nextInt(30, 40));
    }
}