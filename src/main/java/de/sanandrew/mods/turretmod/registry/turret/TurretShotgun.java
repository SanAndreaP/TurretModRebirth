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
import de.sanandrew.mods.sanlib.lib.util.config.Category;
import de.sanandrew.mods.sanlib.lib.util.config.Range;
import de.sanandrew.mods.sanlib.lib.util.config.Value;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretRAM;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.Sounds;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;
import java.util.UUID;

@Category("shotgun")
@SuppressWarnings("WeakerAccess")
public class TurretShotgun
        implements ITurret
{
    private static final UUID ID = UUID.fromString("F7991EC5-2A89-49A6-B8EA-80775973C4C5");
    private static final ResourceLocation REGISTRY_ID = new ResourceLocation(TmrConstants.ID, "turret.shotgun");

    private static AxisAlignedBB rangeBB;

    @Value(comment = "Maximum health this turret has.", range = @Range(minD = 0.1D, maxD = 1024.0D), reqWorldRestart = true)
    public static float health = 20.0F;
    @Value(comment = "Capacity of ammo rounds this turret can hold.", range = @Range(minI = 1, maxI = Short.MAX_VALUE), reqWorldRestart = true)
    public static int ammoCapacity = 256;
    @Value(comment = "Maximum tick time between shots. 20 ticks = 1 second.", range = @Range(minI = 1), reqWorldRestart = true)
    public static int reloadTicks = 20;
    @Value(comment = "Horizontal length of half the edge of the targeting box. The total edge length is [value * 2], with the turret centered in it.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeH = 16.0D;
    @Value(comment = "Vertical length of the edge of the targeting box, from the turret upwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeU = 8.0D;
    @Value(comment = "Vertical length of the edge of the targeting box, from the turret downwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeD = 4.0D;

    @Override
    public void onUpdate(ITurretInst turretInst) {
        EntityLiving turretL = turretInst.get();

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
                        new Tuple(turretL.rotationYawHead, turretL.rotationPitch));
            }
        }
    }

    @Override
    public ResourceLocation getStandardTexture(ITurretInst turretInst) {
        return Resources.TURRET_T1_SHOTGUN.resource;
    }

    @Override
    public ResourceLocation getGlowTexture(ITurretInst turretInst) {
        return Resources.TURRET_T1_SHOTGUN_GLOW.resource;
    }

    @Override
    public AxisAlignedBB getRangeBB(ITurretInst turretInst) {
        if( rangeBB == null ) {
            rangeBB = new AxisAlignedBB(-rangeH, -rangeD, -rangeH, rangeH, rangeU, rangeH);
        }
        return rangeBB;
    }

    @Override
    public SoundEvent getShootSound(ITurretInst turretInst) {
        return Sounds.SHOOT_SHOTGUN;
    }

    @Override
    public UUID getId() {
        return TurretShotgun.ID;
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
        return health;
    }

    @Override
    public int getAmmoCapacity() {
        return ammoCapacity;
    }

    @Override
    public int getReloadTicks() {
        return reloadTicks;
    }

    @Nonnull
    @Override
    public ResourceLocation getRegistryId() {
        return REGISTRY_ID;
    }
}
