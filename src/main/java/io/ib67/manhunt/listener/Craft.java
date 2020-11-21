package io.ib67.manhunt.listener;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.Game;
import io.ib67.manhunt.game.GamePhase;
import io.ib67.manhunt.game.GamePlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class Craft implements Listener {
    @EventHandler
    public void onCraft(CraftItemEvent event) {
        Game game = ManHunt.getInstance().getGame();

        if (game.getPhase() != GamePhase.STARTED ||
                game.isCompassEnabled() ||
                !(event.getWhoClicked() instanceof Player))
            return;

        if (event.getRecipe().getResult().getType() == Material.COMPASS) {
            event.setCurrentItem(new ItemStack(Material.GRASS));
            game.setCompassEnabled(game.isInGame((Player) event.getWhoClicked())
                    .filter(g -> g.getRole() == GamePlayer.Role.HUNTER)
                    .isPresent());
        }
    }
}
