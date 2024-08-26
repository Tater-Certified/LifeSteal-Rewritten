package com.github.certifiedtater.lifesteal_reworked.mixin;

import com.github.certifiedtater.lifesteal_reworked.gamerules.LifeStealGamerules;
import com.github.certifiedtater.lifesteal_reworked.utils.PlayerUtils;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Shadow public abstract ServerWorld getServerWorld();

    @Inject(method = "onDeath", at = @At("TAIL"))
    private void onDeath(DamageSource damageSource, CallbackInfo ci) {
        Entity attacker = damageSource.getAttacker();
        if (attacker instanceof ServerPlayerEntity playerAttacker) {
            PlayerUtils.exchangeHealth(((ServerPlayerEntity) (Object) this), playerAttacker);
        } else if (!getServerWorld().getGameRules().getBoolean(LifeStealGamerules.PLAYERRELATEDONLY)) {
            EntityAttributeInstance killedMaxHealth = this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            PlayerUtils.changeHealth(((ServerPlayerEntity) (Object) this), killedMaxHealth, -attacker.getWorld().getGameRules().getInt(LifeStealGamerules.STEALAMOUNT));
        }
    }
}
