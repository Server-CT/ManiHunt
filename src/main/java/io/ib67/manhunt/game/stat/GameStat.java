package io.ib67.manhunt.game.stat;

import io.ib67.manhunt.game.GamePlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Sound;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class GameStat {
    private Map<UUID, PlayerStat> playerStats = new LinkedHashMap<>();
    @Getter
    @Setter
    private Phase gamePhase = Phase.GETTING_STARTED;
    @Getter
    @Setter
    private long totalTime;

    /**
     * @param player
     * @param advancement
     * @return -1 when player not found.
     */
    public int addAdvancement(Player player, Advancement advancement) {
        if (playerStats.containsKey(player.getUniqueId())) {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0F, 1.0F);
            return playerStats.get(player.getUniqueId()).achieve(advancement);
        } else {
            return -1; //Not Found
        }
    }

    public void addPlayer(GamePlayer player) {
        playerStats.put(player.getPlayer().getUniqueId(), new PlayerStat(player));
    }

    public void addAdvancement(Player player, String advancement, int score) {
        if (playerStats.containsKey(player.getUniqueId())) {
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 2.0F, 1.0F);
            playerStats.get(player.getUniqueId()).achieve(advancement, score);
        }
    }

    public enum Phase {
        GETTING_STARTED, IRON_ARMOR, IN_NETHER, BLAZE_ROD_GOT, FIND_STRONGHOLD, IN_END, KILLED_THE_DRAGON;
    }
}
