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
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import io.netty.buffer.ByteBuf;
import net.darkhax.bookshelf.common.network.AbstractMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PacketUpdateTurretState
        extends AbstractMessage<PacketUpdateTurretState>
{
    private int turretId;
    private int entityToAttackId;
    private int currAmmoCap;
    private ItemStack ammoStack;

    public PacketUpdateTurretState() { }

    public PacketUpdateTurretState(EntityTurret turret) {
        this.turretId = turret.getEntityId();
        TargetProcessor tgtProc = turret.getTargetProcessor();
        if( tgtProc.hasTarget() ) {
            this.entityToAttackId = tgtProc.getTarget().getEntityId();
        } else {
            this.entityToAttackId = -1;
        }
        this.currAmmoCap = turret.getTargetProcessor().getAmmoCount();
        this.ammoStack = turret.getTargetProcessor().getAmmoStack();
    }

    @Override
    public void handleClientMessage(PacketUpdateTurretState packet, EntityPlayer player) {
        Entity e = player.worldObj.getEntityByID(packet.turretId);
        if( e instanceof EntityTurret ) {
            EntityTurret turret = (EntityTurret) e;
            turret.getTargetProcessor().updateClientState(packet.entityToAttackId, packet.currAmmoCap, packet.ammoStack);
        }
    }

    @Override
    public void handleServerMessage(PacketUpdateTurretState packet, EntityPlayer player) {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.turretId = buf.readInt();
        this.entityToAttackId = buf.readInt();
        this.currAmmoCap = buf.readInt();
        this.ammoStack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.turretId);
        buf.writeInt(this.entityToAttackId);
        buf.writeInt(this.currAmmoCap);
        ByteBufUtils.writeItemStack(buf, this.ammoStack);
    }
}
