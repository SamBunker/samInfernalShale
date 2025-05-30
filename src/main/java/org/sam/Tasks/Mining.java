package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.rt4.*;
import org.sam.Constants;
import org.sam.Task;
import org.sam.samInfernalShale;

import java.util.concurrent.ThreadLocalRandom;

public class Mining extends Task {
    samInfernalShale main;
    private final Boolean tickManipulation;

    public Mining(samInfernalShale main, Boolean tickManipulation) {
        super();
        super.name = "Mining Infernal Shale";
        this.main = main;
        this.tickManipulation = tickManipulation;
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
        if (tickManipulation && !Inventory.isFull() && !hasItem(Constants.WET_CLOTH)) {
            Npc jim = Npcs.stream().id(Constants.JIM_ID).nearest().first();
            jim.interact("Take-from");
            Condition.wait(() -> hasItem(Constants.WET_CLOTH), 120, 15);
        }
        if (tickManipulation) {
            GameObject rock = Objects.stream().name(Constants.ORE_NAME).action("Mine").nearest().first();
            Item wetCloth = Inventory.stream().name(Constants.WET_CLOTH).first();

            if (wetCloth != null && rock != null) {
                Movement.step(rock);
                boolean nearRock = Condition.wait(() -> Players.local().tile().distanceTo(rock.tile()) == 1, 50, 40);
                if (nearRock && rock.valid() && rock.actions().contains("Mine") ) {
                    if (wetCloth.interact("Wipe")) {
                        Condition.sleep(ThreadLocalRandom.current().nextInt(85, 109)); // Wait towards end of wet napkin animation
                        if (rock.valid() && rock.name().equals("Infernal shale rocks") && rock.actions().contains("Mine") && rock.interact("Mine")) {
                            Condition.wait(() -> Players.local().animation() != -1, 50, 40); // Start mining
                            Condition.wait(() -> Players.local().animation() == -1, 50, 110); // Finished mining
//                        } //else {
//                            //Objects.stream().name(Constants.ORE_NAME).action("Mine").nearest().second();
                        }
                    }
                }
            }
        }
        if (!tickManipulation) {
            GameObject rock = Objects.stream().name(Constants.ORE_NAME).nearest().first();
            if (rock != null) {
                Movement.step(rock);
                Condition.wait(() -> Players.local().tile().distanceTo(rock.tile()) == 1, 50, 40);
                if (rock.interact("Mine")) {
                    Condition.wait(() -> Players.local().animation() != -1, 50, 40);
                    Condition.wait(() -> Players.local().animation() == -1, 50, 500);
                }
            }
        }
//        if (tickManipulation) {
//            GameObject rock = Objects.stream().name(Constants.ORE_NAME).nearest().first();
//            Item wetCloth = Inventory.stream().name(Constants.WET_CLOTH).first();
//            if (wetCloth != null && rock != null) {
//                Movement.step(rock);
//                Condition.wait(() -> Players.local().tile().distanceTo(rock.tile()) == 1, 50, 40);
//                wetCloth.interact("Wipe");
////                    Condition.wait(() -> Players.local().animation() == Constants.CLOTH_ANIMATION_ID, 10, 10);
//                Condition.sleep(ThreadLocalRandom.current().nextInt(80, 121));
//                if (rock.interact("Mine")) {
//                    Condition.wait(() -> Players.local().animation() != -1, 50, 40);
//                    Condition.wait(() -> Players.local().animation() == -1, 50, 200);
//                }
//            }
//        }
    }
}
