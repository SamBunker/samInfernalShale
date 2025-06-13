package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.rt4.Movement;
import org.sam.Task;
import org.sam.samInfernalShale;

public class Running extends Task {
    samInfernalShale main;

    public Running(samInfernalShale main) {
        super();
        super.name = "Setting Run";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return !Movement.running() && Movement.energyLevel() > Random.nextInt(13, 16);
    }

    @Override
    public void execute() {
        Movement.running(true);
        Condition.wait(Movement::running, 80, 10);
    }
}
