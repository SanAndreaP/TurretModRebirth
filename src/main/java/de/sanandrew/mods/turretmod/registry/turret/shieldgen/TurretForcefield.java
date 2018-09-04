/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.turret.shieldgen;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.util.Resources;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class TurretForcefield
        implements ITurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "turrets/turret_forcefield");
    public static final UUID ID = UUID.fromString("95C3D0DC-000E-4E2D-9551-C9C897E072DC");

    private static final AxisAlignedBB RANGE_BB_I = new AxisAlignedBB(-8.0D, -2.0D, -8.0D, 8.0D, 8.0D, 8.0D);
    private static final AxisAlignedBB RANGE_BB_II = new AxisAlignedBB(-16.0D, -2.0D, -16.0D, 16.0D, 16.0D, 16.0D);
    private static final AxisAlignedBB RANGE_BB_III = new AxisAlignedBB(-24.0D, -2.0D, -24.0D, 24.0D, 24.0D, 24.0D);

    @Override
    public void applyEntityAttributes(ITurretInst turretInst) {
        turretInst.getEntity().getEntityAttribute(TurretAttributes.MAX_INIT_SHOOT_TICKS).setBaseValue(0.0D);
    }

    @Override
    public void onUpdate(ITurretInst turretInst) {
        ShieldTurret shield = turretInst.getRAM(() -> new ShieldTurret(turretInst));
        shield.onTick();
    }

    @Override
    public void writeSpawnData(ITurretInst turretInst, ByteBuf buf) {
        ShieldTurret shield = turretInst.getRAM(() -> new ShieldTurret(turretInst));
        buf.writeFloat(shield.value);
        buf.writeFloat(shield.recovery);
    }

    @Override
    public void readSpawnData(ITurretInst turretInst, ByteBuf buf) {
        ShieldTurret shield = turretInst.getRAM(() -> new ShieldTurret(turretInst));
        shield.value = buf.readFloat();
        shield.recovery = buf.readFloat();
    }

    @Override
    public void writeSyncData(ITurretInst turretInst, ObjectOutputStream stream) throws IOException {
        ShieldTurret shield = turretInst.getRAM(() -> new ShieldTurret(turretInst));
        stream.writeFloat(shield.value);
        stream.writeFloat(shield.recovery);
    }

    @Override
    public void readSyncData(ITurretInst turretInst, ObjectInputStream stream) throws IOException {
        ShieldTurret shield = turretInst.getRAM(() -> new ShieldTurret(turretInst));
        shield.value = stream.readFloat();
        shield.recovery = stream.readFloat();
    }

    @Override
    public void onSave(ITurretInst turretInst, NBTTagCompound nbt) {
        ShieldTurret shield = turretInst.getRAM(() -> new ShieldTurret(turretInst));
        nbt.setFloat("shieldValue", shield.value);
        nbt.setFloat("shieldRecovery", shield.recovery);
    }

    @Override
    public void onLoad(ITurretInst turretInst, NBTTagCompound nbt) {
        ShieldTurret shield = turretInst.getRAM(() -> new ShieldTurret(turretInst));
        shield.value = nbt.getFloat("shieldValue");
        shield.recovery = nbt.getFloat("shieldRecovery");
    }

    @Override
    public ResourceLocation getStandardTexture(ITurretInst turretInst) {
        return Resources.TURRET_T2_SHIELDGEN.resource;
    }

    @Override
    public ResourceLocation getGlowTexture(ITurretInst turretInst) {
        return Resources.TURRET_T2_SHIELDGEN_GLOW.resource;
    }

    @Override
    public AxisAlignedBB getRangeBB(ITurretInst turretInst) {
        if( turretInst == null ) {
            return RANGE_BB_I;
        } else {
            IUpgradeProcessor upgProc = turretInst.getUpgradeProcessor();
            return upgProc.hasUpgrade(Upgrades.SHIELD_STRENGTH_II) ? RANGE_BB_III : upgProc.hasUpgrade(Upgrades.SHIELD_STRENGTH_I) ? RANGE_BB_II : RANGE_BB_I;
        }
    }

    @Override
    public SoundEvent getShootSound(ITurretInst turretInst) {
        return null;
    }

    @Override
    public String getName() {
        return "ii_shieldgen";
    }

    @Override
    public UUID getId() {
        return TurretForcefield.ID;
    }

    @Override
    public ResourceLocation getItemModel() {
        return TurretForcefield.ITEM_MODEL;
    }

    @Override
    public int getTier() {
        return 2;
    }

    @Override
    public boolean canSeeThroughBlocks() {
        return true;
    }

    @Override
    public float getDeactiveHeadPitch() {
        return 0.0F;
    }

    @Override
    public float getHealth() {
        return 30.0F;
    }

    @Override
    public int getAmmoCapacity() {
        return 512;
    }

    @Override
    public int getReloadTicks() {
        return 1;
    }
}
