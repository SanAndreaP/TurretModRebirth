/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo.entry;

import de.sanandrew.mods.turretmod.client.gui.tinfo.GuiTurretInfo;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmo;
import de.sanandrew.mods.turretmod.registry.assembly.RecipeEntryItem;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TurretInfoEntryTurret
        extends TurretInfoEntry
{
    private int drawHeight;
    private float rotation;
    private long lastTimestamp;
    private Class<? extends EntityTurret> turretClass;
    private WeakReference<EntityTurret> turretRenderCache;
    private TurretInfoValues values;

    public TurretInfoEntryTurret(Class<? extends EntityTurret> turret) {
        this(TurretRegistry.INSTANCE.getInfo(turret));
        this.turretClass = turret;
    }

    private TurretInfoEntryTurret(TurretInfo info) {
        super(ItemRegistry.turret.getTurretItem(1, info), Lang.translate(Lang.ENTITY_NAME, EntityList.classToStringMapping.get(info.getTurretClass())));
        this.values = new TurretInfoValues(info);
    }

    @Override
    public void drawPage(GuiTurretInfo gui, int mouseX, int mouseY, int scrollY, float partTicks) {
        int turretHeight = 82;
        int valueHeight = 113;
        int descStart;
        int descHeight;

        gui.mc.fontRenderer.drawString(EnumChatFormatting.ITALIC + this.values.name, 2, 2, 0xFF0080BB);
        Gui.drawRect(2, 12, MAX_ENTRY_WIDTH - 2, 13, 0xFF0080BB);

        gui.doEntryScissoring(2, 15, 54, 82);

        Gui.drawRect(0, 0, MAX_ENTRY_WIDTH, MAX_ENTRY_HEIGHT, 0xFF000000);
        this.drawTurret(gui.mc, 28, 35);

        gui.doEntryScissoring();

        gui.mc.fontRenderer.drawString(this.txtHealth, 60, 15, 0xFF6A6A6A, false);
        gui.mc.fontRenderer.drawString(String.format(Lang.translate(Lang.TINFO_ENTRY_HEALTHVAL), this.values.health), 63, 24, 0xFF000000, false);
        gui.mc.fontRenderer.drawString(this.txtRange, 60, 35, 0xFF6A6A6A, false);
        gui.mc.fontRenderer.drawString(String.format(Lang.translate(Lang.TINFO_ENTRY_RANGEVAL), this.values.range), 63, 44, 0xFF000000, false);
        gui.mc.fontRenderer.drawString(this.txtAmmoCap, 60, 55, 0xFF6A6A6A, false);
        gui.mc.fontRenderer.drawString(String.format(Lang.translate(Lang.TINFO_ENTRY_ROUNDSVAL), this.values.ammoCap), 63, 64, 0xFF000000, false);
        gui.mc.fontRenderer.drawString(this.txtAmmoUse, 60, 75, 0xFF6A6A6A, false);
        gui.mc.fontRenderer.drawString(this.txtCrft, 60, 95, 0xFF6A6A6A, false);

        descStart = Math.max(turretHeight, valueHeight);

        Gui.drawRect(2, 2 + descStart, MAX_ENTRY_WIDTH - 2, 3 + descStart, 0xFF0080BB);
        gui.mc.fontRenderer.drawSplitString(this.values.desc, 2, 5 + descStart, MAX_ENTRY_WIDTH - 2, 0xFF000000);
        descHeight = gui.mc.fontRenderer.splitStringWidth(this.values.desc, MAX_ENTRY_WIDTH - 4) + 7;

        for( int i = 0; i < this.values.ammoStacks.length; i++ ) {
            drawMiniItem(gui, 63 + 10 * i, 84, mouseX, mouseY, scrollY, this.values.ammoStacks[i], true);
        }
        for( int i = 0; i < this.values.recipeStacks.length; i++ ) {
            this.drawItemRecipe(gui, 63 + 10 * i, 104, mouseX, mouseY, scrollY, this.values.recipeStacks[i]);
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
                this.turretRenderCache = new WeakReference<>(this.turretClass.getConstructor(World.class).newInstance(mc.theWorld));
            } catch( Exception e ) {
                return;
            }
        }

        EntityTurret turret = this.turretRenderCache.get();
        if( turret == null ) {
            return;
        }

        turret.inGui = true;

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 50.0F);
        GL11.glScalef(30.0F, 30.0F, 30.0F);

        GL11.glTranslatef(0.0F, turret.height, 0.0F);

        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(135.0F + rotation, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        rotation += 0.2F;

        turret.prevRotationPitch = turret.rotationPitch = 0.0F;
        turret.prevRotationYaw = turret.rotationYaw = 0.0F;
        turret.prevRotationYawHead = turret.rotationYawHead = 0.0F;

        RenderManager.instance.playerViewY = 180.0F;
        RenderManager.instance.renderEntityWithPosYaw(turret, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    private void drawItemRecipe(GuiTurretInfo gui, int x, int y, int mouseX, int mouseY, int scrollY, RecipeEntryItem entryItem) {
        ItemStack[] entryStacks = entryItem.getEntryItemStacks();
        ItemStack stack = entryStacks[(int)((this.lastTimestamp / 1000L) % entryStacks.length)];
        drawMiniItem(gui, x, y, mouseX, mouseY, scrollY, stack, entryItem.shouldDrawTooltip());
    }

    private static final class TurretInfoValues
    {
        public final String name;
        public final String desc;
        public final float health;
        public final int ammoCap;
        public final String range;
        public final RecipeEntryItem[] recipeStacks;
        public final ItemStack[] ammoStacks;

        public TurretInfoValues(TurretInfo info) {
            List<ItemStack> ammoItms = new ArrayList<>();

            this.name = Lang.translate(Lang.ENTITY_NAME, EntityList.classToStringMapping.get(info.getTurretClass()));
            this.desc = Lang.translate(Lang.ENTITY_DESC, EntityList.classToStringMapping.get(info.getTurretClass())).replace("\\n", "\n");
            this.range = info.getInfoRange();
            this.health = info.getTurretHealth();
            this.ammoCap = info.getBaseAmmoCapacity();

            List<TurretAmmo> ammos = AmmoRegistry.INSTANCE.getTypesForTurret(info.getTurretClass());
            for( TurretAmmo ammo : ammos ) {
                ammoItms.add(ItemRegistry.ammo.getAmmoItem(1, ammo));
            }

            this.recipeStacks = TurretAssemblyRecipes.INSTANCE.getRecipeEntry(info.getRecipeId()).resources;

            this.ammoStacks = ammoItms.toArray(new ItemStack[ammoItms.size()]);
        }
    }
}
