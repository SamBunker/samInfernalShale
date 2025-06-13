package org.sam.Tasks.Config;

import org.powbot.api.event.GameObjectActionEvent;
import org.sam.Tasks.Mining;

import java.util.List;

public class MiningConfig {
    private final List<GameObjectActionEvent> selectedRocks;
    private String miningMethod;

    public MiningConfig(List<GameObjectActionEvent> selectedRocks, String miningMethod) {
        this.selectedRocks = selectedRocks;
        this.miningMethod = miningMethod;
    }

    public List<GameObjectActionEvent> getSelectedRocks() {
        return selectedRocks;
    }

    public String getMiningMethod() {
        return miningMethod;
    }

    public void setMiningMethod(String miningMethod) {
        this.miningMethod = miningMethod;
    }
}
