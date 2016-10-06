/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network;

import de.sanandrew.mods.sanlib.lib.network.AbstractMessage;
import de.sanandrew.mods.turretmod.util.EnumGui;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class PacketOpenGui
        extends AbstractMessage<PacketOpenGui>
{
    private byte guiId;
    private int x;
    private int y;
    private int z;

    @SuppressWarnings("unused")
    public PacketOpenGui() { }

    public PacketOpenGui(byte guiId, int x, int y, int z) {
        this.guiId = guiId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void handleClientMessage(PacketOpenGui packet, EntityPlayer player) {
        if( packet.guiId >= 0 && packet.guiId < EnumGui.VALUES.length ) {
            TurretModRebirth.proxy.openGui(player, EnumGui.VALUES[packet.guiId], packet.x, packet.y, packet.z);
        }
    }

    @Override
    public void handleServerMessage(PacketOpenGui packet, EntityPlayer player) {
        if( packet.guiId >= 0 && packet.guiId < EnumGui.VALUES.length ) {
            TurretModRebirth.proxy.openGui(player, EnumGui.VALUES[packet.guiId], packet.x, packet.y, packet.z);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.guiId = buf.readByte();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.guiId);
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
    }
}
