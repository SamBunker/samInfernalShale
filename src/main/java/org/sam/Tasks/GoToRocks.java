package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.rt4.*;
import org.sam.*;

public class GoToRocks extends Task {
    samInfernalShale main;
    public MiningConfig config;

    public GoToRocks(samInfernalShale main, MiningConfig config) {
        super();
        super.name = "Walking to Rocks";
        this.main = main;
        this.config = config;
    }

    @Override
    public boolean activate() {
        return !config.getSelectedRocks().isEmpty() && config.getFirstSelectedRock().getTile().distanceTo(Players.local().tile()) >= 5;
    }

    @Override
    public void execute() {
        if (config.getFirstSelectedRock().getTile().reachable()) {
            Movement.step(config.getFirstSelectedRock().getTile());
            Condition.wait(() -> config.getFirstSelectedRock().getTile().distanceTo(Players.local().tile()) < 1, 80, 20);
        }
    }
}
