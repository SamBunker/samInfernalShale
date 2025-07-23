package org.sam;

import java.util.ArrayList;
import java.util.List;

public class Variables {
    public Variables() {
        super();
    }

    //Integers
    public static int maxCrushAttempts = 28;
    public int rocksMined = 0;
    public int consecutiveFailures = 0;
    public int totalMissedRocks = 0;
    public int miningAttempts = 0;
    public int crushedShaleObtained = 0;
    public int initialCrushedShaleCount = 0;
    public int successfulInteractionsFailed = 0; // Tracks successful rock interactions that failed to mine
    public long scriptStartTime = 0; // Track when script started for profit per hour calculation
    public List<Long> recentProfitTimestamps = new ArrayList<>(); // Track timestamps for profit calculation
    public List<Integer> recentProfitAmounts = new ArrayList<>(); // Track profit amounts for averaging

    // 3T Mining Timing Tracking
    public int recentTimingFailures = 0;
    public long averageClothWipeTime = 230; // Adaptive timing based on success
    public long lastSuccessfulClothDelay = 230;
    public List<Long> recentClothTimings = new ArrayList<>(); // Track last 10 timings
    public int currentPingCompensation = 0;


    // 3T Mining Initialization State Tracking
    public boolean threeTMiningInitialized = false; // Track if 3T mining session is properly initialized
    public boolean initialPositioningComplete = false; // Track if initial rock positioning is done
    public boolean runStateDisabledForMining = false; // Track if run was disabled for 3T mining

    //Strings
    public String currentTask = "Idle";

}