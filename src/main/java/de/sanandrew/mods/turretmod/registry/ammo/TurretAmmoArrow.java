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
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileCrossbowBolt;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TurretAmmoArrow
        implements IAmmunition<EntityProjectileCrossbowBolt>
{
    private final String name;
    private final UUID id;
    private final int capacity;
    private final ResourceLocation itemModel;

    public TurretAmmoArrow(boolean isMulti) {
        this.name = isMulti ? "arrow_pack" : "arrow";
        this.id = isMulti ? Ammunitions.QUIVER : Ammunitions.ARROW;
        this.capacity = isMulti ? 16 : 1;
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
    public Class<EntityProjectileCrossbowBolt> getEntityClass() {
        return EntityProjectileCrossbowBolt.class;
    }

    @Override
    public ITurret getTurret() {
        return Turrets.CROSSBOW;
    }

    @Override
    public float getInfoDamage() {
        return 3.0F;
    }

    @Override
    public UUID getTypeId() {
        return Ammunitions.ARROW;
    }

    @Override
    public UUID getGroupId() {
        return Ammunitions.ARROW;
    }

    @Override
    public String getGroupName() {
        return "arrow";
    }

    @Override
    @Nonnull
    public ItemStack getStoringAmmoItem() {
        return AmmunitionRegistry.INSTANCE.getAmmoItem(AmmunitionRegistry.INSTANCE.getType(Ammunitions.ARROW));
    }

    @Override
    public EntityProjectileCrossbowBolt getEntity(ITurretInst turretInst) {
        return new EntityProjectileCrossbowBolt(turretInst.getEntity().world, turretInst.getEntity(), turretInst.getTargetProcessor().getTarget());
    }

    @Override
    public ResourceLocation getModel() {
        return itemModel;
    }
}
