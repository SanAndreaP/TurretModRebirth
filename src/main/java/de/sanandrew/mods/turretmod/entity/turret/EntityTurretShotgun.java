/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.api.turret.TurretInfo;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.Sounds;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityTurretShotgun
        extends EntityTurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "turrets/turret_shotgun");
    public static final UUID TI_UUID = UUID.fromString("F7991EC5-2A89-49A6-B8EA-80775973C4C5");
    public static final TurretInfo TINFO = new TurretInfo() {
        @Override
        public String getName() {
            return "turret_i_shotgun";
        }

        @Override
        public UUID getUUID() {
            return EntityTurretShotgun.TI_UUID;
        }

        @Override
        public Class<? extends EntityTurret> getTurretClass() {
            return EntityTurretShotgun.class;
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
            return TurretAssemblyRecipes.TURRET_MK1_SG;
        }

        @Override
        public String getInfoRange() {
            return "16";
        }
    };

    private static final AxisAlignedBB RANGE_BB = new AxisAlignedBB(-16.0D, -4.0D, -16.0D, 16.0D, 8.0D, 16.0D);

    public float barrelPos = 1.0F;
    public float prevBarrelPos = 1.0F;

    public EntityTurretShotgun(World world) {
        super(world);
    }

    public EntityTurretShotgun(World world, boolean isUpsideDown, EntityPlayer player) {
        super(world, isUpsideDown, player);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();

        this.getEntityAttribute(TurretAttributes.MAX_RELOAD_TICKS).setBaseValue(20.0D);
    }

    @Override
    public void onUpdate() {
        this.prevBarrelPos = this.barrelPos;

        super.onUpdate();

        if( this.world.isRemote ) {
            if( this.barrelPos < 1.0F ) {
                this.barrelPos += 0.06F * 20.0F / this.targetProc.getMaxShootTicks();
            } else {
                this.barrelPos = 1.0F;
            }

            if( this.wasShooting() ) {
                this.barrelPos = 0.0F;
                TurretModRebirth.proxy.spawnParticle(EnumParticle.SHOTGUN_SHOT, this.posX, this.posY + 1.5F, this.posZ, new Tuple(this.rotationYawHead, this.rotationPitch, this.isUpsideDown));
            }
        }
    }

    @Override
    public ResourceLocation getStandardTexture() {
        return Resources.TURRET_T1_SHOTGUN.getResource();
    }

    @Override
    public ResourceLocation getGlowTexture() {
        return Resources.TURRET_T1_SHOTGUN_GLOW.getResource();
    }

    @Override
    public AxisAlignedBB getRangeBB() {
        return RANGE_BB;
    }

    @Override
    public SoundEvent getShootSound() {
        return Sounds.shoot_shotgun;
    }
}
