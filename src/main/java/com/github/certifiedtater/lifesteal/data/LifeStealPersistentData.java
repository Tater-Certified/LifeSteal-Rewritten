package com.github.certifiedtater.lifesteal.data;

import com.github.certifiedtater.lifesteal.Lifesteal;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.UUID;

public class LifeStealPersistentData extends PersistentState {
    public NbtList deadPlayers = new NbtList();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.put("deadPlayers", deadPlayers);
        return nbt;
    }

    public static LifeStealPersistentData createFromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        LifeStealPersistentData state = new LifeStealPersistentData();
        state.deadPlayers = nbt.getList("deadPlayers", NbtElement.COMPOUND_TYPE);
        return state;
    }

    private static final Type<LifeStealPersistentData> type = new Type<>(
            LifeStealPersistentData::new,
            LifeStealPersistentData::createFromNbt,
            null
    );

    public static LifeStealPersistentData getServerState(MinecraftServer server) {
        // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        // The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
        // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
        // 'StateSaverAndLoader' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.
        LifeStealPersistentData state = persistentStateManager.getOrCreate(type, Lifesteal.MOD_ID);

        // If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
        // Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
        // of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
        // Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
        // there were no actual change to any of the mods state (INCREDIBLY RARE).
        state.markDirty();

        return state;
    }

    public void addDeadPlayer(ServerPlayerEntity player) {
        UUID playerUUID = player.getUuid();
        DeathData data = new DeathData(playerUUID);
        NbtCompound nbtCompound = data.toNBT();
        deadPlayers.add(nbtCompound);
        this.markDirty();
    }

    public void addReviverPlayer(ServerPlayerEntity reviver, UUID playerRevived) {
        int i = getDeathDataIndex(playerRevived);
        if (i >= 0) {
            DeathData deathData = DeathData.fromNBT(deadPlayers.getCompound(i));
            deathData.reviverPlayerID = reviver.getUuid();
            NbtCompound compound = deathData.toNBT();
            deadPlayers.set(i, compound);
            this.markDirty();
        }
    }

    public void removeDeadPlayerData(ServerPlayerEntity player) {
        UUID playerUUID = player.getUuid();
        int index = getDeathDataIndex(playerUUID);
        if (index >= 0) {
            deadPlayers.remove(index);
            this.markDirty();
        }
    }

    public int getDeathDataIndex(UUID uuid) {
        String uuidString = uuid.toString();
        for (int i = 0; i < deadPlayers.size(); i++) {
            NbtCompound data = deadPlayers.getCompound(i);
            if (data.getKeys().stream().findFirst().get().equals(uuidString)) {
                return i;
            }
        }
        return -1;
    }

    public boolean isPlayerDead(UUID uuid) {
        return getPlayerDeathStage(uuid) == DeathStage.DEAD;
    }

    public DeathStage getPlayerDeathStage(UUID uuid) {
        String uuidString = uuid.toString();
        for (int i = 0; i < deadPlayers.size(); i++) {
            NbtCompound data = deadPlayers.getCompound(i);
            if (data.getKeys().stream().findFirst().get().equals(uuidString)) {
                DeathData deathData = DeathData.fromNBT(data);

                if (deathData.reviverPlayerID != null) {
                    return DeathStage.REVIVED;
                } else {
                    return DeathStage.DEAD;
                }
            }
        }
        return DeathStage.ALIVE;
    }
}
