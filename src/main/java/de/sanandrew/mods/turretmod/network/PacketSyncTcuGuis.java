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
import de.sanandrew.mods.turretmod.registry.turret.GuiTcuRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class PacketSyncTcuGuis
        extends AbstractMessage<PacketSyncTcuGuis>
{
    private Map<Integer, ResourceLocation> pageIds;

    @SuppressWarnings("unused")
    public PacketSyncTcuGuis() {
        this.pageIds = new TreeMap<>();
        for( int i = 0, max = GuiTcuRegistry.PAGE_KEYS.size(); i < max; i++ ) {
            this.pageIds.put(i, GuiTcuRegistry.PAGE_KEYS.get(i));
        }
    }

    @Override
    public void handleClientMessage(PacketSyncTcuGuis packet, EntityPlayer player) {
        if( FMLCommonHandler.instance().getSide() == Side.CLIENT ) {
            GuiTcuRegistry.PAGE_KEYS.clear();
            packet.pageIds.forEach((pos, key) -> {
                if( GuiTcuRegistry.PAGE_KEYS.size() >= pos ) {
                    GuiTcuRegistry.PAGE_KEYS.add(key);
                } else {
                    GuiTcuRegistry.PAGE_KEYS.set(pos, key);
                }
            });
        }
    }

    @Override
    public void handleServerMessage(PacketSyncTcuGuis packet, EntityPlayer player) { }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        this.pageIds = new HashMap<>(size);

        for( int i = 0; i < size; i++ ) {
            this.pageIds.put(i, new ResourceLocation(ByteBufUtils.readUTF8String(buf)));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.pageIds.size());
        this.pageIds.forEach((pos, key) -> {
            buf.writeInt(pos);
            ByteBufUtils.writeUTF8String(buf, key.toString());
        });
    }
}
