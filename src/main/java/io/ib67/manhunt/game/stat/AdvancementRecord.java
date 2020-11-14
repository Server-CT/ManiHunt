package io.ib67.manhunt.game.stat;

import lombok.Builder;
import lombok.Getter;
import org.bukkit.advancement.Advancement;

@Builder
@Getter
public class AdvancementRecord {
    private final String advancement;
    private final long usedTime;
    @Builder.Default
    private final long time = System.currentTimeMillis();
    private final int score;
}
