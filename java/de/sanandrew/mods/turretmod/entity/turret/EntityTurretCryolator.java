/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.Sounds;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityTurretCryolator
        extends EntityTurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TurretModRebirth.ID, "turrets/turret_cryolator");
    public static final UUID TI_UUID = UUID.fromString("3AF4D8C3-FCFC-42B0-98A3-BFB669AA7CE6");
    public static final TurretInfo TINFO = new TurretInfo() {
        @Override
        public String getName() {
            return "turret_i_snowball";
        }

        @Override
        public UUID getUUID() {
            return EntityTurretCryolator.TI_UUID;
        }

        @Override
        public Class<? extends EntityTurret> getTurretClass() {
            return EntityTurretCryolator.class;
        }

        @Override
        public float getTurretHealth() {
            return 20.0F;
        }

        @Override
        public int getBaseAmmoCapacity() {
            return 256;
        }

        @Override
        public ResourceLocation getModel() {
            return ITEM_MODEL;
        }

        @Override
        public UUID getRecipeId() {
            return TurretAssemblyRecipes.TURRET_MK1_SB;
        }

        @Override
        public String getInfoRange() {
            return "16";
        }
    };

    {
        this.targetProc = new MyTargetProc();
    }

    public EntityTurretCryolator(World world) {
        super(world);
    }

    public EntityTurretCryolator(World world, boolean isUpsideDown, EntityPlayer player) {
        super(world, isUpsideDown, player);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).setBaseValue(20.0D);
    }

    @Override
    public ResourceLocation getStandardTexture() {
        return Resources.TURRET_T1_SNOWBALL.getResource();
    }

    @Override
    public ResourceLocation getGlowTexture() {
        return Resources.TURRET_T1_SNOWBALL_GLOW.getResource();
    }

    private static final AxisAlignedBB UPPER_BB = new AxisAlignedBB(-16.0D, -4.0D, -16.0D, 16.0D, 16.0D, 16.0D);
    private static final AxisAlignedBB LOWER_BB = new AxisAlignedBB(-16.0D, -16.0D, -16.0D, 16.0D, 4.0D, 16.0D);
    private class MyTargetProc
            extends TargetProcessor
    {
        public MyTargetProc() {
            super(EntityTurretCryolator.this);
        }

        @Override
        public AxisAlignedBB getRangeBB() {
            return (this.turret.isUpsideDown ? LOWER_BB : UPPER_BB).offset(this.turret.posX, this.turret.posY, this.turret.posZ);
        }

        @Override
        public SoundEvent getShootSound() {
            return Sounds.SHOOT_CRYOLATOR;
        }

        @Override
        public SoundEvent getLowAmmoSound() {
            return SoundEvents.BLOCK_DISPENSER_FAIL;
        }

        @Override
        public boolean doAllowTarget(Entity e) {
            if( e instanceof EntityLivingBase ) {
                return !((EntityLivingBase) e).isPotionActive(MobEffects.SLOWNESS) &&  super.doAllowTarget(e);
            }

            return super.doAllowTarget(e);
        }
    }
}
