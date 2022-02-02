package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.element.TextField;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.TargetList;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public abstract class TcuTargetPage
        extends JsonTcuPage
{
    TextField  search;

    TcuTargetPage(ContainerScreen<TcuContainer> tcuScreen) {
        super(tcuScreen);
    }

    @Override
    protected void initGd() {
        this.search = this.guiDefinition.getElementById("search").get(TextField.class);

        final TargetList targets = this.guiDefinition.getElementById("targetList").get(TargetList.class);
        this.search.setResponder(s -> targets.filter(this, s));
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        return this.search.mouseClicked(this, mx, my, btn) || super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.search.keyPressed(this, keyCode, scanCode, modifiers) || this.search.canConsumeInput() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        return this.search.charTyped(this, typedChar, keyCode) || this.search.canConsumeInput() || super.charTyped(typedChar, keyCode);
    }

    public ITurretEntity getTurret() {
        return this.turret;
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
}
