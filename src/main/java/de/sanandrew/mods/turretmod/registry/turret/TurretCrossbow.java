/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.config.Category;
import de.sanandrew.mods.sanlib.lib.util.config.Range;
import de.sanandrew.mods.sanlib.lib.util.config.Value;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
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
        implements ITurret
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

    public static final DualVariants VARIANTS = new Variants();

    @Override
    public ResourceLocation getStandardTexture(ITurretInst turretInst) {
        return VARIANTS.get(turretInst.getVariant()).texture;
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

    private static final class Variants
            extends DualVariants
    {
        Variants() {
            super(Resources.TURRET_T1_CROSSBOW.location);

            for( BlockPlanks.EnumType pType : BlockPlanks.EnumType.values() ) {
                put(0, pType.getMetadata(), "cobblestone", pType.getName());       // COBBLE
                put(1, pType.getMetadata(), "mossy_cobblestone", pType.getName()); // MOSSY_COBBLE
                put(2, pType.getMetadata(), "granite", pType.getName());           // GRANITE
                put(3, pType.getMetadata(), "diorite", pType.getName());           // DIORITE
                put(4, pType.getMetadata(), "andesite", pType.getName());          // ANDESITE
            }
        }

        @Override
        public int checkBase(ItemStack stack) {
            if( ItemStackUtils.isBlock(stack, Blocks.COBBLESTONE) ) {
                return 0;
            } else if( ItemStackUtils.isBlock(stack, Blocks.MOSSY_COBBLESTONE) ) {
                return 1;
            } else if( ItemStackUtils.isBlock(stack, Blocks.STONE) ) {
                switch( stack.getMetadata() ) {
                    case 1:
                        return 2;
                    case 3:
                        return 3;
                    case 5:
                        return 4;
                }
            }

            return -1;
        }

        @Override
        public int checkFrame(ItemStack stack) {
            if( ItemStackUtils.isBlock(stack, Blocks.PLANKS) ) {
                return stack.getMetadata();
            }

            return -1;
        }
    }
}
