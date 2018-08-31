/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.info;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGuiHelper;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconPageRender;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class LexiconRenderInfo
        implements ILexiconPageRender
{
    static final String ID = TmrConstants.ID + ":info";

    private static final int ITEM_TXT_COLOR = 0xFF808080;
    private static final int TXT_COLOR = 0xFF000000;

    private int drawHeight;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void initPage(ILexiconEntry entry, ILexiconGuiHelper helper, List<GuiButton> globalButtons, List<GuiButton> entryButtons) {
        entryButtons.add(helper.getNewLinkButton(entryButtons.size(), 6, 42, "SanAndreasP", "https://minecraft.curseforge.com/members/SanAndreasP", helper.getFontRenderer(), true).get());

        entryButtons.add(helper.getNewLinkButton(entryButtons.size(), 6, 63, "Drullkus (ion cannon model)", "https://minecraft.curseforge.com/members/Drullkus", helper.getFontRenderer(), true).get());
        entryButtons.add(helper.getNewLinkButton(entryButtons.size(), 6, 72, "Darkhax (helper code)", "https://minecraft.curseforge.com/members/Darkhax", helper.getFontRenderer(), true).get());
        entryButtons.add(helper.getNewLinkButton(entryButtons.size(), 6, 81, "Vazkii (rendering code)", "https://minecraft.curseforge.com/members/Vazkii", helper.getFontRenderer(), true).get());

        entryButtons.add(helper.getNewLinkButton(entryButtons.size(), 6, 102, LangUtils.translate(Lang.LEXICON_INFO_ITEM.get("github")), "https://github.com/SanAndreasP/TurretModRebirth", helper.getFontRenderer(), true).get());
        entryButtons.add(helper.getNewLinkButton(entryButtons.size(), 6, 111, LangUtils.translate(Lang.LEXICON_INFO_ITEM.get("issues")), "https://github.com/SanAndreasP/TurretModRebirth/issues/", helper.getFontRenderer(), true).get());
        entryButtons.add(helper.getNewLinkButton(entryButtons.size(), 6, 120, LangUtils.translate(Lang.LEXICON_INFO_ITEM.get("curse")), "https://minecraft.curseforge.com/projects/turret-mod-rebirth", helper.getFontRenderer(), true).get());
    }

    @Override
    public void updateScreen(ILexiconGuiHelper helper) {

    }

    @Override
    public void renderPageEntry(ILexiconEntry entry, ILexiconGuiHelper helper, int mouseX, int mouseY, int scrollY, float partTicks) {
        String s = LangUtils.translate(LangUtils.LEXICON_GROUP_NAME.get(TmrConstants.ID, entry.getGroupId()));
        helper.getFontRenderer().drawString(s, (helper.getLexicon().getEntryWidth() - helper.getFontRenderer().getStringWidth(s)) / 2, 2, helper.getLexicon().getTitleColor());

        this.drawHeight = 14;
        helper.getFontRenderer().drawString(LangUtils.translate(Lang.LEXICON_INFO_ITEM.get("version")), 0, this.drawHeight, ITEM_TXT_COLOR);
        this.drawHeight += 9;
        helper.getFontRenderer().drawString(TmrConstants.VERSION, 6, this.drawHeight, TXT_COLOR);

        this.drawHeight += 12;
        helper.getFontRenderer().drawString(LangUtils.translate(Lang.LEXICON_INFO_ITEM.get("author")), 0, this.drawHeight, ITEM_TXT_COLOR);
        this.drawHeight += 9;
        // author links

        this.drawHeight += 12;
        helper.getFontRenderer().drawString(LangUtils.translate(Lang.LEXICON_INFO_ITEM.get("credits")), 0, this.drawHeight, ITEM_TXT_COLOR);
        this.drawHeight += 27;
        // credit links

        this.drawHeight += 12;
        helper.getFontRenderer().drawString(LangUtils.translate(Lang.LEXICON_INFO_ITEM.get("links")), 0, this.drawHeight, ITEM_TXT_COLOR);
        this.drawHeight += 27;
        // misc links
    }

    @Override
    public int getEntryHeight(ILexiconEntry entry, ILexiconGuiHelper helper) {
        return this.drawHeight;
    }
}
