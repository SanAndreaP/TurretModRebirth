/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.event;

import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.init.TurretModRebirth;
import dev.sanandrea.mods.turretmod.item.upgrades.delegate.leveling.LevelData;
import dev.sanandrea.mods.turretmod.item.upgrades.delegate.leveling.StageLoader;
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
        event.addListener(StageLoader.INSTANCE);
    }

    //sync stages with client
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        DistExecutor.unsafeCallWhenOn(Dist.DEDICATED_SERVER, () -> () -> {
            TurretModRebirth.NETWORK.sendToPlayer(LevelData.getPacket(), (ServerPlayerEntity) event.getPlayer());
            return null;
        });
    }
}
