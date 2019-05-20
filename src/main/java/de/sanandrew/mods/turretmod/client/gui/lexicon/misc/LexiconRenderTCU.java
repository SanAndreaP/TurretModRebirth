/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.misc;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGuiHelper;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.client.gui.lexicon.assembly.LexiconRenderAssemblyRecipe;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyManager;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.gui.GuiButton;

import java.util.List;

class LexiconRenderTCU
        extends LexiconRenderAssemblyRecipe
{
    static final String ID = TmrConstants.ID + ":tcu";

    private static final int H2_COLOR = 0xFF202080;

    private int drawHeight;
    private List<GuiButton> entryButtons;
    private IAssemblyRecipe recipe;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void initPage(ILexiconEntry entry, ILexiconGuiHelper helper, List<GuiButton> globalButtons, List<GuiButton> entryButtons) {
        this.entryButtons = entryButtons;

        this.recipe = AssemblyManager.INSTANCE.findRecipe(entry.getEntryIcon());
    }

    @Override
    public void renderPageEntry(ILexiconEntry entry, ILexiconGuiHelper helper, int mouseX, int mouseY, int scrollY, float partTicks) {
        helper.drawTitleCenter(2, entry);
        this.drawHeight = 14;

        int entryWidth = helper.getLexicon().getEntryWidth();

        helper.drawItem(entry.getEntryIcon(), (entryWidth - 36) / 2, this.drawHeight, 2.0F);
        this.drawHeight += 40;

        if( this.recipe != null ) {
            helper.getFontRenderer().drawString(LangUtils.translate(Lang.LEXICON_ASSEMBLY_RECIPE), 2, this.drawHeight, H2_COLOR);
            this.drawHeight += helper.getFontRenderer().FONT_HEIGHT;
            this.drawHeight += this.renderRecipe(helper, this.recipe, 4, this.drawHeight, mouseX, mouseY, scrollY) + 4;
        }

        this.drawHeight += helper.drawContentString(2, this.drawHeight, entry, this.entryButtons);

        int height = entryWidth / 2;
        if( helper.tryDrawPicture(entry.getPicture(), 0, this.drawHeight + 8, entryWidth, height) ) {
            this.drawHeight += height + 8;
        }

        this.drawHeight += 2;
    }

    @Override
    public int getEntryHeight(ILexiconEntry entry, ILexiconGuiHelper helper) {
        return this.drawHeight;
    }
}
