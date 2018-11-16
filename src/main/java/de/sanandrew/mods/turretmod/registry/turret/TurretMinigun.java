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

@Category("minigun")
@SuppressWarnings("WeakerAccess")
public class TurretMinigun
        implements ITurret
{
    private static final ResourceLocation REGISTRY_ID = new ResourceLocation(TmrConstants.ID, "turret.minigun");

    private static AxisAlignedBB rangeBB;

    @Value(comment = "Maximum health this turret has.", range = @Range(minD = 0.1D, maxD = 1024.0D), reqWorldRestart = true)
    public static float health = 30.0F;
    @Value(comment = "Capacity of ammo rounds this turret can hold.", range = @Range(minI = 1, maxI = Short.MAX_VALUE), reqWorldRestart = true)
    public static int ammoCapacity = 512;
    @Value(comment = "Maximum tick time between shots. 20 ticks = 1 second.", range = @Range(minI = 1), reqWorldRestart = true)
    public static int reloadTicks = 3;
    @Value(comment = "Horizontal length of half the edge of the targeting box. The total edge length is [value * 2], with the turret centered in it.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeH = 20.0D;
    @Value(comment = "Vertical length of the edge of the targeting box, from the turret upwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeU = 20.0D;
    @Value(comment = "Vertical length of the edge of the targeting box, from the turret downwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeD = 4.0D;

    @Override
    public void onUpdate(ITurretInst turretInst) {
        EntityLiving turretL = turretInst.get();

        MyRAM ram = turretInst.getRAM(MyRAM::new);

        ram.prevBarrelLeft = ram.barrelLeft;
        ram.prevBarrelRight = ram.barrelRight;

        if( ram.barrelLeft < ram.maxBarrelLeft ) {
            ram.barrelLeft += 90.0F / turretInst.getTargetProcessor().getMaxShootTicks() * 2.0F;
        } else {
            ram.barrelLeft = ram.maxBarrelLeft;
        }

        if( ram.barrelRight < ram.maxBarrelRight ) {
            ram.barrelRight += 90.0F / turretInst.getTargetProcessor().getMaxShootTicks() * 2.0F;
        } else {
            ram.barrelRight = ram.maxBarrelRight;
        }

        if( turretInst.wasShooting() ) {
            if( ram.isLeftShot ) {
                ram.maxBarrelRight += 90.0F;
                ram.isLeftShot = false;
            } else {
                ram.maxBarrelLeft += 90.0F;
                ram.isLeftShot = true;
            }

            if( turretL.world.isRemote ) {
                TurretModRebirth.proxy.spawnParticle(EnumParticle.MINIGUN_SHOT, turretL.posX, turretL.posY + 1.5F, turretL.posZ,
                                                     new Tuple(turretL.rotationYawHead, turretL.rotationPitch - 7.5F, ram.isLeftShot));
            }
        }
    }

    @Override
    public ResourceLocation getStandardTexture(ITurretInst turretInst) {
        return (turretInst.get().hasCustomName() && turretInst.get().getCustomNameTag().equalsIgnoreCase("silverchiren") ? Resources.TURRET_T2_MINIGUN_EE : Resources.TURRET_T2_MINIGUN).resource;
    }

    @Override
    public ResourceLocation getGlowTexture(ITurretInst turretInst) {
        return Resources.TURRET_T2_MINIGUN_GLOW.resource;
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
        return Sounds.SHOOT_MINIGUN;
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return REGISTRY_ID;
    }

    @Override
    public int getTier() {
        return 2;
    }

    public static final class MyRAM implements ITurretRAM {
        public float barrelLeft = 0.0F;
        public float barrelRight = 0.0F;

        public float prevBarrelLeft = 0.0F;
        public float prevBarrelRight = 0.0F;

        float maxBarrelLeft = 0.0F;
        float maxBarrelRight = 0.0F;

        public boolean isLeftShot = false;
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

    @Override
    public AttackType getAttackType() {
        return AttackType.AIR;
    }
}
