/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network;

import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.common.network.AbstractMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

public class PacketAssemblyToggleAutomate
        extends AbstractMessage<PacketAssemblyToggleAutomate>
{
    private int x;
    private int y;
    private int z;

    public PacketAssemblyToggleAutomate() { }

    public PacketAssemblyToggleAutomate(TileEntityTurretAssembly assembly) {
        this.x = assembly.xCoord;
        this.y = assembly.yCoord;
        this.z = assembly.zCoord;
    }

    @Override
    public void handleClientMessage(PacketAssemblyToggleAutomate packet, EntityPlayer player) {

    }

    @Override
    public void handleServerMessage(PacketAssemblyToggleAutomate packet, EntityPlayer player) {
        TileEntity te = player.worldObj.getTileEntity(packet.x, packet.y, packet.z);
        if( te instanceof TileEntityTurretAssembly ) {
            ((TileEntityTurretAssembly) te).setAutomated(!((TileEntityTurretAssembly) te).isAutomated());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }
}
