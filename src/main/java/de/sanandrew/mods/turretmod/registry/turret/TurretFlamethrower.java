/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.Sounds;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.UUID;

public class TurretFlamethrower
        implements ITurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "turrets/turret_flamethrower");
    private static final UUID ID = UUID.fromString("0C61E401-A5F9-44E9-8B29-3A3DC7762C73");
    private static final AxisAlignedBB RANGE_BB = new AxisAlignedBB(-8.0D, -2.0D, -8.0D, 8.0D, 4.0D, 8.0D);

    @Override
    public ResourceLocation getStandardTexture(ITurretInst turretInst) {
        return Resources.TURRET_T3_FTHROWER.resource;
    }

    @Override
    public ResourceLocation getGlowTexture(ITurretInst turretInst) {
        return Resources.TURRET_T3_FTHROWER_GLOW.resource;
    }

    @Override
    public AxisAlignedBB getRangeBB(ITurretInst turretInst) {
        return RANGE_BB;
    }

    @Override
    public SoundEvent getShootSound(ITurretInst turretInst) {
        return turretInst.getEntity().getRNG().nextBoolean() ? Sounds.SHOOT_FLAMETHROWER : null;
    }

    @Override
    public String getName() {
        return "iii_flamethrower";
    }

    @Override
    public UUID getId() {
        return TurretFlamethrower.ID;
    }

    @Override
    public ResourceLocation getItemModel() {
        return TurretFlamethrower.ITEM_MODEL;
    }

    @Override
    public int getTier() {
        return 3;
    }

    @Override
    public float getHealth() {
        return 40.0F;
    }

    @Override
    public int getAmmoCapacity() {
        return 4096;
    }

    @Override
    public int getReloadTicks() {
        return 1;
    }
}
