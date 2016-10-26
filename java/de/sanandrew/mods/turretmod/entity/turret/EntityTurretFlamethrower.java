/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.turretmod.client.audio.SoundLaser;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileFlame;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeFuelPurifier;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class EntityTurretFlamethrower
        extends EntityTurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TurretModRebirth.ID, "turrets/turret_flamethrower");
    public static final UUID TIII_UUID = UUID.fromString("0C61E401-A5F9-44E9-8B29-3A3DC7762C73");
    public static final TurretInfo TINFO = new TurretInfo() {
        @Override
        public String getName() {
            return "turret_iii_flamethrower";
        }

        @Override
        public UUID getUUID() {
            return EntityTurretFlamethrower.TIII_UUID;
        }

        @Override
        public Class<? extends EntityTurret> getTurretClass() {
            return EntityTurretFlamethrower.class;
        }

        @Override
        public float getTurretHealth() {
            return 40.0F;
        }

        @Override
        public int getBaseAmmoCapacity() {
            return 4096;
        }

        @Override
        public ResourceLocation getModel() {
            return ITEM_MODEL;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.TURRET_MK3_FT;
        }

        @Override
        public String getInfoRange() {
            return "8";
        }
    };

    {
        this.targetProc = new MyTargetProc();
    }

    public EntityTurretFlamethrower(World world) {
        super(world);
    }

    public EntityTurretFlamethrower(World world, boolean isUpsideDown, EntityPlayer player) {
        super(world, isUpsideDown, player);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).setBaseValue(1.0D);
        this.getEntityAttribute(TurretAttributes.MAX_AMMO_CAPACITY).setBaseValue(4096.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
    }

    @Override
    public ResourceLocation getStandardTexture() {
        return Resources.TURRET_T3_FTHROWER.getResource();
    }

    @Override
    public ResourceLocation getGlowTexture() {
        return Resources.TURRET_T3_FTHROWER_GLOW.getResource();
    }

    private static final AxisAlignedBB UPPER_BB = new AxisAlignedBB(-8.0D, -2.0D, -8.0D, 8.0D, 4.0D, 8.0D);
    private static final AxisAlignedBB LOWER_BB = new AxisAlignedBB(-8.0D, -4.0D, -8.0D, 8.0D, 2.0D, 8.0D);
    private class MyTargetProc
            extends TargetProcessor
    {

        public MyTargetProc() {
            super(EntityTurretFlamethrower.this);
        }

        @Override
        public AxisAlignedBB getRangeBB() {
            return (this.turret.isUpsideDown ? LOWER_BB : UPPER_BB).offset(this.turret.posX, this.turret.posY, this.turret.posZ);
        }

        @Override
        public SoundEvent getShootSound() {
            return null;
        }

        @Override
        public SoundEvent getLowAmmoSound() {
            return SoundEvents.BLOCK_DISPENSER_FAIL;
        }

        @Override
        public Entity getProjectile() {
            Entity proj = super.getProjectile();
            if( proj instanceof EntityProjectileFlame ) {
                ((EntityProjectileFlame) proj).purifying = this.turret.getUpgradeProcessor().hasUpgrade(UpgradeRegistry.UPG_FUEL_PURIFY);
            }
            return proj;
        }
    }
}
