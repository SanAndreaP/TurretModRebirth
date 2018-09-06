/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.ammo;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionGroup;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileFlame;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TurretAmmoFireTank
        implements IAmmunition<EntityProjectileFlame>
{
    private final String name;
    private final UUID id;
    private final int capacity;
    private final ResourceLocation itemModel;

    public TurretAmmoFireTank(boolean isMulti) {
        this.name = isMulti ? "fueltank_pack" : "fueltank";
        this.id = isMulti ? Ammunitions.FUELTANK_PACK : Ammunitions.FUELTANK;
        this.capacity = isMulti ? 256 : 16;
        this.itemModel = new ResourceLocation(TmrConstants.ID, "turret_ammo/" + this.name);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public int getAmmoCapacity() {
        return this.capacity;
    }

    @Override
    public Class<EntityProjectileFlame> getEntityClass() {
        return EntityProjectileFlame.class;
    }

    @Override
    public float getDamageInfo() {
        return 0.5F;
    }

    @Override
    public UUID getTypeId() {
        return Ammunitions.FUELTANK;
    }

    @Nonnull
    @Override
    public IAmmunitionGroup getGroup() {
        return Ammunitions.Groups.FUEL_TANK;
    }

    @Override
    @Nonnull
    public ItemStack getStoringAmmoItem() {
        return AmmunitionRegistry.INSTANCE.getAmmoItem(AmmunitionRegistry.INSTANCE.getType(Ammunitions.FUELTANK));
    }

    @Override
    public EntityProjectileFlame getEntity(ITurretInst turretInst) {
        EntityProjectileFlame flame = new EntityProjectileFlame(turretInst.getEntity().world, turretInst.getEntity(), turretInst.getTargetProcessor().getTarget());
        flame.purifying = turretInst.getUpgradeProcessor().hasUpgrade(Upgrades.FUEL_PURIFY);
        return flame;
    }

    @Override
    public ResourceLocation getModel() {
        return this.itemModel;
    }
}
