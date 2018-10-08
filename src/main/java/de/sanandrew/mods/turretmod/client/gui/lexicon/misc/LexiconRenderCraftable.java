/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.misc;

import de.sanandrew.mods.sanlib.api.client.lexicon.CraftingGrid;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGuiHelper;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconPageRender;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class LexiconRenderCraftable
        implements ILexiconPageRender
{
    static final String ID = TmrConstants.ID + ":craftable";

    private int drawHeight;
    private List<GuiButton> entryButtons;
    private List<CraftingGrid> crfGrids;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void initPage(ILexiconEntry entry, ILexiconGuiHelper helper, List<GuiButton> globalButtons, List<GuiButton> entryButtons) {
        this.entryButtons = entryButtons;

        NonNullList<IRecipe> recipes = helper.getMatchingRecipes(entry.getEntryIcon());

        if( !recipes.isEmpty() ) {
            this.crfGrids = new ArrayList<>();
            helper.initCraftings(recipes, this.crfGrids);
        } else {
            this.crfGrids = null;
        }
    }

    @Override
    public void renderPageEntry(ILexiconEntry entry, ILexiconGuiHelper helper, int mouseX, int mouseY, int scrollY, float partTicks) {
        helper.drawTitleCenter(2, entry);
        this.drawHeight = 14;

        int entryWidth = helper.getLexicon().getEntryWidth();

        if( this.crfGrids != null && this.crfGrids.size() > 0 ) {
            CraftingGrid grid = this.crfGrids.get((int) ((System.nanoTime() / 1_000_000_000L) % this.crfGrids.size()));
            Vec3i gridSize = helper.getCraftingGridSize(grid);

            helper.drawCraftingGrid(grid, grid.isShapeless(), (entryWidth - gridSize.getX()) / 2, this.drawHeight, mouseX, mouseY, scrollY);
            this.drawHeight += gridSize.getY() + 4;
        } else {
            helper.drawItemGrid((entryWidth - 36) / 2, this.drawHeight, mouseX, mouseY, scrollY, entry.getEntryIcon(), 2.0F, false);
            this.drawHeight += 40;
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
