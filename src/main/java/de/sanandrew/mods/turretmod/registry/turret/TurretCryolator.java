/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.sanlib.lib.util.config.Category;
import de.sanandrew.mods.sanlib.lib.util.config.Range;
import de.sanandrew.mods.sanlib.lib.util.config.Value;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.Sounds;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.UUID;

@Category("cryolator")
@SuppressWarnings("WeakerAccess")
public class TurretCryolator
        implements ITurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "turrets/turret_cryolator");
    private static final UUID ID = UUID.fromString("3AF4D8C3-FCFC-42B0-98A3-BFB669AA7CE6");

    private static AxisAlignedBB rangeBB;

    @Value(comment = "maximum health this turret has.", range = @Range(minD = 0.1D, maxD = 1024.0D), reqWorldRestart = true)
    public static float maxHealth = 20.0F;
    @Value(comment = "maximum capacity of ammo rounds this turret can hold.", range = @Range(minI = 1, maxI = Short.MAX_VALUE), reqWorldRestart = true)
    public static int maxAmmoCapacity = 256;
    @Value(comment = "maximum tick time between shots. 20 ticks = 1 second.", range = @Range(minI = 1), reqWorldRestart = true)
    public static int maxReloadTicks = 20;
    @Value(comment = "horizontal length of half the edge of the targeting box. The total edge length is [value * 2], with the turret centered in it.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeH = 16.0D;
    @Value(comment = "vertical length of the edge of the targeting box, from the turret upwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeU = 16.0D;
    @Value(comment = "vertical length of the edge of the targeting box, from the turret downwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeD = 8.0D;
    @Value(comment = "base damage a projectile can deal to a target.", range = @Range(minD = 0.0D, maxD = 1024.0D))
    public static float projDamage = 0.0F;
    @Value(comment = "multiplier applied to the speed with which the projectile travels.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float projSpeed = 1.5F;
    @Value(comment = "how much the projectile curves down/up. negative values let projectiles go up, whereas positive values go down.", range = @Range(minD = -10.0D, maxD = 10.0D))
    public static float projArc = 0.05F;
    @Value(comment = "horizontal knockback strength a projectile can apply. Vanilla arrows have a value of 0.1.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float projKnockbackH = 0.0F;
    @Value(comment = "vertical (y) knockback strength a projectile can apply. Vanilla arrows have a value of 0.1.", range = @Range(minD = 0.0D, maxD = 256.0D))
    public static float projKnockbackV = 0.0F;
    @Value(comment = "how much more inaccurate a projectiles' trajectory vector becomes. Higher values result in less accuracy.", range = @Range(minD = 0.0D, maxD = 10.0D))
    public static float projScatter = 0.1F;

    @Override
    public ResourceLocation getStandardTexture(ITurretInst turretInst) {
        return Resources.TURRET_T1_SNOWBALL.resource;
    }

    @Override
    public ResourceLocation getGlowTexture(ITurretInst turretInst) {
        return Resources.TURRET_T1_SNOWBALL_GLOW.resource;
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
        return Sounds.SHOOT_CRYOLATOR;
    }

    @Override
    public String getName() {
        return "i_snowball";
    }

    @Override
    public UUID getId() {
        return TurretCryolator.ID;
    }

    @Override
    public ResourceLocation getItemModel() {
        return TurretCryolator.ITEM_MODEL;
    }

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    public float getHealth() {
        return maxHealth;
    }

    @Override
    public int getAmmoCapacity() {
        return maxAmmoCapacity;
    }

    @Override
    public int getReloadTicks() {
        return maxReloadTicks;
    }
}
