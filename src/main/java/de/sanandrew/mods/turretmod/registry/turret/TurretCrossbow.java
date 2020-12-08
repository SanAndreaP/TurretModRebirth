/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.turret;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;
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

    @Override
    public ResourceLocation getStandardTexture(ITurretInst turretInst) {
        return Variant.get(turretInst.getVariant()).texture;
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

    public static final class Variant
    {
        private static final Table<Integer, Integer, Variant> VARIANTS = TreeBasedTable.create();

        public final int id;
        public final String suffix;
        public final ResourceLocation texture;

        static {
            for( BlockPlanks.EnumType pType : BlockPlanks.EnumType.values() ) {
                put(0, pType.getMetadata(), "cobblestone", pType.getName());       // COBBLE
                put(1, pType.getMetadata(), "mossy_cobblestone", pType.getName()); // MOSSY_COBBLE
                put(2, pType.getMetadata(), "granite", pType.getName());           // GRANITE
                put(3, pType.getMetadata(), "diorite", pType.getName());           // DIORITE
                put(4, pType.getMetadata(), "andesite", pType.getName());          // ANDESITE
            }
        }

        private static void put(int cobbleId, int plankId, String cobbleTx, String plankTx) {
            VARIANTS.put(cobbleId, plankId, new Variant(cobbleId, plankId, cobbleTx, plankTx));
        }

        private Variant(int cobbleId, int plankId, String cobbleName, String plankName) {
            this.id = ((cobbleId << 4) & 0b11110000) | (plankId & 0b1111);
            this.suffix = String.format("%s_%s", cobbleName, plankName);
            this.texture = new ResourceLocation(String.format(Resources.TURRET_T1_CROSSBOW.location, cobbleName, plankName));
        }

        public static Variant get(ItemStack cobbleStack, ItemStack plankStack) {
            int cobble = 0;
            int plank = 0;

            if( ItemStackUtils.isBlock(cobbleStack, Blocks.MOSSY_COBBLESTONE) ) {
                cobble = 1;
            } else if( ItemStackUtils.isBlock(cobbleStack, Blocks.STONE) ) {
                switch( cobbleStack.getMetadata() ) {
                    case 1: cobble = 2; break;
                    case 3: cobble = 3; break;
                    case 5: cobble = 4; break;
                }
            }

            if( ItemStackUtils.isBlock(plankStack, Blocks.PLANKS) ) {
                plank = plankStack.getMetadata();
            }

            return get(cobble, plank);
        }

        public static Variant get(int cobbleId, int plankId) {
            return VARIANTS.contains(cobbleId, plankId) ? VARIANTS.get(cobbleId, plankId) : VARIANTS.get(0, 0);
        }

        public static Variant get(int id) {
            return get((id >> 4) & 0b1111, id & 0b1111);
        }
    }
}
