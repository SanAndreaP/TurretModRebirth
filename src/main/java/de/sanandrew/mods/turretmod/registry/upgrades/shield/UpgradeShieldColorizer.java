package de.sanandrew.mods.turretmod.registry.upgrades.shield;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import de.sanandrew.mods.turretmod.registry.turret.shieldgen.ShieldTurret;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UpgradeShieldColorizer
        implements IUpgrade
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "upgrade.shield.colorizer");
    private static final ITurret[] APPLICABLES = { Turrets.FORCEFIELD };

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return ID;
    }

    @Nullable
    @Override
    public ITurret[] getApplicableTurrets() {
        return APPLICABLES;
    }

    @Override
    public void initialize(ITurretInst turretInst) {
        turretInst.getUpgradeProcessor().setUpgradeInstance(ID, new ShieldColorizer());
        recalcColor(turretInst);
    }

    @Override
    public void onLoad(ITurretInst turretInst, NBTTagCompound nbt) {
        turretInst.getUpgradeProcessor().setUpgradeInstance(ID, new ShieldColorizer(nbt));
        recalcColor(turretInst);
    }

    @Override
    public void onSave(ITurretInst turretInst, NBTTagCompound nbt) {
        ShieldColorizer settings = turretInst.getUpgradeProcessor().getUpgradeInstance(ID);
        if( settings != null ) {
            settings.writeToNbt(nbt);
        }
    }

    @Override
    public void terminate(ITurretInst turretInst, ItemStack stack) {
        turretInst.getUpgradeProcessor().delUpgradeInstance(ID);
        recalcColor(turretInst);
    }

    static void recalcColor(ITurretInst turretInst) {
        ShieldTurret shield = turretInst.getRAM(null);
        if( shield != null ) {
            shield.recalcBaseColor();
        }
    }
}
