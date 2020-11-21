package io.ib67.manhunt;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.ib67.manhunt.event.HuntEndEvent;
import io.ib67.manhunt.event.HuntStartedEvent;
import io.ib67.manhunt.game.Game;
import io.ib67.manhunt.game.stat.PlayerStat;
import io.ib67.manhunt.gui.Vote;
import io.ib67.manhunt.listener.*;
import io.ib67.manhunt.setting.I18n;
import io.ib67.manhunt.setting.MainConfig;
import io.ib67.manhunt.util.SimpleConfig;
import lombok.Getter;
import net.md_5.bungee.chat.TranslationRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.stream.StreamSupport;

public final class ManHunt extends JavaPlugin {
    private static ManHunt instance;
    public static boolean debug = false;
    private final SimpleConfig<MainConfig> mainConfig = new SimpleConfig<>(getDataFolder(), MainConfig.class);
    private final SimpleConfig<I18n> language = new SimpleConfig<>(getDataFolder(), I18n.class);
    @Getter
    private final Map<String, String> mojangLocales = new HashMap<>();
    @Getter
    private Game game;
    private final String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static final JsonParser jsonParser = new JsonParser();

    public static ManHunt getInstance() {
        return instance;
    }

    public MainConfig getMainConfig() {
        return mainConfig.get();
    }

    public I18n getLanguage() {
        return language.get();
    }

    @Override
    public void onEnable() {
        Logging.info("Loading...");
        getDataFolder().mkdirs();
        new File(getDataFolder(), "stats").mkdirs();
        instance = this;
        mainConfig.saveDefault();
        mainConfig.reloadConfig();
        Logging.info("Loading Language: " + mainConfig.get().serverLanguage);
        loadLanguages();
        debug = mainConfig.get().verbose;
        if (debug) {
            Logging.warn("VERBOSE MODE ON.IF YOU DON'T KNOW WHAT IS IT,PLEASE SHUT IT DOWN IN YOUR CONFIG.");
        }
        if (getLanguage().version != I18n.VERSION) {
            Logging.warn("Language file OUTDATED! If you're using translated locale file,please update it.");
            Logging.warn("Now using default settings.");
            language.set(new I18n());
        }
        loadMojangLocale();
        game = new Game(mainConfig.get().maxPlayers,
                        g -> Bukkit.getPluginManager().callEvent(new HuntStartedEvent(g)),
                        g -> Bukkit.getPluginManager().callEvent(new HuntEndEvent(g)));
        loadAdditions();
        loadListeners();
        Logging.info("ManHunt Started! We're waiting for more players.");
    }

    private void loadLanguages() {
        language.setConfigFileName("locale/" + mainConfig.get().serverLanguage + ".json");
        language.saveDefault();
        language.reloadConfig();

    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new AdvancementAndPhase(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerPvP(), this);
        Bukkit.getPluginManager().registerEvents(new Chat(), this);
        Bukkit.getPluginManager().registerEvents(new Craft(), this);
        Bukkit.getPluginManager().registerEvents(new JoinAndLeave(), this);
        Bukkit.getPluginManager().registerEvents(new Death(), this);
        Bukkit.getPluginManager().registerEvents(new Interact(), this);
        Bukkit.getPluginManager().registerEvents(new Respawn(), this);
    }

    private void loadAdditions() {
        //todo
    }

    //@SneakyThrows
    private void loadMojangLocale() {
        if (!(getDataFolder().isDirectory() || getDataFolder().mkdirs()))
            throw new RuntimeException("Invalid data folder.");

        File lang = new File(getDataFolder(), getMainConfig().serverLanguage.toLowerCase() + ".cache");
        if (lang.exists()) {
            Properties properties = new Properties();
            try (InputStream inputStream = new FileInputStream(lang)) {
                properties.load(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                Logging.warn("Failed to load cache.Using spigot-local");
                loadMojangLocaleLocal();
                return;
            }

            Consumer<String> func = e -> {
                String name = properties.getProperty("advancements." + e.replaceAll("/", "\\.") + ".title");
                mojangLocales.put(e, name);
                Logging.debug(e + " -> " + name);
            };
            PlayerStat.mentionedNormal.forEach(func);
            PlayerStat.mentionedSpecial.forEach(func);
            Logging.info("MojangLocales loaded.");
        } else {
            try {
                final String defaultBase = "https://launchermeta.mojang.com/";
                final String baseURL = getMainConfig().mojangServers.launchmetaBaseUrl;
                final String hash = jsonParser.parse(new InputStreamReader(new URL(jsonParser.parse(new InputStreamReader(
                        new URL(StreamSupport.stream(jsonParser.parse(new InputStreamReader(new URL(baseURL +
                                                                                                    "mc/game/version_manifest.json")
                                                                                                    .openStream()))
                                                             .getAsJsonObject()
                                                             .getAsJsonArray("versions")
                                                             .spliterator(), false)
                                        .map(JsonElement::getAsJsonObject)
                                        .filter(jo -> jo.get("id").getAsString().contains(serverVersion))
                                        .findAny()
                                        .orElseThrow(() -> new AssertionError("Impossible null"))
                                        .get("url")
                                        .getAsString()
                                        .replaceFirst(defaultBase, baseURL)).openStream()))
                                                                                           .getAsJsonObject()
                                                                                           .getAsJsonObject("assetIndex")
                                                                                           .get("url")
                                                                                           .getAsString()
                                                                                           .replaceFirst(defaultBase,
                                                                                                         baseURL)).openStream()))
                        .getAsJsonObject()
                        .getAsJsonObject("objects")
                        .getAsJsonObject("minecraft/lang/" + getMainConfig().serverLanguage.toLowerCase() + ".json")
                        .get("hash")
                        .getAsString();
                lang.createNewFile();
                try (InputStream input = new URL(getMainConfig().mojangServers.resourceDownloadBaseUrl +
                                                 hash.charAt(0) +
                                                 hash.charAt(1) +
                                                 "/" +
                                                 hash).openStream()) {
                    Files.copy(input, lang.toPath());
                }
                loadMojangLocale();
            } catch (Exception e) {
                e.printStackTrace();
                Logging.warn("Failed to download cache.Using spigot-local");
                loadMojangLocaleLocal();
            }
        }
    }

    private void loadMojangLocaleLocal() {
        //use spigot local.
        PlayerStat.mentionedNormal.forEach(e -> {
            mojangLocales.put(e,
                              TranslationRegistry.INSTANCE.translate("advancements." +
                                                                     e.replaceAll("\\/", "\\.") +
                                                                     ".title"));
            Logging.debug(e +
                          " -> " +
                          TranslationRegistry.INSTANCE.translate("advancements." +
                                                                 e.replaceAll("\\/", "\\.") +
                                                                 ".title"));
        });
        PlayerStat.mentionedSpecial.forEach(e -> {
            mojangLocales.put(e,
                              TranslationRegistry.INSTANCE.translate("advancements." +
                                                                     e.replaceAll("\\/", "\\.") +
                                                                     ".title"));
            Logging.debug(e +
                          " -> " +
                          TranslationRegistry.INSTANCE.translate("advancements." +
                                                                 e.replaceAll("\\/", "\\.") +
                                                                 ".title"));
        });
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("vote")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Player-only command.");
                return true;
            }
            if (game.isStarted()) {
                sender.sendMessage(getLanguage().GAMING.VOTE.GAME_ALREADY_STARTED);
                return true;
            }
            Player player = (Player) sender;
            Vote vote = ManHunt.getInstance().getGame().vote;
            if (vote != null && vote.getShouldVote().contains(player.getUniqueId())) {
                Bukkit.getScheduler().runTaskLater(ManHunt.getInstance(),
                        () -> player.openInventory(vote.getVoteInv()),
                        10);
            }
        }
        return true;
    }
}
