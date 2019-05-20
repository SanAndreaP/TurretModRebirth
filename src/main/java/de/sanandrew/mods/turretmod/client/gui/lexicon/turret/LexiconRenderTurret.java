/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.turret;

import de.sanandrew.mods.sanlib.api.client.lexicon.IGuiButtonEntry;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGuiHelper;
import de.sanandrew.mods.sanlib.lib.XorShiftRandom;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.client.gui.lexicon.ammo.LexiconGroupAmmo;
import de.sanandrew.mods.turretmod.client.gui.lexicon.assembly.LexiconRenderAssemblyRecipe;
import de.sanandrew.mods.turretmod.client.util.ClientProxy;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyManager;
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
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
class LexiconRenderTurret
        extends LexiconRenderAssemblyRecipe
{
    static final String ID = TmrConstants.ID + ":turret";

    private static final int H2_COLOR = 0xFF202080;

    private IAssemblyRecipe recipe;
    private ITurret turret;
    private WeakReference<EntityTurret> turretCache;
    private int drawHeight;
    private int tickTime;
    private float rotation;
    private float prevRotation;
    private boolean bouncy;

    private List<IGuiButtonEntry> ammoGroupButtons;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void initPage(ILexiconEntry entry, ILexiconGuiHelper helper, List<GuiButton> deprecated1, List<GuiButton> deprecated2) {
        if( entry instanceof LexiconEntryTurret ) {
            this.bouncy = new XorShiftRandom().randomInt(1000) == 0;
            this.turret = ((LexiconEntryTurret) entry).turret;
            this.recipe = AssemblyManager.INSTANCE.findRecipe(TurretRegistry.INSTANCE.getItem(this.turret.getId()));
            if( this.turretCache != null ) {
                this.turretCache.clear();
            }

            this.ammoGroupButtons = new ArrayList<>();
            List<GuiButton> entryButtons = helper.getEntryButtonList();
            AmmunitionRegistry.INSTANCE.getGroups(this.turret).forEach(g -> {
                IGuiButtonEntry entryBtn = helper.getNewEntryButton(entryButtons.size(), 4, 0, ClientProxy.lexiconInstance.getGroup(LexiconGroupAmmo.NAME).getEntry(g.getId().toString()),
                                                                    helper.getFontRenderer());
                this.ammoGroupButtons.add(entryBtn);
                entryButtons.add(entryBtn.get());
            });
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

        drawStat("health", String.format("%.1f HP", this.turret.getHealth()), helper, 60, this.drawHeight, 52, 0, mouseX, mouseY + scrollY);
        drawStat("ammo", String.format("%d", this.turret.getAmmoCapacity()), helper, 60, this.drawHeight + 12, 61, 0, mouseX, mouseY + scrollY);
        drawStat("range", getFormattedRange(this.turret.getRangeBB(null)), helper, 60, this.drawHeight + 24, 70, 0, mouseX, mouseY + scrollY);
        drawStat("reload", String.format("%s", MiscUtils.getTimeFromTicks(this.turret.getReloadTicks())), helper, 60, this.drawHeight + 36, 79, 0, mouseX, mouseY + scrollY);
        drawStat("tier", String.format("%d", this.turret.getTier()), helper, 60, this.drawHeight + 48, 88, 0, mouseX, mouseY + scrollY);

        this.drawHeight += 70;

        if( this.recipe != null ) {
            fr.drawString(LangUtils.translate(Lang.LEXICON_ASSEMBLY_RECIPE), 2, this.drawHeight, H2_COLOR);
            this.drawHeight += 9;
            this.drawHeight += this.renderRecipe(helper, this.recipe, 4, this.drawHeight, mouseX, mouseY, scrollY) + 3;
        }

        fr.drawString(LangUtils.translate(Lang.LEXICON_TURRET_ITEM.get("ammo")), 2, this.drawHeight, H2_COLOR);
        this.drawHeight += 9;
        for( IGuiButtonEntry ammoGroupButton : this.ammoGroupButtons ) {
            ammoGroupButton.get().y = this.drawHeight;
            this.drawHeight += 16;
        }
        this.drawHeight += 4;

        fr.drawString(LangUtils.translate(Lang.LEXICON_DESCRIPTION), 2, this.drawHeight, H2_COLOR);
        this.drawHeight += 9;
        this.drawHeight += helper.drawContentString(4, this.drawHeight, entry, helper.getEntryButtonList());
    }

    private static String getFormattedRange(AxisAlignedBB aabb) {
        return String.format("h: %.0f, u: %.0f, d: %.0f", (aabb.maxX + aabb.maxZ) / 2.0F, aabb.maxY, -aabb.minY);
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

        GlStateManager.rotate(180.0F + (float) (this.bouncy ? Math.sin(rotation * 0.25F) * 10.0F : 0.0F), 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(22.5F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(135.0F + rotation, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);

        turret.prevRotationPitch = turret.rotationPitch = 0.0F;
        turret.prevRotationYaw = turret.rotationYaw = 0.0F;
        turret.prevRotationYawHead = turret.rotationYawHead = 0.0F;

        if( this.bouncy ) {
            GlStateManager.scale(1.0F, 0.9F + Math.sin(rotation * 0.5F) * 0.1F, 1.0F);
        }
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
