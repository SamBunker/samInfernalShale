package org.sam;
import org.powbot.api.Area;
import org.powbot.api.Tile;

import java.util.ArrayList;

public class Constants {

    public Constants() {
        super();
    }

    public ArrayList<String> userTaskList = new ArrayList<String>();

    //STRINGS
    public static final String ORE_NAME = "Infernal shale rocks";
    public static final String WET_CLOTH = "Jim's wet cloth";

    //AREAS
    public static Area INFERNAL_SHALE_AREA = new Area(new Tile(1463,10105,0), new Tile(1475,10085,0));
    public static Area CHASM_OF_FIRE_WORLD = new Area(new Tile(1424, 3680, 0), new Tile(1464, 3648, 0));
    public static Area CHASM_OF_FIRE_ENTRANCE = new Area(new Tile(1433, 3674), new Tile(1436, 3671));
    public static Area LIFT_MIDDLE_FLOOR_DOWN_AREA = new Area(new Tile(1435, 10093, 1), new Tile(1439, 10091, 1));
    public static Area LIFT_TOP_FLOOR_UP_AREA = new Area(new Tile(1436, 10096, 2), new Tile(1439, 10092, 2));
    public static Area ROPE_AREA = new Area(new Tile(1434, 10080, 2), new Tile(1435, 10076, 2));
    public static Area MIDDLE_FLOOR_SAFE_AREA = new Area(new Tile(1440, 10098, 1), new Tile(1438, 10096, 1));
    public static Area CLOSEST_BANK_AREA = new Area(new Tile(1486,3646, 0), new Tile(1483, 3651, 0));

    //Integer IDS
    public static final Integer FIRE_ID = 56370;
    public static final Integer JIM_ID = 14202;
    public static final Integer INFERNAL_SHALE_DEPOSIT_ID = 56362;
    public static final Integer INFERNAL_SHALE_DEPOSIT_DEPLEATED_ID = 56363;
    public static final Integer INFERNAL_SHALE = 30846;
    public static final Integer GEM_BAG_ID = 12020;
    public static final Integer CLOTH_ANIMATION_ID = 12195;
    public static final Integer ROPE = 30234;
    public static final Integer LIFT_TOP = 30258;
    public static final Integer LIFT_BOTTOM = 30259;


}