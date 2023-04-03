/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.item.upgrades.delegate;

import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.client.tcu.TcuTabEvent;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgrade;
import dev.sanandrea.mods.turretmod.item.TurretControlUnit;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = TmrConstants.ID, value = Dist.CLIENT)
public class RemoteAccess
        implements IUpgrade
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "remote_access_upgrade");

    public RemoteAccess() { /* no-op */ }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onTabIconShow(TcuTabEvent.TabIconShow event) {
        if( event.tabId.equals(TurretControlUnit.REMOTE_ACCESS) && (!event.turret.getUpgradeProcessor().hasUpgrade(ID) || !event.isRemote) ) {
            event.setCanceled(true);
        }
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public boolean isCompatibleWithCreativeUpgrade() {
        return true;
    }
}
