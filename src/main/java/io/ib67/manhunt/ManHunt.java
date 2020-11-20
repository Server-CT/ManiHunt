package io.ib67.manhunt;

import io.ib67.manhunt.event.HuntEndEvent;
import io.ib67.manhunt.event.HuntStartedEvent;
import io.ib67.manhunt.game.Game;
import io.ib67.manhunt.listener.*;
import io.ib67.manhunt.setting.I18n;
import io.ib67.manhunt.setting.MainConfig;
import io.ib67.manhunt.util.SimpleConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ManHunt extends JavaPlugin {
    private static ManHunt instance;
    public static boolean debug = false;
    private final SimpleConfig<MainConfig> mainConfig = new SimpleConfig<>(getDataFolder(), MainConfig.class);
    private final SimpleConfig<I18n> language = new SimpleConfig<>(getDataFolder(), I18n.class);
    @Getter
    private Game game;

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
}
