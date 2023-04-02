package de.sanandrew.mods.turretmod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;

public interface TileClientSync
{
    void toBytes(ByteBuf buf);
    void fromBytes(ByteBuf buf);
    TileEntity getTile();
}
