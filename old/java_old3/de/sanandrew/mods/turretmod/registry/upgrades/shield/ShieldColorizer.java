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
    private int color = 0x40FFFFFF;
    private boolean cullFaces = false;
    private boolean colorChanged = false;

    ShieldColorizer() { }

    ShieldColorizer(NBTTagCompound nbt) {
        this.loadFromNbt(nbt);
    }

    @Override
    public void fromBytes(ObjectInputStream stream) throws IOException {
        this.setColor(stream.readInt());
        this.setCullFaces(stream.readBoolean());
    }

    @Override
    public void toBytes(ObjectOutputStream stream) throws IOException {
        stream.writeInt(this.color);
        stream.writeBoolean(this.cullFaces);
    }

    private void loadFromNbt(NBTTagCompound nbt) {
        this.setColor(nbt.getInteger("Color"));
        this.setCullFaces(nbt.getBoolean("CullFaces"));
    }

    void writeToNbt(NBTTagCompound nbt) {
        nbt.setInteger("Color", this.color);
        nbt.setBoolean("CullFaces", this.cullFaces);
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

    public void setCullFaces(boolean cullFaces) {
        this.cullFaces = cullFaces;
        this.colorChanged = true;
    }

    public boolean doCullFaces() {
        return this.cullFaces;
    }
}
