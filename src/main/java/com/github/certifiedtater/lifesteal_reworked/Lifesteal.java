package com.github.certifiedtater.lifesteal_reworked;

import com.github.certifiedtater.lifesteal_reworked.gamerules.LifeStealGamerules;
import net.fabricmc.api.ModInitializer;

public class Lifesteal implements ModInitializer {

    public static final String MOD_ID = "lifesteal";

    @Override
    public void onInitialize() {
        LifeStealGamerules.init();
    }
}
