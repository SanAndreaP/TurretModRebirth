/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.common.network.AbstractMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class PacketDismantle
        extends AbstractMessage<PacketDismantle>
{
    private int turretId;

    public PacketDismantle() { }

    public PacketDismantle(EntityTurret turret) {
        this.turretId = turret.getEntityId();
    }

    @Override
    public void handleClientMessage(PacketDismantle packet, EntityPlayer player) { }

    @Override
    public void handleServerMessage(PacketDismantle packet, EntityPlayer player) {
        Entity e = player.worldObj.getEntityByID(packet.turretId);
        if( e instanceof EntityTurret ) {
            ((EntityTurret) e).tryDismantle(player);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.turretId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.turretId);
    }
}
