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

    // 3T Mining Timing Tracking
    public int recentTimingFailures = 0;
    public long averageClothWipeTime = 230; // Adaptive timing based on success
    public long lastSuccessfulClothDelay = 230;
    public List<Long> recentClothTimings = new ArrayList<>(); // Track last 10 timings
    public int currentPingCompensation = 0;

    //Strings
    public String currentTask = "Idle";

}