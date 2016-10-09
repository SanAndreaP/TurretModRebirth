/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network.packet;

import de.sanandrew.core.manpack.network.IPacket;
import de.sanandrew.core.manpack.util.helpers.InventoryUtils;
import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.core.manpack.util.javatuples.Unit;
import de.sanandrew.mods.turretmod.api.TurretUpgrade;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretBase;
import de.sanandrew.mods.turretmod.network.PacketManager;
import de.sanandrew.mods.turretmod.util.TmrItems;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class PacketEjectAllUpgrades
        implements IPacket
{
    @Override
    public void process(ByteBufInputStream stream, ByteBuf rawData, INetHandler iNetHandler) throws IOException {
        if( iNetHandler instanceof NetHandlerPlayServer ) {
            EntityPlayer player = ((NetHandlerPlayServer) iNetHandler).playerEntity;
            EntityTurretBase turret = (EntityTurretBase) player.worldObj.getEntityByID(stream.readInt());

            List<TurretUpgrade> allUpgrades = turret.getUpgradeHandler().getUpgradeList();
            Iterator<TurretUpgrade> itUpgrades = allUpgrades.iterator();
            while( itUpgrades.hasNext() ) {
                TurretUpgrade upgrade = itUpgrades.next();
                ItemStack stack = TmrItems.turretUpgrade.getStackWithUpgrade(upgrade, 1);
                turret.getUpgradeHandler().removeUpgrade(turret, upgrade);
                itUpgrades.remove();
                if( (stack = InventoryUtils.addStackToInventory(stack, player.inventory)) != null ) {
                    turret.entityDropItem(stack, 0.0F);
                }
                player.inventoryContainer.detectAndSendChanges();
            }
        }
    }

    @Override
    public void writeData(ByteBufOutputStream stream, Tuple data) throws IOException {
        stream.writeInt((Integer) data.getValue(0));
    }

    public static void sendToServer(EntityTurretBase turret) {
        PacketManager.sendToServer(PacketManager.EJECT_ALL_UPGRADES, Unit.with(turret.getEntityId()));
    }
}
