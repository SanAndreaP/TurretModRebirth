package de.sanandrew.mods.turretmod.registry.upgrades.leveling;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.registry.Resources;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

@IUpgrade.InitSynchronizeClient
public class Leveling
        implements IUpgrade
{
    static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "upgrade.leveling");

    private static final String NBT_ITEM_LEVELS = "Levels";
    private static final String NBT_EXPERIENCE  = "Experience";

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public ResourceLocation getBookEntryId() {
        return Resources.PATCHOULI_E_UPGRADE_LEVELING.resource;
    }

    @Override
    public void initialize(ITurretInst turretInst, ItemStack stack) {
        IUpgradeProcessor processor = turretInst.getUpgradeProcessor();
        if( turretInst.get().isServerWorld() ) {
            NBTTagCompound lvlNbt = stack.getSubCompound(NBT_ITEM_LEVELS);
            LevelStorage stg = lvlNbt != null && lvlNbt.hasKey(NBT_EXPERIENCE)
                               ? new LevelStorage(lvlNbt.getInteger(NBT_EXPERIENCE))
                               : new LevelStorage();
            processor.setUpgradeInstance(ID, stg);
        } else {
            processor.setUpgradeInstance(ID, new LevelStorage());
        }
    }

    @Override
    public void onLoad(ITurretInst turretInst, NBTTagCompound nbt) {
        LevelStorage stg = new LevelStorage(nbt.getInteger(NBT_EXPERIENCE));
        turretInst.getUpgradeProcessor().setUpgradeInstance(ID, stg);
        stg.applyEffects(turretInst, false);
    }

    @Override
    public void onSave(ITurretInst turretInst, NBTTagCompound nbt) {
        nbt.setInteger(NBT_EXPERIENCE, turretInst.getUpgradeProcessor().<LevelStorage>getUpgradeInstance(ID).getXp());
    }

    @Override
    public void terminate(ITurretInst turretInst, ItemStack stack) {
        LevelStorage stg = turretInst.getUpgradeProcessor().getUpgradeInstance(ID);
        if( stg != null ) {
            NBTTagCompound lvlNbt = stack.getOrCreateSubCompound(NBT_ITEM_LEVELS);
            lvlNbt.setInteger(NBT_EXPERIENCE, stg.xp);

            stg.removeEffects(turretInst);
            turretInst.getUpgradeProcessor().delUpgradeInstance(ID);
        }
    }
}
