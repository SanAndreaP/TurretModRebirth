package de.sanandrew.mods.turretmod.item.upgrades.delegate.shield;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class UpgradeShieldColorizer
        implements IUpgrade
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "upgrade_shield_colorizer");
//    private static final ITurret[] APPLICABLES = { Turrets.FORCEFIELD }; //TODO: readd forcefield turret

    @Override
    @Nonnull
    public ResourceLocation getId() {
        return ID;
    }

//TODO: readd forcefield turret
//    @Nullable
//    @Override
//    public ITurret[] getApplicableTurrets() {
//        return APPLICABLES;
//    }

    @Override
    public void initialize(ITurretEntity turretInst, ItemStack stack) {
//        turretInst.getUpgradeProcessor().setUpgradeData(ID, new ShieldColorizer());
        recalcColor(turretInst);
    }

    @Override
    public void onLoad(ITurretEntity turretInst, CompoundNBT nbt) {
//        turretInst.getUpgradeProcessor().setUpgradeData(ID, new ShieldColorizer(nbt));
        recalcColor(turretInst);
    }

    @Override
    public void onSave(ITurretEntity turretInst, CompoundNBT nbt) {
        ShieldColorizer settings = turretInst.getUpgradeProcessor().getUpgradeData(ID);
        if( settings != null ) {
            settings.writeToNbt(nbt);
        }
    }

    @Override
    public void terminate(ITurretEntity turretInst, ItemStack stack) {
//        turretInst.getUpgradeProcessor().removeUpgradeData(ID);
        recalcColor(turretInst);
    }

    //TODO: readd forcefield turret
    static void recalcColor(ITurretEntity turretInst) {
//        Forcefield shield = turretInst.getRAM(null);
//        if( shield != null ) {
//            shield.recalcBaseColor();
//        }
    }
}
