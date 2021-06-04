/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.init;

import de.sanandrew.mods.turretmod.api.ITmrPlugin;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.TmrPlugin;
import de.sanandrew.mods.turretmod.api.turret.ITurretRegistry;
import de.sanandrew.mods.turretmod.entity.turret.delegate.Crossbow;
import de.sanandrew.mods.turretmod.entity.turret.variant.DualItemVariants;
import de.sanandrew.mods.turretmod.entity.turret.variant.VariantContainer;
import net.minecraft.util.ResourceLocation;

//TODO: reimplement
@TmrPlugin
public class TmrInternalPlugin
        implements ITmrPlugin
{
    @Override
    public void registerTurrets(ITurretRegistry registry) {
        registry.register(new Crossbow(new ResourceLocation(TmrConstants.ID, "crossbow_turret")));
    }
//
//    @Override
//    public void registerRepairKits(IRepairKitRegistry registry) {
////        RepairKits.initialize(registry);
//    }
//
//    @Override
//    public void registerAmmo(IAmmunitionRegistry registry) {
////        Ammunitions.initialize(registry);
//    }
//
//    @Override
//    public void registerUpgrades(IUpgradeRegistry registry) {
////        Upgrades.initialize(registry);
//    }

    @Override
    public void setup() {
//        ITargetProcessor.TARGET_BUS.register(new TargetingEventHandler());
    }

//    @Override
//    public void registerTcuEntries(IGuiTcuRegistry registry) {
//        GuiTcuRegistry.initializePages(registry);
//    }

//    @Override
//    public void registerProjectiles(IProjectileRegistry registry) {
////        Projectiles.initialize(registry);
//    }

//    @Override
//    @SideOnly(Side.CLIENT)
//    public void registerTurretRenderer(ITurretRenderRegistry<?> registry) {
//        TurretRenderer.initialize(registry);
//    }
//
//    @Override
//    public void registerTurretRenderLayers(ITurretRenderRegistry<?> registry) {
//        TurretRenderer.initializeLayers(registry);
//    }
//
//    @Override
//    public <T extends Entity> void registerProjectileRenderer(IRenderRegistry<ResourceLocation, T, IRender<T>> registry) {
//        RenderProjectile.initialize(registry);
//    }
//
//    @Override
//    @SideOnly(Side.CLIENT)
//    public void registerTcuLabelElements(ILabelRegistry registry) {
//        Labels.initialize(registry);
//    }
//
//    @Override
//    @SideOnly(Side.CLIENT)
//    public void registerTcuGuis(IGuiTcuRegistry registry) {
//        GuiTcuRegistry.initializePagesClient(registry);
//    }
}
