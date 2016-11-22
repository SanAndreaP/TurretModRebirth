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
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class PacketAssemblyToggleAutomate
        extends AbstractMessage<PacketAssemblyToggleAutomate>
{
    private BlockPos pos;

    @SuppressWarnings("unused")
    public PacketAssemblyToggleAutomate() { }

    public PacketAssemblyToggleAutomate(TileEntityTurretAssembly assembly) {
        this.pos = assembly.getPos();
    }

    @Override
    public void handleClientMessage(PacketAssemblyToggleAutomate packet, EntityPlayer player) {

    }

    @Override
    public void handleServerMessage(PacketAssemblyToggleAutomate packet, EntityPlayer player) {
        TileEntity te = player.world.getTileEntity(packet.pos);
        if( te instanceof TileEntityTurretAssembly ) {
            ((TileEntityTurretAssembly) te).setAutomated(!((TileEntityTurretAssembly) te).isAutomated());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
    }
}
