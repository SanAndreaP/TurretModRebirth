package de.sanandrew.mods.turretmod.api;

import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionRegistry;
import de.sanandrew.mods.turretmod.api.ammo.IProjectileRegistry;
import de.sanandrew.mods.turretmod.api.tcu.ITcuRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurretRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ITmrPlugin
{
    default void preSetup(ITmrUtils utils) { }

    default void registerTurrets(ITurretRegistry registry) { }

//    default void registerRepairKits(IRepairKitRegistry registry) { }
//
    default void registerAmmo(IAmmunitionRegistry registry) { }
//
//    default void registerUpgrades(IUpgradeRegistry registry) { }

    default void setup() { }

    default void registerTcuPages(ITcuRegistry registry) { }

    default void registerProjectiles(IProjectileRegistry registry) { }

    @OnlyIn(Dist.CLIENT)
    default void registerTcuLabelElements(de.sanandrew.mods.turretmod.api.client.tcu.ILabelRegistry registry) { }

//    @Override
//    public void registerTurretRenderLayers(ITurretRenderRegistry<?> registry) {
//        TurretRenderer.initializeLayers(registry);
//    }

    @OnlyIn(Dist.CLIENT)
    void registerTcuScreens(ITcuRegistry registry);
}
