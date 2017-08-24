/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo.entry;

import de.sanandrew.mods.turretmod.api.client.turretinfo.IGuiTurretInfo;
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretInfoEntry;
import de.sanandrew.mods.turretmod.api.turret.EntityTurret;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmoRegistry;
import de.sanandrew.mods.turretmod.api.ammo.ITurretAmmo;
import de.sanandrew.mods.turretmod.api.assembly.IRecipeEntry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.api.turret.TurretInfo;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class TurretInfoEntryTurret
        implements ITurretInfoEntry
{
    private int drawHeight;
    private float rotation;
    private long lastTimestamp;
    private Class<? extends EntityTurret> turretClass;
    private WeakReference<EntityTurret> turretRenderCache;
    private TurretInfoValues values;

    private IGuiTurretInfo guiInfo;
    private final ItemStack icon;

    public TurretInfoEntryTurret(Class<? extends EntityTurret> turret) {
        this(TurretRegistry.INSTANCE.getInfo(turret));
        this.turretClass = turret;
    }

    private TurretInfoEntryTurret(TurretInfo info) {
        this.values = new TurretInfoValues(info);
        this.icon = ItemRegistry.turret_placer.getTurretItem(1, info);
    }

    @Override
    public void initEntry(IGuiTurretInfo gui) {
        this.guiInfo = gui;
    }

    @Override
    public String getTitle() {
        return Lang.translateEntityCls(this.turretClass);
    }

    @Nonnull
    @Override
    public ItemStack getIcon() {
        return this.icon;
    }

    @Override
    public void drawPage(int mouseX, int mouseY, int scrollY, float partTicks) {
        int turretHeight = 82;
        int valueHeight = 113;
        int descStart;
        int descHeight;
        Minecraft mc = guiInfo.__getMc();

        mc.fontRenderer.drawString(TextFormatting.ITALIC + this.values.name, 2, 2, 0xFF0080BB);
        Gui.drawRect(2, 12, MAX_ENTRY_WIDTH - 2, 13, 0xFF0080BB);

        this.guiInfo.doEntryScissoring(2, 15, 54, 82);

        Gui.drawRect(0, 0, MAX_ENTRY_WIDTH, MAX_ENTRY_HEIGHT, 0xFF000000);
        this.drawTurret(mc, 28, 35);

        this.guiInfo.doEntryScissoring();

        mc.fontRenderer.drawString(Lang.translate(Lang.TINFO_ENTRY_HEALTH.get()),                         60, 15, 0xFF6A6A6A, false);
        mc.fontRenderer.drawString(Lang.translate(Lang.TINFO_ENTRY_HEALTHVAL.get(), this.values.health),  63, 24, 0xFF000000, false);
        mc.fontRenderer.drawString(Lang.translate(Lang.TINFO_ENTRY_RANGE.get()),                          60, 35, 0xFF6A6A6A, false);
        mc.fontRenderer.drawString(Lang.translate(Lang.TINFO_ENTRY_RANGEVAL.get(), this.values.range),    63, 44, 0xFF000000, false);
        mc.fontRenderer.drawString(Lang.translate(Lang.TINFO_ENTRY_AMMOCAP.get()),                        60, 55, 0xFF6A6A6A, false);
        mc.fontRenderer.drawString(Lang.translate(Lang.TINFO_ENTRY_ROUNDSVAL.get(), this.values.ammoCap), 63, 64, 0xFF000000, false);
        mc.fontRenderer.drawString(Lang.translate(Lang.TINFO_ENTRY_AMMOUSE.get()),                        60, 75, 0xFF6A6A6A, false);
        mc.fontRenderer.drawString(Lang.translate(Lang.TINFO_ENTRY_CRAFTING.get()),                       60, 95, 0xFF6A6A6A, false);

        descStart = Math.max(turretHeight, valueHeight);

        Gui.drawRect(2, 2 + descStart, MAX_ENTRY_WIDTH - 2, 3 + descStart, 0xFF0080BB);
        mc.fontRenderer.drawSplitString(this.values.desc, 2, 5 + descStart, MAX_ENTRY_WIDTH - 2, 0xFF000000);
        descHeight = mc.fontRenderer.getWordWrappedHeight(this.values.desc, MAX_ENTRY_WIDTH - 4) + 7;

        for( int i = 0; i < this.values.ammoStacks.length; i++ ) {
            this.guiInfo.drawMiniItem(63 + 10 * i, 84, mouseX, mouseY, scrollY, this.values.ammoStacks[i], true);
        }
        for( int i = 0; i < this.values.recipeStacks.length; i++ ) {
            this.drawItemRecipe(63 + 10 * i, 104, mouseX, mouseY, scrollY, this.values.recipeStacks[i]);
        }

        this.drawHeight = descStart + descHeight;

        long time = System.currentTimeMillis();
        if( this.lastTimestamp + 1000 < time ) {
            this.lastTimestamp = time;
        }
    }

    @Override
    public int getPageHeight() {
        return this.drawHeight;
    }

    private void drawTurret(Minecraft mc, int x, int y) {
        if( this.turretRenderCache == null || this.turretRenderCache.get() == null || this.turretRenderCache.isEnqueued() ) {
            try {
                this.turretRenderCache = new WeakReference<>(this.turretClass.getConstructor(World.class).newInstance(mc.world));
            } catch( Exception e ) {
                return;
            }
        }

        EntityTurret turret = this.turretRenderCache.get();
        if( turret == null ) {
            return;
        }

        turret.inGui = true;

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 50.0F);
        GlStateManager.scale(30.0F, 30.0F, 30.0F);

        GlStateManager.translate(0.0F, turret.height, 0.0F);

        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(135.0F + this.rotation, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        this.rotation += 0.2F;

        turret.prevRotationPitch = turret.rotationPitch = 0.0F;
        turret.prevRotationYaw = turret.rotationYaw = 0.0F;
        turret.prevRotationYawHead = turret.rotationYawHead = 0.0F;

        Minecraft.getMinecraft().getRenderManager().setRenderShadow(false);
        Minecraft.getMinecraft().getRenderManager().doRenderEntity(turret, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, true);
        Minecraft.getMinecraft().getRenderManager().setRenderShadow(true);
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawItemRecipe(int x, int y, int mouseX, int mouseY, int scrollY, IRecipeEntry entryItem) {
        ItemStack[] entryStacks = entryItem.getEntryItemStacks();
        ItemStack stack = entryStacks[(int)((this.lastTimestamp / 1000L) % entryStacks.length)];
        this.guiInfo.drawMiniItem(x, y, mouseX, mouseY, scrollY, stack, entryItem.shouldDrawTooltip());
    }

    private static final class TurretInfoValues
    {
        public final String name;
        public final String desc;
        public final float health;
        public final int ammoCap;
        public final String range;
        public final IRecipeEntry[] recipeStacks;
        public final ItemStack[] ammoStacks;

        public TurretInfoValues(TurretInfo info) {
            List<ItemStack> ammoItms = new ArrayList<>();

            this.name = Lang.translateEntityCls(info.getTurretClass());
            this.desc = Lang.translateEntityClsDesc(info.getTurretClass()).replace("\\n", "\n");
            this.range = info.getInfoRange();
            this.health = info.getTurretHealth();
            this.ammoCap = info.getBaseAmmoCapacity();

            List<ITurretAmmo> ammos = TurretAmmoRegistry.INSTANCE.getTypesForTurret(info.getTurretClass());
            for( ITurretAmmo ammo : ammos ) {
                ammoItms.add(ItemRegistry.turret_ammo.getAmmoItem(1, ammo));
            }

            TurretAssemblyRegistry.RecipeEntry recipeEntry = TurretAssemblyRegistry.INSTANCE.getRecipeEntry(info.getRecipeId());
            this.recipeStacks = recipeEntry == null ? new IRecipeEntry[0] : recipeEntry.resources;

            this.ammoStacks = ammoItms.toArray(new ItemStack[ammoItms.size()]);
        }
    }
}
