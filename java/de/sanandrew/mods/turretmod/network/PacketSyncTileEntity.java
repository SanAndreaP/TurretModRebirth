/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network;

import de.sanandrew.mods.sanlib.lib.network.AbstractMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class PacketSyncTileEntity
        extends AbstractMessage<PacketSyncTileEntity>
{
    private BlockPos pos;

    private byte[] tileBytes;

    @SuppressWarnings("unused")
    public PacketSyncTileEntity() { }

    public PacketSyncTileEntity(TileClientSync tile) {
        this.pos = tile.getTile().getPos();

        ByteBuf buf = Unpooled.buffer();
        tile.toBytes(buf);
        this.tileBytes = buf.array();
    }

    @Override
    public void handleClientMessage(PacketSyncTileEntity packet, EntityPlayer player) {
        assert player != null;
        assert player.worldObj != null;
        TileEntity te = player.worldObj.getTileEntity(new BlockPos(packet.pos));
        if( te instanceof TileClientSync ) {
            ByteBuf buf = Unpooled.wrappedBuffer(packet.tileBytes);
            ((TileClientSync) te).fromBytes(buf);
        }
    }

    @Override
    public void handleServerMessage(PacketSyncTileEntity packet, EntityPlayer player) { }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        int arrSz = buf.readInt();
        this.tileBytes = new byte[arrSz];
        buf.readBytes(this.tileBytes, 0, arrSz);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeInt(this.tileBytes.length);
        buf.writeBytes(this.tileBytes);
    }

    public static void sync(TileClientSync te) {
        TileEntity tile = te.getTile();
        PacketRegistry.sendToAllAround(new PacketSyncTileEntity(te), tile.getWorld().provider.getDimension(), tile.getPos().getX(), tile.getPos().getY(), tile.getPos().getZ(), 64.0D);
    }
}
