package de.sanandrew.mods.turretmod.event;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.item.upgrades.leveling.LevelStorage;
import de.sanandrew.mods.turretmod.item.upgrades.leveling.StageLoader;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TmrConstants.ID)
public class TurretLevelStageHandler
{
    private TurretLevelStageHandler() { }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener(new StageLoader());
    }

    //sync stages with client
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        DistExecutor.unsafeCallWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            TurretModRebirth.NETWORK.sendToPlayer(LevelStorage.getPacket(), (ServerPlayerEntity) event.getPlayer());
            return null;
        });
    }
}
