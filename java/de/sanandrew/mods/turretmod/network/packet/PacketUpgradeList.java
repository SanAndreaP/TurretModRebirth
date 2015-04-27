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
import de.sanandrew.mods.turretmod.entity.turret.AEntityTurretBase;
import de.sanandrew.mods.turretmod.network.PacketManager;
import de.sanandrew.mods.turretmod.util.TurretMod;
import de.sanandrew.mods.turretmod.util.upgrade.TurretUpgrade;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.network.INetHandler;

import java.io.IOException;
import java.util.List;

public class PacketUpgradeList
        implements IPacket
{
    @Override
    public void process(ByteBufInputStream inStream, ByteBuf rawData, INetHandler iNetHandler) throws IOException {
        TurretMod.proxy.processUpgradeListClt(inStream);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeData(ByteBufOutputStream outStream, Tuple data) throws IOException {
        List<TurretUpgrade> upgrades = (List<TurretUpgrade>) data.getValue(1);
        outStream.writeInt((Integer) data.getValue(0));
        outStream.writeInt(upgrades.size());
        for( TurretUpgrade upg : upgrades ) {
            outStream.writeUTF(upg.getRegistrationName());
        }
        System.out.println("Turret upgrades updated");
    }

    public static void sendPacket(AEntityTurretBase turret) {
        final List<TurretUpgrade> upgrades = turret.getUpgradeList();

        Tuple data = Pair.with(turret.getEntityId(), upgrades);
        PacketManager.sendToAllAround(PacketManager.TURRET_UPGRADE_SYNC, turret.dimension, turret.posX, turret.posY, turret.posZ, 128.0D, data);
    }
}
