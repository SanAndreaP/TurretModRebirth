/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network.packet;

import de.sanandrew.core.manpack.network.IPacket;
import de.sanandrew.core.manpack.util.javatuples.Pair;
import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.core.manpack.util.javatuples.Unit;
import de.sanandrew.mods.turretmod.entity.turret.AEntityTurretBase;
import de.sanandrew.mods.turretmod.network.PacketManager;
import de.sanandrew.mods.turretmod.util.TmrItems;
import de.sanandrew.mods.turretmod.util.TurretMod;
import de.sanandrew.mods.turretmod.util.upgrade.TurretUpgrade;
import de.sanandrew.mods.turretmod.util.upgrade.TurretUpgradeRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PacketEjectAllUpgrades
        implements IPacket
{
    @Override
    public void process(ByteBufInputStream stream, ByteBuf rawData, INetHandler iNetHandler) throws IOException {
        if( iNetHandler instanceof NetHandlerPlayServer ) {
            EntityPlayer player = ((NetHandlerPlayServer) iNetHandler).playerEntity;
            AEntityTurretBase turret = (AEntityTurretBase) player.worldObj.getEntityByID(stream.readInt());

            List<TurretUpgrade> allUpgrades = turret.getUpgradeList();
            Iterator<TurretUpgrade> itUpgrades = allUpgrades.iterator();
            while( itUpgrades.hasNext() ) {
                TurretUpgrade upgrade = itUpgrades.next();
                ItemStack stack = TmrItems.turretUpgrade.getStackWithUpgrade(upgrade, 1);
                turret.removeUpgrade(upgrade);
                itUpgrades.remove();
                if( (stack = addItemStackToInventory(stack, player.inventory)) != null ) {
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

    public static void sendToServer(AEntityTurretBase turret) {
        PacketManager.sendToServer(PacketManager.EJECT_ALL_UPGRADES, Unit.with(turret.getEntityId()));
    }

    //TODO: for SAPMANPACK!
    static ItemStack addItemStackToInventory(ItemStack is, IInventory inv) {
        int invSize = inv.getSizeInventory() - (inv instanceof InventoryPlayer ? 4 : 0);

        int i2;
        ItemStack invIS;
        int rest;
        for( i2 = 0; i2 < invSize && is != null; ++i2 ) {
            invIS = inv.getStackInSlot(i2);
            if( invIS != null && ItemStack.areItemStacksEqual(is, invIS) ) {
                rest = is.stackSize + invIS.stackSize;
                int maxStack = Math.min(invIS.getMaxStackSize(), inv.getInventoryStackLimit());
                if( rest <= maxStack ) {
                    invIS.stackSize = rest;
                    inv.setInventorySlotContents(i2, invIS.copy());
                    is = null;
                    break;
                }

                int rest1 = rest - maxStack;
                invIS.stackSize = maxStack;
                inv.setInventorySlotContents(i2, invIS.copy());
                is.stackSize = rest1;
            }
        }

        for( i2 = 0; i2 < invSize && is != null; ++i2 ) {
            invIS = inv.getStackInSlot(i2);
            if( invIS == null && inv.isItemValidForSlot(i2, is) ) {
                if( is.stackSize <= inv.getInventoryStackLimit() ) {
                    inv.setInventorySlotContents(i2, is.copy());
                    is = null;
                    break;
                }

                rest = is.stackSize - inv.getInventoryStackLimit();
                is.stackSize = inv.getInventoryStackLimit();
                inv.setInventorySlotContents(i2, is.copy());
                is.stackSize = rest;
            }
        }

        return is;
    }
}
