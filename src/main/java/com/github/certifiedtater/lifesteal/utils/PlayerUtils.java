package com.github.certifiedtater.lifesteal.utils;

import com.github.certifiedtater.lifesteal.gamerules.LifeStealGamerules;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameRules;

public final class PlayerUtils {

    public static void exchangeHealth(ServerPlayerEntity killed, ServerPlayerEntity attacker) {
        // Killed Player
       EntityAttributeInstance killedMaxHealth = killed.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
       GameRules gameRules = killed.getWorld().getGameRules();
       double killedMaxHealthDouble = killedMaxHealth.getBaseValue();

       if (killedMaxHealthDouble <= gameRules.getInt(LifeStealGamerules.MINPLAYERHEALTH)) {
           // Check to see if spawn camping is happening
           // TODO This is probably broken
           if (gameRules.getBoolean(LifeStealGamerules.ANTIHEARTDUPE)) {
               // TODO Handle health too low to exchange
               return;
           }

           // Considered dead
           // TODO Handle death scenario
       } else {
           changeHealth(killed, killedMaxHealth, -gameRules.getInt(LifeStealGamerules.STEALAMOUNT));
       }

       // Attacker Player
       EntityAttributeInstance attackerMaxHealth = attacker.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
       if (attackerMaxHealth.getBaseValue() >= gameRules.getInt(LifeStealGamerules.MAXPLAYERHEALTH)) {
            // TODO Handle health too high
       } else {
           changeHealth(attacker, attackerMaxHealth, gameRules.getInt(LifeStealGamerules.STEALAMOUNT));
       }
    }

    public static void changeHealth(ServerPlayerEntity player, EntityAttributeInstance attribute, float by) {
        double currentValue = attribute.getValue();
        attribute.setBaseValue(currentValue + by);
        float health = player.getHealth();
        player.setHealth(health + by);
    }
}
