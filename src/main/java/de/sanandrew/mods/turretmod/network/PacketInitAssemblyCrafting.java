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
import de.sanandrew.mods.turretmod.tileentity.assembly.TileEntityTurretAssembly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketInitAssemblyCrafting
        extends AbstractMessage<PacketInitAssemblyCrafting>
{
    private BlockPos pos;
    private String crfId;
    private int count;

    @SuppressWarnings("unused")
    public PacketInitAssemblyCrafting() { }

    public PacketInitAssemblyCrafting(TileEntityTurretAssembly assembly, ResourceLocation id, int count) {
        this.pos = assembly.getPos();
        this.crfId = id == null ? null : id.toString();
        this.count = count;
    }

    public PacketInitAssemblyCrafting(TileEntityTurretAssembly assembly) {
        this.pos = assembly.getPos();
        this.crfId = null;
        this.count = 0;
    }

    @Override
    public void handleClientMessage(PacketInitAssemblyCrafting packet, EntityPlayer player) {

    }

    @Override
    public void handleServerMessage(PacketInitAssemblyCrafting packet, EntityPlayer player) {
        TileEntity te = player.world.getTileEntity(packet.pos);
        if( te instanceof TileEntityTurretAssembly ) {
            if( packet.crfId.equals("[CANCEL]") ) {
                ((TileEntityTurretAssembly) te).cancelCrafting();
            } else {
                ((TileEntityTurretAssembly) te).beginCrafting(new ResourceLocation(packet.crfId), packet.count);
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.crfId = ByteBufUtils.readUTF8String(buf);
        this.count = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        if( this.crfId == null ) {
            ByteBufUtils.writeUTF8String(buf, "[CANCEL]");
        } else {
            ByteBufUtils.writeUTF8String(buf, this.crfId);
        }
        buf.writeByte(this.count);
    }
}
