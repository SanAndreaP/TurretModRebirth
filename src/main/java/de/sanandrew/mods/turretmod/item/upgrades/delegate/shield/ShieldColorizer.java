/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.item.upgrades.delegate.shield;

import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeData;
import net.minecraft.nbt.CompoundNBT;

@IUpgradeData.Syncable
public class ShieldColorizer
        implements IUpgradeData<ShieldColorizer>
{
    private int color = 0x40FFFFFF;
    private boolean cullFaces = false;
    private boolean colorChanged = false;

    ShieldColorizer() { }

    ShieldColorizer(CompoundNBT nbt) {
        this.loadFromNbt(nbt);
    }

//    @Override
//    public void fromBytes(ObjectInputStream stream) throws IOException {
//        this.setColor(stream.readInt());
//        this.setCullFaces(stream.readBoolean());
//    }
//
//    @Override
//    public void toBytes(ObjectOutputStream stream) throws IOException {
//        stream.writeInt(this.color);
//        stream.writeBoolean(this.cullFaces);
//    }

    private void loadFromNbt(CompoundNBT nbt) {
        this.setColor(nbt.getInt("Color"));
        this.setCullFaces(nbt.getBoolean("CullFaces"));
    }

    void writeToNbt(CompoundNBT nbt) {
        nbt.putInt("Color", this.color);
        nbt.putBoolean("CullFaces", this.cullFaces);
    }

    @Override
    public void onTick(ITurretEntity turretInst) {
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
