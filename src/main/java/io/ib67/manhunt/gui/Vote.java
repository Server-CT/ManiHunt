package io.ib67.manhunt.gui;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.setting.I18n;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Vote implements Listener, InventoryHolder {
    @Getter
    private final LinkedList<UUID> shouldVote;
    private final LinkedList<UUID> voted = new LinkedList<>();
    private final TreeMap<UUID, Integer> voteMap = new TreeMap<>();
    @Getter
    private final Inventory voteInv;
    private final Consumer<Vote> callback;

    public Vote(List<UUID> shouldVote, Consumer<Vote> callback) {
        this.shouldVote = new LinkedList<>(shouldVote);
        this.callback = callback;
        this.voteInv = Bukkit.createInventory(this,
                (this.shouldVote.size() / 9 + (this.shouldVote.size() % 9 == 0 ? 0 : 1)) *
                        9,
                ManHunt.getInstance().getLanguage().GAMING.VOTE.VOTE_TITLE);
        initInventory();
    }

    public Vote(Stream<UUID> shouldVote, Consumer<Vote> callback) {
        this.shouldVote = shouldVote.collect(Collectors.toCollection(LinkedList<UUID>::new));
        this.callback = callback;
        this.voteInv = Bukkit.createInventory(this,
                (this.shouldVote.size() / 9 + (this.shouldVote.size() % 9 == 0 ? 0 : 1)) *
                        9,
                ManHunt.getInstance().getLanguage().GAMING.VOTE.VOTE_TITLE);
        initInventory();
    }

    @SuppressWarnings("deprecated")
    private void initInventory() {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) Objects.requireNonNull(head.getItemMeta());
        for (UUID uuid : shouldVote) {
            meta.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
            meta.setDisplayName(String.format(ManHunt.getInstance().getLanguage().GAMING.VOTE.VOTE_ITEM_FORMAT, meta.getOwner()));
            head.setItemMeta(meta);
            voteInv.addItem(head.clone());
        }
    }

    public Inventory getInventory() {
        return voteInv;
    }

    public void startVote() {
        Bukkit.getPluginManager().registerEvents(this, ManHunt.getInstance());
        shouldVote.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).forEach(p -> p.openInventory(voteInv));
    }

    public void endVote() {
        InventoryClickEvent.getHandlerList().unregister(this);
    }

    public void vote(Player from, OfflinePlayer to) {
        UUID fromUUID = from.getUniqueId();
        I18n i18n = ManHunt.getInstance().getLanguage();
        if (!shouldVote.contains(fromUUID)) {
            from.sendMessage(i18n.GAMING.VOTE.SHOULD_NOT_VOTE);
            return;
        }
        if (voted.contains(fromUUID)) {
            from.sendMessage(i18n.GAMING.VOTE.ALREADY_VOTED);
            return;
        }

        voteMap.put(to.getUniqueId(), voteMap.containsKey(to.getUniqueId()) ? voteMap.get(to.getUniqueId()) + 1 : 1);

        voted.add(fromUUID);
        from.sendMessage(String.format(i18n.GAMING.VOTE.VOTE_SUCCEED, Objects.requireNonNull(to).getName()));

        Bukkit.broadcastMessage(String.format(i18n.GAMING.VOTE.VOTING, voted.size(), shouldVote.size()));
        if (allVoted()) {
            callback.accept(this);
            endVote();
        }
    }

    private boolean allVoted() {
        return voted.size() == shouldVote.size();
    }

    public Player getResult() {
        return Bukkit.getPlayer(voteMap.entrySet()
                                        .stream()
                                        .max(Map.Entry.comparingByValue())
                                        .orElseThrow(() -> new IllegalStateException("Impossible null"))
                                        .getKey());
    }
    @SuppressWarnings("deprecated")
    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player &&
              event.getCurrentItem() != null &&
              event.getInventory().getHolder() instanceof Vote))
            return;
        Player p = (Player) event.getWhoClicked();
        ItemStack itemClicked = event.getCurrentItem();
        event.setCancelled(true);
        if (itemClicked.getType() != Material.PLAYER_HEAD)
            return;

        SkullMeta meta = (SkullMeta) itemClicked.getItemMeta();
        vote(p, Bukkit.getPlayer(Objects.requireNonNull(Objects.requireNonNull(meta).getOwner())));

        p.closeInventory();
    }
}
