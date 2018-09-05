/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.assembly;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGuiHelper;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconPageRender;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.registry.assembly.RecipeEntry;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyInventoryHandler;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

public abstract class LexiconRenderAssemblyRecipe
        implements ILexiconPageRender
{
    protected static void drawStat(String title, String value, ILexiconGuiHelper helper, int x, int y, int iconU, int iconV, int mouseX, int mouseY) {
        FontRenderer fr = helper.getFontRenderer();
        int wVal = fr.getStringWidth(value);

        helper.getGui().mc.renderEngine.bindTexture(Resources.TINFO_ELEMENTS.resource);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        helper.drawTextureRect(x, y, iconU, iconV, 9, 9);
        fr.drawString(value, x + 12, y + 1, 0xFF6A6A6A, false);
        if( mouseX >= x && mouseX < x + 12 + wVal && mouseY >= y && mouseY < y + 10 ) {
            String s = LangUtils.translate(Lang.LEXICON_STAT_ITEM.get(title));
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 50.0F);
            helper.drawRect(x + 5, y, x + 6 + fr.getStringWidth(s), y + 10, 0x80000000);
            fr.drawString(s, x + 6, y + 1, 0xFFFFFFFF, false);
            GlStateManager.popMatrix();
        }
    }

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
