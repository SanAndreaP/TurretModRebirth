/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

public final class DataWatcherBooleans
{
    private final int id;
    private final Entity entity;

    public DataWatcherBooleans(Entity e, int id) {
        this.id = id;
        this.entity = e;
    }

    public void registerDwValue() {
        this.entity.getDataWatcher().addObject(this.id, 0);
    }

    public void setBit(int bit, boolean value) {
        int dwVal = this.entity.getDataWatcher().getWatchableObjectInt(this.id);
        if( value ) {
            dwVal = dwVal | (1 << bit);
        } else {
            dwVal = dwVal & ~( 1 << bit );
        }
        this.entity.getDataWatcher().updateObject(this.id, dwVal);
    }

    public boolean getBit(int bit) {
        return (((this.entity.getDataWatcher().getWatchableObjectInt(this.id) & (1 << bit)) >> bit) & 1) == 1;
    }

    public void writeToNbt(NBTTagCompound nbt) {
        nbt.setInteger("dataWatcherBools", this.entity.getDataWatcher().getWatchableObjectInt(this.id));
    }

    public void readFromNbt(NBTTagCompound nbt) {
        this.entity.getDataWatcher().updateObject(this.id, nbt.getInteger("dataWatcherBools"));
    }

    public enum Turret {
        ACTIVE(0);

        public final int bit;

        Turret(int bit) {
            this.bit = bit;
        }
    }
}
