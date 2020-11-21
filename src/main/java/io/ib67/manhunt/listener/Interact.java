package io.ib67.manhunt.listener;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.Game;
import io.ib67.manhunt.game.GamePhase;
import io.ib67.manhunt.game.GamePlayer;
import io.ib67.manhunt.util.LodestoneCompass;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

public class Interact implements Listener {
    public Map<String, Location> lastLoc = new HashMap<>();
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Game game = ManHunt.getInstance().getGame();

        if (game.getPhase() != GamePhase.STARTED)
            return;


        if ((event.getAction() == Action.RIGHT_CLICK_AIR ||
                event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                event.hasItem() &&
                Objects.requireNonNull(event.getItem()).getType() == Material.COMPASS) {
            event.setCancelled(true);
            final BiConsumer<PlayerInventory, ItemStack> setItem = event.getHand() == EquipmentSlot.HAND ?
                    PlayerInventory::setItemInMainHand :
                    PlayerInventory::setItemInOffHand;
            game.isInGame(event.getPlayer())
                    .filter(g -> g.getRole() == GamePlayer.Role.HUNTER)
                    .map(GamePlayer::getPlayer)
                    .map(Player::getInventory)
                    .ifPresent(i -> {
                        Player runner = game.getRunner();
                        TextComponent actBarMsg = new TextComponent(String.format(ManHunt.getInstance().getLanguage().GAMING.HUNTER.ACTION_BAR_RADOR, runner.getDisplayName()));
                        if (event.getPlayer().getWorld() == runner.getLocation().getWorld()) {
                            if (runner.getLocation().distance(event.getPlayer().getLocation()) >= ManHunt.getInstance().getMainConfig().distanceFar) {
                                actBarMsg.addExtra(" "+String.format(ManHunt.getInstance().getLanguage().GAMING.HUNTER.ACTION_BAR_RADOR_PART_FAR, ManHunt.getInstance().getMainConfig().distanceFar));
                            }
                            setItem.accept(i, LodestoneCompass.allocate(runner.getLocation()));
                            event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, actBarMsg);
                        } else {
                            if (ManHunt.getInstance().getMainConfig().blockCompassWhenDifferentWorld) {
                                event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ManHunt.getInstance().getLanguage().GAMING.HUNTER.FAILED_TO_TRACK));
                                setItem.accept(i, new ItemStack(Material.COMPASS));
                            } else {
                                if (lastLoc.containsKey(event.getPlayer().getWorld().getName())) {
                                    Location loc = lastLoc.get(event.getPlayer().getWorld().getName());
                                    setItem.accept(i, LodestoneCompass.allocate(loc));
                                    if (loc.distance(event.getPlayer().getLocation()) >= ManHunt.getInstance().getMainConfig().distanceFar) {
                                        actBarMsg.addExtra(String.format(ManHunt.getInstance().getLanguage().GAMING.HUNTER.ACTION_BAR_RADOR_PART_FAR, ManHunt.getInstance().getMainConfig().distanceFar));
                                    }
                                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, actBarMsg);
                                } else {
                                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ManHunt.getInstance().getLanguage().GAMING.HUNTER.WARN_RUNNER_NOT_ENTERED));
                                }
                            }
                        }


                    });
        }
    }

    @EventHandler
    public void onMove(PlayerPortalEvent event) {
        Game game = ManHunt.getInstance().getGame();
        game.isInGame(event.getPlayer())
                .filter(g -> g.getRole() == GamePlayer.Role.RUNNER)
                .ifPresent(g -> {
                    lastLoc.put(event.getFrom().getWorld().getName(), event.getFrom());
                });

    }
}
