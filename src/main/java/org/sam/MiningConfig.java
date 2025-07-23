package org.sam;

import org.powbot.api.event.GameObjectActionEvent;

import java.util.List;

public class MiningConfig {
    private final List<GameObjectActionEvent> selectedRocks;
    private String miningMethod;
    private String hammerType;
    private String discordWebhookUrl;

    public MiningConfig(List selectedRocks, String miningMethod, String hammerType, String discordWebhookUrl) {
        this.selectedRocks = selectedRocks;
        this.miningMethod = miningMethod;
        this.hammerType = hammerType;
        this.discordWebhookUrl = discordWebhookUrl;
    }

    public List<GameObjectActionEvent> getSelectedRocks() {
        return selectedRocks;
    }

    public String getMiningMethod() {
        return miningMethod;
    }

    public String getHammerType() {
        return hammerType;
    }

    public String getDiscordWebhookUrl() {
        return discordWebhookUrl;
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