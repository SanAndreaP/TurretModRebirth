/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.gui;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.JsonGuiContainer;
import dev.sanandrea.mods.turretmod.api.Resources;
import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.inventory.container.AssemblyFilterContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class AssemblyFilterScreen
        extends JsonGuiContainer<AssemblyFilterContainer>
{
    public AssemblyFilterScreen(AssemblyFilterContainer filter, PlayerInventory playerInv, ITextComponent title) {
        super(filter, playerInv, title);
    }

    @Override
    protected GuiDefinition buildGuiDefinition() {
        try {
            return GuiDefinition.getNewDefinition(Resources.GUI_ASSEMBLY_FILTER);
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
            return null;
        }
    }
}
