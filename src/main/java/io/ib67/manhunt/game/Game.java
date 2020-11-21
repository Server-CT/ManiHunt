package io.ib67.manhunt.game;

import com.google.common.io.Files;
import com.google.gson.Gson;
import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.stat.GameStat;
import io.ib67.manhunt.gui.Vote;
import io.ib67.manhunt.rador.Rador;
import io.ib67.manhunt.rador.SimpleRador;
import io.ib67.manhunt.setting.I18n;
import io.ib67.manhunt.util.LodestoneCompass;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
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
    private Rador rador;
    public Vote vote;

    public Game(int playersToStart, Consumer<Game> gameStart, Consumer<Game> gameEnd) {
        this.gameStart = gameStart;
        this.gameEnd = gameEnd;
        this.playersToStart = playersToStart;
        Bukkit.getWorld("world").setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        Bukkit.getWorld("world").setDifficulty(Difficulty.PEACEFUL);
    }

    public void setCompassEnabled(boolean status) {
        this.compassEnabled = status;
        if (status) {
            Bukkit.broadcastMessage(ManHunt.getInstance().getLanguage().GAMING.HUNTER.UNLIMITED_COMPASS_UNLOCKED);
            inGamePlayers.stream()
                    .filter(e -> e.getRole() == GamePlayer.Role.HUNTER && !e.getPlayer().getInventory().contains(Material.COMPASS))
                    .forEach(e -> {
                        e.getPlayer().getInventory().addItem(LodestoneCompass.allocate(runner.getLocation()));
                        e.getPlayer().sendMessage(ManHunt.getInstance().getLanguage().GAMING.HUNTER.COMPASS_ARRIVED);
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
        Bukkit.broadcastMessage(ChatColor.GREEN+"Runner: "+runner.getDisplayName()+" !");
        Bukkit.getWorld("world").setDifficulty(Difficulty.valueOf(ManHunt.getInstance().getMainConfig().difficulty));
        Bukkit.getWorld("world").setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        phase = GamePhase.STARTING;
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
                airDrop(runner);
            } else {
                e.setRole(GamePlayer.Role.HUNTER);
                e.getPlayer().sendTitle(i18n.GAMING.HUNTER.TITLE_MAIN,
                        i18n.GAMING.HUNTER.TITLE_SUB,
                        20,
                        2 * 20,
                        20);
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

    @SneakyThrows
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
            p.sendTitle(title, "", 20, 2 * 20, 20);
        });
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
        Bukkit.getScheduler().runTaskLater(ManHunt.getInstance(), Bukkit::shutdown, 30 * 20L);
    }

    private String uploadReport(String report) {
        //TODO
        return "ID_NOT_IMPLEMENTED";
    }

    public boolean isStarted() {
        return phase != GamePhase.WAITING_FOR_PLAYER && phase != GamePhase.STARTING;
    }

    public boolean joinPlayer(Player player) {
        if (isStarted()) {
            if (!isInGame(player).isPresent()) {
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage(ManHunt.getInstance().getLanguage().GAMING.SPECTATOR_RULE);
                return false;
            } else {
                return true;
            }
        }
        player.setGameMode(GameMode.ADVENTURE);
        inGamePlayers.add(GamePlayer.builder().player(player.getName()).build());
        Bukkit.getOnlinePlayers().forEach(e -> e.sendTitle(String.format(ManHunt.getInstance().getLanguage().GAMING.WAITING_FOR_PLAYERS_MAINTITLE,
                inGamePlayers.size(),
                playersToStart), ManHunt.getInstance().getLanguage().GAMING.WAITING_FOR_PLAYERS_SUBTITLE, 0, 600 * 20, 0));
        if (inGamePlayers.size() >= playersToStart) {
            Bukkit.broadcastMessage(ManHunt.getInstance().getLanguage().GAMING.VOTE.VOTE_START);
            Bukkit.getOnlinePlayers().forEach(e -> e.sendTitle("", "", 0, 0, 0));//Clear
            vote = new Vote(inGamePlayers.stream().map(GamePlayer::getPlayer).map(Player::getUniqueId),
                    v -> start(v.getResult()));
            Bukkit.getScheduler().runTaskLater(ManHunt.getInstance(), () -> vote.startVote(), 10);
        }
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
