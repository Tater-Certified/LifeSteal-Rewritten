package com.github.certifiedtater.lifesteal.utils;

import com.github.certifiedtater.lifesteal.data.DeathStage;
import com.github.certifiedtater.lifesteal.data.LifeStealPersistentData;
import com.github.certifiedtater.lifesteal.gamerules.DeathAction;
import com.github.certifiedtater.lifesteal.gamerules.LifeStealGamerules;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;

public final class PlayerUtils {

    public static void exchangeHealth(ServerPlayerEntity killed, ServerPlayerEntity attacker) {
        // Killed Player
       EntityAttributeInstance killedMaxHealth = killed.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
       GameRules gameRules = killed.getWorld().getGameRules();
       double killedMaxHealthDouble = killedMaxHealth.getBaseValue();

       int minHealth = gameRules.getInt(LifeStealGamerules.MINPLAYERHEALTH);
       if (killedMaxHealthDouble <= minHealth) {
           // Check to see if spawn camping is happening
           // TODO This is probably broken
           if (killedMaxHealthDouble < minHealth) {
               // TODO Handle health too low to exchange
               if (gameRules.getBoolean(LifeStealGamerules.ANTIHEARTDUPE)) {
                   return;
               }
           } else {
               // Considered dead
               LifeStealPersistentData storage = LifeStealPersistentData.getServerState(attacker.server);
               storage.addDeadPlayer(killed);
               handleDeadPlayerAction(killed);
           }
       } else {
           changeHealth(killed, killedMaxHealth, -gameRules.getInt(LifeStealGamerules.STEALAMOUNT));
       }

       // Attacker Player
       if (!changeHealth(attacker, gameRules.getInt(LifeStealGamerules.STEALAMOUNT))) {
           attacker.sendMessage(LifeStealText.MAX_HEALTH, true);
       }
    }

    public static void changeHealth(ServerPlayerEntity player, EntityAttributeInstance attribute, float by) {
        double currentValue = attribute.getValue();
        attribute.setBaseValue(currentValue + by);
        float health = player.getHealth();
        player.setHealth(health + by);
    }

    public static boolean changeHealth(ServerPlayerEntity player, float by) {
        EntityAttributeInstance maxHealthAttribute = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        double maxHealth = maxHealthAttribute.getBaseValue();
        if (canChangeHealth(maxHealth, by, player.getWorld().getGameRules())) {
            changeHealth(player, maxHealthAttribute, by);
            return true;
        } else {
            return false;
        }
    }

    public static boolean canChangeHealth(double currentMaxHealth, float by, GameRules gameRules) {
        double newMaxHealth = currentMaxHealth + by;
        return newMaxHealth >= gameRules.getInt(LifeStealGamerules.MINPLAYERHEALTH) && newMaxHealth <= gameRules.getInt(LifeStealGamerules.MAXPLAYERHEALTH);
    }

    public static void handleDeadPlayerAction(ServerPlayerEntity player) {
        GameRules gameRules = player.getWorld().getGameRules();
        DeathAction action = gameRules.get(LifeStealGamerules.DEATH_ACTION).get();
        switch (action) {
            case BAN -> player.networkHandler.disconnect(LifeStealText.DEATH);
            case REVIVE -> setMaxHealth(gameRules.getInt(LifeStealGamerules.MINPLAYERHEALTH), player);
            case SPECTATOR -> player.changeGameMode(GameMode.SPECTATOR);
        }
    }

    public static void handlePlayerJoin(ServerPlayerEntity player) {
        LifeStealPersistentData data = LifeStealPersistentData.getServerState(player.server);
        DeathStage deathStage = data.getPlayerDeathStage(player.getUuid());
        switch (deathStage) {
            case REVIVED -> handlePostRevival(player);
            case DEAD -> handleDeadPlayerAction(player);
        }
    }

    public static void handlePostRevival(ServerPlayerEntity player) {
        GameRules gameRules = player.getWorld().getGameRules();
        setMaxHealth(gameRules.getInt(LifeStealGamerules.MINPLAYERHEALTH), player);
        // TODO Revival message
        LifeStealPersistentData data = LifeStealPersistentData.getServerState(player.server);
        data.removeDeadPlayerData(player);
    }

    private static void setMaxHealth(double value, ServerPlayerEntity player) {
        EntityAttributeInstance maxHealth = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        maxHealth.setBaseValue(value);
    }
}
