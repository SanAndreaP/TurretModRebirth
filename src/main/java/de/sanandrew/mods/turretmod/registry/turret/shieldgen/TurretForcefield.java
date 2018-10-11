/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry.turret.shieldgen;

import de.sanandrew.mods.sanlib.lib.util.config.Category;
import de.sanandrew.mods.sanlib.lib.util.config.Range;
import de.sanandrew.mods.sanlib.lib.util.config.Value;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.api.turret.TurretAttributes;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.util.Resources;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

@Category("forcefield")
@SuppressWarnings("WeakerAccess")
public class TurretForcefield
        implements ITurret
{
    public static final ResourceLocation ITEM_MODEL = new ResourceLocation(TmrConstants.ID, "turrets/turret_forcefield");
    public static final UUID ID = UUID.fromString("95C3D0DC-000E-4E2D-9551-C9C897E072DC");

    private static AxisAlignedBB rangeBB1;
    private static AxisAlignedBB rangeBB2;
    private static AxisAlignedBB rangeBB3;

    @Value(comment = "Maximum health this turret has.", range = @Range(minD = 0.1D, maxD = 1024.0D), reqWorldRestart = true)
    public static float health = 30.0F;
    @Value(comment = "Capacity of ammo rounds this turret can hold.", range = @Range(minI = 1, maxI = Short.MAX_VALUE), reqWorldRestart = true)
    public static int ammoCapacity = 512;
    @Value(comment = "Maximum tick time between shots. 20 ticks = 1 second.", range = @Range(minI = 1), reqWorldRestart = true)
    public static int reloadTicks = 1;
    @Value(comment = "Horizontal length of half the edge of the targeting box without upgrades. The total edge length is [value * 2], with the turret centered in it.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeFirstH = 8.0D;
    @Value(comment = "Vertical length of the edge of the targeting box without upgrades, from the turret upwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeFirstU = 8.0D;
    @Value(comment = "Vertical length of the edge of the targeting box without upgrades, from the turret downwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeFirstD = 2.0D;
    @Value(comment = "Horizontal length of half the edge of the targeting box with shield strength 1 upgrade. The total edge length is [value * 2], with the turret centered in it.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeSecondH = 16.0D;
    @Value(comment = "Vertical length of the edge of the targeting box with shield strength 1 upgrade, from the turret upwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeSecondU = 16.0D;
    @Value(comment = "Vertical length of the edge of the targeting box with shield strength 1 upgrade, from the turret downwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeSecondD = 2.0D;
    @Value(comment = "Horizontal length of half the edge of the targeting box with shield strength 2 upgrade. The total edge length is [value * 2], with the turret centered in it.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeThirdH = 24.0D;
    @Value(comment = "Vertical length of the edge of the targeting box with shield strength 2 upgrade, from the turret upwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeThirdU = 24.0D;
    @Value(comment = "Vertical length of the edge of the targeting box with shield strength 2 upgrade, from the turret downwards.", range = @Range(minD = 1.0D), reqMcRestart = true)
    public static double rangeThirdD = 2.0D;

    @Override
    public void applyEntityAttributes(ITurretInst turretInst) {
        turretInst.get().getEntityAttribute(TurretAttributes.MAX_INIT_SHOOT_TICKS).setBaseValue(0.0D);
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
        if( rangeBB1 == null ) {
            rangeBB1 = new AxisAlignedBB(-rangeFirstH, -rangeFirstD, -rangeFirstH, rangeFirstH, rangeFirstU, rangeFirstH);
        }
        if( rangeBB2 == null ) {
            rangeBB2 = new AxisAlignedBB(-rangeSecondH, -rangeSecondD, -rangeSecondH, rangeSecondH, rangeSecondU, rangeSecondH);
        }
        if( rangeBB3 == null ) {
            rangeBB3 = new AxisAlignedBB(-rangeThirdH, -rangeThirdD, -rangeThirdH, rangeThirdH, rangeThirdU, rangeThirdH);
        }

        if( turretInst == null ) {
            return rangeBB1;
        } else {
            IUpgradeProcessor upgProc = turretInst.getUpgradeProcessor();
            return upgProc.hasUpgrade(Upgrades.SHIELD_STRENGTH_II) ? rangeBB3 : upgProc.hasUpgrade(Upgrades.SHIELD_STRENGTH_I) ? rangeBB2 : rangeBB1;
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

    @Override
    public AttackType getAttackType() {
        return AttackType.ALL;
    }
}
