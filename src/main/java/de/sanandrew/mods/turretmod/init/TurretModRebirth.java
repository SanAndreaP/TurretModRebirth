/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.init;

import de.sanandrew.mods.sanlib.lib.network.MessageHandler;
import de.sanandrew.mods.turretmod.api.ITmrPlugin;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.TmrPlugin;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.client.init.ClientProxy;
import de.sanandrew.mods.turretmod.datagenerator.AssemblyProvider;
import de.sanandrew.mods.turretmod.datagenerator.ElectrolyteProvider;
import de.sanandrew.mods.turretmod.datagenerator.PatchouliProvider;
import de.sanandrew.mods.turretmod.datagenerator.TmrItemModelProvider;
import de.sanandrew.mods.turretmod.entity.EntityRegistry;
import de.sanandrew.mods.turretmod.entity.projectile.ProjectileRegistry;
import de.sanandrew.mods.turretmod.entity.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.inventory.ContainerRegistry;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.item.repairkits.RepairKitRegistry;
import de.sanandrew.mods.turretmod.item.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.leveling.StageLoader;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.recipe.RecipeRegistry;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyManager;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Mod(TmrConstants.ID)
public class TurretModRebirth
{
    public static final List<ITmrPlugin> PLUGINS = new ArrayList<>();
    public static final MessageHandler   NETWORK = new MessageHandler(TmrConstants.ID, "1.0.0");
    public static final IProxy           PROXY   = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);

    public TurretModRebirth() {
        IEventBus meb = FMLJavaModLoadingContext.get().getModEventBus();

        TmrConfig.register(meb);

        meb.addListener(this::gatherData);
        meb.addListener(this::constructMod);
        meb.addListener(PROXY::setupClient);
        meb.addListener(this::setupCommon);

        MinecraftForge.EVENT_BUS.addListener(StageLoader::enableSync);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerLogout);
    }

    private void constructMod(FMLConstructModEvent event) {
        IEventBus meb = FMLJavaModLoadingContext.get().getModEventBus();

        loadPlugins(ModList.get().getAllScanData());

        PacketRegistry.initialize();

        PLUGINS.forEach(plugin -> plugin.registerTurrets(TurretRegistry.INSTANCE));
        PLUGINS.forEach(plugin -> plugin.registerAmmo(AmmunitionRegistry.INSTANCE));
        PLUGINS.forEach(plugin -> plugin.registerProjectiles(ProjectileRegistry.INSTANCE));
        PLUGINS.forEach(plugin -> plugin.registerRepairKits(RepairKitRegistry.INSTANCE));
        PLUGINS.forEach(plugin -> plugin.registerUpgrades(UpgradeRegistry.INSTANCE));
        PLUGINS.forEach(plugin -> plugin.manageAssembly(AssemblyManager.INSTANCE));

        // add forge registries AFTER mod registries to load things properly
        BlockRegistry.register(meb);
        ItemRegistry.register(meb);
        EntityRegistry.register(meb);
        RecipeRegistry.register(meb);
        ContainerRegistry.register(meb);
        SoundRegistry.register(meb);
    }

    private void setupCommon(FMLCommonSetupEvent event) {
        PLUGINS.forEach(ITmrPlugin::setup);
        PLUGINS.forEach(plugin -> plugin.registerTcuPages(ItemRegistry.TURRET_CONTROL_UNIT));
    }

    private void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        if( event.includeServer() ) {
            gen.addProvider(new ElectrolyteProvider(gen));
            gen.addProvider(new AssemblyProvider(gen));
        }
        if( event.includeClient() ) {
            gen.addProvider(new TmrItemModelProvider(gen, event.getExistingFileHelper()));
            gen.addProvider(new PatchouliProvider(gen));
        }
    }

    private static void loadPlugins(List<ModFileScanData> dataTable) {
        Type tmrPluginType = Type.getType(TmrPlugin.class);
        for( ModFileScanData scanData : dataTable ) {
            Set<ModFileScanData.AnnotationData> annotationDataSet = scanData.getAnnotations();
            for( ModFileScanData.AnnotationData annotationData : annotationDataSet ) {
                if( Objects.equals(annotationData.getAnnotationType(), tmrPluginType) ) {
                    try {
                        Class<?>                    asmClass         = Class.forName(annotationData.getMemberName());
                        Class<? extends ITmrPlugin> asmInstanceClass = asmClass.asSubclass(ITmrPlugin.class);
                        ITmrPlugin                  instance         = asmInstanceClass.getConstructor().newInstance();
                        PLUGINS.add(instance);
                    } catch( ClassNotFoundException | IllegalAccessException | ExceptionInInitializerError | InstantiationException | NoSuchMethodException | InvocationTargetException e ) {
                        TmrConstants.LOG.error("Failed to load: {}", annotationData.getMemberName(), e);
                    }
                }
            }
        }

        PLUGINS.sort(TmrInternalPlugin::getSortId);
    }

    @SuppressWarnings("java:S2142")
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch( InterruptedException ignored ) { /* no-op */ }
            System.exit(143);
        }).start();
    }

//    @Mod.EventHandler
//    public void interModComm(FMLInterModComms.IMCEvent event) {
//        event.getMessages().forEach(message -> {
//            if( message.key.equals(TmrConstants.ID + ":checkProjForShield") && message.isFunctionMessage() ) {
//                message.getFunctionValue(Entity.class, Entity.class).ifPresent(ForcefieldHandler.PROJ_GET_OWNER::add);
//            }
//        });
//    }
}
