/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.init;

import dev.sanandrea.mods.turretmod.api.turret.IForcefield;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.world.PlayerList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public interface IProxy
{
    void setupClient(FMLClientSetupEvent event);

    void fillPlayerListClient(Map<UUID, PlayerList.PlayerData> map);

    boolean checkTurretGlowing(ITurretEntity turretInst);

    PlayerEntity getNetworkPlayer(Supplier<NetworkEvent.Context> networkContextSupplier);

    IRenderClassProvider getRenderClassProvider();

    void openTcuGuiRemote(ItemStack stack, ITurretEntity turret, ResourceLocation type, boolean initial, boolean isRemote);

    boolean isSneakPressed();

    boolean hasClientForcefield(ITurretEntity turretEntity, Class<? extends IForcefield> forcefieldClass);

    void addClientForcefield(ITurretEntity turretEntity, IForcefield forcefield);

    void removeClientForcefield(ITurretEntity turretEntity, Class<? extends IForcefield> forcefieldClass);

    MinecraftServer getServer(World level);

    <T extends IParticleData> void spawnParticle(World level, T particle, double x, double y, double z, int count, float mX, float mY, float mZ, float mMax);

    default <T extends IParticleData> void spawnParticle(World level, T particle, Vector3d pos, int count, float mX, float mY, float mZ, float mMax) {
        spawnParticle(level, particle, pos.x, pos.y, pos.z, count, mX, mY, mZ, mMax);
    }
}
