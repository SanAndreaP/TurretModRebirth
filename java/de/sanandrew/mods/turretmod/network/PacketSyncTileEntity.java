/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.darkhax.bookshelf.common.network.AbstractMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class PacketSyncTileEntity
        extends AbstractMessage<PacketSyncTileEntity>
{
    private int x;
    private int y;
    private int z;

    private byte[] tileBytes;

    public PacketSyncTileEntity() { }

    public PacketSyncTileEntity(TileClientSync tile) {
        this.x = tile.getTile().xCoord;
        this.y = tile.getTile().yCoord;
        this.z = tile.getTile().zCoord;

        ByteBuf buf = Unpooled.buffer();
        tile.toBytes(buf);
        this.tileBytes = buf.array();
    }

    @Override
    public void handleClientMessage(PacketSyncTileEntity packet, EntityPlayer player) {
        TileEntity te = player.worldObj.getTileEntity(packet.x, packet.y, packet.z);
        if( te instanceof TileClientSync ) {
            ByteBuf buf = Unpooled.wrappedBuffer(packet.tileBytes);
            ((TileClientSync) te).fromBytes(buf);
        }
    }

    @Override
    public void handleServerMessage(PacketSyncTileEntity packet, EntityPlayer player) { }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        int arrSz = buf.readInt();
        this.tileBytes = new byte[arrSz];
        buf.readBytes(this.tileBytes, 0, arrSz);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.tileBytes.length);
        buf.writeBytes(this.tileBytes);
    }

    public static void sync(TileClientSync te) {
        TileEntity tile = te.getTile();
        PacketRegistry.sendToAllAround(new PacketSyncTileEntity(te), tile.getWorldObj().provider.dimensionId, tile.xCoord, tile.yCoord, tile.zCoord, 64.0D);
    }
}
