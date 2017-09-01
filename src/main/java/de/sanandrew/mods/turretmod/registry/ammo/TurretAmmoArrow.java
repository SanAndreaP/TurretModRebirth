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
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.UUID;

public abstract class TurretAmmoArrow
        implements IAmmunition<EntityProjectileCrossbowBolt>
{
    private final String name;
    private final UUID uuid;
    private final int capacity;
    private final ResourceLocation itemModel;

    public TurretAmmoArrow(boolean quiver) {
        this.name = quiver ? "arrow_lrg" : "arrow_sng";
        this.uuid = quiver ? Ammunitions.QUIVER : Ammunitions.ARROW;
        this.capacity = quiver ? 16 : 1;
        this.itemModel = new ResourceLocation(TmrConstants.ID, "turret_ammo/" + (quiver ? "arrow_pack" : "arrow"));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public UUID getId() {
        return this.uuid;
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
    public String getInfoName() {
        return "arrow";
    }

    @Override
    @Nonnull
    public ItemStack getStoringAmmoItem() {
        return ItemRegistry.turret_ammo.getAmmoItem(1, AmmunitionRegistry.INSTANCE.getType(Ammunitions.ARROW));
    }

    @Override
    public EntityProjectileCrossbowBolt getEntity(ITurretInst turretInst) {
        return new EntityProjectileCrossbowBolt(turretInst.getEntity().world, turretInst.getEntity(), turretInst.getTargetProcessor().getTarget());
    }

    @Override
    public ResourceLocation getModel() {
        return itemModel;
    }

    public static class Single
            extends TurretAmmoArrow
    {
        public Single() {
            super(false);
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.ARROW_SNG;
        }
    }

    public static class Quiver
            extends TurretAmmoArrow
    {
        public Quiver() {
            super(true);
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.ARROW_MTP;
        }
    }
}
