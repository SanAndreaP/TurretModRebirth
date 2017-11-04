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
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileMinigunPebble;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TurretAmmoMinigunShell
        implements IAmmunition<EntityProjectileMinigunPebble>
{
    private final String name;
    private final UUID id;
    private final int capacity;
    private final ResourceLocation itemModel;

    public TurretAmmoMinigunShell(boolean isMulti) {
        this.name = isMulti ? "minigun_shell_pack" : "minigun_shell";
        this.id = isMulti ? Ammunitions.MGSHELL_PACK : Ammunitions.MGSHELL;
        this.capacity = isMulti ? 64 : 4;
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
    public Class<EntityProjectileMinigunPebble> getEntityClass() {
        return EntityProjectileMinigunPebble.class;
    }

    @Override
    public ITurret getTurret() {
        return Turrets.MINIGUN;
    }

    @Override
    public float getInfoDamage() {
        return 0.3F;
    }

    @Override
    public UUID getTypeId() {
        return Ammunitions.MGSHELL;
    }

    @Override
    public UUID getGroupId() {
        return Ammunitions.MGSHELL;
    }

    @Override
    public String getInfoName() {
        return "minigun_shell";
    }

    @Override
    public ItemStack getStoringAmmoItem() {
        return AmmunitionRegistry.INSTANCE.getAmmoItem(AmmunitionRegistry.INSTANCE.getType(Ammunitions.MGSHELL));
    }

    @Override
    public EntityProjectileMinigunPebble getEntity(ITurretInst turretInst) {
        return new EntityProjectileMinigunPebble(turretInst.getEntity().world, turretInst.getEntity(), turretInst.getTargetProcessor().getTarget());
    }

    @Override
    public ResourceLocation getModel() {
        return this.itemModel;
    }
}
