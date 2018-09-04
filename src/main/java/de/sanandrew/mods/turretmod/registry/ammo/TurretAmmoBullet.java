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
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileBullet;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TurretAmmoBullet
        implements IAmmunition<EntityProjectileBullet>
{
    private final String name;
    private final UUID id;
    private final int capacity;
    private final ResourceLocation itemModel;

    public TurretAmmoBullet(boolean isMulti) {
        this.name = isMulti ? "bullet_pack" : "bullet";
        this.id = isMulti ? Ammunitions.BULLET_PACK : Ammunitions.BULLET;
        this.capacity = isMulti ? 32 : 2;
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
    public Class<EntityProjectileBullet> getEntityClass() {
        return EntityProjectileBullet.class;
    }

    @Override
    public ITurret getTurret() {
        return Turrets.REVOLVER;
    }

    @Override
    public float getInfoDamage() {
        return 2.75F;
    }

    @Override
    public UUID getTypeId() {
        return Ammunitions.BULLET;
    }

    @Override
    public UUID getGroupId() {
        return Ammunitions.BULLET;
    }

    @Override
    public String getGroupName() {
        return "bullet";
    }

    @Override
    @Nonnull
    public ItemStack getStoringAmmoItem() {
        return AmmunitionRegistry.INSTANCE.getAmmoItem(AmmunitionRegistry.INSTANCE.getType(Ammunitions.BULLET));
    }

    @Override
    public EntityProjectileBullet getEntity(ITurretInst turretInst) {
        return new EntityProjectileBullet(turretInst.getEntity().world, turretInst.getEntity(), turretInst.getTargetProcessor().getTarget());
    }

    @Override
    public ResourceLocation getModel() {
        return this.itemModel;
    }
}
