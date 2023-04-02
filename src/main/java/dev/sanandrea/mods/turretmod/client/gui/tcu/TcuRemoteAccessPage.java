/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ProgressBar;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip;
import dev.sanandrea.mods.turretmod.api.Resources;
import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.tcu.TcuContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.Locale;

public class TcuRemoteAccessPage
        extends JsonTcuPage
{
    public TcuRemoteAccessPage(ContainerScreen<TcuContainer> tcuScreen) {
        super(tcuScreen);
    }

    @Override
    protected GuiDefinition buildGuiDefinition() {
        try {
            return GuiDefinition.getNewDefinition(Resources.GUI_TCU_REMOTE_ACCESS);
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
            return null;
        }
    }

    @Override
    protected void initGd() {
        this.guiDefinition.getElementById("health").get(ProgressBar.class).setPercentFunc(
                g -> this.turret.get().getHealth() / this.turret.get().getMaxHealth()
        );

        IGuiElement e = this.guiDefinition.getElementById("healthTtip").get(Tooltip.class).get(Tooltip.CONTENT).get();
        if( e instanceof Text ) {
            ((Text) e).setTextFunc((g, orig) -> {
                String key;
                if( orig instanceof TranslationTextComponent ) {
                    key = ((TranslationTextComponent) orig).getKey();
                } else {
                    key = orig.getString();
                }

                return new TranslationTextComponent(key, String.format(Locale.getDefault(), "%.1f", this.turret.get().getHealth() / 2.0F),
                                                         String.format(Locale.getDefault(), "%.1f", this.turret.get().getMaxHealth() / 2.0F));
            });
        }
    }
}
