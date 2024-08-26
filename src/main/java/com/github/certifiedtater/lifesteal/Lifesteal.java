package com.github.certifiedtater.lifesteal;

import com.github.certifiedtater.lifesteal.gamerules.LifeStealGamerules;
import net.fabricmc.api.ModInitializer;

public class Lifesteal implements ModInitializer {

    public static final String MOD_ID = "lifesteal";

    @Override
    public void onInitialize() {
        LifeStealGamerules.init();
    }
}
