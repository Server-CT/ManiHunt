package io.ib67.manhunt.game;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.stat.GameStat;
import io.ib67.manhunt.gui.Vote;
import io.ib67.manhunt.rador.Rador;
import io.ib67.manhunt.rador.SimpleRador;
import io.ib67.manhunt.setting.I18n;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

public class Game {
    protected List<GamePlayer> inGamePlayers = new LinkedList<>();
    private final int playersToStart;
    @Getter
    private GameResult result = GameResult.NOT_PRODUCED;
    private final Consumer<Game> gameEnd;
    private final Consumer<Game> gameStart;
    @Getter
    private Player runner;
    private long startTime;
    @Getter
    private GamePhase phase = GamePhase.WAITING_FOR_PLAYER;
    @Getter
    private final GameStat gameStat = new GameStat();
    @Getter
    private boolean compassEnabled = false;
    public boolean runnerNether = false;
    public boolean runnerEnd = false;
    private Rador rador;

    public Game(int playersToStart, Consumer<Game> gameStart, Consumer<Game> gameEnd) {
        this.gameStart = gameStart;
        this.gameEnd = gameEnd;
        this.playersToStart = playersToStart;
    }

    public void setCompassEnabled(boolean status) {
        this.compassEnabled = status;
        if (status) {
            Bukkit.broadcastMessage(ManHunt.getInstance().getLanguage().GAMING.HUNTER.UNLIMITED_COMPASS_UNLOCKED);
        } else {
            Bukkit.broadcastMessage(ManHunt.getInstance().getLanguage().GAMING.HUNTER.UNLIMITED_COMPASS_LOCKED);
        }
    }

    public void start(Player runner) {
        phase = GamePhase.STARTING;
        startTime = System.currentTimeMillis();
        this.runner = runner;
        I18n i18n = ManHunt.getInstance().getLanguage();
        Bukkit.broadcastMessage(i18n.GAMING.VOTE_START);
        inGamePlayers.forEach(e -> {
            gameStat.addPlayer(e);
            e.getPlayer().sendMessage(i18n.GAMING.GAME_INTRODUCTION);
            if (e.getPlayer().getUniqueId().equals(runner.getUniqueId())) {
                e.setRole(GamePlayer.Role.RUNNER);
                e.getPlayer().sendTitle(i18n.GAMING.RUNNER.TITLE_MAIN,
                                        i18n.GAMING.RUNNER.TITLE_SUB,
                                        10 * 20,
                                        20 * 20,
                                        10 * 20);
                airDrop(runner);
            } else {
                e.setRole(GamePlayer.Role.HUNTER);
                e.getPlayer().sendTitle(i18n.GAMING.HUNTER.TITLE_MAIN,
                                        i18n.GAMING.HUNTER.TITLE_SUB,
                                        10 * 20,
                                        20 * 20,
                                        10 * 20);
            }
        });
        initRador();
        phase = GamePhase.STARTED;
        gameStart.accept(this);
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

    private void initRador() {
        rador = new SimpleRador(runner, ManHunt.getInstance().getMainConfig().radorWarnDistance);
        rador.start();
    }

    public void stop(GameResult result) {
        gameStat.setTotalTime(System.currentTimeMillis() - startTime);
        this.result = result;
        phase = GamePhase.END;
        rador.stop();
        String title = result == GameResult.HUNTER_WIN ?
                       ManHunt.getInstance().getLanguage().GAMING.HUNTER.WON :
                       ManHunt.getInstance().getLanguage().GAMING.RUNNER.WON;
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
            player.sendMessage(ManHunt.getInstance().getLanguage().GAMING.SPECTATOR_RULE);
            return false;
        }
        inGamePlayers.add(GamePlayer.builder().player(player.getName()).build());
        Bukkit.broadcastMessage(String.format(ManHunt.getInstance().getLanguage().GAMING.WAITING_FOR_PLAYERS,
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


    public Optional<GamePlayer> isInGame(Player player) {
        return inGamePlayers.stream().filter(s -> s.getPlayer().equals(player)).findFirst();
    }

    public List<GamePlayer> getInGamePlayers() {
        return inGamePlayers;
    }
}
