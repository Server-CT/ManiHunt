package io.ib67.manhunt.radar;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.setting.I18n;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

public class SimpleRadar implements Radar {
    private final Player player;
    private final Set<Player> nearbyPlayers = new HashSet<>();
    private int warnDistance;
    private BukkitTask keeper;

    public SimpleRadar(Player player, int warnDistance) {
        this.player = player;
        this.warnDistance = warnDistance;
    }

    @Override
    public void start() {
        stop();
        keeper = Bukkit.getScheduler().runTaskTimer(ManHunt.getInstance(), () -> {
            I18n i18n = ManHunt.getInstance().getLanguage();
            if (nearbyPlayers.size() != 0) {
                TextComponent textComponent = new TextComponent(String.format(i18n.GAMING.SIMPLE_RADAR.NEAR,
                                                                              setAsStr(),
                                                                              warnDistance));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,textComponent);
            } else {
                TextComponent textComponent = new TextComponent(String.format(i18n.GAMING.SIMPLE_RADAR.SAFE,
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
                player.sendMessage(String.format(i18n.GAMING.SIMPLE_RADAR.HINT_CHAT_COMING, p.getName()));
                if (Arrays.stream(p.getInventory().getContents()).anyMatch(e -> e.getType().name().endsWith("BED"))
                        && ManHunt.getInstance().getMainConfig().enableBedHint) {
                    player.sendMessage(i18n.GAMING.SIMPLE_RADAR.HUNTER_HAS_BED);
                }
            }
        } else {
            if (nearbyPlayers.contains(p)) {
                nearbyPlayers.remove(p);
                player.sendMessage(String.format(i18n.GAMING.SIMPLE_RADAR.HINT_CHAT_LEAVE, p.getName()));
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
