package io.ib67.manhunt.setting;

import org.bukkit.ChatColor;

public class I18n {
    public Gaming gaming = new Gaming();

    public static class Gaming {
        public String WAITING_FOR_PLAYERS = ChatColor.GOLD + "正在等待更多玩家进入游戏! (%d/%d)";
        public String VOTE_START = ChatColor.GREEN + "人数满足 正在进行投票！" + ChatColor.GRAY + "如果不小心关闭界面，请使用 /vote 再次打开。";
        public Hunter hunter = new Hunter();
        public Runner runner = new Runner();
        public String SPECTATOR_RULE = ChatColor.GREEN + "游戏已开始，请保持安静。";
        public String[] gameIntroduction = new String[]{
                ChatColor.AQUA + "欢迎来到 " + ChatColor.RED + "ManHunt!",
                ChatColor.WHITE + "在本游戏中，将有 %s 名玩家扮演" + ChatColor.RED + "猎人" + ChatColor.WHITE + "，1 名玩家扮演" + ChatColor.RED + "逃亡者" + ChatColor.WHITE + "。",
                ChatColor.WHITE + "游戏规则:",
                ChatColor.WHITE + "- " + ChatColor.GREEN + "猎人杀死逃亡者时，猎人胜利。",
                ChatColor.WHITE + "- " + ChatColor.GREEN + "逃亡者杀死末影龙时，逃亡者胜利",
                ChatColor.WHITE + "当猎人第一次造出指南针后，" + ChatColor.RED + "将会开启无限指南针，并通过右键交互定位逃亡者位置",
                ChatColor.WHITE + "而逃亡者每完成一个成就后，" + ChatColor.LIGHT_PURPLE + "将有 %s 的机率拿到一样加成物品。 " + ChatColor.GRAY
                        + "前提：服务器开启加成模式",
                ChatColor.WHITE + "游戏通过 投票 选举逃亡者。"
        };

        public static class Hunter {
            public String WON = ChatColor.RED + "游戏结束   猎人 胜利";
            public String TITLE_MAIN = ChatColor.RED.toString() + ChatColor.MAGIC + "%%% 游戏开始 %%%";
            public String TITLE_SUB = "找到逃亡者并杀死他";
        }

        public static class Runner {
            public String WON = ChatColor.GREEN + "游戏结束   逃亡者 胜利";
            public String TITLE_MAIN = ChatColor.RED.toString() + ChatColor.MAGIC + "%%% 游戏开始 %%%";
            public String TITLE_SUB = "杀死末影龙，同时躲避猎人！";
            public String COMPASS_HINT_SAFE = ChatColor.GREEN + "半径 %sM 内无猎人";
        }
    }
}
