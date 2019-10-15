package de.sanandrew.mods.turretmod.registry.upgrades.leveling;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

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
    public void initialize(ITurretInst turretInst, ItemStack stack) {
        IUpgradeProcessor processor = turretInst.getUpgradeProcessor();
        if( processor.getUpgradeInstance(ID) == null ) {
            if( turretInst.get().isServerWorld() ) {
                NBTTagCompound lvlNbt = stack.getSubCompound(NBT_ITEM_LEVELS);
                LevelStorage stg = lvlNbt != null && lvlNbt.hasKey(NBT_EXPERIENCE)
                                   ? new LevelStorage(lvlNbt.getInteger(NBT_EXPERIENCE))
                                   : new LevelStorage();
                processor.setUpgradeInstance(ID, stg);
                UpgradeRegistry.INSTANCE.syncWithClients(turretInst, ID);
            } else {
                processor.setUpgradeInstance(ID, new LevelStorage());
            }
        }
    }

    @Override
    public void onLoad(ITurretInst turretInst, NBTTagCompound nbt) {
        LevelStorage stg = new LevelStorage(nbt.getInteger("Experience"));
        turretInst.getUpgradeProcessor().setUpgradeInstance(ID, stg);
        stg.applyEffects(turretInst);
    }

    @Override
    public void onSave(ITurretInst turretInst, NBTTagCompound nbt) {
        nbt.setInteger("Experience", turretInst.getUpgradeProcessor().<LevelStorage>getUpgradeInstance(ID).getXp());
    }

    @Override
    public void terminate(ITurretInst turretInst, ItemStack stack) {
        LevelStorage stg = turretInst.getUpgradeProcessor().getUpgradeInstance(ID);
        if( stg != null ) {
            NBTTagCompound lvlNbt = stack.getOrCreateSubCompound(NBT_ITEM_LEVELS);
            lvlNbt.setInteger(NBT_EXPERIENCE, stg.xp);

            turretInst.getUpgradeProcessor().delUpgradeInstance(ID);
        }
    }
}
