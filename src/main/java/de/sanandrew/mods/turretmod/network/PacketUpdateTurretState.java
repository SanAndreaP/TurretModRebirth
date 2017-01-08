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
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class PacketUpdateTurretState
        extends AbstractMessage<PacketUpdateTurretState>
{
    private int turretId;
    private int entityToAttackId;
    private int currAmmoCap;
    private ItemStack ammoStack;
    private boolean isShooting;

    @SuppressWarnings("unused")
    public PacketUpdateTurretState() { }

    public PacketUpdateTurretState(EntityTurret turret) {
        this.turretId = turret.getEntityId();
        ITargetProcessor tgtProc = turret.getTargetProcessor();
        if( tgtProc.hasTarget() ) {
            this.entityToAttackId = tgtProc.getTarget().getEntityId();
        } else {
            this.entityToAttackId = -1;
        }
        this.currAmmoCap = tgtProc.getAmmoCount();
        this.ammoStack = tgtProc.getAmmoStack();
        this.isShooting = tgtProc.isShooting();
    }

    @Override
    public void handleClientMessage(PacketUpdateTurretState packet, EntityPlayer player) {
        Entity e = player.world.getEntityByID(packet.turretId);
        if( e instanceof EntityTurret ) {
            EntityTurret turret = (EntityTurret) e;
            ((TargetProcessor) turret.getTargetProcessor()).updateClientState(packet.entityToAttackId, packet.currAmmoCap, packet.ammoStack, packet.isShooting);
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
        this.isShooting = buf.readBoolean();
        this.ammoStack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.turretId);
        buf.writeInt(this.entityToAttackId);
        buf.writeInt(this.currAmmoCap);
        buf.writeBoolean(this.isShooting);
        ByteBufUtils.writeItemStack(buf, this.ammoStack);
    }
}
