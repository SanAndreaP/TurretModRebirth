/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.common.registry.EntityRegistry;
import de.sanandrew.mods.turretmod.entity.projectile.EntityProjectileCrossbowBolt;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.inventory.ContainerTurretAssembly;
import de.sanandrew.mods.turretmod.network.PacketOpenGui;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import net.darkhax.bookshelf.lib.Tuple;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;

public class CommonProxy
        implements IGuiHandler
{
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(PlayerList.INSTANCE);
//        FMLCommonHandler.instance().bus().register(PlayerList.INSTANCE);
    }

    public void init(FMLInitializationEvent event) {
        EntityRegistry.registerModEntity(EntityTurretCrossbow.class, "turret_mkI_crossbow", 0, TurretModRebirth.instance, 128, 1, false);
        EntityRegistry.registerModEntity(EntityProjectileCrossbowBolt.class, "turret_proj_arrow", 1, TurretModRebirth.instance, 128, 1, true);
    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    @Override
    public final Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if( id >= 0 && id < EnumGui.VALUES.length ) {
            switch( EnumGui.VALUES[id] ) {
                case GUI_TASSEMBLY_MAN:
                    TileEntity te = world.getTileEntity(x, y, z);
                    if( te instanceof TileEntityTurretAssembly ) {
                        return new ContainerTurretAssembly(player.inventory, (TileEntityTurretAssembly) te);
                    }
                    break;
            }
        } else {
            TurretModRebirth.LOG.log(Level.WARN, "Gui ID %d cannot be opened as it isn't a valid index in EnumGui!", id);
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
            PacketRegistry.sendToPlayer(new PacketOpenGui((byte) guiId, x, y, z), (EntityPlayerMP) player);
        } else {
            FMLNetworkHandler.openGui(player, TurretModRebirth.instance, guiId, player.worldObj, x, y, z);
        }
    }

    public void spawnParticle(EnumParticle particle, double x, double y, double z, Tuple data) {

    }
}
