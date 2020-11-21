package io.ib67.manhunt;

import com.google.gson.JsonParser;
import io.ib67.manhunt.event.HuntEndEvent;
import io.ib67.manhunt.event.HuntStartedEvent;
import io.ib67.manhunt.game.Game;
import io.ib67.manhunt.game.stat.PlayerStat;
import io.ib67.manhunt.listener.*;
import io.ib67.manhunt.setting.I18n;
import io.ib67.manhunt.setting.MainConfig;
import io.ib67.manhunt.util.SimpleConfig;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.chat.TranslationRegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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

    @SneakyThrows
    private void loadMojangLocale() {
        File lang = new File(getDataFolder().getAbsolutePath() + getMainConfig().serverLanguage.toLowerCase() + ".cache");
        if (lang.exists()) {
            Properties properties = new Properties();
            InputStream inputStream = new FileInputStream(lang);
            properties.load(inputStream);

            PlayerStat.mentionedNormal.forEach(e -> {
                mojangLocales.put(e, properties.getProperty("advancements." + e.replaceAll("/", "\\.") + ".title"));
                Logging.debug(e + " -> " + properties.getProperty("advancements." + e.replaceAll("/", "\\.") + ".title"));
            });
            PlayerStat.mentionedSpecial.forEach(e -> {
                mojangLocales.put(e, properties.getProperty("advancements." + e.replaceAll("/", "\\.") + ".title"));
                Logging.debug(e + " -> " + properties.getProperty("advancements." + e.replaceAll("/", "\\.") + ".title"));
            });
            Logging.info("MojangLocales loaded.");
            inputStream.close();
            return;
        } else {
            Logging.warn("Cache not found.Using spigot-local");
            loadMojangLocaleLocal();
            return;
        }
       /* getLogger().info("Fetching versions from Mojang Servers.");
        HttpClient httpClient = new HttpClient(new URI(getMainConfig().mojangServers.launchmetaBaseUrl + "mc/game/version_manifest.json"));
        HttpResponse response = httpClient.sendData(HttpClient.HTTP_METHOD.GET);
        if (response.hasError() || response.getCode() != 200) {
            Logging.warn("Failed to load from network.Using spigot-local");
            loadMojangLocaleLocal();
            return;
        }
        String resp = response.getData();
        JsonObject jsonObject = jsonParser.parse(resp).getAsJsonObject();
        String newUrl = null;
        String langVer = null;
        for (JsonElement version : jsonObject.getAsJsonArray("versions")) {
            JsonObject jo = version.getAsJsonObject();
            if (jo.get("id").toString().contains(serverVersion)) {
                langVer = jo.get("id").toString();
                newUrl = jo.get("url").toString().replaceAll("https://launchermeta.mojang.com/", getMainConfig().mojangServers.launchmetaBaseUrl);
                break;
            }
        }
        if (newUrl == null) {
            Logging.warn("Failed to load from network.Using spigot-local");
            loadMojangLocaleLocal();
            return;
        }
        httpClient = new HttpClient(new URI(newUrl));
        if (response.hasError() || response.getCode() != 200) {
            Logging.warn("Failed to load from network.Using spigot-local");
            loadMojangLocaleLocal();
            return;
        }
        resp = response.getData();
        jsonObject = jsonParser.parse(resp).getAsJsonObject();
        newUrl = jsonObject.get("assetIndex").getAsJsonObject().get("url").toString().replaceAll("https://launchermeta.mojang.com/", getMainConfig().mojangServers.launchmetaBaseUrl);
        httpClient = new HttpClient(new URI(newUrl));
        if (response.hasError() || response.getCode() != 200) {
            Logging.warn("Failed to load from network.Using spigot-local");
            loadMojangLocaleLocal();
            return;
        }
        resp = response.getData();
        jsonObject = jsonParser.parse(resp).getAsJsonObject();
        String hash = jsonObject.getAsJsonObject("objects").getAsJsonObject("minecraft/lang/" + getMainConfig().serverLanguage.toLowerCase() + ".json").get("hash").toString();
        //download properties
        newUrl = getMainConfig().mojangServers.resourceDownloadBaseUrl + hash.substring(0, 1) + "/" + hash;
        newUrl = jsonObject.get("assetIndex").getAsJsonObject().get("url").toString().replaceAll("https://launchermeta.mojang.com/", getMainConfig().mojangServers.launchmetaBaseUrl);
        httpClient = new HttpClient(new URI(newUrl));
        if (response.hasError() || response.getCode() != 200) {
            Logging.warn("Failed to load from network.Using spigot-local");
            loadMojangLocaleLocal();
            return;
        }
        resp = response.getData();
        Logging.info("Caching mojang language file...");
        Files.write(resp.getBytes(), lang);
        loadMojangLocale();*/
    }

    private void loadMojangLocaleLocal() {
        //use spigot local.
        PlayerStat.mentionedNormal.forEach(e -> {
            mojangLocales.put(e, TranslationRegistry.INSTANCE.translate("advancements." + e.replaceAll("\\/", "\\.") + ".title"));
            Logging.debug(e + " -> " + TranslationRegistry.INSTANCE.translate("advancements." + e.replaceAll("\\/", "\\.") + ".title"));
        });
        PlayerStat.mentionedSpecial.forEach(e -> {
            mojangLocales.put(e, TranslationRegistry.INSTANCE.translate("advancements." + e.replaceAll("\\/", "\\.") + ".title"));
            Logging.debug(e + " -> " + TranslationRegistry.INSTANCE.translate("advancements." + e.replaceAll("\\/", "\\.") + ".title"));
        });
    }
}
