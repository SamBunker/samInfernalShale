package org.sam;

public class Variables {
    public Variables() {
        super();
    }

    //Integers
    public static int maxCrushAttempts = 28;
    public static int rocksMined = 0;

    //Strings
    public String currentTask = "Idle";
    public String miningMethod = getOption("Mining Method");
    Item gemBag = Inventory.stream().id(Constants.GEM_BAG_ID).first();

    //GameObjectActionEvents
    public List<GameObjectActionEvent> selectedRocks = getOption("SelectedRocks");
}