/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.turret;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGuiHelper;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.client.gui.lexicon.assembly.LexiconRenderAssemblyRecipe;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.registry.assembly.RecipeEntry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
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
import java.util.List;

@SideOnly(Side.CLIENT)
public class LexiconRenderTurret
        extends LexiconRenderAssemblyRecipe
{
    static final String ID = TmrConstants.ID + ":turret";

    private static final int HDR_COLOR = 0xFF202080;
    private static final int TXT_COLOR = 0xFF000000;

    private RecipeEntry recipe;
    private ITurret turret;
    private WeakReference<EntityTurret> turretCache;
    private int drawHeight;
    private int tickTime;
    private float rotation;
    private float prevRotation;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void initPage(ILexiconEntry entry, ILexiconGuiHelper helper, List<GuiButton> globalButtons, List<GuiButton> entryButtons) {
        if( entry instanceof LexiconEntryTurret ) {
            this.turret = ((LexiconEntryTurret) entry).turret;
            this.recipe = TurretAssemblyRegistry.INSTANCE.getRecipeEntry(TurretRegistry.INSTANCE.getTurretItem(this.turret));
            if( this.turretCache != null ) {
                this.turretCache.clear();
            }
        }
    }

    @Override
    public void updateScreen(ILexiconGuiHelper helper) {
        this.tickTime++;
        this.prevRotation = this.rotation;
        this.rotation = this.tickTime * 1.0F;
    }

    @Override
    public void renderPageEntry(ILexiconEntry entry, ILexiconGuiHelper helper, int mouseX, int mouseY, int scrollY, float partTicks) {
        FontRenderer fr = helper.getFontRenderer();

        helper.drawTitleCenter(2, entry);
        this.drawHeight = 14;

        GlStateManager.enableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        helper.getGui().mc.renderEngine.bindTexture(Resources.TINFO_ELEMENTS.resource);
        helper.drawTextureRect(3, this.drawHeight, 0, 0, 52, 67);
        helper.doEntryScissoring(4, this.drawHeight + 1, 50, 65);
        this.drawTurret(helper.getGui().mc, 27, this.drawHeight + 65, partTicks);
        helper.doEntryScissoring();

        drawTurretStat("values.health", String.format("%.1f HP", this.turret.getHealth()), helper, this.drawHeight, 52, mouseX, mouseY);
        drawTurretStat("values.ammo", String.format("%d", this.turret.getAmmoCapacity()), helper, this.drawHeight + 12, 61, mouseX, mouseY);
        drawTurretStat("values.range", getFormattedRange(this.turret.getRangeBB(null)), helper, this.drawHeight + 24, 70, mouseX, mouseY);
        drawTurretStat("values.reload", String.format("%s", MiscUtils.getTimeFromTicks(this.turret.getReloadTicks())), helper, this.drawHeight + 36, 79, mouseX, mouseY);
        drawTurretStat("values.tier", String.format("%d", this.turret.getTier()), helper, this.drawHeight + 48, 88, mouseX, mouseY);

        this.drawHeight += 70;

        fr.drawString(LangUtils.translate(Lang.LEXICON_ASSEMBLY_RECIPE), 2, this.drawHeight, HDR_COLOR);
        this.drawHeight += 9;
        this.drawHeight += this.renderRecipe(helper, this.recipe, 4, this.drawHeight, mouseX, mouseY, scrollY) + 3;

        fr.drawString(LangUtils.translate(Lang.LEXICON_TURRET_ITEM.get("desc")), 2, this.drawHeight, HDR_COLOR);
        this.drawHeight += 9;
        this.drawHeight += helper.drawContentString(4, this.drawHeight, entry, helper.getEntryButtonList());
    }

    private static String getFormattedRange(AxisAlignedBB aabb) {
        return String.format("h: %.0f, u: %.0f, d: %.0f", (aabb.maxX + aabb.maxZ) / 2.0F, aabb.maxY, -aabb.minY);
    }

    private static void drawTurretStat(String title, String value, ILexiconGuiHelper helper, int y, int iconU, int mouseX, int mouseY) {
        FontRenderer fr = helper.getFontRenderer();
        int wVal = fr.getStringWidth(value);

        helper.getGui().mc.renderEngine.bindTexture(Resources.TINFO_ELEMENTS.resource);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        helper.drawTextureRect(60, y, iconU, 0, 9, 9);
        fr.drawString(value, 60 + 12, y + 1, 0xFF6A6A6A, false);
        if( mouseX >= 60 && mouseX < 60 + 12 + wVal && mouseY >= y && mouseY < y + 10 ) {
            String s = LangUtils.translate(Lang.LEXICON_TURRET_ITEM.get(title));
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 50.0F);
            helper.drawRect(60 + 5, y, 60 + 6 + fr.getStringWidth(s), y + 10, 0x80000000);
            fr.drawString(s, 60 + 6, y + 1, 0xFFFFFFFF, false);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public int getEntryHeight(ILexiconEntry entry, ILexiconGuiHelper helper) {
        return this.drawHeight;
    }

    @SuppressWarnings("SameParameterValue")
    private void drawTurret(Minecraft mc, int x, int y, float partTicks) {
        if( this.turretCache == null || this.turretCache.get() == null || this.turretCache.isEnqueued() ) {
            try {
                this.turretCache = new WeakReference<>(new EntityTurret(mc.world, this.turret));
            } catch( Exception e ) {
                return;
            }
        }

        EntityTurret turret = this.turretCache.get();
        if( turret == null ) {
            return;
        }

        turret.inGui = true;

        float rotation = this.prevRotation + (this.rotation - this.prevRotation) * partTicks;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 1.0F, y - 6.0F, 50.0F);
        GlStateManager.scale(25.0F, 25.0F, 25.0F);

        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(22.5F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(135.0F + rotation, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);

        turret.prevRotationPitch = turret.rotationPitch = 0.0F;
        turret.prevRotationYaw = turret.rotationYaw = 0.0F;
        turret.prevRotationYawHead = turret.rotationYawHead = 0.0F;

        Minecraft.getMinecraft().getRenderManager().setRenderShadow(false);
        Minecraft.getMinecraft().getRenderManager().renderEntity(turret, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, true);
        Minecraft.getMinecraft().getRenderManager().setRenderShadow(true);

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }
}
