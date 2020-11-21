package io.ib67.manhunt.rador;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.setting.I18n;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class SimpleRador implements Rador {
    private final Player player;
    private final Set<Player> nearbyPlayers = new HashSet<>();
    private int warnDistance;
    private BukkitTask keeper;

    public SimpleRador(Player player, int warnDistance) {
        this.player = player;
        this.warnDistance = warnDistance;
    }

    @Override
    public void start() {
        stop();
        keeper = Bukkit.getScheduler().runTaskTimer(ManHunt.getInstance(), () -> {
            I18n i18n = ManHunt.getInstance().getLanguage();
            if (nearbyPlayers.size() != 0) {
                TextComponent textComponent = new TextComponent(String.format(i18n.GAMING.SIMPLE_RADOR.NEAR,
                                                                              setAsStr(),
                                                                              warnDistance));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,textComponent);
            } else {
                TextComponent textComponent = new TextComponent(String.format(i18n.GAMING.SIMPLE_RADOR.SAFE,
                                                                              warnDistance));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,textComponent);
            }

        }, 0L, 10L); //0.5s
    }

    @Override
    public void stop() {
        if (keeper != null && !keeper.isCancelled()) keeper.cancel();
    }

    private String setAsStr() {
        CharSequence separator;
        StringJoiner sb = new StringJoiner(", ");
        nearbyPlayers.forEach(e -> sb.add(e.getDisplayName()));
        return sb.toString();
    }

    @Override
    public void onMove(Player p) {
        if (p == player) {
            return;
        }
        I18n i18n = ManHunt.getInstance().getLanguage();
        if (p.getLocation().getWorld() != player.getLocation().getWorld()) {
            return;
        }
        double distance = player.getLocation().distance(p.getLocation());
        if (distance <= warnDistance) {
            if (!nearbyPlayers.contains(p)) {
                nearbyPlayers.add(p);
                player.sendMessage(String.format(i18n.GAMING.SIMPLE_RADOR.HINT_CHAT_COMING, p.getName()));
            }
        } else {
            if (nearbyPlayers.contains(p)) {
                nearbyPlayers.add(p);
                player.sendMessage(String.format(i18n.GAMING.SIMPLE_RADOR.HINT_CHAT_LEAVE, p.getName()));
            }
        }
    }

    @Override
    public Set<Player> getNearbyList() {
        return nearbyPlayers;
    }

    @Override
    public void setWarnDistance(int i) {
        this.warnDistance = i;
    }
}
