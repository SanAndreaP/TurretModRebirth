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
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.Sounds;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.UUID;

public class EntityTurretFlamethrower
        extends EntityTurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "turrets/turret_flamethrower");
    public static final UUID TIII_UUID = UUID.fromString("0C61E401-A5F9-44E9-8B29-3A3DC7762C73");
    private static final AxisAlignedBB RANGE_BB = new AxisAlignedBB(-8.0D, -2.0D, -8.0D, 8.0D, 4.0D, 8.0D);
    private boolean doShootSound;

    @SuppressWarnings("unused")
    public EntityTurretFlamethrower(World world) {
        super(world);
    }

    @SuppressWarnings("unused")
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

    @Override
    public AxisAlignedBB getRangeBB() {
        return RANGE_BB;
    }

    @Override
    public SoundEvent getShootSound() {
        return (doShootSound = !doShootSound) ? Sounds.shoot_flamethrower : null;
    }

}
