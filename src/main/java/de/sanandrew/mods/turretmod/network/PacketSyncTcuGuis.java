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
    private Map<Integer, ResourceLocation> guis;

    @SuppressWarnings("unused")
    public PacketSyncTcuGuis() {
        this.guis = new TreeMap<>();
        for( int i = 0, max = GuiTcuRegistry.GUI_RESOURCES.size(); i < max; i++ ) {
            this.guis.put(i, GuiTcuRegistry.GUI_RESOURCES.get(i));
        }
    }

    @Override
    public void handleClientMessage(PacketSyncTcuGuis packet, EntityPlayer player) {
        if( FMLCommonHandler.instance().getSide() == Side.CLIENT ) {
            GuiTcuRegistry.GUI_RESOURCES.clear();
            packet.guis.forEach((pos, gui) -> {
                if( GuiTcuRegistry.GUI_RESOURCES.size() >= pos ) {
                    GuiTcuRegistry.GUI_RESOURCES.add(gui);
                } else {
                    GuiTcuRegistry.GUI_RESOURCES.set(pos, gui);
                }
            });
        }
    }

    @Override
    public void handleServerMessage(PacketSyncTcuGuis packet, EntityPlayer player) { }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();
        this.guis = new HashMap<>(size);

        for( int i = 0; i < size; i++ ) {
            this.guis.put(i, new ResourceLocation(ByteBufUtils.readUTF8String(buf)));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.guis.size());
        this.guis.forEach((pos, gui) -> {
            buf.writeInt(pos);
            ByteBufUtils.writeUTF8String(buf, gui.toString());
        });
    }
}
