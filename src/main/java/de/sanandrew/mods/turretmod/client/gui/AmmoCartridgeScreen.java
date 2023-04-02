/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.gui;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.JsonGuiContainer;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.inventory.container.AmmoCartridgeContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class AmmoCartridgeScreen
        extends JsonGuiContainer<AmmoCartridgeContainer>
{
    public AmmoCartridgeScreen(AmmoCartridgeContainer cartridge, PlayerInventory playerInv, ITextComponent title) {
        super(cartridge, playerInv, title);
    }

    @Override
    protected GuiDefinition buildGuiDefinition() {
        try {
            return GuiDefinition.getNewDefinition(Resources.GUI_CARTRIDGE);
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
            return null;
        }
    }
}
