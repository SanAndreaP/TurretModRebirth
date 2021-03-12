/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.network;

import de.sanandrew.mods.sanlib.lib.network.AbstractMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class PacketSyncAttackTarget
        extends AbstractMessage<PacketSyncAttackTarget>
{
    private int targetEntityId;
    private int attackerEntityId;

    public PacketSyncAttackTarget() { }

    public PacketSyncAttackTarget(Entity target, Entity attacker) {
        this.targetEntityId = target.getEntityId();
        this.attackerEntityId = attacker != null ? attacker.getEntityId() : 0;
    }

    @Override
    public void handleClientMessage(PacketSyncAttackTarget packet, EntityPlayer player) {
        Entity e = player.world.getEntityByID(packet.targetEntityId);
        Entity a = packet.attackerEntityId != 0 ? player.world.getEntityByID(packet.attackerEntityId) : null;
        if( e instanceof EntityLiving && (a == null || a instanceof EntityLivingBase) ) {
            ((EntityLiving) e).setAttackTarget((EntityLivingBase) a);
            ((EntityLiving) e).setRevengeTarget((EntityLivingBase) a);
        }
    }

    @Override
    public void handleServerMessage(PacketSyncAttackTarget packet, EntityPlayer player) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.targetEntityId = buf.readInt();
        this.attackerEntityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.targetEntityId);
        buf.writeInt(this.attackerEntityId);
    }
}
