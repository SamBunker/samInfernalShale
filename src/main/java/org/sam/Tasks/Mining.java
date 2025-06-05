package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.Tile;
import org.powbot.api.event.GameObjectActionEvent;
import org.powbot.api.rt4.*;
import org.powbot.mobile.Toggle;
import org.sam.Constants;
import org.sam.Task;
import org.sam.samInfernalShale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

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

    private boolean tryMine(GameObject rock, Integer time) {
        if (rock != null && rock.valid() && rock.name().equals("Infernal shale rocks") &&
        rock.actions().contains("Mine") && rock.interact("Mine")) {
            Condition.wait(() -> Players.local().animation() != -1, 50, 40); // Start mining
            Condition.wait(() -> Players.local().animation() == -1, 50, time); // Finished mining
            return true;
        }
        return false;
    }

    @Override
    public boolean activate() {
        return Constants.INFERNAL_SHALE_AREA.contains(Players.local()) && !Inventory.isFull();
    }

    @Override
    public void execute() {
        if (Combat.specialPercentage() == 100) {
            Combat.specialAttack(true);
        }


        //AtomicReference<GameObject> rockRef = new AtomicReference<>();

        if (tickManipulation && !Inventory.isFull() && !hasItem(Constants.WET_CLOTH)) {
            Npc jim = Npcs.stream().id(Constants.JIM_ID).nearest().first();
            jim.interact("Take-from");
            Condition.wait(() -> hasItem(Constants.WET_CLOTH), 120, 15);
        }
        //List<GameObject> rocks = Objects.stream().name(Constants.ORE_NAME).action("Mine").nearest().list();
        //rockRef.set(rocks.get(0)); //get the closest rock

        if (tickManipulation) {
            Item wetCloth = Inventory.stream().name(Constants.WET_CLOTH).first();

            if (wetCloth != null && selectedRocks != null) {
                for (int i = 0; i < selectedRocks.size(); i++) {
                    GameObjectActionEvent event = selectedRocks.get(i);
                    GameObject targetRock = Objects.stream().at(event.getTile()).name(event.getName()).action("Mine").first();

                    Movement.step(targetRock.tile());
                    boolean nearRock = Condition.wait(() -> Players.local().tile().distanceTo(targetRock.tile()) == 1, 50, 40);
                    if (nearRock && targetRock.valid() && targetRock.actions().contains("Mine")) {
                        if (wetCloth.interact("Wipe")) {
                            Condition.sleep(ThreadLocalRandom.current().nextInt(85, 109)); // Wait towards end of wet napkin animation
                            if (tryMine(targetRock, 110)) {
                                break;
                            } else {
                                if (i + 1 < selectedRocks.size()) {
                                    GameObjectActionEvent nextEvent = selectedRocks.get(i + 1);
                                    GameObject nextRock = Objects.stream()
                                            .at(nextEvent.getTile())
                                            .name(nextEvent.getName())
                                            .action("Mine")
                                            .first();

                                    if (nextRock != null && nextRock.valid()) {
                                        Movement.moveTo(nextRock);
                                        Condition.wait(() -> Players.local().tile().distanceTo(nextRock) < 1, 40, 60);
                                        break;
                                    }
                                } else if (i + 1 >= selectedRocks.size()) {
                                    GameObjectActionEvent nextEvent = selectedRocks.get(0);

                                }
                            }
                            Movement.step(targetRock.tile());
                            return;
                        }
                    }
                }
            }
        }
//        if (!tickManipulation) {
//            if (rockRef.get() != null) {
//                Movement.step(rockRef.get());
//                boolean nearRock = Condition.wait(() -> Players.local().tile().distanceTo(rockRef.get().tile()) == 1, 50, 40);
//                if (nearRock && !tryMine(rockRef.get(), 500)) {
//                    rockRef.set(rocks.get(1));
//                    tryMine(rockRef.get(), 500);
//                }
//            }
//            return;
//        }
        //
    }
}
