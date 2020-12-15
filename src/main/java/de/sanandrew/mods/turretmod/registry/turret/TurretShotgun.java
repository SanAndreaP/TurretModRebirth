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
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;

@Category("shotgun")
@SuppressWarnings("WeakerAccess")
public class TurretShotgun
        implements ITurret, IVariantHolder
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "turret_shotgun");

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
            String txPath = Resources.TURRET_T1_SHOTGUN.resource.getPath();
            int logMeta = pType.getMetadata();
            ItemStack log = new ItemStack(logMeta < 4 ? Blocks.LOG : Blocks.LOG2, 1, logMeta - (logMeta < 4 ? 0 : 4));

            VARIANTS.register(new ItemStack(Blocks.STONE, 1, 0), log,
                              VARIANTS.buildVariant(TmrConstants.ID, txPath, "stone", pType.getName()));
            for( BlockStoneBrick.EnumType sType : BlockStoneBrick.EnumType.values() ) {
                ItemStack brick = new ItemStack(Blocks.STONEBRICK, 1, sType.getMetadata());

                VARIANTS.register(brick, log,
                                  VARIANTS.buildVariant(TmrConstants.ID, txPath, sType.getName(), pType.getName()));
            }
        }
    }

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
                EnumEffect.SHOTGUN_SMOKE.addEffect(turretL, new Tuple(turretL.rotationYawHead, turretL.rotationPitch));
            }
        }
    }

    @Override
    public ResourceLocation getStandardTexture(ITurretInst turretInst) {
        return turretInst.getVariant().getTexture();
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

    public static class MyRAM
            implements ITurretRAM
    {
        public float barrelPos     = 1.0F;
        public float prevBarrelPos = 1.0F;
    }
}
