/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.entity.turret;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.api.turret.TurretInfo;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.Sounds;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityTurretCryolator
        extends EntityTurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "turrets/turret_cryolator");
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
            return TurretAssemblyRecipes.TURRET_MK1_CL;
        }

        @Override
        public String getInfoRange() {
            return "16";
        }
    };

    private static final AxisAlignedBB RANGE_BB = new AxisAlignedBB(-16.0D, -4.0D, -16.0D, 16.0D, 16.0D, 16.0D);

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

    @Override
    public AxisAlignedBB getRangeBB() {
        return RANGE_BB;
    }

    @Override
    public SoundEvent getShootSound() {
        return Sounds.SHOOT_CRYOLATOR;
    }
}
