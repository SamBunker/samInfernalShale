package org.sam;

import org.powbot.api.event.GameObjectActionEvent;

import java.util.List;

public class MiningConfig {
    private final List<GameObjectActionEvent> selectedRocks;
    private String miningMethod;

    public MiningConfig(List selectedRocks, String miningMethod) {
        this.selectedRocks = selectedRocks;
        this.miningMethod = miningMethod;
    }

    public List<GameObjectActionEvent> getSelectedRocks() {
        return selectedRocks;
    }

    public String getMiningMethod() {
        return miningMethod;
    }

    public GameObjectActionEvent getFirstSelectedRock() {
        List<GameObjectActionEvent> rocks = getSelectedRocks();
        if (rocks == null || rocks.isEmpty()) {
            return null;
        }
        return rocks.get(0);
    }

    public void addSelectedRock(GameObjectActionEvent event) {
        this.selectedRocks.add(event);
    }

    public void removeSelectedRock(int index) {
        if (index >= 0 && index < this.selectedRocks.size()) {
            this.selectedRocks.remove(index);
        }
    }
}