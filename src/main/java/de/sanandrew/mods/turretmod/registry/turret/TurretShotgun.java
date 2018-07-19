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

    @Override
    public void applyEntityAttributes(ITurretInst turretInst) {
        turretInst.getEntity().getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).setBaseValue(20.0D);
    }

    @Override
    public void onUpdate(ITurretInst turretInst) {
        EntityLiving turretL = turretInst.getEntity();

        if( turretL.world.isRemote ) {
            MyRAM ram = turretInst.getRAM(MyRAM::new);
            ram.prevBarrelPos = ram.barrelPos;

            if( ram.barrelPos < 1.0F ) {
                ram.barrelPos += 0.06F * 20.0F / turretInst.getTargetProcessor().getMaxShootTicks();
            } else {
                ram.barrelPos = 1.0F;
            }

            if( turretInst.wasShooting() ) {
                ram.barrelPos = 0.0F;
                TurretModRebirth.proxy.spawnParticle(EnumParticle.SHOTGUN_SHOT, turretL.posX, turretL.posY + 1.5F, turretL.posZ,
                        new Tuple(turretL.rotationYawHead, turretL.rotationPitch, turretInst.isUpsideDown()));
            }
        }
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
        return Sounds.SHOOT_SHOTGUN;
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
    public ResourceLocation getItemModel() {
        return TurretShotgun.ITEM_MODEL;
    }

    @Override
    public int getTier() {
        return 1;
    }

    public static class MyRAM implements ITurretRAM
    {
        public float barrelPos = 1.0F;
        public float prevBarrelPos = 1.0F;
    }

    @Override
    public float getHealth() {
        return 20.0F;
    }

    @Override
    public int getAmmoCapacity() {
        return 256;
    }
}
