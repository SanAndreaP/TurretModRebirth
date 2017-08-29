/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretRAM;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.Sounds;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.UUID;

public class TurretRevolver
        implements ITurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "turrets/turret_revolver");
    public static final UUID TII_UUID = UUID.fromString("4449D836-F122-409A-8E6C-D7B7438FD08C");

    private static final AxisAlignedBB RANGE_BB = new AxisAlignedBB(-20.0D, -4.0D, -20.0D, 20.0D, 10.0D, 20.0D);

    @Override
    public void applyEntityAttributes(ITurretInst turretInst) {
        turretInst.getEntity().getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).setBaseValue(15.0D);
        turretInst.getEntity().getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
    }

    @Override
    public void onUpdate(ITurretInst turretInst) {
        EntityLiving turretL = turretInst.getEntity();

        MyRAM ram = turretInst.getRAM(MyRAM::new);
        ram.prevBarrelLeft = ram.barrelLeft;
        ram.prevBarrelRight = ram.barrelRight;

        if( ram.barrelLeft < 1.0F ) {
            ram.barrelLeft += 0.06F * 20.0F / turretInst.getTargetProcessor().getMaxShootTicks();
        } else {
            ram.barrelLeft = 1.0F;
        }

        if( ram.barrelRight < 1.0F ) {
            ram.barrelRight += 0.06F * 20.0F / turretInst.getTargetProcessor().getMaxShootTicks();
        } else {
            ram.barrelRight = 1.0F;
        }

        if( turretInst.wasShooting() ) {
            float partShift;
            if( ram.isLeftShot ) {
                ram.barrelRight = 0.0F;
                ram.isLeftShot = false;
                partShift = 10.0F;
            } else {
                ram.barrelLeft = 0.0F;
                ram.isLeftShot = true;
                partShift = -10.0F;
            }

            if( turretL.world.isRemote ) {
                TurretModRebirth.proxy.spawnParticle(EnumParticle.SHOTGUN_SHOT, turretL.posX, turretL.posY + 1.5F, turretL.posZ,
                                                     new Tuple(turretL.rotationYawHead + partShift, turretL.rotationPitch, turretInst.isUpsideDown()));
            }
        }
    }

    @Override
    public ResourceLocation getStandardTexture(ITurretInst turretInst) {
        return Resources.TURRET_T2_REVOLVER.getResource();
    }

    @Override
    public ResourceLocation getGlowTexture(ITurretInst turretInst) {
        return Resources.TURRET_T2_REVOLVER_GLOW.getResource();
    }

    @Override
    public AxisAlignedBB getRangeBB(ITurretInst turretInst) {
        return RANGE_BB;
    }

    @Override
    public SoundEvent getShootSound(ITurretInst turretInst) {
        return Sounds.shoot_revolver;
    }

    @Override
    public String getName() {
        return "ii_revolver";
    }

    @Override
    public UUID getId() {
        return TurretRevolver.TII_UUID;
    }

    @Override
    public float getInfoHealth() {
        return 30.0F;
    }

    @Override
    public int getInfoBaseAmmoCapacity() {
        return 256;
    }

    @Override
    public ResourceLocation getItemModel() {
        return TurretRevolver.ITEM_MODEL;
    }

    @Override
    public UUID getRecipeId() {
        return TurretAssemblyRecipes.TURRET_MK2_RV;
    }

    @Override
    public String getInfoRange() {
        return "20";
    }

    public static class MyRAM implements ITurretRAM
    {
        public float barrelLeft = 1.0F;
        public float barrelRight = 1.0F;

        public float prevBarrelLeft = 1.0F;
        public float prevBarrelRight = 1.0F;

        public boolean isLeftShot = false;
    }
}
