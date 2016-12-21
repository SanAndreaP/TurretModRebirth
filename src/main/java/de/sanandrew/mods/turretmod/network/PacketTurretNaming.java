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
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketTurretNaming
        extends AbstractMessage<PacketTurretNaming>
{
    private int turretId;
    private String name;

    @SuppressWarnings("unused")
    public PacketTurretNaming() { }

    public PacketTurretNaming(EntityTurret turret, String name) {
        this.turretId = turret.getEntityId();
        this.name = name;
    }

    @Override
    public void handleClientMessage(PacketTurretNaming packet, EntityPlayer player) { }

    @Override
    public void handleServerMessage(PacketTurretNaming packet, EntityPlayer player) {
        Entity e = player.world.getEntityByID(packet.turretId);
        if( e instanceof EntityTurret ) {
            EntityTurret turret = (EntityTurret) e;
            turret.setCustomNameTag(packet.name);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.turretId = buf.readInt();
        this.name = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.turretId);
        ByteBufUtils.writeUTF8String(buf, this.name);
    }
}
