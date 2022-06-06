package de.sanandrew.mods.turretmod.init;

import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.world.PlayerList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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

    void fillPlayerListClient(Map<UUID, PlayerList.PlayerData> map);

    boolean checkTurretGlowing(ITurretEntity turretInst);

    PlayerEntity getNetworkPlayer(Supplier<NetworkEvent.Context> networkContextSupplier);

    IRenderClassProvider getRenderClassProvider();

    void openTcuGuiRemote(ItemStack stack, ITurretEntity turret, ResourceLocation type, boolean initial);

    boolean isSneakPressed();
}
