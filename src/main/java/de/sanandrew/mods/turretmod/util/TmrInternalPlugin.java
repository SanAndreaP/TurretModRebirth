/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.turretmod.api.ITmrPlugin;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.TmrPlugin;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionRegistry;
import de.sanandrew.mods.turretmod.api.assembly.ITurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.client.tcu.ILabelRegistry;
import de.sanandrew.mods.turretmod.api.client.turret.ITurretRenderRegistry;
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretInfoCategoryRegistry;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKitRegistry;
import de.sanandrew.mods.turretmod.api.turret.IGuiTcuRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretRegistry;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeRegistry;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiInfo;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiTargetCreatures;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiTargetPlayers;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.GuiUpgrades;
import de.sanandrew.mods.turretmod.client.gui.tcu.label.Labels;
import de.sanandrew.mods.turretmod.client.gui.tcu.page.PlayerHeads;
import de.sanandrew.mods.turretmod.client.gui.tinfo.TurretInfoCategoryRegistry;
import de.sanandrew.mods.turretmod.client.render.turret.RenderTurret;
import de.sanandrew.mods.turretmod.entity.turret.UpgradeProcessor;
import de.sanandrew.mods.turretmod.event.TargetingEventHandler;
import de.sanandrew.mods.turretmod.inventory.ContainerTurretUpgrades;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.Ammunitions;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKits;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@TmrPlugin
public class TmrInternalPlugin
        implements ITmrPlugin
{
    private static final ResourceLocation GUI_INFO = new ResourceLocation(TmrConstants.ID, "info");
    private static final ResourceLocation GUI_TARGETS_MOB = new ResourceLocation(TmrConstants.ID, "targets_creature");
    private static final ResourceLocation GUI_TARGETS_PLAYER = new ResourceLocation(TmrConstants.ID, "targets_player");
    private static final ResourceLocation GUI_TARGETS_SMART = new ResourceLocation(TmrConstants.ID, "targets_smart");
    private static final ResourceLocation GUI_UPGRADES = new ResourceLocation(TmrConstants.ID, "upgrades");

    @Override
    public void registerAssemblyRecipes(ITurretAssemblyRegistry registry) {
        TurretAssemblyRecipes.initialize(registry);
    }

    @Override
    public void registerTurrets(ITurretRegistry registry) {
        Turrets.initialize(registry);
    }

    @Override
    public void registerRepairKits(IRepairKitRegistry registry) {
        RepairKits.initialize(registry);
    }

    @Override
    public void registerAmmo(IAmmunitionRegistry registry) {
        Ammunitions.initialize(registry);
    }

    @Override
    public void registerUpgrades(IUpgradeRegistry registry) {
        Upgrades.initialize(registry);
    }

    @Override
    public void postInit() {
        ITargetProcessor.TARGET_BUS.register(new TargetingEventHandler());
    }

    @Override
    public void registerTcuEntries(IGuiTcuRegistry registry) {
        registry.registerGuiEntry(GUI_INFO, 0, null);
        registry.registerGuiEntry(GUI_TARGETS_MOB, 1, null);
        registry.registerGuiEntry(GUI_TARGETS_PLAYER, 2, null);
//        registry.registerGuiEntry(GUI_TARGETS_SMART, 3, null);
        registry.registerGuiEntry(GUI_UPGRADES, 4, (player, turretInst) -> new ContainerTurretUpgrades(player.inventory, (UpgradeProcessor) turretInst.getUpgradeProcessor()));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerTurretInfoCategories(ITurretInfoCategoryRegistry registry) {
        TurretInfoCategoryRegistry.initialize(registry);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerTurretRenderer(ITurretRenderRegistry<?> registry) {
        RenderTurret.initialize(registry);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerTcuLabelElements(ILabelRegistry registry) {
        Labels.initialize(registry);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerTcuGuis(IGuiTcuRegistry registry) {
        registry.registerGui(GUI_INFO, new ItemStack(Items.BOOK), GuiInfo::new, null);
        registry.registerGui(GUI_TARGETS_MOB, new ItemStack(Items.SKULL, 1, 2), GuiTargetCreatures::new, IGuiTcuInst::hasPermision);
        registry.registerGui(GUI_TARGETS_PLAYER, PlayerHeads::getRandomSkull, GuiTargetPlayers::new, IGuiTcuInst::hasPermision);
        registry.registerGui(GUI_UPGRADES, new ItemStack(ItemRegistry.TURRET_UPGRADE), GuiUpgrades::new, IGuiTcuInst::hasPermision);
    }
}
