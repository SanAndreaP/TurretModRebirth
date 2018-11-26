package de.sanandrew.mods.turretmod.registry.upgrades.shield;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeInstance;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@IUpgradeInstance.Tickable
public class ShieldColorizer
        implements IUpgradeInstance<ShieldColorizer>
{
    private int color = 0xFFFFFFFF;
    private boolean colorChanged = false;

    ShieldColorizer() { }

    ShieldColorizer(NBTTagCompound nbt) {
        this.loadFromNbt(nbt);
    }

    @Override
    public void fromBytes(ObjectInputStream stream) throws IOException {
        this.setColor(stream.readInt());
    }

    @Override
    public void toBytes(ObjectOutputStream stream) throws IOException {
        stream.writeInt(this.color);
    }

    private void loadFromNbt(NBTTagCompound nbt) {
        this.setColor(nbt.getInteger("Color"));
    }

    void writeToNbt(NBTTagCompound nbt) {
        nbt.setInteger("Color", this.color);
    }

    @Override
    public void onTick(ITurretInst turretInst) {
        if( this.colorChanged ) {
            this.colorChanged = false;
            UpgradeShieldColorizer.recalcColor(turretInst);
        }
    }

    public void setColor(int color) {
        this.color = color;
        this.colorChanged = true;
    }

    public int getColor() {
        return this.color;
    }
}
