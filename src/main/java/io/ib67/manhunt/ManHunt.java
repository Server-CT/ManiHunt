package io.ib67.manhunt;

import io.ib67.manhunt.event.HuntEndEvent;
import io.ib67.manhunt.event.HuntStartedEvent;
import io.ib67.manhunt.game.Game;
import io.ib67.manhunt.gui.Vote;
import io.ib67.manhunt.listener.JoinAndLeave;
import io.ib67.manhunt.setting.I18N;
import io.ib67.manhunt.setting.MainConfig;
import io.ib67.manhunt.util.SimpleConfig;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ManHunt extends JavaPlugin {
    public static boolean debug = false;
    private SimpleConfig<MainConfig> mainConfig = new SimpleConfig<>(getDataFolder(), MainConfig.class);
    private SimpleConfig<I18N> language = new SimpleConfig<>(getDataFolder(), I18N.class);
    @Getter
    private Game game;

    public static ManHunt get() {
        return ManHunt.getPlugin(ManHunt.class);
    }

    public I18N getLanguage() {
        return language.get();
    }

    @Override
    public void onEnable() {
        mainConfig.saveDefault();
        mainConfig.reloadConfig();
        language.setConfigFileName("lang.json");
        language.saveDefault();
        language.reloadConfig();
        debug = mainConfig.get().verbose;
        game = new Game(mainConfig.get().maxPlayers, g -> {
            Bukkit.getPluginManager().callEvent(new HuntStartedEvent(g));
        }, g -> {
            Bukkit.getPluginManager().callEvent(new HuntEndEvent(g));
        });
        //new Vote(Bukkit.getOnlinePlayers().stream(), v -> {
        //  v.endVote();
        //}).startVote();
        loadAdditions();
        loadListeners();
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new JoinAndLeave(), this);
    }

    private void loadAdditions() {
        //todo
    }
}
