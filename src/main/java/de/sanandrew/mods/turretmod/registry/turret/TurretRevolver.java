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
import de.sanandrew.mods.turretmod.api.turret.IVariant;
import de.sanandrew.mods.turretmod.api.turret.IVariantHolder;
import de.sanandrew.mods.turretmod.registry.EnumEffect;
import de.sanandrew.mods.turretmod.registry.Resources;
import de.sanandrew.mods.turretmod.registry.Sounds;
import de.sanandrew.mods.turretmod.registry.turret.variant.DualItemVariants;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;

@Category("revolver")
@SuppressWarnings("WeakerAccess")
public class TurretRevolver
        implements ITurret, IVariantHolder
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "turret_revolver");

    private static AxisAlignedBB rangeBB;

    @Value(comment = "Maximum health this turret has.", range = @Range(minD = 0.1D, maxD = 1024.0D), reqWorldRestart = true)
    public static float  health       = 30.0F;
    @Value(comment = "Capacity of ammo rounds this turret can hold.", range = @Range(minI = 1, maxI = Short.MAX_VALUE), reqWorldRestart = true)
    public static int    ammoCapacity = 256;
    @Value(comment = "Maximum tick time between shots. 20 ticks = 1 second.", range = @Range(minI = 1), reqWorldRestart = true)
    public static int    reloadTicks  = 15;
    @Value(comment = "Horizontal length of half the edge of the targeting box. The total edge length is [value * 2], with the turret centered in it.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeH       = 20.0D;
    @Value(comment = "Vertical length of the edge of the targeting box, from the turret upwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeU       = 10.0D;
    @Value(comment = "Vertical length of the edge of the targeting box, from the turret downwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeD       = 4.0D;

    public static final DualItemVariants VARIANTS = new DualItemVariants();

    static {
        for( BlockStoneBrick.EnumType bType : BlockStoneBrick.EnumType.values() ) {
            ItemStack brick     = new ItemStack(Blocks.STONEBRICK, 1, bType.getMetadata());
            String    brickName = bType.getName();
            String    txPath    = Resources.TURRET_T2_REVOLVER.resource.getPath();

            VARIANTS.register(new ItemStack(Blocks.STONE_SLAB, 1, 0), brick, txPath, "stone", brickName);
            VARIANTS.register(new ItemStack(Blocks.STONE_SLAB, 1, 1), brick, txPath, "sandstone", brickName);
            VARIANTS.register(new ItemStack(Blocks.STONE_SLAB, 1, 3), brick, txPath, "cobblestone", brickName);
            VARIANTS.register(new ItemStack(Blocks.STONE_SLAB, 1, 4), brick, txPath, "bricks", brickName);
            VARIANTS.register(new ItemStack(Blocks.STONE_SLAB, 1, 5), brick, txPath, "stone_bricks", brickName);
            VARIANTS.register(new ItemStack(Blocks.STONE_SLAB2, 1, 0), brick, txPath, "red_sandstone", brickName);
        }
    }

    @Override
    public void onUpdate(ITurretInst turretInst) {
        EntityLiving turretL = turretInst.get();

        MyRAM ram = turretInst.getRAM(MyRAM::new);
        ram.prevBarrelLeft = ram.barrelLeft;
        ram.prevBarrelRight = ram.barrelRight;

        if( ram.barrelLeft < 1.0F ) {
            ram.barrelLeft += 0.06F * 20.0F / turretInst.getTargetProcessor().getMaxShootTicks();
        } else {
            ram.barrelLeft = 1.0F;
        }

        if( ram.barrelRight < 1.0F ) {
            ram.barrelRight += 0.06F * 20.0F / turretInst.getTargetProcessor().getMaxShootTicks();
        } else {
            ram.barrelRight = 1.0F;
        }

        if( turretInst.wasShooting() ) {
            float partShift;
            if( ram.isLeftShot ) {
                ram.barrelRight = 0.0F;
                ram.isLeftShot = false;
                partShift = 10.0F;
            } else {
                ram.barrelLeft = 0.0F;
                ram.isLeftShot = true;
                partShift = -10.0F;
            }

            if( turretL.world.isRemote ) {
                EnumEffect.SHOTGUN_SMOKE.addEffect(turretL, new Tuple(turretL.rotationYawHead + partShift, turretL.rotationPitch));
            }
        }
    }

    @Override
    public ResourceLocation getStandardTexture(ITurretInst turretInst) {
        return turretInst.getVariant().getTexture();
    }

    @Override
    public ResourceLocation getGlowTexture(ITurretInst turretInst) {
        return Resources.TURRET_T2_REVOLVER_GLOW.resource;
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
        return Sounds.SHOOT_REVOLVER;
    }

    @Override
    public int getTier() {
        return 2;
    }

    public static class MyRAM
            implements ITurretRAM
    {
        public float barrelLeft  = 1.0F;
        public float barrelRight = 1.0F;

        public float prevBarrelLeft  = 1.0F;
        public float prevBarrelRight = 1.0F;

        boolean isLeftShot = false;
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
    public void registerVariant(IVariant variant) {
        VARIANTS.register(variant);
    }

    @Override
    public IVariant getVariant(ResourceLocation id) {
        return VARIANTS.getOrDefault(id);
    }

    @Override
    public boolean isDefaultVariant(IVariant variant) {
        return VARIANTS.isDefaultVariant(variant);
    }
}
