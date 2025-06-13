package org.sam;

import org.powbot.api.event.GameObjectActionEvent;
import org.powbot.api.rt4.*;
import org.sam.Tasks.Config.MiningConfig;

public class Functions {
    static MiningConfig config;

    public Functions() {
        super();
    }

    public static boolean hasItem(String... name) {
        return Inventory.stream().nameContains(name).isNotEmpty() ||
                Equipment.stream().nameContains(name).isNotEmpty();
    }

    public static Item getGemBag() {
        return Inventory.stream().id(Constants.GEM_BAG_ID).first();
    }

    public static Item getWetCloth() {
        return Inventory.stream().name(Constants.WET_CLOTH).first();
    }

    public static Item getHammer() {
        return Inventory.stream().name(Constants.HAMMER).first();
    }

    public static GameObject getTargetRock(GameObjectActionEvent event) {
        return Objects.stream().at(event.getTile()).name(event.getName()).action("Mine").first();
    }

    public static GameObjectActionEvent getFirstSelectedRock() {
        return config.getSelectedRocks().get(0);
    }
}