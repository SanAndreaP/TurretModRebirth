/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.registry.turret;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.event.OpenTcuGuiEvent;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.event.OpenTcuContainerEvent;
import de.sanandrew.mods.turretmod.api.turret.IGuiTcuRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuContainer;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuScreen;
import de.sanandrew.mods.turretmod.client.gui.tcu.TargetType;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiInfo;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiLevels;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiRemoteAccess;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiShieldColorizer;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiSmartTargets;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiTargets;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiUpgrades;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.PlayerHeads;
import de.sanandrew.mods.turretmod.entity.turret.UpgradeProcessor;
import de.sanandrew.mods.turretmod.inventory.container.ContainerTurretRemoteAccess;
import de.sanandrew.mods.turretmod.inventory.container.ContainerTurretUpgrades;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketSyncTcuGuis;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
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
import java.util.function.Function;
import java.util.function.Supplier;

public final class GuiTcuRegistry
        implements IGuiTcuRegistry
{
    public static final ResourceLocation INFO           = new ResourceLocation(TmrConstants.ID, "info");
    public static final ResourceLocation TARGETS_MOB    = new ResourceLocation(TmrConstants.ID, "targets.creature");
    public static final ResourceLocation TARGETS_PLAYER = new ResourceLocation(TmrConstants.ID, "targets.player");
    public static final ResourceLocation TARGETS_SMART  = new ResourceLocation(TmrConstants.ID, "targets.smart");
    public static final ResourceLocation UPGRADES       = new ResourceLocation(TmrConstants.ID, "upgrades");
    public static final ResourceLocation COLORIZER      = new ResourceLocation(TmrConstants.ID, "colorizer");
    public static final ResourceLocation LEVELING       = new ResourceLocation(TmrConstants.ID, "leveling");
    public static final ResourceLocation REMOTE_ACCESS  = new ResourceLocation(TmrConstants.ID, "remote_access");

    public static final List<ResourceLocation> PAGE_KEYS = new ArrayList<>();
    public static final GuiTcuRegistry         INSTANCE  = new GuiTcuRegistry();

    @SideOnly(Side.CLIENT)
    private static Map<ResourceLocation, Page>             pages;
    private static Map<ResourceLocation, ContainerFactory> containers;

    private GuiTcuRegistry() { }

    @SideOnly(Side.CLIENT)
    public Gui openGUI(int type, EntityPlayer player, ITurretInst turretInst, boolean isRemote) {
        if( type >= 0 && type < PAGE_KEYS.size() ) {
            Page page = getPage(PAGE_KEYS.get(type));
            if( page != null ) {
                OpenTcuGuiEvent event = new OpenTcuGuiEvent(player, turretInst, page.factory);
                if( !MinecraftForge.EVENT_BUS.post(event) ) {
                    IGuiTCU   guiDelegate = event.factory.get();
                    Container cnt         = guiDelegate.getContainer(player, turretInst, isRemote);

                    if( cnt != null ) {
                        return new GuiTcuContainer(PAGE_KEYS.get(type), guiDelegate, cnt, turretInst, isRemote);
                    } else {
                        return new GuiTcuScreen(PAGE_KEYS.get(type), guiDelegate, turretInst, isRemote);
                    }
                }
            }
        }

        return null;
    }

    public Page getPage(ResourceLocation location) {
        return pages.get(location);
    }

    public Container openContainer(int type, EntityPlayer player, ITurretInst turretInst, boolean isRemote) {
        if( type >= 0 && type < PAGE_KEYS.size() ) {
            ResourceLocation      key   = PAGE_KEYS.get(type);
            OpenTcuContainerEvent event = new OpenTcuContainerEvent(player, turretInst, containers.get(key));
            if( !MinecraftForge.EVENT_BUS.post(event) ) {
                if( event.factory != null ) {
                    return event.factory.get(player, turretInst, isRemote);
                }
            }
        }

        return null;
    }

    @Override
    public void registerPage(ResourceLocation key, int position, @Nullable ContainerFactory containerFactory) {
        if( containers == null ) {
            containers = new HashMap<>();
        }

        if( position >= PAGE_KEYS.size() ) {
            PAGE_KEYS.add(key);
        } else {
            PAGE_KEYS.set(position, key);
        }

        containers.put(key, containerFactory);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerPageGUI(ResourceLocation key, ItemStack icon, Supplier<IGuiTCU> factory, Function<IGuiTcuInst<?>, Boolean> canShowTabFunc) {
        if( pages == null ) {
            pages = new HashMap<>();
        }

        pages.put(key, new Page(() -> icon, factory, canShowTabFunc));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerPageGUI(ResourceLocation key, Supplier<ItemStack> iconSupplier, Supplier<IGuiTCU> factory, Function<IGuiTcuInst<?>, Boolean> canShowTabFunc) {
        if( pages == null ) {
            pages = new HashMap<>();
        }

        pages.put(key, new Page(iconSupplier, factory, canShowTabFunc));
    }

    public static void initializePages(IGuiTcuRegistry registry) {
        registry.registerPage(INFO,           0, null);
        registry.registerPage(TARGETS_MOB,    1, null);
        registry.registerPage(TARGETS_PLAYER, 2, null);
        registry.registerPage(TARGETS_SMART,  3, null);
        registry.registerPage(UPGRADES,       4, (player, turretInst, isRemote) -> new ContainerTurretUpgrades(player.inventory, (UpgradeProcessor) turretInst.getUpgradeProcessor(), isRemote));
        registry.registerPage(COLORIZER,      5, null);
        registry.registerPage(LEVELING,       6, null);
        registry.registerPage(REMOTE_ACCESS,  7, (player, turretInst, isRemote) -> new ContainerTurretRemoteAccess(player.inventory, turretInst));
    }

    @SideOnly(Side.CLIENT)
    public static void initializePagesClient(IGuiTcuRegistry registry) {
        registry.registerPageGUI(INFO,           new ItemStack(Items.BOOK),                     GuiInfo::new,                              null);
        registry.registerPageGUI(TARGETS_MOB,    new ItemStack(Items.SKULL, 1, 2),              () -> new GuiTargets(TargetType.CREATURE), IGuiTcuInst::hasPermision);
        registry.registerPageGUI(TARGETS_PLAYER, PlayerHeads::getRandomSkull,                   () -> new GuiTargets(TargetType.PLAYER),   IGuiTcuInst::hasPermision);
        registry.registerPageGUI(TARGETS_SMART,  getUpgradeItem(Upgrades.SMART_TGT),            GuiSmartTargets::new,                      GuiSmartTargets::showTab);
        registry.registerPageGUI(UPGRADES,       getUpgradeItem(UpgradeRegistry.EMPTY_UPGRADE), GuiUpgrades::new,                          IGuiTcuInst::hasPermision);
        registry.registerPageGUI(COLORIZER,      getUpgradeItem(Upgrades.SHIELD_COLORIZER),     GuiShieldColorizer::new,                   GuiShieldColorizer::showTab);
        registry.registerPageGUI(LEVELING,       getUpgradeItem(Upgrades.LEVELING),             GuiLevels::new,                            GuiLevels::showTab);
        registry.registerPageGUI(REMOTE_ACCESS,  getUpgradeItem(Upgrades.REMOTE_ACCESS),        GuiRemoteAccess::new,                      GuiRemoteAccess::showTab);
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

    private static ItemStack getUpgradeItem(IUpgrade upgrade) {
        return UpgradeRegistry.INSTANCE.getItem(upgrade.getId());
    }

    @SideOnly(Side.CLIENT)
    public static final class Page
    {
        private final Supplier<ItemStack>               icon;
        private final Function<IGuiTcuInst<?>, Boolean> canShowTabFunc;
        private final Supplier<IGuiTCU>                 factory;

        private Page(Supplier<ItemStack> icon, Supplier<IGuiTCU> factory, Function<IGuiTcuInst<?>, Boolean> canShowTabFunc) {
            this.icon = icon;
            this.factory = factory;
            this.canShowTabFunc = canShowTabFunc;
        }

        public boolean showTab(IGuiTcuInst<?> gui) {
            return this.canShowTabFunc == null || this.canShowTabFunc.apply(gui);
        }

        public ItemStack getIcon() {
            return this.icon.get();
        }
    }

}
