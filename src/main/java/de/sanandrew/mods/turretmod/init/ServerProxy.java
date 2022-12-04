package de.sanandrew.mods.turretmod.init;

import de.sanandrew.mods.turretmod.api.turret.IForcefield;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.world.PlayerList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class ServerProxy
        implements IProxy, IRenderClassProvider
{
    @Override
    public void setupClient(FMLClientSetupEvent event) {
        // client-side only
    }

    @Override
    public void fillPlayerListClient(Map<UUID, PlayerList.PlayerData> map) {
        // client-side only
    }

    @Override
    public boolean checkTurretGlowing(ITurretEntity turretInst) {
        return false;
    }

    @Override
    public PlayerEntity getNetworkPlayer(Supplier<NetworkEvent.Context> networkContextSupplier) {
        return networkContextSupplier.get().getSender();
    }

    @Override
    public IRenderClassProvider getRenderClassProvider() {
        return this;
    }

    @Override
    public void openTcuGuiRemote(ItemStack stack, ITurretEntity turret, ResourceLocation type, boolean initial) {
        // client-side only
    }

    @Override
    public boolean isSneakPressed() {
        return false;
    }

    @Override
    public boolean hasClientForcefield(ITurretEntity turretEntity, Class<? extends IForcefield> forcefieldClass) {
        return false;
    }

    @Override
    public void addClientForcefield(ITurretEntity turretEntity, IForcefield forcefield) { }

    @Override
    public void removeClientForcefield(ITurretEntity turretEntity, Class<? extends IForcefield> forcefieldClass) { }

    @Override
    public MinecraftServer getServer(World level) {
        return level.getServer();
    }

    @Override
    public <T extends IParticleData> void spawnParticle(World level, T particle, double x, double y, double z, int count, float mX, float mY, float mZ, float mMax) {
        if( level instanceof ServerWorld ) {
            ((ServerWorld) level).sendParticles(particle, x, y, z, count, mX, mY, mZ, mMax);
        }
    }
}
