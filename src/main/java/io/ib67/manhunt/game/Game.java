package io.ib67.manhunt.game;

import com.google.common.io.Files;
import com.google.gson.Gson;
import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.region.GamingRegion;
import io.ib67.manhunt.game.region.RegionProvider;
import io.ib67.manhunt.game.stat.GameStat;
import io.ib67.manhunt.gui.Vote;
import io.ib67.manhunt.radar.Radar;
import io.ib67.manhunt.radar.SimpleRadar;
import io.ib67.manhunt.setting.I18n;
import io.ib67.manhunt.util.LodestoneCompass;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
    @Getter
    private Radar radar;
    public Vote vote;
    private GamingRegion region;

    public Game(int playersToStart, Consumer<Game> gameStart, Consumer<Game> gameEnd, RegionProvider<?> regionProvider) {
        this.gameStart = gameStart;
        this.gameEnd = gameEnd;
        this.playersToStart = playersToStart;
        this.region = regionProvider.claim(this);
        region.gameStatus(GamePhase.WAITING_FOR_PLAYER);
    }

    public void setCompassEnabled(boolean status) {
        this.compassEnabled = status;
        if (status) {
            Bukkit.broadcastMessage(ManHunt.getInstance().getLanguage().GAMING.HUNTER.UNLIMITED_COMPASS_UNLOCKED);
            inGamePlayers.stream()
                    .filter(e -> e.getRole() == GamePlayer.Role.HUNTER && !e.getPlayer().getInventory().contains(Material.COMPASS))
                    .forEach(e -> {
                        e.getPlayer().getInventory().addItem(LodestoneCompass.allocate(e.getPlayer(), runner.getLocation()));
                        e.getPlayer().sendMessage(ManHunt.getInstance().getLanguage().GAMING.HUNTER.COMPASS_ARRIVED);
                        e.getPlayer().sendMessage(ManHunt.getInstance().getLanguage().GAMING.HUNTER.UNLIMITED_COMPASS_USAGE);
                    });
        } else {
            inGamePlayers.stream()
                    .filter(e -> e.getRole() == GamePlayer.Role.HUNTER)
                    .forEach(e -> e.getPlayer().getInventory().remove(Material.COMPASS));
            Bukkit.broadcastMessage(ManHunt.getInstance().getLanguage().GAMING.HUNTER.UNLIMITED_COMPASS_LOCKED);
        }
    }

    public void start(Player runner) {
        vote = null;
        if (runner == null) {
            Bukkit.broadcastMessage("An exception was occurred! Please feedback to server admin :: ERROR: RUNNER_IS_NULL");
            Bukkit.broadcastMessage("GAME INTERRUPTED.");
            return;
        }
        phase = GamePhase.STARTING;
        region.gameStatus(phase);
        startTime = System.currentTimeMillis();
        this.runner = runner;
        I18n i18n = ManHunt.getInstance().getLanguage();
        inGamePlayers.forEach(e -> {
            gameStat.addPlayer(e);
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
            e.getPlayer().sendMessage(i18n.GAMING.GAME_INTRODUCTION);
            if (e.getPlayer().getUniqueId().equals(runner.getUniqueId())) {
                e.setRole(GamePlayer.Role.RUNNER);
                e.getPlayer().sendTitle(i18n.GAMING.RUNNER.TITLE_MAIN,
                        i18n.GAMING.RUNNER.TITLE_SUB,
                        20,
                        2 * 20,
                        20);
            } else {
                e.setRole(GamePlayer.Role.HUNTER);
                e.getPlayer().sendTitle(i18n.GAMING.HUNTER.TITLE_MAIN,
                        i18n.GAMING.HUNTER.TITLE_SUB,
                        20,
                        2 * 20,
                        20);
            }
        });
        Bukkit.broadcastMessage(String.format(i18n.GAMING.RUNNER.THE_RUNNER_IS, runner.getDisplayName()));
        initRador();
        phase = GamePhase.STARTED;
        region.gameStatus(phase); // Region handles airdrop.
        gameStart.accept(this);
    }


    private void initRador() {
        radar = new SimpleRadar(runner, ManHunt.getInstance().getMainConfig().radorWarnDistance);
        radar.start();
    }

    @SneakyThrows
    public void stop(GameResult result) {
        gameStat.setTotalTime(System.currentTimeMillis() - startTime);
        this.result = result;
        phase = GamePhase.END;
        radar.stop();
        String title = result == GameResult.HUNTER_WIN ?
                ManHunt.getInstance().getLanguage().GAMING.HUNTER.WON :
                ManHunt.getInstance().getLanguage().GAMING.RUNNER.WON;
        inGamePlayers.stream().map(GamePlayer::getPlayer).forEach(p -> {
            if (p == null) return;
            p.setGameMode(GameMode.SPECTATOR);
            //p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            p.sendTitle(title, "", 20, 2 * 20, 20);
        });
        region.gameStatus(phase);

        gameStat.readySerialization();
        String report = new Gson().toJson(gameStat);
        String statId;
        if (ManHunt.getInstance().getMainConfig().uploadStats) {
            statId = uploadReport(report);
        } else {
            statId = UUID.randomUUID().toString() + "-LOCAL";
        }
        Files.write(report.getBytes(), new File(ManHunt.getInstance().getDataFolder(), "stats/" + statId + ".json"));
        gameEnd.accept(this);
        Bukkit.broadcastMessage(ManHunt.getInstance().getLanguage().GAMING.SHUTDOWN);
        Bukkit.getScheduler().runTaskLater(ManHunt.getInstance(), Bukkit::shutdown, ManHunt.getInstance().getMainConfig().shutdownTimer * 20L);
    }

    private String uploadReport(String report) {
        //TODO
        return UUID.randomUUID().toString() + "-LOCAL";
    }

    public boolean isStarted() {
        return phase != GamePhase.WAITING_FOR_PLAYER && phase != GamePhase.STARTING;
    }

    public void joinPlayer(Player player) {
        if (isStarted()) {
            if (!isInGame(player).isPresent()) {
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(ManHunt.getInstance().getLanguage().GAMING.SPECTATOR_RULE);
                region.joinPlayer(player, true);
            }
            return;
        }
        player.setGameMode(GameMode.ADVENTURE);
        inGamePlayers.add(GamePlayer.builder().player(player.getName()).build());
        Bukkit.getOnlinePlayers().forEach(e -> e.sendTitle(String.format(ManHunt.getInstance().getLanguage().GAMING.WAITING_FOR_PLAYERS_MAINTITLE,
                inGamePlayers.size(),
                playersToStart), ManHunt.getInstance().getLanguage().GAMING.WAITING_FOR_PLAYERS_SUBTITLE, 0, 600 * 20, 0));
        if (inGamePlayers.size() >= playersToStart) {
            Bukkit.broadcastMessage(ManHunt.getInstance().getLanguage().GAMING.VOTE.VOTE_START);
            Bukkit.getOnlinePlayers().forEach(e -> e.sendTitle("", "", 0, 0, 0));//Clear
            region.joinPlayer(player, false);
            vote = new Vote(inGamePlayers.stream().map(GamePlayer::getPlayer).map(Player::getUniqueId),
                    v -> start(v.getResult()));
            Bukkit.getScheduler().runTaskLater(ManHunt.getInstance(), () -> vote.startVote(), 10);
        }
    }

    public void kickPlayer(String player) {
        inGamePlayers.stream()
                .filter(e -> e.getPlayer().getName().equals(player))
                .findFirst()
                .ifPresent(e -> {
                    region.deletePlayer(e.getPlayer());
                    inGamePlayers.remove(e);
                });
    }


    public Optional<GamePlayer> isInGame(Player player) {
        return inGamePlayers.stream().filter(s -> {
            Player playe = s.getPlayer();
            if (playe == null) return false;
            return playe.getName().equals(player.getName());
        }).findFirst();
    }

    public List<GamePlayer> getInGamePlayers() {
        return inGamePlayers;
    }
}
