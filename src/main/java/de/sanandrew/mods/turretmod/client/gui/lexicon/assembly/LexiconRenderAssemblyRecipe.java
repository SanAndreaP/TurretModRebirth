/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.assembly;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGuiHelper;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconPageRender;
import de.sanandrew.mods.turretmod.registry.assembly.RecipeEntry;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyInventoryHandler;
import net.minecraft.item.ItemStack;

public abstract class LexiconRenderAssemblyRecipe
        implements ILexiconPageRender
{
    @SuppressWarnings("SameParameterValue")
    protected int renderRecipe(ILexiconGuiHelper helper, RecipeEntry recipe, int posX, int posY, int mouseX, int mouseY, int scrollY) {
        final ItemStack[] EMPTY_IS = new ItemStack[] {ItemStack.EMPTY};
        for( int i = 0; i < AssemblyInventoryHandler.RESOURCE_SLOTS; i++ ) {
            int x = posX + (i % 9) * 9;
            int y = posY + (i / 9) * 9;
            ItemStack[] rendered = i < recipe.resources.length ? recipe.resources[i].getEntryItemStacks() : EMPTY_IS;

            helper.drawItemGrid(x, y, mouseX, mouseY, scrollY, rendered[(int) ((System.currentTimeMillis() / 1000L) % rendered.length)], 0.5F, true);
        }

        return (AssemblyInventoryHandler.RESOURCE_SLOTS / 9) * 9;
    }
}
