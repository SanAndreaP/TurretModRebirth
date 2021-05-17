/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;

import java.util.HashMap;
import java.util.Map;

public final class DataWatcherBooleans<T extends Entity>
{
    private final T entity;
    private final DataParameter<Integer> param;
    private static final Map<Class<? extends Entity>, DataParameter<Integer>> PARAMS = new HashMap<>();

    DataWatcherBooleans(T e) {
        this.entity = e;
        Class<? extends Entity> entityCls = e.getClass();
        if( !PARAMS.containsKey(entityCls) ) {
            PARAMS.put(entityCls, EntityDataManager.defineId(e.getClass(), DataSerializers.INT));
        }
        this.param = PARAMS.get(entityCls);
    }

    void registerDwValue() {
        this.entity.getEntityData().define(this.param, 0);
    }

    void setBit(int bit, boolean value) {
        int dwVal = this.entity.getEntityData().get(this.param);
        if( value ) {
            dwVal = dwVal | (1 << bit);
        } else {
            dwVal = dwVal & ~( 1 << bit );
        }
        this.entity.getEntityData().set(this.param, dwVal);
    }

    boolean getBit(int bit) {
        return (((this.entity.getEntityData().get(this.param) & (1 << bit)) >> bit) & 1) == 1;
    }

    void save(CompoundNBT nbt) {
        nbt.putInt("dataWatcherBools", this.entity.getEntityData().get(this.param));
    }

    void load(CompoundNBT nbt) {
        this.entity.getEntityData().set(this.param, nbt.getInt("dataWatcherBools"));
    }

    public enum Turret {
        ACTIVE(0);

        public final int bit;

        Turret(int bit) {
            this.bit = bit;
        }
    }
}
