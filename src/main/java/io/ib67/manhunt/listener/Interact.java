package io.ib67.manhunt.listener;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.Game;
import io.ib67.manhunt.game.GamePhase;
import io.ib67.manhunt.game.GamePlayer;
import io.ib67.manhunt.util.LodestoneCompass;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Objects;
import java.util.function.BiConsumer;

public class Interact implements Listener {
    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Game game = ManHunt.getInstance().getGame();

        if (game.getPhase() != GamePhase.STARTED)
            return;


        if (event.getAction() == Action.RIGHT_CLICK_AIR ||
            event.getAction() == Action.RIGHT_CLICK_BLOCK &&
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
                    .ifPresent(i -> setItem.accept(i, LodestoneCompass.allocate(game.getRunner().getLocation())));
        }
    }
}
