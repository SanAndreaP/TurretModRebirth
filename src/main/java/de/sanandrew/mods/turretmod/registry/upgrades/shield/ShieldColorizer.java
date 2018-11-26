package de.sanandrew.mods.turretmod.registry.upgrades.shield;

import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeInstance;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ShieldColorizer
        implements IUpgradeInstance<ShieldColorizer>
{
    public int color = 0x40FFFFFF;

    ShieldColorizer() { }

    ShieldColorizer(NBTTagCompound nbt) {
        this.loadFromNbt(nbt);
    }

    @Override
    public void fromBytes(ObjectInputStream stream) throws IOException {
        this.color = stream.readInt();
    }

    @Override
    public void toBytes(ObjectOutputStream stream) throws IOException {
        stream.writeInt(this.color);
    }

    private void loadFromNbt(NBTTagCompound nbt) {
        this.color = nbt.getInteger("Color");
    }

    void writeToNbt(NBTTagCompound nbt) {
        nbt.setInteger("Color", this.color);
    }
}
