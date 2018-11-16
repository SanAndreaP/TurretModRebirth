/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.ammo;

import de.sanandrew.mods.sanlib.api.client.lexicon.IGuiButtonEntry;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGuiHelper;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.client.gui.lexicon.assembly.LexiconRenderAssemblyRecipe;
import de.sanandrew.mods.turretmod.client.gui.lexicon.turret.LexiconGroupTurret;
import de.sanandrew.mods.turretmod.client.util.ClientProxy;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.RecipeEntry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class LexiconRenderAmmo
        extends LexiconRenderAssemblyRecipe
{
    static final String ID = TmrConstants.ID + ":ammo";

    private static final int H2_COLOR = 0xFF202080;

    private RecipeEntry recipe;
    private int drawHeight;
    private IAmmunition currAmmo;
    private ItemStack currAmmoItem;
    private boolean ammoChanged = false;
    private int subtypeRowCount;

    private List<GuiButtonAmmoItem> ammoButtons;
    private IGuiButtonEntry turretLink;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void initPage(ILexiconEntry entry, ILexiconGuiHelper helper, List<GuiButton> deprecated1, List<GuiButton> deprecated2) {
        this.ammoButtons = new ArrayList<>();
        this.ammoChanged = true;

        List<GuiButton> entryButtons = helper.getEntryButtonList();

        if( entry instanceof LexiconEntryAmmo ) {
            IAmmunition[] types = ((LexiconEntryAmmo) entry).ammoTypes;
            if( types.length > 0 ) {
                this.currAmmo = types[0];

                int width = helper.getLexicon().getEntryWidth();
                int rowElemCnt = width / 18;
                double rowCnt = types.length / (double) rowElemCnt;
                this.subtypeRowCount = MathHelper.ceil(rowCnt);
                int lastRowElemCnt = MathHelper.ceil((rowCnt - (this.subtypeRowCount - 1)) * rowElemCnt);

                for( int row = 0; row < this.subtypeRowCount; row++ ) {
                    int x, max;
                    int min = row * rowElemCnt;
                    if( row == this.subtypeRowCount - 1 ) {
                        x = (width - lastRowElemCnt * 18) / 2;
                        max = min + lastRowElemCnt;
                    } else {
                        x = (width - rowElemCnt * 18) / 2;
                        max = min + rowElemCnt;
                    }

                    for( int i = min; i < max; i++ ) {
                        GuiButtonAmmoItem btn = new GuiButtonAmmoItem(types[i], entryButtons.size(), x + (i - min) * 18, 14 + row * 18);
                        this.ammoButtons.add(btn);
                        entryButtons.add(btn);
                    }
                }

                String turretName = this.currAmmo.getGroup().getTurret().getId().toString();
                this.turretLink = helper.getNewEntryButton(entryButtons.size(), 4, 0, ClientProxy.lexiconInstance.getGroup(LexiconGroupTurret.NAME).getEntry(turretName),
                                                           helper.getFontRenderer());
                entryButtons.add(this.turretLink.get());
            }
        }
    }

    @Override
    public void updateScreen(ILexiconGuiHelper helper) {
        if( this.ammoChanged ) {
            this.ammoChanged = false;

            this.ammoButtons.forEach(btn -> btn.inactive = (btn.ammo != this.currAmmo));
            this.currAmmoItem = AmmunitionRegistry.INSTANCE.getAmmoItem(this.currAmmo);
            this.recipe = TurretAssemblyRegistry.INSTANCE.getRecipeEntry(this.currAmmoItem);
        }
    }

    @Override
    public void renderPageEntry(ILexiconEntry entry, ILexiconGuiHelper helper, int mouseX, int mouseY, int scrollY, float partTicks) {
        FontRenderer fr = helper.getFontRenderer();

        helper.drawTitleCenter(2, entry);
        this.drawHeight = 14;

        // subtype buttons
        this.drawHeight += 3 + this.subtypeRowCount * 18;

        String s = TextFormatting.ITALIC + this.currAmmoItem.getDisplayName();
        fr.drawString(s, 2, this.drawHeight, helper.getLexicon().getTitleColor());
        this.drawHeight += 14;

        if( this.recipe != null ) {
            fr.drawString(LangUtils.translate(Lang.LEXICON_ASSEMBLY_RECIPE), 2, this.drawHeight, H2_COLOR);
            this.drawHeight += 9;
            this.drawHeight += this.renderRecipe(helper, this.recipe, 4, this.drawHeight, mouseX, mouseY, scrollY) + 6;
        }

        fr.drawString(LangUtils.translate(Lang.LEXICON_DETAILS), 2, this.drawHeight, H2_COLOR);
        this.drawHeight += 9;

        drawStat("rounds", String.format("%d", currAmmo.getAmmoCapacity()), helper, 4, this.drawHeight, 61, 0, mouseX, mouseY + scrollY);
        drawStat("damage", String.format("%.1f DP", currAmmo.getDamageInfo()), helper, 40, this.drawHeight, 52, 9, mouseX, mouseY + scrollY);
        this.drawHeight += 15;

        fr.drawString(LangUtils.translate(Lang.LEXICON_AMMO_ITEM.get("turret")), 2, this.drawHeight, H2_COLOR);
        this.turretLink.get().y = this.drawHeight + 9;
        this.drawHeight += 26;

        fr.drawString(LangUtils.translate(Lang.LEXICON_DESCRIPTION), 2, this.drawHeight, H2_COLOR);
        this.drawHeight += 11;

        this.drawHeight += helper.drawContentString(4, this.drawHeight, entry, helper.getEntryButtonList());
    }

    @Override
    public boolean actionPerformed(GuiButton button, ILexiconGuiHelper helper) {
        if( button instanceof GuiButtonAmmoItem ) {
            this.currAmmo = ((GuiButtonAmmoItem) button).ammo;
            this.ammoChanged = true;
            this.updateScreen(helper);
            return true;
        }

        return helper.linkActionPerformed(button);
    }

    @Override
    public int getEntryHeight(ILexiconEntry entry, ILexiconGuiHelper helper) {
        return this.drawHeight;
    }
}
