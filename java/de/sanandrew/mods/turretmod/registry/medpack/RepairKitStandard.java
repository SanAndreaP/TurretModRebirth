/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.medpack;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class RepairKitStandard
        implements TurretRepairKit
{
    private final String name;
    private final UUID uuid;
    private final float heal;
    private final ResourceLocation itemModel;

    public RepairKitStandard(String name, UUID uuid, float heal, ResourceLocation model) {
        this.name = name;
        this.uuid = uuid;
        this.heal = heal;
        this.itemModel = model;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final UUID getUUID() {
        return this.uuid;
    }

    @Override
    public final float getHealAmount() {
        return this.heal;
    }

    @Override
    public final void onHeal(EntityTurret turret) { }

    @Override
    public boolean isApplicable(EntityTurret turret) {
        return turret.getHealth() <= turret.getMaxHealth() - this.heal;
    }

    @Override
    public ResourceLocation getModel() {
        return this.itemModel;
    }
}
