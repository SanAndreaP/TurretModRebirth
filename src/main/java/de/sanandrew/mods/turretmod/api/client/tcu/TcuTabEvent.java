package de.sanandrew.mods.turretmod.api.client.tcu;

import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
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

    public TcuTabEvent(IGui screen, ITurretEntity turret) {
        this.screen = screen;
        this.turret = turret;
    }

    @Cancelable
    public static class TabIconShow
            extends TcuTabEvent
    {
        public final ResourceLocation tabId;

        public TabIconShow(IGui screen, ITurretEntity turret, ResourceLocation tabId) {
            super(screen, turret);
            this.tabId = tabId;
        }
    }
}
