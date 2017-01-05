/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityTurretCrossbow
        extends EntityTurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TurretModRebirth.ID, "turrets/turret_crossbow");
    public static final UUID TI_UUID = UUID.fromString("50E1E69C-395C-486C-BB9D-41E82C8B22E2");
    public static final TurretInfo TINFO = new TurretInfo()
    {
        @Override
        public String getName() {
            return "turret_i_crossbow";
        }

        @Override
        public UUID getUUID() {
            return EntityTurretCrossbow.TI_UUID;
        }

        @Override
        public Class<? extends EntityTurret> getTurretClass() {
            return EntityTurretCrossbow.class;
        }

        @Override
        public float getTurretHealth() {
            return 20.0F;
        }

        @Override
        public int getBaseAmmoCapacity() {
            return 256;
        }

        @Override
        public ResourceLocation getModel() {
            return ITEM_MODEL;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.TURRET_MK1_CB;
        }

        @Override
        public String getInfoRange() {
            return "16";
        }
    };

    private static final AxisAlignedBB RANGE_BB = new AxisAlignedBB(-16.0D, -4.0D, -16.0D, 16.0D, 8.0D, 16.0D);

    {
        this.targetProc = new MyTargetProc();
    }

    public EntityTurretCrossbow(World world) {
        super(world);
    }

    public EntityTurretCrossbow(World world, boolean isUpsideDown, EntityPlayer player) {
        super(world, isUpsideDown, player);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).setBaseValue(20.0D);
    }

    @Override
    public ResourceLocation getStandardTexture() {
        return Resources.TURRET_T1_CROSSBOW.getResource();
    }

    @Override
    public ResourceLocation getGlowTexture() {
        return Resources.TURRET_T1_CROSSBOW_GLOW.getResource();
    }

    @Override
    public AxisAlignedBB getRangeBB() {
        return RANGE_BB;
    }

    private class MyTargetProc
            extends TargetProcessor
    {

        public MyTargetProc() {
            super(EntityTurretCrossbow.this);
        }

        @Override
        public SoundEvent getShootSound() {
            return SoundEvents.BLOCK_DISPENSER_LAUNCH;
        }

        @Override
        public SoundEvent getLowAmmoSound() {
            return SoundEvents.BLOCK_DISPENSER_FAIL;
        }
    }
}
