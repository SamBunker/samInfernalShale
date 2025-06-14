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
        return Movement.running();
    }

    @Override
    public void execute() {
        Movement.running(false);
        Condition.sleep(Random.nextInt(22, 50));
    }
}
