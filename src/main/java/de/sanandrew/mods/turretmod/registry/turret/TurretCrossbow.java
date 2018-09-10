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
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.UUID;

public class TurretCrossbow
        implements ITurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "turrets/turret_crossbow");
    private static final UUID ID = UUID.fromString("50E1E69C-395C-486C-BB9D-41E82C8B22E2");

    private static final AxisAlignedBB RANGE_BB = new AxisAlignedBB(-16.0D, -4.0D, -16.0D, 16.0D, 8.0D, 16.0D);

    @Override
    public ResourceLocation getStandardTexture(ITurretInst turretInst) {
        return Resources.TURRET_T1_CROSSBOW.resource;
    }

    @Override
    public ResourceLocation getGlowTexture(ITurretInst turretInst) {
        return Resources.TURRET_T1_CROSSBOW_GLOW.resource;
    }

    @Override
    public AxisAlignedBB getRangeBB(ITurretInst turretInst) {
        return RANGE_BB;
    }

    @Override
    public SoundEvent getShootSound(ITurretInst turretInst) {
        return SoundEvents.BLOCK_DISPENSER_LAUNCH;
    }

    @Override
    public String getName() {
        return "i_crossbow";
    }

    @Override
    public UUID getId() {
        return de.sanandrew.mods.turretmod.registry.turret.TurretCrossbow.ID;
    }

    @Override
    public ResourceLocation getItemModel() {
        return de.sanandrew.mods.turretmod.registry.turret.TurretCrossbow.ITEM_MODEL;
    }

    @Override
    public int getTier() {
        return 1;
    }

    @Override
    public float getHealth() {
        return Config.maxHealth;
    }

    @Override
    public int getAmmoCapacity() {
        return Config.maxAmmoCapacity;
    }

    @Override
    public int getReloadTicks() {
        return Config.maxReloadTicks;
    }

    @Category("Crossbow")
    @SuppressWarnings("WeakerAccess")
    public static final class Config
    {
        @Value(range = @Range(minD = 0.1F, maxD = 1024.0D))
        public static float maxHealth = 20.0F;
        @Value(range = @Range(minI = 1, maxI = Short.MAX_VALUE))
        public static int maxAmmoCapacity = 256;
        @Value(range = @Range(minI = 1))
        public static int maxReloadTicks = 20;
        @Value(range = @Range(minD = 0.0F, maxD = 1024.0D))
        public static float projDamage = 2.0F;
        @Value(range = @Range(minD = 0.0F, maxD = 256.0D))
        public static float projKnockbackH = 0.01F;
        @Value(range = @Range(minD = 0.0F, maxD = 256.0D))
        public static float projKnockbackV = 0.2F;
    }
}
