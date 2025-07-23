package org.sam;
import org.powbot.api.Area;
import org.powbot.api.Tile;

public class Constants {

    public Constants() {
        super();
    }

    //STRINGS
    public static final String ORE_NAME = "Infernal shale rocks";
    public static final String WET_CLOTH = "Jim's wet cloth";
    public static final String HAMMER = "Hammer";
    public static final String CHISEL = "Chisel";

    //AREAS
    public static Area INFERNAL_SHALE_AREA = new Area(new Tile(1463,10105,0), new Tile(1475,10085,0));

    //Integer IDS
    public static final Integer JIM_ID = 14202;
    public static final Integer INFERNAL_SHALE_DEPOSIT_ID = 56362;
    public static final Integer INFERNAL_SHALE_DEPOSIT_DEPLEATED_ID = 56363;
    public static final Integer INFERNAL_SHALE = 30846;
    public static final Integer CRUSHED_INFERNAL_SHALE = 30848;
    public static final Integer GEM_BAG_ID = 12020;
    public static final Integer WET_CLOTH_ID = 30808;

    // 3T Mining Timing Constants
    public static final int WET_CLOTH_MIN_WAIT = 180; // Minimum wait after wiping
    public static final int WET_CLOTH_MAX_WAIT = 320; // Maximum wait after wiping  
    public static final int WET_CLOTH_BASE_DELAY = 230; // Base delay (current: 280ms fixed)
    public static final int TIMING_ADJUSTMENT_THRESHOLD = 2; // Failures before adjustment
    public static final int PING_COMPENSATION_MULTIPLIER = 15; // Ping * this value added to delays

}