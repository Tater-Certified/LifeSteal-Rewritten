package com.github.certifiedtater.lifesteal;

import com.github.certifiedtater.lifesteal.gamerules.LifeStealGamerules;
import com.github.certifiedtater.lifesteal.utils.PlayerUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class Lifesteal implements ModInitializer {

    public static final String MOD_ID = "lifesteal";

    @Override
    public void onInitialize() {
        LifeStealGamerules.init();
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> PlayerUtils.handlePlayerJoin(handler.getPlayer()));
    }
}
