package io.ib67.manhunt.game;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.stat.GameStat;
import io.ib67.manhunt.game.stat.PlayerStat;
import io.ib67.manhunt.gui.Vote;
import io.ib67.manhunt.setting.I18N;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.*;
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
    private final GameStat gameStat = new GameStat();

    public Game(int playersToStart, Consumer<Game> gameStart, Consumer<Game> gameEnd) {
        this.gameStart = gameStart;
        this.gameEnd = gameEnd;
        this.playersToStart = playersToStart;
    }

    public void start(Player runner) {
        phase = GamePhase.STARTING;
        startTime = System.currentTimeMillis();
        Bukkit.broadcastMessage(ManHunt.get().getLanguage().gaming.VOTE_START); //todo move
        I18N i18n = ManHunt.get().getLanguage();
        inGamePlayers.forEach(e -> {
            gameStat.addPlayer(e);
            e.getPlayer().sendMessage(i18n.gaming.gameIntroduction);
            if (e.getPlayer().getUniqueId().equals(runner)) {
                e.setRole(GamePlayer.Role.RUNNER);
                e.getPlayer().sendTitle(i18n.gaming.hunter.TITLE_MAIN, i18n.gaming.hunter.TITLE_SUB, 10 * 20, 20 * 20, 10 * 20);
                airDrop(runner);
            } else {
                e.setRole(GamePlayer.Role.HUNTER);
                e.getPlayer().sendTitle(i18n.gaming.hunter.TITLE_MAIN, i18n.gaming.hunter.TITLE_SUB, 10 * 20, 20 * 20, 10 * 20);
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
        //TODO
        gameStat.setTotalTime(System.currentTimeMillis() - startTime);
        this.result = result;
        phase = GamePhase.END;
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
        Bukkit.broadcastMessage(
                ManHunt.get().getLanguage().gaming.WAITING_FOR_PLAYERS
                        .replaceAll("%s", inGamePlayers.size() + "")
                        .replaceAll("%s", playersToStart + "")
        );
        if (inGamePlayers.size() >= playersToStart) {
            new Vote(inGamePlayers.stream().map(e -> e.getPlayer().getUniqueId()), v -> start(v.getResult())).startVote();
        }
        return true;
    }

    public void kickPlayer(String player) {
        inGamePlayers.stream().filter(e -> e.getPlayer().getName().equals(player)).findFirst().ifPresent(inGamePlayers::remove);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            //Player Died Event
            Player player = (Player) e.getEntity();
            isInGame(player.getName()).ifPresent(p -> {
                if (p.getRole() == GamePlayer.Role.RUNNER)
                    stop(GameResult.HUNTER_WIN);
            });
            return;
        } else if (e.getEntityType() == EntityType.ENDER_DRAGON)
            stop(GameResult.RUNNER_WIN);
    }

    public Optional<GamePlayer> isInGame(String name) {
        return inGamePlayers.stream().filter(s -> s.getPlayer().getName().equals(name)).findFirst();
    }

    private String vote() {
        //todo
        return null;
    }
}
