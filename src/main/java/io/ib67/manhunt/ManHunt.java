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

    ;

    public I18n getLanguage() {
        return language.get();
    }

    @Override
    public void onEnable() {
        instance = this;
        mainConfig.saveDefault();
        mainConfig.reloadConfig();
        language.setConfigFileName("lang.json");
        language.saveDefault();
        language.reloadConfig();
        debug = mainConfig.get().verbose;
        game = new Game(mainConfig.get().maxPlayers,
                        g -> Bukkit.getPluginManager().callEvent(new HuntStartedEvent(g)),
                        g -> Bukkit.getPluginManager().callEvent(new HuntEndEvent(g)));
        loadAdditions();
        loadListeners();
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
