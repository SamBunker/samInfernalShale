package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.sam.Task;
import org.sam.samInfernalShale;

public class TimingReset extends Task {
    samInfernalShale main;

    public TimingReset(samInfernalShale main) {
        super();
        super.name = "Resetting Timing";
        this.main = main;
    }

    @Override
    public boolean activate() {
        return main.vars.consecutiveFailures >= 3;
    }

    @Override
    public void execute() {
        System.out.println("3 consecutive failures detected - resetting timing with 2-3 second pause");
        
        // Reset the failure counter
        main.vars.consecutiveFailures = 0;
        
        // Pause for 2-3 seconds to reset timing
        Condition.sleep(Random.nextInt(2000, 3000));
        
        System.out.println("Timing reset complete - resuming mining");
    }
}