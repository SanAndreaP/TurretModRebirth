package de.sanandrew.mods.turretmod.item.upgrades.leveling;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

@IUpgrade.InitSynchronizeClient
public class Leveling
        implements IUpgrade
{
    static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "upgrade_leveling");

    private static final String NBT_ITEM_LEVELS = "Levels";
    private static final String NBT_EXPERIENCE  = "Experience";

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void initialize(ITurretEntity turretInst, ItemStack stack) {
        IUpgradeProcessor processor = turretInst.getUpgradeProcessor();
        if( !turretInst.get().level.isClientSide() ) {
            CompoundNBT lvlNbt = stack.getTagElement(NBT_ITEM_LEVELS);
            LevelStorage stg = lvlNbt != null && lvlNbt.contains(NBT_EXPERIENCE)
                               ? new LevelStorage(lvlNbt.getInt(NBT_EXPERIENCE))
                               : new LevelStorage();
            processor.setUpgradeInstance(ID, stg);
        } else {
            processor.setUpgradeInstance(ID, new LevelStorage());
        }
    }

    @Override
    public void onLoad(ITurretEntity turretInst, CompoundNBT nbt) {
        LevelStorage stg = new LevelStorage(nbt.getInt(NBT_EXPERIENCE));
        turretInst.getUpgradeProcessor().setUpgradeInstance(ID, stg);
        stg.applyEffects(turretInst, false);
    }

    @Override
    public void onSave(ITurretEntity turretInst, CompoundNBT nbt) {
        nbt.putInt(NBT_EXPERIENCE, turretInst.getUpgradeProcessor().<LevelStorage>getUpgradeInstance(ID).getFullXp());
    }

    @Override
    public void terminate(ITurretEntity turretInst, ItemStack stack) {
        LevelStorage stg = turretInst.getUpgradeProcessor().getUpgradeInstance(ID);
        if( stg != null ) {
            CompoundNBT lvlNbt = stack.getOrCreateTagElement(NBT_ITEM_LEVELS);
            lvlNbt.putInt(NBT_EXPERIENCE, stg.xp);

            stg.clearEffects(turretInst);
            turretInst.getUpgradeProcessor().delUpgradeInstance(ID);
        }
    }
}
