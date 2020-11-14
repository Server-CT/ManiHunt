package io.ib67.manhunt.game;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.gui.Vote;
import io.ib67.manhunt.setting.I18n;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

public class Game implements Listener {
    protected List<GamePlayer> inGamePlayers = new LinkedList<>();
    private final int playersToStart;
    @Getter
    private GameResult result = GameResult.NOT_PRODUCED;
    private final Consumer<Game> gameEnd;
    private final Consumer<Game> gameStart;
    @Getter
    private String runner;
    private long startTime;
    @Getter
    private GamePhase phase = GamePhase.WAITING_FOR_PLAYER;

    public Game(int playersToStart, Consumer<Game> gameStart, Consumer<Game> gameEnd) {
        this.gameStart = gameStart;
        this.gameEnd = gameEnd;
        this.playersToStart = playersToStart;
    }

    public void start(Player runner) {
        phase = GamePhase.STARTING;
        startTime = System.currentTimeMillis();
        I18n i18n = ManHunt.get().getLanguage();
        Bukkit.broadcastMessage(i18n.gaming.VOTE_START);
        inGamePlayers.forEach(e -> {
            e.getPlayer().sendMessage(i18n.gaming.gameIntroduction);
            if (e.getPlayer().getUniqueId().equals(runner.getUniqueId())) {
                e.setRole(GamePlayer.Role.RUNNER);
                e.getPlayer().sendTitle(i18n.gaming.hunter.TITLE_MAIN,
                                        i18n.gaming.hunter.TITLE_SUB,
                                        10 * 20,
                                        20 * 20,
                                        10 * 20);
                airDrop(runner);
            } else {
                e.setRole(GamePlayer.Role.HUNTER);
                e.getPlayer().sendTitle(i18n.gaming.hunter.TITLE_MAIN,
                                        i18n.gaming.hunter.TITLE_SUB,
                                        10 * 20,
                                        20 * 20,
                                        10 * 20);
            }
        });
        gameStart.accept(this);
        phase = GamePhase.STARTED;
    }

    private void airDrop(Player runner) {
        Location loc = runner.getLocation();
        loc = new Location(loc.getWorld(), loc.getBlockX(), 0, loc.getBlockZ());
        Random random = new Random();
        loc.add(random.nextInt(200) + 100, 0, random.nextInt(200) + 100);
        loc = loc.getWorld().getHighestBlockAt(loc).getLocation();
        loc.getBlock().setType(Material.GLASS);
        loc.setY(loc.getY() + 1);
        runner.teleport(loc);
    }

    public void stop(GameResult result) {
        this.result = result;
        phase = GamePhase.END;
        String title = result == GameResult.HUNTER_WIN ?
                       ManHunt.get().getLanguage().gaming.hunter.WON :
                       ManHunt.get().getLanguage().gaming.runner.WON;
        inGamePlayers.stream().map(GamePlayer::getPlayer).forEach(p -> {
            p.setGameMode(GameMode.SPECTATOR);
            p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            p.sendTitle(title, "", 10 * 20, 20 * 20, 10 * 20);
        });
        gameEnd.accept(this);
    }

    public boolean isStarted() {
        return phase != GamePhase.WAITING_FOR_PLAYER;
    }

    public boolean joinPlayer(Player player) {
        if (isStarted()) {
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(ManHunt.get().getLanguage().gaming.SPECTATOR_RULE);
            return false;
        }
        inGamePlayers.add(GamePlayer.builder().player(player.getName()).build());
        Bukkit.broadcastMessage(String.format(ManHunt.get().getLanguage().gaming.WAITING_FOR_PLAYERS,
                                              inGamePlayers.size(),
                                              playersToStart));
        if (inGamePlayers.size() >= playersToStart)
            new Vote(inGamePlayers.stream().map(GamePlayer::getPlayer).map(Player::getUniqueId),
                     v -> start(v.getResult())).startVote();
        return true;
    }

    public void kickPlayer(String player) {
        inGamePlayers.stream()
                .filter(e -> e.getPlayer().getName().equals(player))
                .findFirst()
                .ifPresent(inGamePlayers::remove);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            //Player Died Event
            Player player = (Player) e.getEntity();
            isInGame(player).ifPresent(p -> {
                if (p.getRole() == GamePlayer.Role.RUNNER)
                    stop(GameResult.HUNTER_WIN);
            });
        } else if (e.getEntityType() == EntityType.ENDER_DRAGON)
            stop(GameResult.RUNNER_WIN);
    }

    public Optional<GamePlayer> isInGame(Player player) {
        return inGamePlayers.stream().filter(s -> s.getPlayer().equals(player)).findFirst();
    }

    public List<GamePlayer> getInGamePlayers() {
        return inGamePlayers;
    }
}
