/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo.entry;

import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.client.turretinfo.IGuiTurretInfo;
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretInfoEntry;
import de.sanandrew.mods.turretmod.client.shader.ShaderGrayscale;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.RecipeEntry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SideOnly(Side.CLIENT)
//TODO: optimize rendering
public class TurretInfoEntryAmmo
        implements ITurretInfoEntry
{
    private static final ShaderGrayscale SHADER_GRAYSCALE = new ShaderGrayscale(TextureMap.LOCATION_BLOCKS_TEXTURE);

    private int drawHeight;
    private int shownAmmo;
    private List<GuiButtonAmmoItem> ammoBtn;
    private long lastTimestamp;

    private IGuiTurretInfo guiInfo;
    private final ItemStack icon;
    private final String title;
    private final IAmmunition[] ammos;

    public TurretInfoEntryAmmo(UUID groupId) {
        IAmmunition[] ammos = AmmunitionRegistry.INSTANCE.getTypes(groupId);

        this.ammos = ammos;
        this.title = ammos[0].getInfoName();
        this.icon = this.ammos[0].getStoringAmmoItem();
    }

    @Nonnull
    @Override
    public ItemStack getIcon() {
        return this.icon;
    }

    @Override
    public String getTitle() {
        return Lang.TINFO_ENTRY_AMMO_NAME.get(this.title);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initEntry(IGuiTurretInfo gui) {
        this.guiInfo = gui;
        this.ammoBtn = new ArrayList<>(MathHelper.ceil(this.ammos.length * (1/0.75D)));
        this.shownAmmo = 0;
        for( int i = 0; i < this.ammos.length; i++ ) {
            GuiButtonAmmoItem btn = new GuiButtonAmmoItem(gui.__getButtons().size(), gui.getEntryX() + i*16, gui.getEntryY(), i);
            this.ammoBtn.add(btn);
            btn.enabled = i != this.shownAmmo;
            gui.__getButtons().add(btn);
        }
    }

    @Override
    public void drawPage(int mouseX, int mouseY, int scrollY, float partTicks) {
        IAmmunition<?> ammo = this.ammos[this.shownAmmo];
        Minecraft mc = this.guiInfo.__getMc();
        ItemStack ammoItem = AmmunitionRegistry.INSTANCE.getAmmoItem(ammo);

        mc.fontRenderer.drawString(TextFormatting.ITALIC + LangUtils.translate(this.getTitle()), 2, 20, 0xFF0080BB);
        Gui.drawRect(2, 30, MAX_ENTRY_WIDTH - 2, 31, 0xFF0080BB);

        mc.getTextureManager().bindTexture(Resources.GUI_TURRETINFO.resource);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.guiInfo.__drawTexturedRect(2, 34, 192, 18, 34, 34);

        this.guiInfo.renderStack(ammoItem, 3, 35, 2.0F);

        mc.fontRenderer.drawString(LangUtils.translate(Lang.TINFO_ENTRY_ROUNDS), 42, 34, 0xFF6A6A6A, false);
        mc.fontRenderer.drawString(String.format("%d", ammo.getAmmoCapacity()),                      45, 43, 0xFF000000, false);
        mc.fontRenderer.drawString(LangUtils.translate(Lang.TINFO_ENTRY_DPS),                             42, 54, 0xFF6A6A6A, false);
        mc.fontRenderer.drawString(LangUtils.translate(Lang.TINFO_ENTRY_HEALTHVAL, ammo.getInfoDamage()), 45, 63, 0xFF000000, false);
        mc.fontRenderer.drawString(LangUtils.translate(Lang.TINFO_ENTRY_TURRET),                          42, 74, 0xFF6A6A6A, false);
        mc.fontRenderer.drawString(LangUtils.translate(Lang.TURRET_NAME.get(ammo.getTurret().getName())), 45, 83, 0xFF000000, false);
        mc.fontRenderer.drawString(LangUtils.translate(Lang.TINFO_ENTRY_CRAFTING),                        42, 94, 0xFF6A6A6A, false);

        String text = LangUtils.translate(Lang.TINFO_ENTRY_AMMO_DESC.get(ammo.getInfoName())).replace("\\n", "\n");
        mc.fontRenderer.drawSplitString(text, 2, 117, MAX_ENTRY_WIDTH - 2, 0xFF000000);
        this.drawHeight = mc.fontRenderer.getWordWrappedHeight(text, MAX_ENTRY_WIDTH - 2) + 2;

        Gui.drawRect(2, 114, MAX_ENTRY_WIDTH - 2, 115, 0xFF0080BB);

        RecipeEntry recipeEntry = TurretAssemblyRegistry.INSTANCE.getRecipeEntry(ammoItem);
        if( recipeEntry != null ) {
            for( int i = 0; i < recipeEntry.resources.length; i++ ) {
                ItemStack[] stacks = recipeEntry.resources[i].getEntryItemStacks();
                this.guiInfo.drawMiniItem(45 + 10 * i, 103, mouseX, mouseY, scrollY, stacks[(int) (this.lastTimestamp / 1000L % stacks.length)], recipeEntry.resources[i].shouldDrawTooltip());
            }
        }

        this.drawHeight += 116;

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 100.0F);
        Gui.drawRect(0, scrollY, MAX_ENTRY_WIDTH, 16 + scrollY, 0x80000000);
        GlStateManager.popMatrix();

        long time = System.currentTimeMillis();
        if( this.lastTimestamp + 1000 < time ) {
            this.lastTimestamp = time;
        }
    }

    @Override
    public int getPageHeight() {
        return this.drawHeight;
    }

    @Override
    public boolean actionPerformed(GuiButton btn) {
        if( btn instanceof GuiButtonAmmoItem ) {
            this.shownAmmo = ((GuiButtonAmmoItem) btn).ammoIndex;
            this.ammoBtn.forEach(ammoBtnInst -> ammoBtnInst.enabled = ammoBtnInst.ammoIndex != this.shownAmmo);

            return true;
        }

        return false;
    }

    private class GuiButtonAmmoItem
            extends GuiButton
    {
        public final int ammoIndex;
        @Nonnull
        public final ItemStack stack;

        public GuiButtonAmmoItem(int id, int x, int y, int index) {
            super(id, x, y, 16, 16, "");
            this.ammoIndex = index;
            this.stack = AmmunitionRegistry.INSTANCE.getAmmoItem(TurretInfoEntryAmmo.this.ammos[this.ammoIndex]);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partTicks) {
            if( this.visible ) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, 0.0F, 100.0F);
                Gui.drawRect(this.x, this.y, this.x + this.width, this.y + 1, 0xA0000000);
                Gui.drawRect(this.x, this.y + this.height - 1, this.x + this.width, this.y + this.height, 0xA0000000);
                Gui.drawRect(this.x, this.y + 1, this.x + 1, this.y + this.height - 1, 0x40000000);
                Gui.drawRect(this.x + this.width - 1, this.y + 1, this.x + this.width, this.y + this.height - 1, 0x40000000);
                if( this.enabled ) {
                    Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x80000000);

                    SHADER_GRAYSCALE.render(() -> TurretInfoEntryAmmo.this.guiInfo.renderStack(this.stack, this.x, this.y, 1.0F), 0.75F);
                } else {
                    Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x80FFFFFF);
                    TurretInfoEntryAmmo.this.guiInfo.renderStack(this.stack, this.x, this.y, 1.0F);
                }
                GlStateManager.popMatrix();
            }
        }
    }
}
