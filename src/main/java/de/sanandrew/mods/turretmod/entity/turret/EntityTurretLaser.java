/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.turretmod.client.audio.SoundLaser;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class EntityTurretLaser
        extends EntityTurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TurretModRebirth.ID, "turrets/turret_laser");
    public static final UUID TIII_UUID = UUID.fromString("F6196022-3F9D-4D3F-B3C1-9ED644DB436B");
    public static final TurretInfo TINFO = new TurretInfo() {
        @Override
        public String getName() {
            return "turret_iii_laser";
        }

        @Override
        public UUID getUUID() {
            return EntityTurretLaser.TIII_UUID;
        }

        @Override
        public Class<? extends EntityTurret> getTurretClass() {
            return EntityTurretLaser.class;
        }

        @Override
        public float getTurretHealth() {
            return 40.0F;
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
            return TurretAssemblyRecipes.TURRET_MK3_LR;
        }

        @Override
        public String getInfoRange() {
            return "24";
        }
    };

    @SideOnly(Side.CLIENT)
    public SoundLaser laserSound;

    {
        this.targetProc = new MyTargetProc();
    }

    public EntityTurretLaser(World world) {
        super(world);
    }

    public EntityTurretLaser(World world, boolean isUpsideDown, EntityPlayer player) {
        super(world, isUpsideDown, player);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).setBaseValue(5.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
    }

    @Override
    public ResourceLocation getStandardTexture() {
        return Resources.TURRET_T3_LASER.getResource();
    }

    @Override
    public ResourceLocation getGlowTexture() {
        return Resources.TURRET_T3_LASER_GLOW.getResource();
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if( this.world.isRemote ) {
            TurretModRebirth.proxy.playTurretLaser(this);
        }
    }

    private static final AxisAlignedBB UPPER_BB = new AxisAlignedBB(-24.0D, -4.0D, -24.0D, 24.0D, 12.0D, 24.0D);
    private static final AxisAlignedBB LOWER_BB = new AxisAlignedBB(-24.0D, -12.0D, -24.0D, 24.0D, 4.0D, 24.0D);
    private class MyTargetProc
            extends TargetProcessor
    {

        public MyTargetProc() {
            super(EntityTurretLaser.this);
        }

        @Override
        public AxisAlignedBB getRangeBB() {
            return (this.turret.isUpsideDown ? LOWER_BB : UPPER_BB).offset(this.turret.posX, this.turret.posY, this.turret.posZ);
        }

        @Override
        public SoundEvent getShootSound() {
            return null;
        }

        @Override
        public SoundEvent getLowAmmoSound() {
            return SoundEvents.BLOCK_DISPENSER_FAIL;
        }
    }
}
