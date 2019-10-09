package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Leveling
        implements IUpgrade
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "upgrade.leveling");

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void initialize(ITurretInst turretInst) {
        turretInst.getUpgradeProcessor().setUpgradeInstance(ID, new LevelStorage());
    }

    @Override
    public void onLoad(ITurretInst turretInst, NBTTagCompound nbt) {
        turretInst.getUpgradeProcessor().setUpgradeInstance(ID, new LevelStorage(nbt.getInteger("Experience")));
    }

    @Override
    public void onSave(ITurretInst turretInst, NBTTagCompound nbt) {
        nbt.setInteger("Experience", turretInst.getUpgradeProcessor().<LevelStorage>getUpgradeInstance(ID).getXp());
    }

    @Override
    public void terminate(ITurretInst turretInst, ItemStack stack) {
        turretInst.getUpgradeProcessor().delUpgradeInstance(ID);
    }

    public static class LevelStorage
            implements IUpgradeInstance<LevelStorage>
    {
        private int prevXp;
        private int xp;

        LevelStorage() {
            this.xp = 0;
            this.prevXp = 0;
        }

        LevelStorage(int xp) {
            this.xp = xp;
            this.prevXp = 0;
        }

        @Override
        public void fromBytes(ObjectInputStream stream) throws IOException {
            this.xp = stream.readInt();
        }

        @Override
        public void toBytes(ObjectOutputStream stream) throws IOException {
            stream.writeInt(this.xp);
        }

        public int getXp() {
            return this.xp;
        }

        public void addXp(int xp) {
            if( xp > 0 ) {
                this.xp += xp;
            }
        }
    }
}
