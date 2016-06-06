/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.client.gui.tinfo.TurretInfoCategory;
import de.sanandrew.mods.turretmod.entity.turret.TargetProcessor;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.medpack.RepairKitRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityPotatoGenerator;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.RecipesIngots;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = TurretModRebirth.ID, version = TurretModRebirth.VERSION, name = "Turret Mod Rebirth")
public class TurretModRebirth
{
    public static final String ID = "sapturretmod";
    public static final String VERSION = "4.0.0-alpha.1";
    public static final Logger LOG = LogManager.getLogger(ID);
    public static final String CHANNEL = "SapTurretModNWCH";

    public static SimpleNetworkWrapper network;

    private static final String MOD_PROXY_CLIENT = "de.sanandrew.mods.turretmod.client.util.ClientProxy";
    private static final String MOD_PROXY_COMMON = "de.sanandrew.mods.turretmod.util.CommonProxy";

    @Mod.Instance(TurretModRebirth.ID)
    public static TurretModRebirth instance;
    @SidedProxy(modId = TurretModRebirth.ID, clientSide = TurretModRebirth.MOD_PROXY_CLIENT, serverSide = TurretModRebirth.MOD_PROXY_COMMON)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel(CHANNEL);

        PacketRegistry.initialize();

        AmmoRegistry.INSTANCE.initialize();
        RepairKitRegistry.INSTANCE.initialize();
        TurretRegistry.INSTANCE.initialize();
        UpgradeRegistry.INSTANCE.initialize();
        proxy.preInit(event);
        BlockRegistry.initialize();
        ItemRegistry.initialize();
        TurretAssemblyRecipes.initialize();

        TileEntityPotatoGenerator.initializeRecipes();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);

        CraftingManager.getInstance().addRecipe(new ItemStack(BlockRegistry.turretAssembly, 1),
                "ROR", "IAI", "CFC",
                'R', new ItemStack(Items.repeater),
                'O', new ItemStack(Blocks.obsidian),
                'I', new ItemStack(Items.iron_ingot),
                'A', new ItemStack(Blocks.anvil, 1, 0),
                'C', new ItemStack(Blocks.cobblestone),
                'F', new ItemStack(Blocks.furnace));
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        TargetProcessor.initialize();

        proxy.postInit(event);
    }
}
