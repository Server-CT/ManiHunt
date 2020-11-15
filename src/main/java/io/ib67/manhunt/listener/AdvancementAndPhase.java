package io.ib67.manhunt.listener;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.Game;
import io.ib67.manhunt.game.GamePlayer;
import io.ib67.manhunt.game.stat.GameStat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public class AdvancementAndPhase implements Listener {
    @EventHandler
    public void onAdv(PlayerAdvancementDoneEvent adv) {
        int score = ManHunt.getInstance().getGame().getGameStat().addAdvancement(adv.getPlayer(), adv.getAdvancement());
        if (score > 0) {
            adv.getPlayer().sendMessage(String.format(ManHunt.getInstance().getLanguage().GAMING.ARCHIVE_TARGET,
                                                      score));
        }
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        String name = event.getRecipe().getResult().getType().toString();
        if (name.contains("IRON") && name.contains("CHESTPLATE") || name.contains("BOOT") || name.contains("HELMET") || name.contains("LEGGING")) {
            if (ManHunt.getInstance().getGame().getGameStat().getGamePhase().ordinal() < GameStat.Phase.IRON_ARMOR.ordinal()) {
                ManHunt.getInstance().getGame().getGameStat().setGamePhase(GameStat.Phase.IRON_ARMOR);
            }//IN_NETHER, BLAZE_ROD_GOT, FIND_STRONGHOLD, IN_END, KILLED_THE_DRAGON;
        }
    }

    @EventHandler
    public void onUseItem(PlayerInteractEvent a) {
        if (a.getItem().getType() == Material.ENDER_EYE) {
            if (ManHunt.getInstance().getGame().getGameStat().getGamePhase().ordinal() < GameStat.Phase.FIND_STRONGHOLD.ordinal()) {
                ManHunt.getInstance().getGame().getGameStat().setGamePhase(GameStat.Phase.FIND_STRONGHOLD);
            }
        }
    }

    @EventHandler
    public void onKillDragon(EntityDeathEvent e) {
        if (e.getEntity().getType() == EntityType.ENDER_DRAGON) {
            if (ManHunt.getInstance().getGame().getGameStat().getGamePhase().ordinal() < GameStat.Phase.KILLED_THE_DRAGON.ordinal()) {
                ManHunt.getInstance().getGame().getGameStat().setGamePhase(GameStat.Phase.KILLED_THE_DRAGON);
            }
        }
    }

    @EventHandler
    public void onIntoNether(PlayerPortalEvent e) {
        Game game = ManHunt.getInstance().getGame();
        if (e.getTo().getWorld().getEnvironment() == World.Environment.NETHER) {
            if (game.isInGame(e.getPlayer()).filter(g -> g.getRole() == GamePlayer.Role.RUNNER).isPresent() &&
                !game.runnerNether) {
                game.runnerNether = true;
                Bukkit.broadcastMessage(ManHunt.getInstance().getLanguage().GAMING.RUNNER.ARRIVE_NETHER);
            }
            if (game.getGameStat().getGamePhase().ordinal() < GameStat.Phase.IN_NETHER.ordinal()) {
                game.getGameStat().setGamePhase(GameStat.Phase.IN_NETHER);
            }
        } else if (e.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
            if (game.isInGame(e.getPlayer()).filter(g -> g.getRole() == GamePlayer.Role.RUNNER).isPresent() &&
                !game.runnerEnd) {
                game.runnerEnd = true;
                Bukkit.broadcastMessage(ManHunt.getInstance().getLanguage().GAMING.RUNNER.ARRIVE_END);
            }
            if (game.getGameStat().getGamePhase().ordinal() < GameStat.Phase.IN_END.ordinal()) {
                game.getGameStat().setGamePhase(GameStat.Phase.IN_END);
            }
        }
    }
}
