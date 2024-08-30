package com.github.certifiedtater.lifesteal.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;

import java.util.UUID;

public class DeathData {
    public final UUID deadPlayerID;
    public UUID reviverPlayerID;

    public DeathData(UUID deadPlayerID) {
        this.deadPlayerID = deadPlayerID;
    }

    public DeathData(UUID deadPlayerID, UUID reviverPlayerID) {
        this.deadPlayerID = deadPlayerID;
        this.reviverPlayerID = reviverPlayerID;
    }

    public NbtCompound toNBT() {
        NbtCompound compound = new NbtCompound();
        String reviverPlayerIDString;
        if (reviverPlayerID != null) {
            reviverPlayerIDString = reviverPlayerID.toString();
        } else {
            reviverPlayerIDString = "";
        }
        compound.put(deadPlayerID.toString(), NbtString.of(reviverPlayerIDString));
        return compound;
    }

    public static DeathData fromNBT(NbtCompound compound) {
        String deadPlayerString = compound.getKeys().stream().findFirst().get();
        UUID deadPlayerUUID = UUID.fromString(deadPlayerString);
        String reviverPlayerString = compound.getString(deadPlayerString);
        UUID reviverPlayerUUID = null;
        if (!reviverPlayerString.isEmpty()) {
            reviverPlayerUUID = UUID.fromString(reviverPlayerString);
        }
        return new DeathData(deadPlayerUUID, reviverPlayerUUID);
    }
}
