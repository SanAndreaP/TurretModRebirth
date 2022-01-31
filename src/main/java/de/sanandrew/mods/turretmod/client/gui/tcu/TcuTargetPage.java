package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public abstract class TcuTargetPage
        extends JsonTcuPage
{
    TcuTargetPage(ContainerScreen<TcuContainer> tcuScreen) {
        super(tcuScreen);
    }

    public static class Creatures
            extends TcuTargetPage
    {
        public Creatures(ContainerScreen<TcuContainer> tcuScreen) {
            super(tcuScreen);
        }

        @Override
        public GuiDefinition buildGuiDefinition() {
            try {
                return GuiDefinition.getNewDefinition(Resources.GUI_TCU_TARGET_CREATURES);
            } catch( IOException e ) {
                TmrConstants.LOG.log(Level.ERROR, e);
                return null;
            }
        }
    }

    public ITurretEntity getTurret() {
        return this.turret;
    }
}
