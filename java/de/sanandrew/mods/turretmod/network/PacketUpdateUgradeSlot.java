/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PacketUpdateUgradeSlot
        extends PacketRegistry.AbstractMessage<PacketUpdateUgradeSlot>
{
    private int turretID;
    private byte slot;
    private ItemStack stack;

    @SuppressWarnings("unused")
    public PacketUpdateUgradeSlot() {}

    public PacketUpdateUgradeSlot(EntityTurret turret, int slot, ItemStack stack) {
        this.turretID = turret.getEntityId();
        this.slot = (byte) slot;
        this.stack = stack;
    }

    @Override
    public void handleClientMessage(PacketUpdateUgradeSlot packet, EntityPlayer player) {
        Entity e = player.worldObj.getEntityByID(packet.turretID);
        if( e instanceof EntityTurret ) {
            ((EntityTurret) e).getUpgradeProcessor().setInventorySlotContents(packet.slot, packet.stack);
        }
    }

    @Override
    public void handleServerMessage(PacketUpdateUgradeSlot packet, EntityPlayer player) {}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.turretID = buf.readInt();
        this.slot = buf.readByte();
        this.stack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.turretID);
        buf.writeByte(this.slot);
        ByteBufUtils.writeItemStack(buf, this.stack);
    }
}
