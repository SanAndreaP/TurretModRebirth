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
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.projectile.CrossbowBolt;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TurretAmmoArrow
        implements IAmmunition<CrossbowBolt>
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

    @Nonnull
    @Override
    public IAmmunitionGroup getGroup() {
        return Ammunitions.Groups.ARROW;
    }

    @Override
    public Class<CrossbowBolt> getEntityClass() {
        return CrossbowBolt.class;
    }

    @Override
    public float getDamageInfo() {
        return 3.0F;
    }

    @Override
    public UUID getTypeId() {
        return Ammunitions.ARROW;
    }

    @Override
    @Nonnull
    public ItemStack getStoringAmmoItem() {
        return AmmunitionRegistry.INSTANCE.getAmmoItem(AmmunitionRegistry.INSTANCE.getType(Ammunitions.ARROW));
    }

    @Override
    public CrossbowBolt getEntity(ITurretInst turretInst) {
        return new CrossbowBolt(turretInst.get().world, turretInst.get(), turretInst.getTargetProcessor().getTarget());
    }

    @Override
    public ResourceLocation getModel() {
        return itemModel;
    }
}
