/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.Sounds;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.UUID;

public class TurretRevolver
        implements ITurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "turrets/turret_revolver");
    public static final UUID TII_UUID = UUID.fromString("4449D836-F122-409A-8E6C-D7B7438FD08C");

    private static final AxisAlignedBB RANGE_BB = new AxisAlignedBB(-20.0D, -4.0D, -20.0D, 20.0D, 10.0D, 20.0D);

    public static final int BARREL_LEFT = 0;
    public static final int PREV_BARREL_LEFT = 1;
    public static final int BARREL_RIGHT = 2;
    public static final int PREV_BARREL_RIGHT = 3;
    public static final int LEFT_SHOT = 4;

    @Override
    public void entityInit(ITurretInst turretInst) {
        turretInst.setField(BARREL_LEFT, 1.0F);
        turretInst.setField(PREV_BARREL_LEFT, 1.0F);
        turretInst.setField(BARREL_RIGHT, 1.0F);
        turretInst.setField(PREV_BARREL_RIGHT, 1.0F);
        turretInst.setField(LEFT_SHOT, false);
    }

    @Override
    public void applyEntityAttributes(ITurretInst turretInst) {
        turretInst.getEntity().getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).setBaseValue(15.0D);
        turretInst.getEntity().getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
    }

    @Override
    public void onUpdate(ITurretInst turretInst) {
        EntityLiving turretL = turretInst.getEntity();

        if( turretL.world.isRemote ) {
            turretInst.setField(PREV_BARREL_LEFT, turretInst.getField(BARREL_LEFT));
            turretInst.setField(PREV_BARREL_RIGHT, turretInst.getField(BARREL_RIGHT));

            if( turretInst.wasShooting() ) {
                float partShift;
                if( turretInst.getField(LEFT_SHOT) ) {
                    turretInst.setField(BARREL_RIGHT, 0.0F);
                    turretInst.setField(LEFT_SHOT, false);
                    partShift = 10.0F;
                } else {
                    turretInst.setField(BARREL_LEFT, 0.0F);
                    turretInst.setField(LEFT_SHOT, true);
                    partShift = 10.0F;
                }

                TurretModRebirth.proxy.spawnParticle(EnumParticle.SHOTGUN_SHOT, turretL.posX, turretL.posY + 1.5F, turretL.posZ,
                                                     new Tuple(turretL.rotationYawHead + partShift, turretL.rotationPitch, turretInst.isUpsideDown()));
            }

            if( turretInst.<Float>getField(BARREL_LEFT) < 1.0F ) {
                incrField(turretInst, BARREL_LEFT, 0.06F * 20.0F / turretInst.getTargetProcessor().getMaxShootTicks());
            } else {
                turretInst.setField(BARREL_LEFT, 1.0F);
            }

            if( turretInst.<Float>getField(BARREL_RIGHT) < 1.0F ) {
                incrField(turretInst, BARREL_RIGHT, 0.06F * 20.0F / turretInst.getTargetProcessor().getMaxShootTicks());
            } else {
                turretInst.setField(BARREL_RIGHT, 1.0F);
            }
        }
    }

    private static void incrField(ITurretInst turretInst, int fieldId, float value) {
        turretInst.setField(fieldId, turretInst.<Float>getField(fieldId) + value);
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
        return "turret_ii_revolver";
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
}
