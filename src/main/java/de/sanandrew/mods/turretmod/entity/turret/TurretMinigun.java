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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.UUID;

public class TurretMinigun
        implements ITurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "turrets/turret_minigun");
    public static final UUID TII_UUID = UUID.fromString("97E1FB65-EE36-43BA-A900-583B4BD7973A");

    private static final AxisAlignedBB RANGE_BB = new AxisAlignedBB(-20.0D, -4.0D, -20.0D, 20.0D, 10.0D, 20.0D);

    public static final int MAX_BARREL_LEFT = 0;
    public static final int BARREL_LEFT = 1;
    public static final int PREV_BARREL_LEFT = 2;
    public static final int MAX_BARREL_RIGHT = 3;
    public static final int BARREL_RIGHT = 4;
    public static final int PREV_BARREL_RIGHT = 5;
    public static final int LEFT_SHOT = 6;

    @Override
    public void entityInit(ITurretInst turretInst) {
        turretInst.setField(BARREL_LEFT, 0.0F);
        turretInst.setField(MAX_BARREL_LEFT, 0.0F);
        turretInst.setField(PREV_BARREL_LEFT, 0.0F);
        turretInst.setField(BARREL_RIGHT, 0.0F);
        turretInst.setField(MAX_BARREL_RIGHT, 0.0F);
        turretInst.setField(PREV_BARREL_RIGHT, 0.0F);
        turretInst.setField(LEFT_SHOT, false);
    }

    @Override
    public void applyEntityAttributes(ITurretInst turretInst) {
        turretInst.getEntity().getEntityAttribute(TurretAttributes.MAX_AMMO_CAPACITY).setBaseValue(512.0D);
        turretInst.getEntity().getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).setBaseValue(3.0D);
        turretInst.getEntity().getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
    }

    @Override
    public void onUpdate(ITurretInst turretInst) {
        EntityLiving turretL = turretInst.getEntity();

        if( turretL.world.isRemote ) {
            turretInst.setField(PREV_BARREL_LEFT, turretInst.getField(BARREL_LEFT));
            turretInst.setField(PREV_BARREL_RIGHT, turretInst.getField(BARREL_RIGHT));

            if( turretInst.wasShooting() ) {
                if( turretInst.getField(LEFT_SHOT) ) {
                    incrField(turretInst, MAX_BARREL_RIGHT, 90.0F);
                    turretInst.setField(LEFT_SHOT, false);
                } else {
                    incrField(turretInst, MAX_BARREL_LEFT, 90.0F);
                    turretInst.setField(LEFT_SHOT, true);
                }

                TurretModRebirth.proxy.spawnParticle(EnumParticle.MINIGUN_SHOT, turretL.posX, turretL.posY + 1.5F, turretL.posZ,
                                                     new Tuple(turretL.rotationYawHead, turretL.rotationPitch - 7.5F, turretInst.isUpsideDown(), turretInst.<Boolean>getField(LEFT_SHOT)));
            }

            if( turretInst.<Float>getField(BARREL_LEFT) < turretInst.<Float>getField(MAX_BARREL_LEFT) ) {
                incrField(turretInst, BARREL_LEFT, 90.0F / turretInst.getTargetProcessor().getMaxShootTicks() * 2.0F);
            } else {
                turretInst.setField(BARREL_LEFT, turretInst.getField(MAX_BARREL_LEFT));
            }

            if( turretInst.<Float>getField(BARREL_RIGHT) < turretInst.<Float>getField(MAX_BARREL_RIGHT) ) {
                incrField(turretInst, BARREL_RIGHT, 90.0F / turretInst.getTargetProcessor().getMaxShootTicks() * 2.0F);
            } else {
                turretInst.setField(BARREL_RIGHT, turretInst.getField(MAX_BARREL_RIGHT));
            }
        }
    }

    private static void incrField(ITurretInst turretInst, int fieldId, float value) {
        turretInst.setField(fieldId, turretInst.<Float>getField(fieldId) + value);
    }

    @Override
    public ResourceLocation getStandardTexture(ITurretInst turretInst) {
        return (turretInst.getEntity().hasCustomName() && turretInst.getEntity().getCustomNameTag().equalsIgnoreCase("silverchiren") ? Resources.TURRET_T2_MINIGUN_EE : Resources.TURRET_T2_MINIGUN).getResource();
    }

    @Override
    public ResourceLocation getGlowTexture(ITurretInst turretInst) {
        return Resources.TURRET_T2_MINIGUN_GLOW.getResource();
    }

    @Override
    public AxisAlignedBB getRangeBB(ITurretInst turretInst) {
        return RANGE_BB;
    }

    @Override
    public SoundEvent getShootSound(ITurretInst turretInst) {
        return Sounds.shoot_minigun;
    }
    @Override
    public String getName() {
        return "turret_ii_minigun";
    }

    @Override
    public UUID getId() {
        return TII_UUID;
    }

    @Override
    public float getInfoHealth() {
        return 30.0F;
    }

    @Override
    public int getInfoBaseAmmoCapacity() {
        return 512;
    }

    @Override
    public ResourceLocation getItemModel() {
        return ITEM_MODEL;
    }

    @Override
    public UUID getRecipeId() {
        return TurretAssemblyRecipes.TURRET_MK2_MG;
    }

    @Override
    public String getInfoRange() {
        return "20";
    }
}
