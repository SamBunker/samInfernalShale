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

    //Integer IDS
    public static final Integer FIRE_ID = 56370;
    public static final Integer JIM_ID = 14202;
    public static final Integer INFERNAL_SHALE_DEPOSIT_ID = 56362;
    public static final Integer INFERNAL_SHALE_DEPOSIT_DEPLEATED_ID = 56363;
    public static final Integer INFERNAL_SHALE = 30846;
    public static final Integer GEM_BAG_ID = 12020;
    public static final Integer CLOTH_ANIMATION_ID = 12195;

    //    public static Tile TILE_CONSTANT = new Tile(0, 0, 0);
    //    public static final Integer INTEGER_CONSTANT = 31845;

}