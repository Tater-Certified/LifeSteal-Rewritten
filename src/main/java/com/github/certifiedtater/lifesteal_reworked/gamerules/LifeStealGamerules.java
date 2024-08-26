package com.github.certifiedtater.lifesteal_reworked.gamerules;

import com.github.certifiedtater.lifesteal_reworked.Lifesteal;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.gamerule.v1.rule.EnumRule;
import net.minecraft.world.GameRules;

public final class LifeStealGamerules {
    public static void init() {
    }

    /**
     * If true: Players only get base health removed by player kills
     * If false: Players get health removed from any death
     */
    public static final GameRules.Key<GameRules.BooleanRule> PLAYERRELATEDONLY = GameRuleRegistry.register(Lifesteal.MOD_ID + ":playerKillOnly", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));

    /**
     * The action to take when the player goes below the allowed minimum health as defined by {@link #MINPLAYERHEALTH}
     */
    public static final GameRules.Key<EnumRule<DeathAction>> DEATH_ACTION =
            registerPlayerRule("deathAction", GameRuleFactory.createEnumRule(DeathAction.UNSET));

    /**
     * Whether to allow gifting hearts to other players, via the command or altar.
     */
    public static final GameRules.Key<GameRules.BooleanRule> GIFTHEARTS = GameRuleRegistry.register(Lifesteal.MOD_ID + ":giftHearts", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));

    /**
     * Whether to allow creating an altar to revive and exchange player hearts for heart crystal items.
     * Disabling this effectively disables trading and revival, except the /gift command
     */
    public static final GameRules.Key<GameRules.BooleanRule> ALTARS = GameRuleRegistry.register(Lifesteal.MOD_ID + ":enableAltars", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    /**
     * Whether to disable getting "free" hearts from killing people with the minimum HP.
     * This can prevent spawn camping and harvesting tons of hearts from teammates
     */
    public static final GameRules.Key<GameRules.BooleanRule> ANTIHEARTDUPE = GameRuleRegistry.register(Lifesteal.MOD_ID + ":enableAntiHeartDupe", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    /**
     * The amount of health "stolen" from players when other players kill them
     */
    public static final GameRules.Key<GameRules.IntRule> STEALAMOUNT = GameRuleRegistry.register(Lifesteal.MOD_ID + ":stealAmount", GameRules.Category.PLAYER, GameRuleFactory.createIntRule(2, 0));

    /**
     * This value determines the threshold for being considered "dead".
     * If a player reaches lower than this value, they will be categorized as dead unless BanWhenMaxHealth is disabled
     */
    public static final GameRules.Key<GameRules.IntRule> MINPLAYERHEALTH = GameRuleRegistry.register(Lifesteal.MOD_ID + ":minPlayerHealth", GameRules.Category.PLAYER, GameRuleFactory.createIntRule(1, 1));

    /**
     * The max amount of health a player can obtain
     */
    public static final GameRules.Key<GameRules.IntRule> MAXPLAYERHEALTH = GameRuleRegistry.register(Lifesteal.MOD_ID + ":maxPlayerHealth", GameRules.Category.PLAYER, GameRuleFactory.createIntRule(40, 1));

    /**
     * The amount of health received from heart crystals
     */
    public static final GameRules.Key<GameRules.IntRule> HEARTBONUS = GameRuleRegistry.register(Lifesteal.MOD_ID + ":healthFromCrystal", GameRules.Category.PLAYER, GameRuleFactory.createIntRule(2, 0));

    private static <R extends GameRules.Rule<R>, T extends GameRules.Type<R>> GameRules.Key<R> registerPlayerRule(String name, T rule) {
        return GameRuleRegistry.register(Lifesteal.MOD_ID + ':' + name, GameRules.Category.PLAYER, rule);
    }
}
