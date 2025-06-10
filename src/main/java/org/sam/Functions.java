package org.sam;

public class Functions {
    public Functions() {
        super();
    }

    public boolean hasItem(String name) {
        return Inventory.stream().name(name).isNotEmpty() ||
                Equipment.stream().name(name).isNotEmpty();
    }

    public static Item getGemBag() {
        return Inventory.stream().id(Constants.GEM_BAG_ID).first();
    }
}