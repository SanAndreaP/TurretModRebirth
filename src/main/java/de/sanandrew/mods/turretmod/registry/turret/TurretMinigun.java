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
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInfo;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretRAM;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.Sounds;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.UUID;

public class TurretMinigun
        implements ITurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "turrets/turret_minigun");
    public static final UUID TII_UUID = UUID.fromString("97E1FB65-EE36-43BA-A900-583B4BD7973A");

    private static final AxisAlignedBB RANGE_BB = new AxisAlignedBB(-20.0D, -4.0D, -20.0D, 20.0D, 10.0D, 20.0D);

    @Override
    public void applyEntityAttributes(ITurretInst turretInst) {
        turretInst.getEntity().getEntityAttribute(TurretAttributes.MAX_AMMO_CAPACITY).setBaseValue(512.0D);
        turretInst.getEntity().getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).setBaseValue(3.0D);
        turretInst.getEntity().getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
    }

    @Override
    public void onUpdate(ITurretInst turretInst) {
        EntityLiving turretL = turretInst.getEntity();

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
                ram.isLeftShot = false;
            }

            if( turretL.world.isRemote ) {
                TurretModRebirth.proxy.spawnParticle(EnumParticle.MINIGUN_SHOT, turretL.posX, turretL.posY + 1.5F, turretL.posZ,
                                                     new Tuple(turretL.rotationYawHead, turretL.rotationPitch - 7.5F, turretInst.isUpsideDown(), ram.isLeftShot));
            }
        }
    }

    @Override
    public ResourceLocation getStandardTexture(ITurretInst turretInst) {
        return (turretInst.getEntity().hasCustomName() && turretInst.getEntity().getCustomNameTag().equalsIgnoreCase("silverchiren") ? Resources.TURRET_T2_MINIGUN_EE : Resources.TURRET_T2_MINIGUN).getResource();
    }

    @Override
    public ResourceLocation getGlowTexture(ITurretInst turretInst) {
        return Resources.TURRET_T2_MINIGUN_GLOW.getResource();
    }

    @Override
    public AxisAlignedBB getRangeBB(ITurretInst turretInst) {
        return RANGE_BB;
    }

    @Override
    public SoundEvent getShootSound(ITurretInst turretInst) {
        return Sounds.shoot_minigun;
    }
    @Override
    public String getName() {
        return "ii_minigun";
    }

    @Override
    public UUID getId() {
        return TII_UUID;
    }

    @Override
    public ResourceLocation getItemModel() {
        return ITEM_MODEL;
    }

    @Override
    public ITurretInfo getInfo() {
        return MyInfo.INSTANCE;
    }

    public static final class MyRAM implements ITurretRAM {
        public float barrelLeft = 0.0F;
        public float barrelRight = 0.0F;

        public float prevBarrelLeft = 0.0F;
        public float prevBarrelRight = 0.0F;

        public float maxBarrelLeft = 0.0F;
        public float maxBarrelRight = 0.0F;

        public boolean isLeftShot = false;
    }

    public static final class MyInfo implements ITurretInfo
    {
        static final ITurretInfo INSTANCE = new TurretCrossbow.MyInfo();

        @Override
        public float getHealth() {
            return 30.0F;
        }

        @Override
        public int getAmmoCapacity() {
            return 512;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.TURRET_MK2_MG;
        }

        @Override
        public String getRange() {
            return "20";
        }
    }
}
