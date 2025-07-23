package org.sam.Tasks;

import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.sam.Constants;
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
        System.out.println("Performing intelligent timing reset...");
        
        // Reset adaptive variables
        main.vars.recentTimingFailures = 0;
        main.vars.averageClothWipeTime = Constants.WET_CLOTH_BASE_DELAY; // Reset to base
        main.vars.recentClothTimings.clear();
        
        // Dynamic pause based on failure severity
        int pauseTime = 1500 + (main.vars.consecutiveFailures * 200); // 1.5s + 200ms per failure
        pauseTime = Math.min(pauseTime, 4000); // Max 4 seconds
        
        System.out.println("3+ consecutive failures detected - intelligent reset with " + pauseTime + "ms pause");
        Condition.sleep(pauseTime);
        
        // Reset the failure counter
        main.vars.consecutiveFailures = 0;
        
        System.out.println("Timing reset complete. Adaptive timing restored.");
    }
}