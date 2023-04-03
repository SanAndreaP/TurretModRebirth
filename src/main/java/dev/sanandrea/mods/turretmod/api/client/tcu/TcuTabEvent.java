/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.api.client.tcu;

import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Event.HasResult
@OnlyIn(Dist.CLIENT)
public abstract class TcuTabEvent
        extends Event
{
    public final IGui          screen;
    public final ITurretEntity turret;
    public final boolean isRemote;

    public TcuTabEvent(IGui screen, ITurretEntity turret, boolean isRemote) {
        this.screen = screen;
        this.turret = turret;
        this.isRemote = isRemote;
    }

    @Cancelable
    public static class TabIconShow
            extends TcuTabEvent
    {
        public final ResourceLocation tabId;

        public TabIconShow(IGui screen, ITurretEntity turret, ResourceLocation tabId, boolean isRemote) {
            super(screen, turret, isRemote);
            this.tabId = tabId;
        }
    }
}
