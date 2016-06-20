/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.core.manpack.util.javatuples.Quartet;
import de.sanandrew.mods.turretmod.network.PacketManager;
import io.netty.buffer.ByteBufInputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class CommonProxy
        implements IGuiHandler
{
    public void init() {}

    public void processTargetListClt(ByteBufInputStream stream) throws IOException {}

    public void processUpgradeListClt(ByteBufInputStream stream) throws IOException {}

    public void processTransmitterExpTime(ByteBufInputStream stream) throws IOException {}

    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if( SAPUtils.isIndexInRange(EnumGui.VALUES, id) ) {

        } else {
            TurretMod.MOD_LOG.printf(Level.WARN, "Container ID %d cannot be opened as it isn't a valid index in EnumGui!", id);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public void openGui(EntityPlayer player, EnumGui id, int x, int y, int z) {
        int guiId = id.ordinal();

        if( player instanceof EntityPlayerMP && getServerGuiElement(guiId, player, player.worldObj, x, y, z) == null ) {
            PacketManager.sendToPlayer(PacketManager.OPEN_CLIENT_GUI, (EntityPlayerMP) player, Quartet.with((byte) guiId, x, y, z));
        } else {
            FMLNetworkHandler.openGui(player, TurretMod.instance, guiId, player.worldObj, x, y, z);
        }
    }
}
