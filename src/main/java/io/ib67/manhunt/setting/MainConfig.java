package io.ib67.manhunt.setting;

public class MainConfig {
    public int maxPlayers;
    public String overworldName;
    public boolean verbose;
    public boolean muteSpectatorInGlobalChannel = false;
    public String serverLanguage = "zh_CN";
    public int radorWarnDistance = 30;
    public Scores playerScores = new Scores();

    public static class Scores {
        public int critical = 400;
        public int advancementNormal = 500;
        public int advancementSpecial = 1000;
    }
}
