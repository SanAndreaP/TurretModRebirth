/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.ammo;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGuiHelper;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconPageRender;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class LexiconRenderAmmo
        implements ILexiconPageRender
{
    static final String ID = TmrConstants.ID + ":ammo";

    private static final int ITEM_TXT_COLOR = 0xFF808080;
    private static final int TXT_COLOR = 0xFF000000;

    private int drawHeight;
    private List<GuiButtonAmmoItem> ammoButtons;
    private IAmmunition<?> currAmmo;
    private boolean ammoChanged = false;

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

            int rowElemCnt = helper.getLexicon().getEntryWidth() / 18;
            double rowCnt = types.length / (float) rowElemCnt;
            int rowCntTotal = MathHelper.ceil(rowCnt);
            int lastRowCnt = MathHelper.ceil((rowCnt - (rowCntTotal - 1)) * 18.0F);

            for( int i = 0, row = 1; i < types.length; i++ ) {
                GuiButtonAmmoItem btn = new GuiButtonAmmoItem(types[i], entryButtons.size(), i * 16, 14);
                if( i == 0 ) {
                    this.currAmmo = types[i];
                }
                this.ammoButtons.add(btn);
                helper.getEntryButtonList().add(btn);
            }
        }
    }

    @Override
    public void updateScreen(ILexiconGuiHelper helper) {
        if( this.ammoChanged ) {
            this.ammoChanged = false;

            this.ammoButtons.forEach(btn -> btn.inactive = (btn.ammo != this.currAmmo));
        }
    }

    @Override
    public void renderPageEntry(ILexiconEntry entry, ILexiconGuiHelper helper, int mouseX, int mouseY, int scrollY, float partTicks) {
        String s = LangUtils.translate(LangUtils.LEXICON_GROUP_NAME.get(TmrConstants.ID, entry.getGroupId()));
        helper.getFontRenderer().drawString(s, (helper.getLexicon().getEntryWidth() - helper.getFontRenderer().getStringWidth(s)) / 2, 2, helper.getLexicon().getTitleColor());

        this.drawHeight = 14;
//        helper.getFontRenderer().drawString(LangUtils.translate(Lang.LEXICON_INFO_ITEM.get("version")), 0, this.drawHeight, ITEM_TXT_COLOR);
//        this.drawHeight += 9;
//        helper.getFontRenderer().drawString(TmrConstants.VERSION, 6, this.drawHeight, TXT_COLOR);
//
//        this.drawHeight += 12;
//        helper.getFontRenderer().drawString(LangUtils.translate(Lang.LEXICON_INFO_ITEM.get("author")), 0, this.drawHeight, ITEM_TXT_COLOR);
//        this.drawHeight += 9;
//        // author links
//
//        this.drawHeight += 12;
//        helper.getFontRenderer().drawString(LangUtils.translate(Lang.LEXICON_INFO_ITEM.get("credits")), 0, this.drawHeight, ITEM_TXT_COLOR);
//        this.drawHeight += 27;
//        // credit links
//
//        this.drawHeight += 12;
//        helper.getFontRenderer().drawString(LangUtils.translate(Lang.LEXICON_INFO_ITEM.get("links")), 0, this.drawHeight, ITEM_TXT_COLOR);
//        this.drawHeight += 27;
        // misc links
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
