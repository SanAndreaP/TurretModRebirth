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
import de.sanandrew.mods.turretmod.api.turret.IVariant;
import de.sanandrew.mods.turretmod.api.turret.IVariantHolder;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.Sounds;
import de.sanandrew.mods.turretmod.registry.turret.variant.SingleItemVariants;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;

@Category("cryolator")
@SuppressWarnings("WeakerAccess")
public class TurretCryolator
        implements ITurret, IVariantHolder
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "turret_cryolator");

    private static AxisAlignedBB rangeBB;

    @Value(comment = "Maximum health this turret has.", range = @Range(minD = 0.1D, maxD = 1024.0D), reqWorldRestart = true)
    public static float  maxHealth       = 20.0F;
    @Value(comment = "Maximum capacity of ammo rounds this turret can hold.", range = @Range(minI = 1, maxI = Short.MAX_VALUE), reqWorldRestart = true)
    public static int    maxAmmoCapacity = 256;
    @Value(comment = "Maximum tick time between shots. 20 ticks = 1 second.", range = @Range(minI = 1), reqWorldRestart = true)
    public static int    maxReloadTicks  = 20;
    @Value(comment = "Horizontal length of half the edge of the targeting box. The total edge length is [value * 2], with the turret centered in it.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeH          = 16.0D;
    @Value(comment = "Vertical length of the edge of the targeting box, from the turret upwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeU          = 16.0D;
    @Value(comment = "Vertical length of the edge of the targeting box, from the turret downwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeD          = 8.0D;

    public static final SingleItemVariants VARIANTS = new SingleItemVariants();

    static {
        String txPath = Resources.TURRET_T1_CRYOLATOR.resource.getPath();

        for( BlockPlanks.EnumType plank : BlockPlanks.EnumType.values() ) {
            VARIANTS.register(new ItemStack(Blocks.PLANKS, 1, plank.getMetadata()), txPath, plank.getName());
        }
    }

    @Override
    public ResourceLocation getStandardTexture(ITurretInst turretInst) {
        return turretInst.getVariant().getTexture();
    }

    @Override
    public ResourceLocation getGlowTexture(ITurretInst turretInst) {
        return Resources.TURRET_T1_CRYOLATOR_GLOW.resource;
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

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public IVariant getVariant(ResourceLocation id) {
        return VARIANTS.getOrDefault(id);
    }

    @Override
    public boolean isDefaultVariant(IVariant variant) {
        return VARIANTS.isDefaultVariant(variant);
    }

    @Override
    public void registerVariant(IVariant variant) {
        VARIANTS.register(variant);
    }
}
