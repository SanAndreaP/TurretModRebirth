/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.common.network.AbstractMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import java.util.UUID;

public class PacketInitAssemblyCrafting
        extends AbstractMessage<PacketInitAssemblyCrafting>
{
    private int x;
    private int y;
    private int z;
    private String crfUUID;

    public PacketInitAssemblyCrafting() { }

    public PacketInitAssemblyCrafting(TileEntityTurretAssembly assembly, UUID uuid) {
        this.x = assembly.xCoord;
        this.y = assembly.yCoord;
        this.z = assembly.zCoord;
        this.crfUUID = uuid.toString();
    }

    @Override
    public void handleClientMessage(PacketInitAssemblyCrafting packet, EntityPlayer player) {

    }

    @Override
    public void handleServerMessage(PacketInitAssemblyCrafting packet, EntityPlayer player) {
        TileEntity te = player.worldObj.getTileEntity(packet.x, packet.y, packet.z);
        if( te instanceof TileEntityTurretAssembly ) {
            ((TileEntityTurretAssembly) te).addRecipeToQueue(UUID.fromString(packet.crfUUID), 1);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.crfUUID = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        ByteBufUtils.writeUTF8String(buf, this.crfUUID);
    }
}
