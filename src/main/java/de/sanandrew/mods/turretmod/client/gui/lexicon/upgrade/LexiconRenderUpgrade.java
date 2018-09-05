/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.upgrade;

import de.sanandrew.mods.sanlib.api.client.lexicon.IGuiButtonEntry;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGuiHelper;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.upgrade.ITurretUpgrade;
import de.sanandrew.mods.turretmod.client.gui.lexicon.ammo.LexiconGroupAmmo;
import de.sanandrew.mods.turretmod.client.gui.lexicon.assembly.LexiconRenderAssemblyRecipe;
import de.sanandrew.mods.turretmod.client.util.ClientProxy;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.RecipeEntry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class LexiconRenderUpgrade
        extends LexiconRenderAssemblyRecipe
{
    static final String ID = TmrConstants.ID + ":upgrade";

    private static final int H2_COLOR = 0xFF202080;

    private RecipeEntry recipe;
    private ITurretUpgrade upgrade;
    private int drawHeight;

    private List<IGuiButtonEntry> ammoGroupButtons;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void initPage(ILexiconEntry entry, ILexiconGuiHelper helper, List<GuiButton> deprecated1, List<GuiButton> deprecated2) {
        if( entry instanceof LexiconEntryUpgrade ) {
            this.upgrade = ((LexiconEntryUpgrade) entry).upgrade;
            this.recipe = TurretAssemblyRegistry.INSTANCE.getRecipeEntry(UpgradeRegistry.INSTANCE.getUpgradeItem(this.upgrade));

//            this.ammoGroupButtons = new ArrayList<>();
//            List<GuiButton> entryButtons = helper.getEntryButtonList();
//            AmmunitionRegistry.INSTANCE.getGroupsForTurret(this.turret).forEach(g -> {
//                IGuiButtonEntry entryBtn = helper.getNewEntryButton(entryButtons.size(), 4, 0, ClientProxy.lexiconInstance.getGroup(LexiconGroupAmmo.NAME).getEntry(g.getName()),
//                                                                    helper.getFontRenderer());
//                this.ammoGroupButtons.add(entryBtn);
//                entryButtons.add(entryBtn.get());
//            });
        }
    }

    @Override
    public void updateScreen(ILexiconGuiHelper helper) {
    }

    @Override
    public void renderPageEntry(ILexiconEntry entry, ILexiconGuiHelper helper, int mouseX, int mouseY, int scrollY, float partTicks) {
        FontRenderer fr = helper.getFontRenderer();

        helper.drawTitleCenter(2, entry);
        this.drawHeight = 14;

//        GlStateManager.enableBlend();
//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        helper.getGui().mc.renderEngine.bindTexture(Resources.TINFO_ELEMENTS.resource);
//        helper.drawTextureRect(3, this.drawHeight, 0, 0, 52, 67);
//        helper.doEntryScissoring(4, this.drawHeight + 1, 50, 65);
//        this.drawTurret(helper.getGui().mc, 27, this.drawHeight + 65, partTicks);
//        helper.doEntryScissoring();
//
//        drawStat("health", String.format("%.1f HP", this.turret.getHealth()), helper, 60, this.drawHeight, 52, 0, mouseX, mouseY);
//        drawStat("ammo", String.format("%d", this.turret.getAmmoCapacity()), helper, 60, this.drawHeight + 12, 61, 0, mouseX, mouseY);
//        drawStat("range", getFormattedRange(this.turret.getRangeBB(null)), helper, 60, this.drawHeight + 24, 70, 0, mouseX, mouseY);
//        drawStat("reload", String.format("%s", MiscUtils.getTimeFromTicks(this.turret.getReloadTicks())), helper, 60, this.drawHeight + 36, 79, 0, mouseX, mouseY);
//        drawStat("tier", String.format("%d", this.turret.getTier()), helper, 60, this.drawHeight + 48, 88, 0, mouseX, mouseY);
//
//        this.drawHeight += 70;
//
//        if( this.recipe != null ) {
//            fr.drawString(LangUtils.translate(Lang.LEXICON_ASSEMBLY_RECIPE), 2, this.drawHeight, H2_COLOR);
//            this.drawHeight += 9;
//            this.drawHeight += this.renderRecipe(helper, this.recipe, 4, this.drawHeight, mouseX, mouseY, scrollY) + 3;
//        }
//
//        fr.drawString(LangUtils.translate(Lang.LEXICON_TURRET_ITEM.get("ammo")), 2, this.drawHeight, H2_COLOR);
//        this.drawHeight += 9;
//        for( int i = 0, max = this.ammoGroupButtons.size(); i < max; i++ ) {
//            this.ammoGroupButtons.get(i).get().y = i * 16;
//            this.drawHeight += 16;
//        }
//        this.drawHeight += 4;
//
//        fr.drawString(LangUtils.translate(Lang.LEXICON_DESCRIPTION), 2, this.drawHeight, H2_COLOR);
//        this.drawHeight += 9;
//        this.drawHeight += helper.drawContentString(4, this.drawHeight, entry, helper.getEntryButtonList());
    }

    private static String getFormattedRange(AxisAlignedBB aabb) {
        return String.format("h: %.0f, u: %.0f, d: %.0f", (aabb.maxX + aabb.maxZ) / 2.0F, aabb.maxY, -aabb.minY);
    }

    @Override
    public int getEntryHeight(ILexiconEntry entry, ILexiconGuiHelper helper) {
        return this.drawHeight;
    }
}
