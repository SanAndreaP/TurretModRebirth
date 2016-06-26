/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import java.util.UUID;

public class PacketInitAssemblyCrafting
        extends PacketRegistry.AbstractMessage<PacketInitAssemblyCrafting>
{
    private BlockPos pos;
    private String crfUUID;
    private int count;

    @SuppressWarnings("unused")
    public PacketInitAssemblyCrafting() { }

    public PacketInitAssemblyCrafting(TileEntityTurretAssembly assembly, UUID uuid, int count) {
        this.pos = assembly.getPos();
        this.crfUUID = uuid == null ? null : uuid.toString();
        this.count = count;
    }

    public PacketInitAssemblyCrafting(TileEntityTurretAssembly assembly) {
        this.pos = assembly.getPos();
        this.crfUUID = null;
        this.count = 0;
    }

    @Override
    public void handleClientMessage(PacketInitAssemblyCrafting packet, EntityPlayer player) {

    }

    @Override
    public void handleServerMessage(PacketInitAssemblyCrafting packet, EntityPlayer player) {
        TileEntity te = player.worldObj.getTileEntity(packet.pos);
        if( te instanceof TileEntityTurretAssembly ) {
            if( packet.crfUUID.equals("[CANCEL]") ) {
                ((TileEntityTurretAssembly) te).cancelCrafting();
            } else {
                ((TileEntityTurretAssembly) te).beginCrafting(UUID.fromString(packet.crfUUID), packet.count);
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.crfUUID = ByteBufUtils.readUTF8String(buf);
        this.count = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        if( this.crfUUID == null ) {
            ByteBufUtils.writeUTF8String(buf, "[CANCEL]");
        } else {
            ByteBufUtils.writeUTF8String(buf, this.crfUUID);
        }
        buf.writeByte(this.count);
    }
}
