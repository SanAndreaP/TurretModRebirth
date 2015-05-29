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
import de.sanandrew.core.manpack.util.helpers.InventoryUtils;
import de.sanandrew.core.manpack.util.javatuples.Pair;
import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.mods.turretmod.api.TurretUpgrade;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretBase;
import de.sanandrew.mods.turretmod.network.PacketManager;
import de.sanandrew.mods.turretmod.util.TmrItems;
import de.sanandrew.mods.turretmod.util.TurretMod;
import de.sanandrew.mods.turretmod.api.registry.TurretUpgradeRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PacketEjectUpgrade
        implements IPacket
{
    @Override
    public void process(ByteBufInputStream stream, ByteBuf rawData, INetHandler iNetHandler) throws IOException {
        if( iNetHandler instanceof NetHandlerPlayServer ) {
            EntityPlayer player = ((NetHandlerPlayServer) iNetHandler).playerEntity;
            EntityTurretBase turret = (EntityTurretBase) player.worldObj.getEntityByID(stream.readInt());
            String upgradeName = stream.readUTF();
            TurretUpgrade upgrade = TurretUpgradeRegistry.getUpgrade(upgradeName);
            if( upgrade != null ) {
                List<TurretUpgrade> ejectingUpgrades = getRcurDepUpgrades(upgrade, turret.getUpgradeList());
                for( TurretUpgrade ejectUpg : ejectingUpgrades ) {
                    ItemStack stack = TmrItems.turretUpgrade.getStackWithUpgrade(ejectUpg, 1);
                    turret.removeUpgrade(ejectUpg);
                    if( (stack = InventoryUtils.addStackToInventory(stack, player.inventory)) != null ) {
                        turret.entityDropItem(stack, 0.0F);
                    }
                    player.inventoryContainer.detectAndSendChanges();
                }
            } else {
                TurretMod.MOD_LOG.printf(Level.WARN, "Cannot eject upgrade %s! This is an invalid upgrade name!", upgradeName);
            }
        }
    }

    @Override
    public void writeData(ByteBufOutputStream stream, Tuple data) throws IOException {
        stream.writeInt((Integer) data.getValue(0));
        stream.writeUTF((String) data.getValue(1));
    }

    public static void sendToServer(EntityTurretBase turret, TurretUpgrade upgrade) {
        PacketManager.sendToServer(PacketManager.EJECT_UPGRADE, Pair.with(turret.getEntityId(), TurretUpgradeRegistry.getRegistrationName(upgrade)));
    }

    private static List<TurretUpgrade> getRcurDepUpgrades(TurretUpgrade baseUpgrade, List<TurretUpgrade> currTurretUpgrades) {
        List<TurretUpgrade> currDepUpgrades = new ArrayList<>();
        if( currTurretUpgrades.contains(baseUpgrade) ) {
            currDepUpgrades.add(baseUpgrade);
            for( TurretUpgrade upg : currTurretUpgrades ) {
                if( upg.getDependantOn() == baseUpgrade ) {
                    currDepUpgrades.addAll(getRcurDepUpgrades(upg, currTurretUpgrades));
                }
            }
        }

        return currDepUpgrades;
    }
}
