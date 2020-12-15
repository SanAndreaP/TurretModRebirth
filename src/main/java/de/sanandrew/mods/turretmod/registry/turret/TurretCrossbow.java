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
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;

@Category("crossbow")
public class TurretCrossbow
        implements ITurret, IVariantHolder
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "turret_crossbow");

    private static AxisAlignedBB rangeBB;

    @Value(comment = "Maximum health this turret has.", range = @Range(minD = 0.1D, maxD = 1024.0D), reqWorldRestart = true)
    public static float  health       = 20.0F;
    @Value(comment = "Capacity of ammo rounds this turret can hold.", range = @Range(minI = 1, maxI = Short.MAX_VALUE), reqWorldRestart = true)
    public static int    ammoCapacity = 256;
    @Value(comment = "Maximum tick time between shots. 20 ticks = 1 second.", range = @Range(minI = 1), reqWorldRestart = true)
    public static int    reloadTicks  = 20;
    @Value(comment = "Horizontal length of half the edge of the targeting box. The total edge length is [value * 2], with the turret centered in it.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeH       = 16.0D;
    @Value(comment = "Vertical length of the edge of the targeting box, from the turret upwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeU       = 8.0D;
    @Value(comment = "Vertical length of the edge of the targeting box, from the turret downwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeD       = 4.0D;

    public static final DualItemVariants VARIANTS = new DualItemVariants();

    static {
        for( BlockPlanks.EnumType pType : BlockPlanks.EnumType.values() ) {
            ItemStack plank = new ItemStack(Blocks.PLANKS, 1, pType.getMetadata());
            String plankName = pType.getName();
            String txPath = Resources.TURRET_T1_CROSSBOW.resource.getPath();

            VARIANTS.register(new ItemStack(Blocks.COBBLESTONE, 1, 0), plank,
                              VARIANTS.buildVariant(TmrConstants.ID, txPath, "cobblestone", plankName));       // COBBLE
            VARIANTS.register(new ItemStack(Blocks.MOSSY_COBBLESTONE, 1, 0), plank,
                              VARIANTS.buildVariant(TmrConstants.ID, txPath, "mossy_cobblestone", plankName)); // MOSSY_COBBLE
            VARIANTS.register(new ItemStack(Blocks.STONE, 1, 1), plank,
                              VARIANTS.buildVariant(TmrConstants.ID, txPath, "granite", plankName));           // GRANITE
            VARIANTS.register(new ItemStack(Blocks.STONE, 1, 3), plank,
                              VARIANTS.buildVariant(TmrConstants.ID, txPath, "diorite", plankName));           // DIORITE
            VARIANTS.register(new ItemStack(Blocks.STONE, 1, 5), plank,
                              VARIANTS.buildVariant(TmrConstants.ID, txPath, "andesite", plankName));          // ANDESITE
        }
    }

    @Override
    public ResourceLocation getStandardTexture(ITurretInst turretInst) {
        return turretInst.getVariant().getTexture();
    }

    @Override
    public ResourceLocation getGlowTexture(ITurretInst turretInst) {
        return Resources.TURRET_T1_CROSSBOW_GLOW.resource;
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
        return SoundEvents.BLOCK_DISPENSER_LAUNCH;
    }

    @Override
    public int getTier() {
        return 1;
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
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public IVariant getVariant(ITurretInst turretInst, ResourceLocation id) {
        return VARIANTS.getOrDefault(id);
    }

    @Override
    public void registerVariant(IVariant variant) {
        VARIANTS.register(variant);
    }

    @Override
    public boolean isDefaultVariant(IVariant variant) {
        return VARIANTS.isDefaultVariant(variant);
    }
}
