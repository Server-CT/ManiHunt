package io.ib67.manhunt.setting;

public class MainConfig {
    public int maxPlayers = 3;
    public boolean verbose = false;
    public boolean muteSpectatorInGlobalChannel = false;
    public String serverLanguage = "zh_CN";
    public String difficulty = "NORMAL";
    public int radarWarnDistance = 30;
    public int airDropBasicOnX = 100;
    public int airDropBasicOnZ = 100;
    public int airDropRandomOnX = 200;
    public int airDropRandomOnZ = 200;
    public Scores playerScores = new Scores();
    public boolean blockCompassWhenDifferentWorld = false;
    public boolean disableTeamMateDamage = false;
    public int distanceFar = 2000;
    public boolean enableBedHint = true;
    public boolean uploadStats = true;
    public boolean enableAirDrop = true;
    public boolean tryToAvoidOcean = true;
    public boolean enableCompassOnStart = false;
    public Servers mojangServers = new Servers();

    public static class Servers {
        public String launchmetaBaseUrl = "https://launchermeta.mojang.com/";
        public String resourceDownloadBaseUrl = "http://resources.download.minecraft.net/";
    }

    public static class Scores {
        public int critical = 400;
        public int kill = 800;
        public int advancementNormal = 500;
        public int advancementSpecial = 1000;
    }
}
