package io.ib67.manhunt.game.stat;

import io.ib67.manhunt.ManHunt;
import io.ib67.manhunt.game.GamePlayer;
import io.ib67.manhunt.setting.MainConfig;
import io.ib67.manhunt.util.AdvancementTranslator;
import org.bukkit.Statistic;
import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("unused")
public class PlayerStat {
    private final GamePlayer player;
    private final static List<String> mentionedNormal = Arrays.asList("story/upgrade_tools", "story/smelt_iron", "story/lava_bucket",
            "story/enter_the_nether", "nether/obtain_blaze_rod", "story/follow_ender_eye", "story/enter_the_end");
    private final static List<String> mentionedSpecial = Arrays.asList("story/mine_diamond", "story/enchant_item"
            , "story/form_obsidian", "nether/return_to_sender", "nether/brew_potion", "nether/distract_piglin", "adventure/trade");

    public PlayerStat(GamePlayer p) {
        player = p;
    }

    public int normalScore;
    public int specialScore;

    {
        MainConfig cfg = ManHunt.getInstance().getMainConfig();
        normalScore = cfg.playerScores.advancementNormal;
        specialScore = cfg.playerScores.advancementSpecial;
    }

    /**
     * Minute.
     */
    private int playedTime;
    private int takenDamages;
    private int causedDamages;
    private int walkDistance;
    private int deathCounts;
    private final List<AdvancementRecord> advancements = new LinkedList<>();
    private int finalScore;

    private transient long lastAdvancementTime = System.currentTimeMillis();

    public void calculate() {
        Player p = player.getPlayer();
        playedTime = p.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 / 60;
        takenDamages = p.getStatistic(Statistic.DAMAGE_TAKEN) - p.getStatistic(Statistic.DAMAGE_BLOCKED_BY_SHIELD);
        causedDamages = p.getStatistic(Statistic.DAMAGE_DEALT);
        walkDistance = p.getStatistic(Statistic.BOAT_ONE_CM)
                + p.getStatistic(Statistic.AVIATE_ONE_CM)
                + p.getStatistic(Statistic.HORSE_ONE_CM)
                + p.getStatistic(Statistic.MINECART_ONE_CM)
                + p.getStatistic(Statistic.PIG_ONE_CM)
                + p.getStatistic(Statistic.STRIDER_ONE_CM)
                + p.getStatistic(Statistic.CLIMB_ONE_CM)
                + p.getStatistic(Statistic.CROUCH_ONE_CM)
                + p.getStatistic(Statistic.FLY_ONE_CM)
                + p.getStatistic(Statistic.SPRINT_ONE_CM)
                + p.getStatistic(Statistic.SWIM_ONE_CM)
                + p.getStatistic(Statistic.WALK_ONE_CM)
                + p.getStatistic(Statistic.WALK_ON_WATER_ONE_CM)
                + p.getStatistic(Statistic.WALK_UNDER_WATER_ONE_CM);
        deathCounts = p.getStatistic(Statistic.DEATHS);
        int scoresOfAdvs = 0;
        for (AdvancementRecord advancement : advancements) {
            scoresOfAdvs = scoresOfAdvs + advancement.getScore();
        }
        int scoreOfStats = 0;
        if (takenDamages != 0) {
            scoreOfStats = (int) (scoreOfStats + Math.ceil(causedDamages) / Math.ceil(takenDamages));
        } else {
            scoreOfStats = scoreOfStats + causedDamages;
        }
        scoreOfStats = scoreOfStats - deathCounts * 100;
        finalScore = scoreOfStats + scoresOfAdvs;
    }

    /**
     * Add to statistics
     *
     * @param adv
     * @return score (0 == nothing happened)
     */
    public int achieve(Advancement adv) {
        long distance = System.currentTimeMillis() - lastAdvancementTime;
        if (mentionedNormal.contains(adv.getKey().getKey())) {
            advancements.add(
                    AdvancementRecord.builder()
                            .score(Math.round(normalScore / distance))
                            .usedTime(distance)
                            .advancement(AdvancementTranslator.translate(adv.getKey().getKey()))
                            .build());
            lastAdvancementTime = System.currentTimeMillis();
            return normalScore; //ScoreAddition by time.
        } else if (mentionedSpecial.contains(adv.getKey().getKey())) {
            int score = Math.round(specialScore / distance);
            advancements.add(
                    AdvancementRecord.builder()
                            .score(score)
                            .usedTime(distance)
                            .advancement(AdvancementTranslator.translate(adv.getKey().getKey()))
                            .build());
            lastAdvancementTime = System.currentTimeMillis();
            return score;
        }
        return 0;
    }

    public void achieve(String advancement, int score) {
        advancements.add(AdvancementRecord.builder()
                .advancement(advancement)
                .score(score)
                .usedTime(lastAdvancementTime - System.currentTimeMillis())
                .build());
        lastAdvancementTime = System.currentTimeMillis();
    }
}
