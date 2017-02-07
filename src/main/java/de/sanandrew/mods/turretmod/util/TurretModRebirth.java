/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.sanlib.lib.util.EntityUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.ITmrPlugin;
import de.sanandrew.mods.turretmod.api.ITmrUtils;
import de.sanandrew.mods.turretmod.api.TmrPlugin;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import de.sanandrew.mods.turretmod.entity.turret.UpgradeProcessor;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketUpdateTurretState;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKitRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityElectrolyteGenerator;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mod(modid = TmrConstants.ID, version = TmrConstants.VERSION, name = TmrConstants.NAME, guiFactory = TurretModRebirth.GUI_FACTORY, dependencies = TmrConstants.DEPENDENCIES)
public class TurretModRebirth
        implements ITmrUtils
{
    public static final String GUI_FACTORY = "de.sanandrew.mods.turretmod.client.gui.config.TmrGuiFactory";

    public static SimpleNetworkWrapper network;

    private static final String MOD_PROXY_CLIENT = "de.sanandrew.mods.turretmod.client.util.ClientProxy";
    private static final String MOD_PROXY_COMMON = "de.sanandrew.mods.turretmod.util.CommonProxy";

    private static final List<ITmrPlugin> PLUGINS = new ArrayList<>();

    @Mod.Instance(TmrConstants.ID)
    public static TurretModRebirth instance;
    @SidedProxy(modId = TmrConstants.ID, clientSide = TurretModRebirth.MOD_PROXY_CLIENT, serverSide = TurretModRebirth.MOD_PROXY_COMMON)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        event.getModMetadata().autogenerated = false;

        loadPlugins(event.getAsmData());

        TmrConfiguration.initConfiguration(event);

        network = NetworkRegistry.INSTANCE.newSimpleChannel(TmrConstants.CHANNEL);

        TmrConstants.utils = instance;
        TmrConstants.repkitRegistry = RepairKitRegistry.INSTANCE;

        PacketRegistry.initialize();
        Sounds.initialize();

        AmmoRegistry.INSTANCE.initialize();
        PLUGINS.forEach(plugin -> plugin.registerRepairKits(RepairKitRegistry.INSTANCE));
        TurretRegistry.INSTANCE.initialize();
        UpgradeRegistry.INSTANCE.initialize();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        PLUGINS.forEach(plugin -> plugin.registerAssemblyRecipes(TurretAssemblyRegistry.INSTANCE));
        TileEntityElectrolyteGenerator.initializeRecipes();
        CraftingRecipes.initialize();

        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PLUGINS.forEach(ITmrPlugin::postInit);

        TargetProcessor.initialize();

        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void onMissingMappings(FMLMissingMappingsEvent event) {
        remap(event, BlockRegistry.electrolyte_generator, "sapturretmod:potato_generator");
        remap(event, ItemRegistry.repair_kit, "sapturretmod:turret_repair_kit");
        remap(event, ItemRegistry.assembly_upg_auto, "sapturretmod:turret_assembly_auto");
        remap(event, ItemRegistry.assembly_upg_speed, "sapturretmod:turret_assembly_speed");
        remap(event, ItemRegistry.assembly_upg_filter, "sapturretmod:turret_assembly_filter");
    }

    private static void remap(FMLMissingMappingsEvent event, final Block block, final String oldName) {
        event.get().stream().filter(mapping -> mapping != null && mapping.name.equals(oldName) && mapping.type == GameRegistry.Type.BLOCK).forEach(mapping -> mapping.remap(block));
        Item itm = Item.getItemFromBlock(block);
        if( itm != null ) {
            remap(event, itm, oldName);
        }
    }

    private static void remap(FMLMissingMappingsEvent event, final Item item, final String oldName) {
        event.get().stream().filter(mapping -> mapping != null && mapping.name.equals(oldName) && mapping.type == GameRegistry.Type.ITEM).forEach(mapping -> mapping.remap(item));
    }

    private static void loadPlugins(ASMDataTable dataTable) {
        String annotationClassName = TmrPlugin.class.getCanonicalName();
        Set<ASMDataTable.ASMData> asmDatas = dataTable.getAll(annotationClassName);
        for (ASMDataTable.ASMData asmData : asmDatas) {
            try {
                Class<?> asmClass = Class.forName(asmData.getClassName());
                Class<? extends ITmrPlugin> asmInstanceClass = asmClass.asSubclass(ITmrPlugin.class);
                ITmrPlugin instance = asmInstanceClass.getConstructor().newInstance();
                PLUGINS.add(instance);
            } catch (ClassNotFoundException | IllegalAccessException | ExceptionInInitializerError | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                TmrConstants.LOG.error("Failed to load: {}", asmData.getClassName(), e);
            }
        }
    }

    @Override
    public ITargetProcessor getNewTargetProcInstance(EntityTurret turret) {
        return new TargetProcessor(turret);
    }

    @Override
    public IUpgradeProcessor getNewUpgradeProcInstance(EntityTurret turret) {
        return new UpgradeProcessor(turret);
    }

    @Override
    public boolean isTCUItem(ItemStack stack) {
        return ItemStackUtils.isItem(stack, ItemRegistry.turret_control_unit);
    }

    @Override
    public void onTurretDeath(EntityTurret turret) {
        ((TargetProcessor) turret.getTargetProcessor()).dropAmmo();
        ((UpgradeProcessor) turret.getUpgradeProcessor()).dropUpgrades();
    }

    @Override
    public void updateTurretState(EntityTurret turret) {
        PacketRegistry.sendToAllAround(new PacketUpdateTurretState(turret), turret.dimension, turret.posX, turret.posY, turret.posZ, 64.0D);
    }

    @Override
    public ItemStack getPickedTurretResult(RayTraceResult target, EntityTurret turret) {
        return ItemRegistry.turret_placer.getTurretItem(1, TurretRegistry.INSTANCE.getInfo(turret.getClass()));
    }

    @Override
    public void openGui(EntityPlayer player, EnumGui id, int x, int y, int z) {
        proxy.openGui(player, id, x, y, z);
    }

    @Override
    public boolean canPlayerEditAll() {
        return TmrConfiguration.playerCanEditAll;
    }

    @Override
    public boolean canOpEditAll() {
        return TmrConfiguration.opCanEditAll;
    }

    @Override
    public <T extends Entity> List<T> getPassengersOfClass(Entity e, Class<T> psgClass) {
        return EntityUtils.getPassengersOfClass(e, psgClass);
    }

    @Override
    public boolean isStackValid(ItemStack stack) {
        return ItemStackUtils.isValid(stack);
    }
}
