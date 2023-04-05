/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.item.upgrades.delegate.leveling;

import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.client.tcu.TcuTabEvent;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgrade;
import dev.sanandrea.mods.turretmod.api.upgrade.IUpgradeData;
import dev.sanandrea.mods.turretmod.item.TurretControlUnit;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = TmrConstants.ID, value = Dist.CLIENT)
public class Leveling
        implements IUpgrade
{
    static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "leveling_upgrade");

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public IUpgradeData<?> getData(ITurretEntity turretInst) {
        return new LevelData();
    }

    @Override
    public void terminate(ITurretEntity turretInst, ItemStack stack) {
        LevelData storage = turretInst.getUpgradeProcessor().getUpgradeData(ID);
        if( storage != null ) {
            storage.clearEffects(turretInst);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onTabIconShow(TcuTabEvent.TabIconShow event) {
        if( event.tabId.equals(TurretControlUnit.LEVELS) && !event.turret.getUpgradeProcessor().hasUpgrade(ID) ) {
            event.setCanceled(true);
        }
    }

    @Override
    public boolean isCompatibleWithCreativeUpgrade() {
        return true;
    }
}