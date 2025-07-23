package org.sam.Tasks;
import org.powbot.api.Condition;
import org.powbot.api.Random;
import org.powbot.api.Tile;
import org.powbot.api.event.GameObjectActionEvent;
import org.powbot.api.rt4.*;
import org.powbot.api.rt4.stream.TileRadius;
import org.sam.*;
import org.sam.Constants;


public class ThreeTick extends Task {
    samInfernalShale main;
    public MiningConfig config;
    Variables vars = new Variables();

    public ThreeTick(samInfernalShale main, MiningConfig config) {
        super();
        super.name = "3T Infernal Shale";
        this.main = main;
        this.config = config;
    }

    @Override
    public boolean activate() {
        return Constants.INFERNAL_SHALE_AREA.contains(Players.local())
                && !Inventory.isFull()
                && Functions.hasItem(" pickaxe")
                && Functions.hasItem(Constants.HAMMER)
                && Functions.hasItem(Constants.CHISEL)
                && Functions.hasItem(Constants.WET_CLOTH);
    }

    @Override
    public void execute() {
        GameObjectActionEvent event = config.getFirstSelectedRock();
        if (!event.getName().contains(Constants.ORE_NAME)) return;

        Functions.getTargetRock(event);
        if (!Functions.getTargetRock(event).valid()) return;

        TileRadius radius = new TileRadius(Functions.getTargetRock(event).tile(), 5);
        if (radius.getTile().distanceTo(Players.local().tile()) > 4) {
            event.getTile().matrix().interact("Walk here");
            Condition.wait(() -> radius.getTile().distanceTo(Players.local().tile()) < 1, 37, 20);
        }

        // Improved wet cloth timing with adaptive delays
        if (!Functions.getFirstInventoryItemByID(Constants.WET_CLOTH_ID).interact("Wipe")) return;

        long startTime = System.currentTimeMillis();
        int currentAnimation = Players.local().animation();

        // Calculate adaptive timing based on recent performance
        long adaptiveDelay = calculateAdaptiveDelay();
        long maxWait = adaptiveDelay + 80; // Allow some variance

        Condition.wait(() -> {
            long elapsed = System.currentTimeMillis() - startTime;
            boolean animationChanged = Players.local().animation() != currentAnimation;
            boolean validState = Players.local().animation() != -1; // Ensure animation is valid
            
            return (elapsed >= adaptiveDelay && animationChanged && validState) || elapsed >= maxWait;
        }, 10, (int)(maxWait / 10)); // Dynamic iteration limit

        // Track timing for adaptive learning
        long clothCompleteTime = System.currentTimeMillis() - startTime;
        trackClothTiming(clothCompleteTime);

        // Precise delay instead of random 50-70ms
        Condition.sleep(calculateOptimalPostClothDelay());

        if (Functions.getTargetRock(event).interact("Mine")) {
            main.vars.miningAttempts++;
            System.out.println("Mining attempt #" + main.vars.miningAttempts);
            Condition.wait(() -> Players.local().animation() == 12186, 15, 100); //What animation is this?
            config.removeSelectedRock(0);
            config.addSelectedRock(event);

            Tile nextEventTile = config.getFirstSelectedRock().getTile();
            if (nextEventTile.distanceTo(Players.local().tile()) >= 1.1) {
                nextEventTile.matrix().interact("Walk here");
                long initialCount = Inventory.stream().id(Constants.INFERNAL_SHALE).count();

                if (initialCount >= 2 && nextEventTile.matrix().distanceTo(Players.local().tile()) < 2.1) {
                    Item shale = Inventory.stream().id(Constants.INFERNAL_SHALE).last();
                    
                    // Adaptive chiseling system - replaces fixed random timing
                    performAdaptiveChiseling(shale, initialCount);
                }
            }
        }
        Condition.sleep(Random.nextInt(30, 40));
    }

    private long calculateAdaptiveDelay() {
        // Start with base delay
        long baseDelay = Constants.WET_CLOTH_BASE_DELAY;
        
        // Adjust based on recent failures
        if (main.vars.recentTimingFailures >= 1) {
            baseDelay += (main.vars.recentTimingFailures * 25); // Add 25ms per recent failure
        }
        
        // Adjust based on average successful timing
        if (main.vars.averageClothWipeTime > 0) {
            baseDelay = (baseDelay + main.vars.averageClothWipeTime) / 2; // Blend with historical data
        }
        
        // Apply ping compensation
        baseDelay += main.vars.currentPingCompensation;
        
        // Keep within bounds
        return Math.max(Constants.WET_CLOTH_MIN_WAIT, 
               Math.min(Constants.WET_CLOTH_MAX_WAIT, baseDelay));
    }

    private int calculateOptimalPostClothDelay() {
        // Instead of random 50-70ms, use calculated delay based on timing success
        int baseDelay = 45; // Slightly lower than current minimum
        
        // Adjust based on recent performance
        if (main.vars.recentTimingFailures > 0) {
            baseDelay += (main.vars.recentTimingFailures * 8); // Add time if struggling
        }
        
        // Add small randomization to avoid detection (±10ms)
        return baseDelay + Random.nextInt(-10, 10);
    }

    private void trackClothTiming(long timing) {
        // Track successful timing for adaptive learning
        main.vars.recentClothTimings.add(timing);
        
        // Keep only last 10 timings
        if (main.vars.recentClothTimings.size() > 10) {
            main.vars.recentClothTimings.remove(0);
        }
        
        // Update average
        if (!main.vars.recentClothTimings.isEmpty()) {
            main.vars.averageClothWipeTime = main.vars.recentClothTimings.stream()
                .mapToLong(Long::longValue)
                .sum() / main.vars.recentClothTimings.size();
        }
    }

    private void performAdaptiveChiseling(Item shale, long initialCount) {
        // Determine number of chiseling operations based on inventory count
        int chiselingOperations = (initialCount >= Random.nextInt(6, 8)) ? 2 : 1;
        
        System.out.println("Starting adaptive chiseling: " + chiselingOperations + " operations");
        
        // Pre-chiseling delay with adaptive timing
        int preChiselingDelay = calculateOptimalPreChiselingDelay();
        Condition.sleep(preChiselingDelay);
        
        // Perform chiseling operations
        for (int i = 0; i < chiselingOperations; i++) {
            performSingleChiselingOperation(shale, initialCount, i + 1, chiselingOperations);
        }
    }

    private void performSingleChiselingOperation(Item shale, long initialCount, int operationNumber, int totalOperations) {
        long startTime = System.currentTimeMillis();
        main.vars.chiselingAttempts++;
        
        System.out.println("Chiseling operation " + operationNumber + "/" + totalOperations + " starting...");
        
        if (Functions.getHammer().useOn(shale)) {
            // Adaptive success detection with consistent timeout
            long adaptiveTimeout = calculateAdaptiveChiselingTimeout();
            
            boolean success = Condition.wait(() -> {
                long currentCount = Inventory.stream().id(Constants.INFERNAL_SHALE).count();
                return currentCount <= initialCount - operationNumber; // Track progressive reduction
            }, 10, (int)(adaptiveTimeout / 10));
            
            long chiselingTime = System.currentTimeMillis() - startTime;
            
            if (success) {
                main.vars.chiselingSuccesses++;
                trackSuccessfulChiseling(chiselingTime);
                System.out.println("Chiseling operation " + operationNumber + " successful in " + chiselingTime + "ms");
            } else {
                main.vars.recentChiselingFailures++;
                adjustChiselingTimingAfterFailure();
                System.out.println("Chiseling operation " + operationNumber + " failed/timeout after " + chiselingTime + "ms");
            }
            
            // Inter-operation delay for multiple chiseling operations
            if (operationNumber < totalOperations) {
                int interOperationDelay = calculateInterOperationDelay();
                Condition.sleep(interOperationDelay);
            }
        } else {
            System.out.println("Failed to use hammer on shale for operation " + operationNumber);
            main.vars.recentChiselingFailures++;
        }
    }

    private int calculateOptimalPreChiselingDelay() {
        // Base delay with adaptive adjustment
        int baseDelay = Constants.CHISEL_BASE_DELAY;
        
        // Adjust based on recent chiseling failures
        if (main.vars.recentChiselingFailures > 0) {
            baseDelay += (main.vars.recentChiselingFailures * 8); // Add 8ms per recent failure
        }
        
        // Adjust based on successful timing history
        if (main.vars.averageChiselingTime > 0) {
            baseDelay = (int)((baseDelay + main.vars.averageChiselingTime) / 2);
        }
        
        // Apply ping compensation (shared with cloth timing)
        baseDelay += main.vars.currentPingCompensation;
        
        // Keep within bounds and add small randomization
        baseDelay = Math.max(Constants.CHISEL_PRE_DELAY_MIN, 
                    Math.min(Constants.CHISEL_PRE_DELAY_MAX, baseDelay));
        
        return baseDelay + Random.nextInt(-5, 5); // ±5ms randomization
    }

    private long calculateAdaptiveChiselingTimeout() {
        // Start with base timeout
        long timeout = Constants.CHISEL_SUCCESS_TIMEOUT;
        
        // Increase timeout if experiencing failures
        if (main.vars.recentChiselingFailures > 0) {
            timeout += (main.vars.recentChiselingFailures * 15); // Add 15ms per failure
        }
        
        // Adjust based on network conditions (shared ping compensation)
        timeout += main.vars.currentPingCompensation;
        
        // Ensure reasonable bounds
        return Math.max(Constants.CHISEL_MIN_WAIT, 
               Math.min(Constants.CHISEL_MAX_WAIT + 40, timeout)); // Max timeout slightly higher
    }

    private int calculateInterOperationDelay() {
        // Minimal delay between chiseling operations
        int baseDelay = 15; // Shorter than pre-chiseling delay
        
        // Slight adjustment based on recent performance
        if (main.vars.recentChiselingFailures > 0) {
            baseDelay += (main.vars.recentChiselingFailures * 3);
        }
        
        return baseDelay + Random.nextInt(-3, 3); // ±3ms randomization
    }

    private void trackSuccessfulChiseling(long chiselingTime) {
        // Track successful chiseling for adaptive learning
        main.vars.recentChiselingTimings.add(chiselingTime);
        
        // Keep only last 10 timings
        if (main.vars.recentChiselingTimings.size() > 10) {
            main.vars.recentChiselingTimings.remove(0);
        }
        
        // Update average chiseling time
        if (!main.vars.recentChiselingTimings.isEmpty()) {
            main.vars.averageChiselingTime = main.vars.recentChiselingTimings.stream()
                .mapToLong(Long::longValue)
                .sum() / main.vars.recentChiselingTimings.size();
        }
        
        // Reduce failure count on success (gradual recovery)
        main.vars.recentChiselingFailures = Math.max(0, main.vars.recentChiselingFailures - 1);
        main.vars.lastSuccessfulChiselingDelay = chiselingTime;
    }

    private void adjustChiselingTimingAfterFailure() {
        // Increase timing slightly for next attempt (similar to cloth timing adjustment)
        main.vars.averageChiselingTime += 10; // Add 10ms to base timing
        
        // Ensure we don't exceed maximum
        main.vars.averageChiselingTime = Math.min(main.vars.averageChiselingTime, Constants.CHISEL_MAX_WAIT);
        
        System.out.println("Chiseling timing adjusted due to failure. New average: " + main.vars.averageChiselingTime + "ms");
    }
}