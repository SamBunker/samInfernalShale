package org.sam.Tasks;

import org.powbot.mobile.script.ScriptManager;
import org.sam.GemBagManager;
import org.sam.Task;
import org.sam.samInfernalShale;

public class HandleGemBag extends Task {
    samInfernalShale main;
    private final GemBagManager gemBagManager;

    public HandleGemBag(samInfernalShale main, GemBagManager gemBagManager) {
        super();
        super.name = "Handling the Gem Bag";
        this.main = main;
        this.gemBagManager = gemBagManager;
    }

    @Override
    public boolean activate() {
        return gemBagManager.anyGemFull();
    }

    @Override
    public void execute() {
        System.out.println("GemBag has a gem with 60 count. Handling gems...");
        

        ScriptManager.INSTANCE.stop();
    }
}
