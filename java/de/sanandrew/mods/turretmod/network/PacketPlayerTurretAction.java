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
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class PacketPlayerTurretAction
        extends AbstractMessage<PacketPlayerTurretAction>
{
    public static final byte TOGGLE_ACTIVE = 0;
    public static final byte DISMANTLE = 1;

    private int turretId;
    private byte actionId;

    @SuppressWarnings("unused")
    public PacketPlayerTurretAction() { }

    public PacketPlayerTurretAction(EntityTurret turret, byte action) {
        this.turretId = turret.getEntityId();
        this.actionId = action;
    }

    @Override
    public void handleClientMessage(PacketPlayerTurretAction packet, EntityPlayer player) { }

    @Override
    public void handleServerMessage(PacketPlayerTurretAction packet, EntityPlayer player) {
        Entity e = player.worldObj.getEntityByID(packet.turretId);
        if( e instanceof EntityTurret ) {
            EntityTurret turret = (EntityTurret) e;
            switch( packet.actionId ) {
                case DISMANTLE:
                    turret.tryDismantle(player);
                    break;
                case TOGGLE_ACTIVE:
                    turret.setActive(!turret.isActive());
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.turretId = buf.readInt();
        this.actionId = buf.readByte();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.turretId);
        buf.writeByte(this.actionId);
    }
}
