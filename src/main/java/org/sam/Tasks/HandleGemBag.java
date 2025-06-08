package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.Locatable;
import org.powbot.api.Random;
import org.powbot.api.rt4.*;
import org.powbot.dax.api.json.Location;
import org.powbot.mobile.script.ScriptManager;
import org.sam.Constants;
import org.sam.GemBagManager;
import org.sam.Task;
import org.sam.samInfernalShale;

public class HandleGemBag extends Task {
    samInfernalShale main;
    private final GemBagManager gemBagManager;

    public HandleGemBag(samInfernalShale main, GemBagManager gemBagManager) {
        super();
        super.name = "Handling the Gem Bag";
        this.main = main;
        this.gemBagManager = gemBagManager;
    }

    @Override
    public boolean activate() {
        return gemBagManager.anyGemFull();
    }

    @Override
    public void execute() {
        Item gemBagItem = Inventory.stream().id(Constants.GEM_BAG_ID).first();
        System.out.println("GemBag has a gem with 60 count. Handling gems...");
        GameObject fireDoor = Objects.stream().id(Constants.FIRE_ID).nearest().first();

        if (fireDoor == null || !fireDoor.valid()) return;

        if (!fireDoor.inViewport()) {
            Camera.turnTo(fireDoor);
            Movement.moveTo(fireDoor);
        }
        fireDoor.interact("Pass");
        Condition.wait(() -> !Players.local().inMotion(), 120, 40);

        if (Prayer.prayerPoints() > 0) {
            Prayer.prayer(Prayer.Effect.PROTECT_FROM_MELEE, true);
        }

        GameObject bottomLift = Objects.stream().id(Constants.LIFT_BOTTOM).nearest().first();
        if (bottomLift == null || !bottomLift.valid()) return;

        if (!bottomLift.inViewport()) {
            Camera.turnTo(bottomLift);
            Movement.moveTo(bottomLift);
        }
        bottomLift.interact("Enter");
        Condition.wait(() -> !Players.local().inMotion(), 120, 40);

        Movement.moveTo(Constants.LIFT_MIDDLE_FLOOR_DOWN_AREA.getRandomTile());
        Condition.wait(() -> Constants.LIFT_MIDDLE_FLOOR_DOWN_AREA.contains(Players.local()), 80, 500);

        GameObject middleLift = Objects.stream().id(Constants.LIFT_BOTTOM).nearest().first();
        if (middleLift == null || !bottomLift.valid()) {
            Movement.moveTo(Constants.MIDDLE_FLOOR_SAFE_AREA.getRandomTile());
            Condition.wait(() -> !Players.local().inMotion(), 120, 300);
        }

        if (!middleLift.inViewport()) {
            Camera.turnTo(middleLift);
            Movement.builder(Constants.LIFT_MIDDLE_FLOOR_DOWN_AREA.getRandomTile()).move();
            Condition.wait(() -> !Players.local().inMotion(), 120, 300);
        }

        middleLift.interact("Enter");
        Condition.wait(() -> Constants.LIFT_TOP_FLOOR_UP_AREA.contains(Players.local()), 80, 200);

        GameObject rope = Objects.stream().id(Constants.ROPE).nearest().first();
        if (rope == null || !rope.valid()) return;

        if (!rope.inViewport()) {
            Camera.turnTo(rope);
            Movement.step(Constants.ROPE_AREA.getRandomTile());
            Condition.wait(() -> !Players.local().inMotion(), 120, 300);
        }

        rope.interact("Climb-up");
        Condition.wait(() -> Constants.CHASM_OF_FIRE_WORLD.contains(Players.local()), 80, 120);

        if (Prayer.prayerActive(Prayer.Effect.PROTECT_FROM_MELEE)) {
            Prayer.prayer(Prayer.Effect.PROTECT_FROM_MELEE, false);
            Condition.wait(() -> !Prayer.prayersActive(), 20, 80);
        }

        Movement.builder(Constants.CLOSEST_BANK_AREA.getRandomTile()).setUseTeleports(false).setAutoRun(true).move();
        boolean bankIsOpen = Bank.opened();

        if (!Bank.inViewport()) {
            Camera.turnTo(Constants.CLOSEST_BANK_AREA.getRandomTile());
            Movement.moveTo(Constants.CLOSEST_BANK_AREA.getRandomTile());
            Condition.wait(() -> Constants.CLOSEST_BANK_AREA.contains(Players.local()), 80, 150);
        }
        Condition.wait(() -> Bank.open(), 50, 100);
        Condition.wait(() -> bankIsOpen, 20, 80);
        if (gemBagItem.interact("Empty")) {
            gemBagManager.reset();
            Condition.sleep(Random.nextInt(89, 121));
        }
        Condition.sleep(Random.nextInt(28, 67));
        Bank.close(true);
        Condition.wait(() -> !bankIsOpen, 150, 20);
    }
}
