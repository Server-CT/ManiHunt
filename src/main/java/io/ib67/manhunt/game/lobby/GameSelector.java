package io.ib67.manhunt.game.lobby;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

public class GameSelector implements Listener, InventoryHolder {
    private final Inventory holdingInv;
    private final GameManager manager = ManHunt.getInstance().getGameManager();

    public GameSelector() {
        holdingInv = Bukkit.createInventory(this,
                                            9 * 9,
                                            ManHunt.getInstance().getLanguage().GAMING.GAME_SELECTOR.MAIN_TITLE);
        //todo @czm23333 游戏选择&随机分配菜单
        IntStream.range(0, 9 * 9 - 1).forEach(i -> {
            ItemStack temp = new ItemStack(Material.WHITE_WOOL);
            ItemMeta meta = temp.hasItemMeta() ?
                            temp.getItemMeta() :
                            Bukkit.getItemFactory().getItemMeta(Material.WHITE_WOOL);
            Objects.requireNonNull(meta).setDisplayName(ChatColor.GREEN + "Game " + i);
            meta.setLore(Collections.singletonList(ChatColor.WHITE + "Click to join"));
            temp.setItemMeta(meta);
            holdingInv.setItem(i, temp);
        });
        ItemStack temp = new ItemStack(Material.RED_WOOL);
        ItemMeta meta = temp.hasItemMeta() ?
                        temp.getItemMeta() :
                        Bukkit.getItemFactory().getItemMeta(Material.RED_WOOL);
        Objects.requireNonNull(meta).setDisplayName(ChatColor.RED + "Random join");
        temp.setItemMeta(meta);
        holdingInv.setItem(9 * 9 - 1, temp);

        Bukkit.getPluginManager().registerEvents(this, ManHunt.getInstance());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player &&
              event.getCurrentItem() != null &&
              event.getInventory().getHolder() == this))
            return;
        Player p = (Player) event.getWhoClicked();
        event.setCancelled(true);

        manager.join(p, event.getSlot() == 9 * 9 - 1 ?
                        IntStream.range(0, 9 * 9 - 1)
                                .mapToObj(String::valueOf)
                                .map(manager::getGame)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .filter(g -> g.getInGamePlayers()
                                                     .size() > 0)
                                .max(Comparator.comparingInt(g -> g.getInGamePlayers().size()))
                                .orElseGet(() -> manager.checkCreateGame("0")) :
                        manager.checkCreateGame(String.valueOf(event.getSlot())));

        p.closeInventory();
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if ((!(event.getPlayer() instanceof Player)) || event.getInventory().getHolder() != this)
            return;

        if (!manager.isInGame((Player) event.getPlayer()))
            openInventory((Player) event.getPlayer());
    }

    public void openInventory(Player p) {
        p.openInventory(holdingInv);
    }

    /**
     * Get the object's inventory.
     *
     * @return The inventory.
     */
    @Override
    public Inventory getInventory() {
        return holdingInv;
    }
}
