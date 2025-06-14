package org.sam;

import org.powbot.api.rt4.Equipment;
import org.powbot.api.rt4.Inventory;
import org.powbot.api.rt4.Item;

public class Functions {
    public Functions() {
        super();
    }

    public static boolean hasItem(String name) {
        return Inventory.stream().name(name).isNotEmpty() ||
                Equipment.stream().name(name).isNotEmpty();
    }

    public static Item getGemBag() {
        return Inventory.stream().id(Constants.GEM_BAG_ID).first();
    }
}