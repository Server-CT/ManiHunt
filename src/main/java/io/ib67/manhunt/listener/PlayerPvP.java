package io.ib67.manhunt.listener;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.stat.GameStat;
import io.ib67.manhunt.setting.I18n;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerPvP implements Listener {
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!ManHunt.getInstance().getGame().isStarted()) {
            e.setCancelled(true);
        }
        if (e.getDamager().getType() == EntityType.PLAYER && e.getEntity().getType() == EntityType.PLAYER) {
            Player runner = ManHunt.getInstance().getGame().getRunner();
            Player damager = (Player) e.getDamager();
            Player defender = (Player) e.getEntity();
            GameStat gameStat = ManHunt.getInstance().getGame().getGameStat();
            I18n i18N = ManHunt.getInstance().getLanguage();
            int score = ManHunt.getInstance().getMainConfig().playerScores.critical;
            if (defender.equals(runner)) {
                if (e.getFinalDamage() > 7) {
                    //重拳出击！(附魔钻剑或铁，钻剑暴击)
                    gameStat.addAdvancement(damager, "CRITICAL_TO_RUNNER", score);
                    damager.sendMessage(String.format(i18N.GAMING.CRITICAL_TARGET, score));
                }
            } else if (damager.equals(runner)) {
                if (e.getFinalDamage() > 7) {
                    gameStat.addAdvancement(damager, "CRITICAL_TO_HUNTER", score);
                    damager.sendMessage(String.format(i18N.GAMING.CRITICAL_TARGET, score));
                }
            }
        }
    }
}
