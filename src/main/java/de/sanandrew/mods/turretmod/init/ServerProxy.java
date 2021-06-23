package de.sanandrew.mods.turretmod.init;

import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class ServerProxy
        implements IProxy, IRenderClassProvider
{
    @Override
    public void setupClient(FMLClientSetupEvent event) { }

    @Override
    public void fillPlayerListClient(Map<UUID, ITextComponent> map) { }

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
    public void openTcuGuiRemote(ItemStack stack, ITurretEntity turret, ResourceLocation type) { }
}
