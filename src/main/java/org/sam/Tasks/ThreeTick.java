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
                && Functions.hasItem(Constants.WET_CLOTH);
    }

    @Override
    public void execute() {
        // Initialize 3T mining session if needed
        if (!main.vars.threeTMiningInitialized) {
            if (!initialize3TMiningSession()) {
                return; // Initialization not complete, try again next tick
            }
        }
        
        // Proceed with normal 3T mining cycle
        execute3TMiningCycle();
    }
    
    private boolean initialize3TMiningSession() {
        System.out.println("Initializing 3T mining session...");
        
        // Step 1: Validate rock selection and get target
        GameObjectActionEvent event = config.getFirstSelectedRock();
        if (!event.getName().contains(Constants.ORE_NAME)) {
            System.out.println("Invalid rock selection during initialization");
            return false;
        }

        Functions.getTargetRock(event);
        if (!Functions.getTargetRock(event).valid()) {
            System.out.println("Target rock not valid during initialization");
            return false;
        }
        
        // Step 2: Enable run for efficient movement to first rock
        if (!main.vars.initialPositioningComplete) {
            return performInitialPositioning(event);
        }
        
        // Step 3: Disable run for precise 3T mining
        if (main.vars.initialPositioningComplete && !main.vars.runStateDisabledForMining) {
            return disableRunForMining();
        }
        
        // Step 4: Mark initialization complete
        main.vars.threeTMiningInitialized = true;
        System.out.println("3T mining session initialized successfully!");
        return true;
    }
    
    private boolean performInitialPositioning(GameObjectActionEvent event) {
        System.out.println("Performing initial positioning to first rock...");
        
        // Enable run for efficient movement
        if (!Movement.running()) {
            Movement.running(true);
            System.out.println("Run enabled for initial positioning");
        }
        
        TileRadius radius = new TileRadius(Functions.getTargetRock(event).tile(), 5);
        double currentDistance = radius.getTile().distanceTo(Players.local().tile());
        
        // Move to optimal position (within 2 tiles for precise 3T mining)
        if (currentDistance > 2.0) {
            event.getTile().matrix().interact("Walk here");
            System.out.println("Moving to first rock, current distance: " + String.format("%.1f", currentDistance));
            
            // Wait for movement completion with timeout
            boolean positionReached = Condition.wait(() -> {
                double newDistance = new TileRadius(Functions.getTargetRock(event).tile(), 5)
                    .getTile().distanceTo(Players.local().tile());
                return newDistance <= 2.0;
            }, 50, 100); // 5 second timeout
            
            if (!positionReached) {
                System.out.println("Failed to reach optimal position, trying again...");
                return false; // Try again next tick
            }
        }
        
        main.vars.initialPositioningComplete = true;
        System.out.println("Initial positioning complete! Distance: " + 
            String.format("%.1f", radius.getTile().distanceTo(Players.local().tile())));
            
        return false; // Continue initialization next tick (disable run step)
    }
    
    private boolean disableRunForMining() {
        System.out.println("Disabling run for precise 3T mining...");
        
        if (Movement.running()) {
            Movement.running(false);
            System.out.println("Run disabled for 3T mining precision");
            
            // Small delay to ensure run state change is registered
            Condition.sleep(50);
        }
        
        main.vars.runStateDisabledForMining = true;
        return false; // Complete initialization next tick
    }
    
    private void execute3TMiningCycle() {
        GameObjectActionEvent event = config.getFirstSelectedRock();
        if (!event.getName().contains(Constants.ORE_NAME)) return;

        Functions.getTargetRock(event);
        if (!Functions.getTargetRock(event).valid()) return;

        // Fine positioning check (should already be positioned from initialization)
        TileRadius radius = new TileRadius(Functions.getTargetRock(event).tile(), 5);
        if (radius.getTile().distanceTo(Players.local().tile()) > 3) {
            System.out.println("Distance too great during mining cycle, re-positioning...");
            event.getTile().matrix().interact("Walk here");
            Condition.wait(() -> radius.getTile().distanceTo(Players.local().tile()) < 2, 25, 40);
        }

        // Begin the cloth wipe cycle (timing-critical section)
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
                
                // Chisel while walking to optimize tick usage
                performChiselingWhileWalking();
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

    private void performChiselingWhileWalking() {
        // Only chisel if we have shale to chisel and the necessary tools
        long shaleCount = Inventory.stream().id(Constants.INFERNAL_SHALE).count();
        if (shaleCount < 2 || !Functions.hasItem(Constants.HAMMER)) {
            System.out.println("Skipping chiseling while walking - insufficient shale or no hammer");
            return;
        }

        System.out.println("Starting chiseling while walking to next rock...");
        
        // Perform 1-2 chiseling operations while walking
        int chiselingOperations = (shaleCount >= 4) ? 2 : 1;
        
        for (int i = 0; i < chiselingOperations; i++) {
            performSingleChiselingOperation(i + 1, chiselingOperations);
            
            // Small delay between operations if doing multiple
            if (i < chiselingOperations - 1) {
                Condition.sleep(Random.nextInt(15, 25));
            }
        }
        
        System.out.println("Completed " + chiselingOperations + " chiseling operations while walking");
    }

    private void performSingleChiselingOperation(int operationNumber, int totalOperations) {
        Item shale = Inventory.stream().id(Constants.INFERNAL_SHALE).last();
        if (shale == null || !shale.valid()) {
            System.out.println("No valid shale found for chiseling operation " + operationNumber);
            return;
        }

        long startTime = System.currentTimeMillis();
        long initialShaleCount = Inventory.stream().id(Constants.INFERNAL_SHALE).count();
        
        System.out.println("Chiseling operation " + operationNumber + "/" + totalOperations + " starting while walking...");
        
        if (Functions.getHammer().useOn(shale)) {
            // Wait for chiseling success with a reasonable timeout
            boolean success = Condition.wait(() -> {
                long currentCount = Inventory.stream().id(Constants.INFERNAL_SHALE).count();
                return currentCount < initialShaleCount; // Check if shale was reduced
            }, 10, 15); // 150ms timeout (15 iterations * 10ms)
            
            long chiselingTime = System.currentTimeMillis() - startTime;
            
            if (success) {
                System.out.println("Chiseling operation " + operationNumber + " successful in " + chiselingTime + "ms while walking");
            } else {
                System.out.println("Chiseling operation " + operationNumber + " failed/timeout after " + chiselingTime + "ms while walking");
            }
        } else {
            System.out.println("Failed to use hammer on shale for operation " + operationNumber + " while walking");
        }
    }







}