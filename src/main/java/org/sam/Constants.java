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

    //Integer IDS
    public static final Integer FIRE_ID = 56370;
    public static final Integer JIM_ID = 14202;
    public static final Integer INFERNAL_SHALE_DEPOSIT_ID = 56362;
    public static final Integer INFERNAL_SHALE_DEPOSIT_DEPLEATED_ID = 56363;
    public static final Integer INFERNAL_SHALE = 30846;



    //    public static Tile TILE_CONSTANT = new Tile(0, 0, 0);
    //    public static final Integer INTEGER_CONSTANT = 31845;

}