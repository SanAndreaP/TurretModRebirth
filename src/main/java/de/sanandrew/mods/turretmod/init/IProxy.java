package de.sanandrew.mods.turretmod.init;

import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public interface IProxy
{
    void setupClient(FMLClientSetupEvent event);

    void fillPlayerListClient(Map<UUID, ITextComponent> map);

    boolean checkTurretGlowing(ITurretInst turretInst);

    PlayerEntity getNetworkPlayer(Supplier<NetworkEvent.Context> networkContextSupplier);
}
