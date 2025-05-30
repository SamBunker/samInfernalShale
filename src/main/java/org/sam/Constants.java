package org.sam;
import org.powbot.api.Area;
import org.powbot.api.Tile;

import java.util.ArrayList;

public class Constants {

    public Constants() {
        super();
    }

    public ArrayList<String> userTaskList = new ArrayList<String>();

    public static final String CONSTANT_NAME = "Bank Chest-wreck";

    public static Tile TILE_CONSTANT = new Tile(0, 0, 0);

    public static Area AREA_CONSTANT = new Area(new Tile(0,0,0), new Tile(0,0,0));

    public static final Integer INTEGER_CONSTANT = 31845;
}