/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.upgrade;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGuiHelper;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.client.gui.lexicon.assembly.LexiconRenderAssemblyRecipe;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyManager;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;

import java.util.List;

public class LexiconRenderAssemblyUpgrade
        extends LexiconRenderAssemblyRecipe
{
    static final String ID = TmrConstants.ID + ":abupgrade";

    private static final int H2_COLOR = 0xFF202080;

    private IAssemblyRecipe recipe;
    private ItemStack stack;
    private int drawHeight;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void initPage(ILexiconEntry entry, ILexiconGuiHelper helper, List<GuiButton> deprecated1, List<GuiButton> deprecated2) {
        if( entry instanceof LexiconEntryAssemblyUpgrade ) {
            this.stack = entry.getEntryIcon();
            this.recipe = AssemblyManager.INSTANCE.findRecipe(this.stack);
        }
    }

    @Override
    public void renderPageEntry(ILexiconEntry entry, ILexiconGuiHelper helper, int mouseX, int mouseY, int scrollY, float partTicks) {
        FontRenderer fr = helper.getFontRenderer();

        helper.drawTitleCenter(2, entry);
        this.drawHeight = 14;

        helper.drawItem(this.stack, (helper.getLexicon().getEntryWidth() - 36) / 2, this.drawHeight, 2.0D);
        this.drawHeight += 40;

        if( this.recipe != null ) {
            fr.drawString(LangUtils.translate(Lang.LEXICON_ASSEMBLY_RECIPE), 2, this.drawHeight, H2_COLOR);
            this.drawHeight += 9;
            this.drawHeight += this.renderRecipe(helper, this.recipe, 4, this.drawHeight, mouseX, mouseY, scrollY) + 3;
        }

        fr.drawString(LangUtils.translate(Lang.LEXICON_DESCRIPTION), 2, this.drawHeight, H2_COLOR);
        this.drawHeight += 9;
        this.drawHeight += helper.drawContentString(4, this.drawHeight, entry, helper.getEntryButtonList());
    }

    @Override
    public int getEntryHeight(ILexiconEntry entry, ILexiconGuiHelper helper) {
        return this.drawHeight;
    }
}
