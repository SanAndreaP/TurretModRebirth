/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconInst;
import de.sanandrew.mods.turretmod.api.ITmrPlugin;
import de.sanandrew.mods.turretmod.api.TmrPlugin;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionRegistry;
import de.sanandrew.mods.turretmod.api.ammo.IProjectileRegistry;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyManager;
import de.sanandrew.mods.turretmod.api.client.render.IRender;
import de.sanandrew.mods.turretmod.api.client.render.IRenderRegistry;
import de.sanandrew.mods.turretmod.api.client.tcu.ILabelRegistry;
import de.sanandrew.mods.turretmod.api.client.turret.ITurretRenderRegistry;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteManager;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKitRegistry;
import de.sanandrew.mods.turretmod.api.turret.IGuiTcuRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretRegistry;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeRegistry;
import de.sanandrew.mods.turretmod.client.gui.lexicon.Categories;
import de.sanandrew.mods.turretmod.client.gui.tcu.GuiTcuHelper;
import de.sanandrew.mods.turretmod.client.gui.tcu.label.Labels;
import de.sanandrew.mods.turretmod.client.render.projectile.RenderProjectile;
import de.sanandrew.mods.turretmod.client.render.turret.RenderTurret;
import de.sanandrew.mods.turretmod.event.TargetingEventHandler;
import de.sanandrew.mods.turretmod.registry.ammo.Ammunitions;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyRecipeLoader;
import de.sanandrew.mods.turretmod.registry.electrolytegen.ElectrolyteRecipeLoader;
import de.sanandrew.mods.turretmod.registry.projectile.Projectiles;
import de.sanandrew.mods.turretmod.registry.repairkit.RepairKits;
import de.sanandrew.mods.turretmod.registry.turret.GuiTcuRegistry;
import de.sanandrew.mods.turretmod.registry.turret.Turrets;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

@TmrPlugin
public class TmrInternalPlugin
        implements ITmrPlugin
{
    @Override
    public void registerAssemblyRecipes(IAssemblyManager registry) {
        AssemblyRecipeLoader.initialize(registry);
    }

    @Override
    public void registerElectrolyteRecipes(IElectrolyteManager registry) {
        ElectrolyteRecipeLoader.initialize(registry);
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
        GuiTcuRegistry.initialize(registry);
    }

    @Override
    public void registerProjectiles(IProjectileRegistry registry) {
        Projectiles.initialize(registry);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerLexicon(ILexiconInst registry) {
        Categories.initialize(registry);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerTurretRenderer(ITurretRenderRegistry<?> registry) {
        RenderTurret.initialize(registry);
    }

    @Override
    public <T extends Entity> void registerProjectileRenderer(IRenderRegistry<ResourceLocation, T, IRender<T>> registry) {
        RenderProjectile.initialize(registry);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerTcuLabelElements(ILabelRegistry registry) {
        Labels.initialize(registry);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerTcuGuis(IGuiTcuRegistry registry) {
        GuiTcuHelper.initialize(registry);
    }
}
