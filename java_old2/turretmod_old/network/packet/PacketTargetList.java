/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.network.packet;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import de.sanandrew.core.manpack.network.IPacket;
import de.sanandrew.core.manpack.util.javatuples.Pair;
import de.sanandrew.core.manpack.util.javatuples.Tuple;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretBase;
import de.sanandrew.mods.turretmod.network.PacketManager;
import de.sanandrew.mods.turretmod.util.TurretMod;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.network.INetHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PacketTargetList
        implements IPacket
{
    @Override
    public void process(ByteBufInputStream inStream, ByteBuf rawData, INetHandler iNetHandler) throws IOException {
        TurretMod.proxy.processTargetListClt(inStream);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeData(ByteBufOutputStream outStream, Tuple data) throws IOException {
        List<Class<? extends EntityLiving>> applicableTargets = (List<Class<? extends EntityLiving>>) data.getValue(1);
        outStream.writeInt((Integer) data.getValue(0));
        outStream.writeInt(applicableTargets.size());
        for( Class targetCls : applicableTargets ) {
            outStream.writeUTF((String) EntityList.classToStringMapping.get(targetCls));
        }
    }

    public static void sendPacket(EntityTurretBase turret) {
        final Map<Class<? extends EntityLiving>, Boolean> targets = turret.getTargetHandler().getTargetList();
        List<Class<? extends EntityLiving>> applicableTargets = new ArrayList<>(Collections2.filter(targets.keySet(), new Predicate<Class<? extends EntityLiving>>() {
                                                                                        @Override public boolean apply(Class<? extends EntityLiving> input) {
                                                                                            return targets.get(input);
                                                                                        }
                                                                                    }));

        Tuple data = Pair.with(turret.getEntityId(), applicableTargets);
        PacketManager.sendToAllAround(PacketManager.TURRET_TARGET_SYNC, turret.dimension, turret.posX, turret.posY, turret.posZ, 128.0D, data);
    }
}
