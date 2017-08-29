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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.UUID;

public class TurretShotgun
        implements ITurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "turrets/turret_shotgun");
    public static final UUID TI_UUID = UUID.fromString("F7991EC5-2A89-49A6-B8EA-80775973C4C5");

    private static final AxisAlignedBB RANGE_BB = new AxisAlignedBB(-16.0D, -4.0D, -16.0D, 16.0D, 8.0D, 16.0D);

//    public float barrelPos = 1.0F;
//    public float prevBarrelPos = 1.0F;
    public static final int BARREL_POS = 0;
    public static final int PREV_BARREL_POS = 1;

    @Override
    public void entityInit(ITurretInst turretInst) {
        turretInst.setField(BARREL_POS, 1.0F);
        turretInst.setField(PREV_BARREL_POS, 1.0F);
    }

    @Override
    public void applyEntityAttributes(ITurretInst turretInst) {
        turretInst.getEntity().getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).setBaseValue(20.0D);
    }

    @Override
    public void onUpdate(ITurretInst turretInst) {
        EntityLiving turretL = turretInst.getEntity();

        if( turretL.world.isRemote ) {
            if( turretInst.<Float>getField(BARREL_POS) < 1.0F ) {
                incrField(turretInst, BARREL_POS, 0.06F * 20.0F / turretInst.getTargetProcessor().getMaxShootTicks());
            } else {
                turretInst.setField(BARREL_POS, 1.0F);
            }

            if( turretInst.wasShooting() ) {
                turretInst.setField(BARREL_POS, 0.0F);
                TurretModRebirth.proxy.spawnParticle(EnumParticle.SHOTGUN_SHOT, turretL.posX, turretL.posY + 1.5F, turretL.posZ,
                                                     new Tuple(turretL.rotationYawHead, turretL.rotationPitch, turretInst.isUpsideDown()));
            }
        }
    }

    private static void incrField(ITurretInst turretInst, int fieldId, float value) {
        turretInst.setField(fieldId, turretInst.<Float>getField(fieldId) + value);
    }

    @Override
    public ResourceLocation getStandardTexture(ITurretInst turretInst) {
        return Resources.TURRET_T1_SHOTGUN.getResource();
    }

    @Override
    public ResourceLocation getGlowTexture(ITurretInst turretInst) {
        return Resources.TURRET_T1_SHOTGUN_GLOW.getResource();
    }

    @Override
    public AxisAlignedBB getRangeBB(ITurretInst turretInst) {
        return RANGE_BB;
    }

    @Override
    public SoundEvent getShootSound(ITurretInst turretInst) {
        return Sounds.shoot_shotgun;
    }

    @Override
    public String getName() {
        return "i_shotgun";
    }

    @Override
    public UUID getId() {
        return TurretShotgun.TI_UUID;
    }

    @Override
    public float getInfoHealth() {
        return 20.0F;
    }

    @Override
    public int getInfoBaseAmmoCapacity() {
        return 256;
    }

    @Override
    public ResourceLocation getItemModel() {
        return TurretShotgun.ITEM_MODEL;
    }

    @Override
    public UUID getRecipeId() {
        return TurretAssemblyRecipes.TURRET_MK1_SG;
    }

    @Override
    public String getInfoRange() {
        return "16";
    }
}
