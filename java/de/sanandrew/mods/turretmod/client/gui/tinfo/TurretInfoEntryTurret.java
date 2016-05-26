/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.AmmoRegistry;
import de.sanandrew.mods.turretmod.registry.ammo.TurretAmmo;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.util.TurretAssemblyRecipes;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class TurretInfoEntryTurret
        extends TurretInfoEntry
{
    private static final RenderItem ITEM_RENDER = new RenderItem();

    private int drawHeight;
    private float rotation;
    private Class<? extends EntityTurret> turretClass;
    private WeakReference<EntityTurret> turretRenderCache;
    private TurretInfoValues values;

    public TurretInfoEntryTurret(Class<? extends EntityTurret> turret) {
        this(TurretRegistry.INSTANCE.getInfo(turret));
        this.turretClass = turret;
    }

    private TurretInfoEntryTurret(TurretInfo info) {
        super(ItemRegistry.turret.getTurretItem(1, info), String.format("entity.%s.%s.name", TurretModRebirth.ID, info.getName()));
        this.values = new TurretInfoValues(info);
    }

    @Override
    public void drawPage(GuiTurretInfo gui, int mouseX, int mouseY, int scrollY, float partTicks) {
        int turretHeight = 82;
        int valueHeight = 100;
        int descStart;
        int descHeight;

        gui.mc.fontRenderer.drawString(EnumChatFormatting.BOLD + StatCollector.translateToLocal(this.values.name), 2, 2, 0xFF0080BB);
        Gui.drawRect(2, 12, MAX_ENTRY_WIDTH - 2, 13, 0xFF0080BB);

        gui.doEntryScissoring(2, 16, 54, 92);

        Gui.drawRect(2, 16, 56, 98, 0xFF000000);
        this.drawTurret(gui.mc, 28, 35);

        gui.doEntryScissoring();

        GL11.glPushMatrix();
        gui.mc.fontRenderer.drawString("Health:", 60, 16, 0xFF6A6A6A, false);
        gui.mc.fontRenderer.drawString(this.values.health, 63, 25, 0xFF000000, false);
        gui.mc.fontRenderer.drawString("Ammo Capacity:", 60, 37, 0xFF6A6A6A, false);
        gui.mc.fontRenderer.drawString(this.values.ammoCap, 63, 46, 0xFF000000, false);
        gui.mc.fontRenderer.drawString("Ammo Usable:", 60, 59, 0xFF6A6A6A, false);
        gui.mc.fontRenderer.drawString("Crafting:", 60, 80, 0xFF6A6A6A, false);

        GL11.glPopMatrix();

        descStart = Math.max(turretHeight, valueHeight);

        Gui.drawRect(2, 2 + descStart, MAX_ENTRY_WIDTH - 2, 3 + descStart, 0xFF0080BB);
        String text = StatCollector.translateToLocal(this.values.desc).replace("\\n", "\n");
        gui.mc.fontRenderer.drawSplitString(text, 2, 6 + descStart, MAX_ENTRY_WIDTH - 2, 0xFF000000);
        descHeight = gui.mc.fontRenderer.splitStringWidth(text, MAX_ENTRY_WIDTH - 4) + 7;

        for( int i = 0; i < this.values.ammoStacks.length; i++ ) {
            drawItem(gui.mc, 63 + 10 * i, 68, mouseX, mouseY, scrollY, this.values.ammoStacks[i]);
        }
        for( int i = 0; i < this.values.recipeStacks.length; i++ ) {
            drawItem(gui.mc, 63 + 10 * i, 89, mouseX, mouseY, scrollY, this.values.recipeStacks[i]);
        }

        this.drawHeight = descStart + descHeight;
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

    private static void drawItem(Minecraft mc, int x, int y, int mouseX, int mouseY, int scrollY, ItemStack stack) {
        GL11.glPushMatrix();
        if( mouseY >= 0 && mouseY < MAX_ENTRY_HEIGHT && mouseX >= x && mouseX < x + 9 && mouseY >= y - scrollY && mouseY < y + 9 - scrollY ) {
            Gui.drawRect(x, y, x + 9, y + 9, 0xFFD0D0D0);
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, MAX_ENTRY_HEIGHT - 20 + scrollY, 32.0F);
            Gui.drawRect(0, 0, MAX_ENTRY_WIDTH, 20, 0xD0000000);

            mc.fontRenderer.drawString(stack.getDisplayName(), 22, 2, 0xFFFFFFFF, false);

            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();
            ITEM_RENDER.zLevel = 300.0F;
            ITEM_RENDER.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, 2, 2);
            ITEM_RENDER.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, 2, 2);
            ITEM_RENDER.zLevel = 0.0F;
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);

            GL11.glPopMatrix();
        } else {
            Gui.drawRect(x, y, x + 9, y + 9, 0xFFA0A0A0);
        }

        GL11.glTranslatef(0.0F, 0.0F, 32.0F);

        GL11.glScalef(0.5F, 0.5F, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();
        ITEM_RENDER.zLevel = 200.0F;
        ITEM_RENDER.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x*2 + 1, y*2 + 1);
        ITEM_RENDER.renderItemOverlayIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x*2 + 1, y*2 + 1);
        ITEM_RENDER.zLevel = 0.0F;
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);

        GL11.glPopMatrix();
    }

    private static final class TurretInfoValues
    {
        public final String name;
        public final String desc;
        public final String health;
        public final String ammoCap;
        public final ItemStack[] recipeStacks;
        public final ItemStack[] ammoStacks;

        public TurretInfoValues(TurretInfo info) {
            List<ItemStack> ammoItms = new ArrayList<>();

            this.name = String.format("entity.%s.%s.name", TurretModRebirth.ID, info.getName());
            this.desc = String.format("entity.%s.%s.desc", TurretModRebirth.ID, info.getName());
            this.health = String.format("%.2f HP", info.getTurretHealth());
            this.ammoCap = String.format("%d Rounds", info.getBaseAmmoCapacity());

            List<TurretAmmo> ammos = AmmoRegistry.INSTANCE.getTypesForTurret(info.getTurretClass());
            for( TurretAmmo ammo : ammos ) {
                ammoItms.add(ItemRegistry.ammo.getAmmoItem(1, ammo));
            }

            this.recipeStacks = TurretAssemblyRecipes.INSTANCE.getRecipeEntry(info.getRecipeId()).resources.clone();

            this.ammoStacks = ammoItms.toArray(new ItemStack[ammoItms.size()]);
        }
    }
}
