package io.ib67.manhunt.setting;

import org.bukkit.ChatColor;

public class I18n {
    public static final int VERSION = 3;
    public Gaming GAMING = new Gaming();
    /**
     * 配置文件版本号
     */
    public int version = VERSION;

    public static class Gaming {
        public String WAITING_FOR_PLAYERS_SUBTITLE = ChatColor.GOLD + "正在等待更多玩家进入游戏!";
        public String WAITING_FOR_PLAYERS_MAINTITLE = " %d / %d";
        public Hunter HUNTER = new Hunter();
        public Runner RUNNER = new Runner();
        public Vote VOTE = new Vote();
        public String SPECTATOR_RULE = ChatColor.GREEN + "游戏已开始，请保持安静。";
        public String[] GAME_INTRODUCTION = new String[]{
                ChatColor.AQUA + "欢迎来到 " + ChatColor.RED + "ManHunt!",
                ChatColor.WHITE + "在本游戏中，将有多名玩家扮演" + ChatColor.RED + "猎人" + ChatColor.WHITE + "，1 名玩家扮演" + ChatColor.RED + "逃亡者" + ChatColor.WHITE + "。",
                ChatColor.WHITE + "游戏规则:",
                ChatColor.WHITE + "- " + ChatColor.GREEN + "猎人杀死逃亡者时，猎人胜利。",
                ChatColor.WHITE + "- " + ChatColor.GREEN + "逃亡者杀死末影龙时，逃亡者胜利",
                ChatColor.WHITE + "当猎人第一次造出指南针后，" + ChatColor.RED + "将会开启无限指南针，并通过右键交互定位逃亡者位置",
                ChatColor.WHITE + "并且每个人每完成一个成就后，" + ChatColor.GREEN + "将有机率拿到一样加成物品。 " + ChatColor.GRAY
                        + "前提：服务器开启加成模式",
                ChatColor.WHITE + "游戏通过 投票 选举逃亡者。"
        };
        public String ARCHIVE_TARGET = ChatColor.GOLD + "达成成就: +%dXP";
        public String KILL_TARGET = ChatColor.GOLD + "杀死对手: +%dXP";
        public String CRITICAL_TARGET = ChatColor.GOLD + "重拳出击！: +%dXP";
        public String DONT_RUN_AWAY = ChatColor.RED + "请不要跑出出生点。";
        public String SHUTDOWN = "30S 后将会自动重启。";
        public SimpleRador SIMPLE_RADOR = new SimpleRador();

        public static class SimpleRador {
            public String NEAR = ChatColor.RED + "%s 正在靠近! (<=%dM)";
            public String SAFE = ChatColor.GREEN + "半径 %dM 内无猎人出现。";
            public String HINT_CHAT_COMING = "猎人 %s 正在接近。";
            public String HINT_CHAT_LEAVE = "猎人 %s 离开雷达范围之外。";
        }

        public static class Hunter {
            public String WON = ChatColor.RED + "游戏结束！猎人 胜利";
            public String TITLE_MAIN = ChatColor.RED.toString() + ChatColor.MAGIC + "%%% " + ChatColor.RESET + ChatColor.RED + "游戏开始 %%%";
            public String TITLE_SUB = "找到逃亡者并杀死他";
            public String UNLIMITED_COMPASS_UNLOCKED = ChatColor.RED + "猎人已解锁无限指南针";
            public String UNLIMITED_COMPASS_USAGE = ChatColor.GREEN + "请持续 " + ChatColor.GOLD + "[右键] " + ChatColor.GREEN + "指南针以刷新指向。";
            public String UNLIMITED_COMPASS_LOCKED = ChatColor.RED + "无限指南针已被销毁！猎人需要重新制作指南针";
            public String FAILED_TO_TRACK = ChatColor.RED + "无法追踪逃亡者！";
            public String WARN_RUNNER_NOT_ENTERED = ChatColor.RED + "逃亡者尚未到达这个世界";
            public String ACTION_BAR_RADOR = ChatColor.AQUA + "TRACKING: %s";
            public String ACTION_BAR_RADOR_PART_FAR = ChatColor.RED + "DISTANCE >> %d";
            public String COMPASS_ARRIVED = ChatColor.LIGHT_PURPLE + "指南针已经到达！请查收背包";
        }

        public static class Runner {
            public String WON = ChatColor.GREEN + "游戏结束！逃亡者 胜利";
            public String TITLE_MAIN = ChatColor.RED.toString() + ChatColor.MAGIC + "%%% " + ChatColor.RESET + ChatColor.RED + "游戏开始 %%%";
            public String TITLE_SUB = "杀死末影龙，同时躲避猎人！";
            public String ARRIVE_NETHER = ChatColor.BOLD + "逃亡者已到达 地狱";
            public String ARRIVE_END = ChatColor.BOLD + "逃亡者已到达 末地";
        }

        public static class Vote {
            public String VOTE_START = ChatColor.GREEN + "人数满足 正在进行投票！" + ChatColor.GRAY + "如果不小心关闭界面，请使用 /vote 再次打开。";
            public String ALREADY_VOTED = ChatColor.RED + "您已经投票过了";
            public String SHOULD_NOT_VOTE = ChatColor.RED + "您没有投票的权利";
            public String VOTE_SUCCEED = ChatColor.GREEN + "您成功投给了 %s";
            public String VOTING = ChatColor.GOLD + "投票中: %d / %d 已投票";
            public String GAME_ALREADY_STARTED = ChatColor.RED + "投票已结束。";
            public String VOTE_TITLE = "谁 是 幸 运 嘉 宾 ？";
            public String VOTE_ITEM_FORMAT = "那当然是 %s";
        }
    }
}
