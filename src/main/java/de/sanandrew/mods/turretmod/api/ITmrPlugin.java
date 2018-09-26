package de.sanandrew.mods.turretmod.api;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconInst;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionRegistry;
import de.sanandrew.mods.turretmod.api.ammo.IProjectileRegistry;
import de.sanandrew.mods.turretmod.api.assembly.ITurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.api.client.render.IRender;
import de.sanandrew.mods.turretmod.api.client.render.IRenderRegistry;
import de.sanandrew.mods.turretmod.api.client.tcu.ILabelRegistry;
import de.sanandrew.mods.turretmod.api.client.turret.ITurretRenderRegistry;
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretInfoCategoryRegistry;
import de.sanandrew.mods.turretmod.api.repairkit.IRepairKitRegistry;
import de.sanandrew.mods.turretmod.api.turret.IGuiTcuRegistry;
import de.sanandrew.mods.turretmod.api.turret.ITurretRegistry;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeRegistry;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public interface ITmrPlugin
{
    default void preInit(ITmrUtils utils) { }

    default void registerAssemblyRecipes(ITurretAssemblyRegistry registry) { }

    default void registerTurrets(ITurretRegistry registry) { }

    default void registerRepairKits(IRepairKitRegistry registry) { }

    default void registerAmmo(IAmmunitionRegistry registry) { }

    default void registerUpgrades(IUpgradeRegistry registry) { }

    default void postInit() { }

    default void registerTcuEntries(IGuiTcuRegistry registry) { }

    default void registerProjectiles(IProjectileRegistry registry) { }

    @SideOnly(Side.CLIENT)
    default void registerLexicon(ILexiconInst registry) { }

    @SideOnly(Side.CLIENT)
    default void registerTurretRenderer(ITurretRenderRegistry<?> registry) { }

    @SideOnly(Side.CLIENT)
    default <T extends Entity> void registerProjectileRenderer(IRenderRegistry<UUID, T, IRender<T>, Render<T>> registry) { }

    @SideOnly(Side.CLIENT)
    default void registerTcuLabelElements(ILabelRegistry registry) { }

    @SideOnly(Side.CLIENT)
    default void registerTcuGuis(IGuiTcuRegistry registry) { }
}
