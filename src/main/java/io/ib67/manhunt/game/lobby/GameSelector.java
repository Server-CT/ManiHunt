package io.ib67.manhunt.game.lobby;

import io.ib67.manhunt.ManHunt;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GameSelector implements InventoryHolder {
    private Inventory theHoldingInv;

    public GameSelector() {
        theHoldingInv = Bukkit.createInventory(this, InventoryType.CHEST, ManHunt.getInstance().getLanguage().GAMING.GAME_SELECTOR.MAIN_TITLE);
        //todo @czm23333 游戏选择&随机分配菜单
    }

    /**
     * Get the object's inventory.
     *
     * @return The inventory.
     */
    @Override
    public Inventory getInventory() {
        return theHoldingInv;
    }
}
