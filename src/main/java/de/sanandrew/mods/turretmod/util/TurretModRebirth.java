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
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import de.sanandrew.mods.turretmod.entity.turret.UpgradeProcessor;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketUpdateTurretState;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.registry.electrolytegen.ElectrolyteHelper;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKitRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityElectrolyteGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mod(modid = TmrConstants.ID, version = TmrConstants.VERSION, name = TmrConstants.NAME, guiFactory = TurretModRebirth.GUI_FACTORY, dependencies = TmrConstants.DEPENDENCIES)
public class TurretModRebirth
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

        TmrConstants.utils = TmrUtils.INSTANCE;
        TmrConstants.repkitRegistry = RepairKitRegistry.INSTANCE;

        PacketRegistry.initialize();

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

        ElectrolyteHelper.initialize();

        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PLUGINS.forEach(ITmrPlugin::postInit);

        TargetProcessor.initialize();

        proxy.postInit(event);
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


}
