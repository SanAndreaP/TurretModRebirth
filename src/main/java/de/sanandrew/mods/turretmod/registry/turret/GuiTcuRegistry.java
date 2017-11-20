/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.turretmod.api.client.event.OpenTcuGuiEvent;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.event.OpenTcuContainerEvent;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuContainer;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuScreen;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketSyncTcuGuis;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class GuiTcuRegistry
        implements de.sanandrew.mods.turretmod.api.turret.IGuiTcuRegistry
{
    public static final List<ResourceLocation> GUI_RESOURCES = new ArrayList<>();
    public static final GuiTcuRegistry INSTANCE = new GuiTcuRegistry();

    @SideOnly(Side.CLIENT)
    private static Map<ResourceLocation, GuiEntry> guis;
    private static Map<ResourceLocation, BiFunction<EntityPlayer, ITurretInst, Container>> containers;

    private GuiTcuRegistry() { }

    @SideOnly(Side.CLIENT)
    public Gui openGUI(int type, EntityPlayer player, ITurretInst turretInst) {
        if( type >= 0 && type < GUI_RESOURCES.size() ) {
            GuiEntry entry = getGuiEntry(GUI_RESOURCES.get(type));
            if( entry != null ) {
                OpenTcuGuiEvent event = new OpenTcuGuiEvent(player, turretInst, entry.factory);
                if( !MinecraftForge.EVENT_BUS.post(event) ) {
                    IGuiTCU guiDelegate = event.factory.get();
                    Container cnt = guiDelegate.getContainer(player, turretInst);
                    if( cnt != null ) {
                        return new GuiTcuContainer(guiDelegate, cnt, turretInst);
                    } else {
                        return new GuiTcuScreen(guiDelegate, turretInst);
                    }
                }
            }
        }

        return null;
    }

    public GuiEntry getGuiEntry(ResourceLocation location) {
        return guis.get(location);
    }

    public Container openContainer(int type, EntityPlayer player, ITurretInst turretInst) {
        if( type >= 0 && type < GUI_RESOURCES.size() ) {
            ResourceLocation loc = GUI_RESOURCES.get(type);
            OpenTcuContainerEvent event = new OpenTcuContainerEvent(player, turretInst, containers.get(loc));
            if( !MinecraftForge.EVENT_BUS.post(event) ) {
                if( event.factory != null ) {
                    return event.factory.apply(player, turretInst);
                }
            }
        }

        return null;
    }

    @Override
    public void registerGuiEntry(ResourceLocation location, int position, @Nullable BiFunction<EntityPlayer, ITurretInst, Container> containerFactory) {
        if( containers == null ) {
            containers = new HashMap<>();
        }

        if( position >= GUI_RESOURCES.size() ) {
            GUI_RESOURCES.add(location);
        } else {
            GUI_RESOURCES.set(position, location);
        }

        containers.put(location, containerFactory);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerGui(ResourceLocation location, ItemStack icon, Supplier<IGuiTCU> factory, Function<IGuiTcuInst<?>, Boolean> canShowTabFunc) {
        if( guis == null ) {
            guis = new HashMap<>();
        }

        guis.put(location, new GuiEntry(icon, factory, canShowTabFunc));
    }

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if( event.getEntity() instanceof EntityPlayerMP && !event.getWorld().isRemote ) {
            PacketRegistry.sendToPlayer(new PacketSyncTcuGuis(), (EntityPlayerMP) event.getEntity());
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if( !event.getWorld().isRemote ) {
            PacketRegistry.sendToAll(new PacketSyncTcuGuis());
        }
    }

    @SideOnly(Side.CLIENT)
    public static final class GuiEntry
    {
        public final ItemStack icon;
        final Function<IGuiTcuInst<?>, Boolean> canShowTabFunc;
        final Supplier<IGuiTCU> factory;

        GuiEntry(ItemStack icon, Supplier<IGuiTCU> factory, Function<IGuiTcuInst<?>, Boolean> canShowTabFunc) {
            this.icon = icon;
            this.factory = factory;
            this.canShowTabFunc = canShowTabFunc;
        }

        public boolean showTab(IGuiTcuInst<?> gui) {
            return this.canShowTabFunc == null || this.canShowTabFunc.apply(gui);
        }
    }
}
