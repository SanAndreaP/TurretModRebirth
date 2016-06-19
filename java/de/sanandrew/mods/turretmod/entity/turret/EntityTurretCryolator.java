/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.turretmod.client.event.RenderForcefieldHandler;
import de.sanandrew.mods.turretmod.client.util.ForcefieldProvider;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileCryoCell;
import de.sanandrew.mods.turretmod.entity.projectile.EntityTurretProjectile;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.darkhax.bookshelf.lib.ColorObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityTurretCryolator
        extends EntityTurret
{
    public static final UUID TI_UUID = UUID.fromString("3AF4D8C3-FCFC-42B0-98A3-BFB669AA7CE6");
    public static final TurretInfo TINFO = new TurretInfo()
    {
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
        public String getIcon() {
            return "turret_snowball";
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
    public void onUpdate() {
        super.onUpdate();
    }

    @Override
    public ResourceLocation getStandardTexture() {
        return Resources.TURRET_T1_SNOWBALL.getResource();
    }

    @Override
    public ResourceLocation getGlowTexture() {
        return Resources.TURRET_T1_SNOWBALL_GLOW.getResource();
    }

    private class MyTargetProc
            extends TargetProcessor
    {
        public MyTargetProc() {
            super(EntityTurretCryolator.this);
        }

        @Override
        public double getRange() {
            return 16;
        }

        @Override
        public String getShootSound() {
            return TurretModRebirth.ID + ":shoot.cryolator";
        }

        @Override
        public String getLowAmmoSound() {
            return "random.click";
        }

        @Override
        public boolean doAllowTarget(Entity e) {
            if( e instanceof EntityLivingBase ) {
                return !((EntityLivingBase) e).isPotionActive(Potion.moveSlowdown) &&  super.doAllowTarget(e);
            }

            return super.doAllowTarget(e);
        }
    }
}
