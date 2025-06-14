package org.sam.Tasks.Config;

import org.powbot.api.event.GameObjectActionEvent;

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

    public GameObjectActionEvent getFirstSelectedRock() {
        return this.getSelectedRocks().get(0);
    }

    // Optional: Utility methods for safer list manipulation
    public void addSelectedRock(GameObjectActionEvent event) {
        this.selectedRocks.add(event);
    }

    public void removeSelectedRock(int index) {
        if (index >= 0 && index < this.selectedRocks.size()) {
            this.selectedRocks.remove(index);
        }
    }
}
