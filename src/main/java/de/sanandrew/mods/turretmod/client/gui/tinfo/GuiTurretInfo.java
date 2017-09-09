/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo;

import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.turretinfo.IGuiTurretInfo;
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretInfoCategory;
import de.sanandrew.mods.turretmod.api.client.turretinfo.ITurretInfoEntry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiTurretInfo
        extends GuiScreen
        implements GuiYesNoCallback, IGuiTurretInfo
{
    private static final int X_SIZE = 192;
    private static final int Y_SIZE = 236;

    private int guiLeft;
    private int guiTop;

    public final ITurretInfoCategory category;
    public ITurretInfoCategory categoryHighlight;
    public final ITurretInfoEntry entry;

    public float scroll = 0.0F;
    private int dHeight;
    private boolean isScrolling;
    public int entryX;
    public int entryY;
    private URI clickedURI;

    public final List<GuiButton> entryButtons;

    public GuiTurretInfo(int category, int entry) {
        this.category = category < 0 ? null : TurretInfoCategoryRegistry.INSTANCE.getCategory(category);
        this.entry = entry < 0 ? null : (this.category != null ? this.category.getEntry(entry) : null);
        this.entryButtons = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();

        this.guiLeft = (this.width - X_SIZE) / 2;
        this.guiTop = (this.height - Y_SIZE) / 2;

        this.entryX = this.guiLeft + 9;
        this.entryY = this.guiTop + 19;

        this.buttonList.clear();
        this.entryButtons.clear();

        this.buttonList.add(new GuiButtonNav(this.buttonList.size(), this.guiLeft + 53, this.guiTop + 206, 0));
        this.buttonList.add(new GuiButtonNav(this.buttonList.size(), this.guiLeft + 83, this.guiTop + 206, 1));
        this.buttonList.add(new GuiButtonNav(this.buttonList.size(), this.guiLeft + 114, this.guiTop + 206, 2));

        if( this.category == null ) {
            int catLng = TurretInfoCategoryRegistry.INSTANCE.getCategoryCount();
            for( int i = 0; i < catLng; i++ ) {
                this.buttonList.add(new GuiButtonCategory(this.buttonList.size(), i, this.guiLeft + 12 + 32 * i, this.guiTop + 24, this));
            }
        } else if( this.entry == null ) {
            int entLng = this.category.getEntryCount();
            for( int i = 0; i < entLng; i++ ) {
                this.entryButtons.add(new GuiButtonEntry(this.entryButtons.size(), i, 5, 19 + 14 * i, this));
            }
        } else {
            this.entry.initEntry(this);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partTicks) {
        boolean mouseDown = Mouse.isButtonDown(0);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawDefaultBackground();

        this.mc.renderEngine.bindTexture(Resources.GUI_TURRETINFO.getResource());

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, X_SIZE, Y_SIZE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(this.entryX + ITurretInfoEntry.MAX_ENTRY_WIDTH, this.entryY, 0.0F);
        drawRect(0, 0, 6, ITurretInfoEntry.MAX_ENTRY_HEIGHT, 0x30000000);
        if( this.dHeight > 0 ) {
            drawRect(0, Math.round((ITurretInfoEntry.MAX_ENTRY_HEIGHT - 16) * this.scroll), 6, Math.round((ITurretInfoEntry.MAX_ENTRY_HEIGHT - 16) * this.scroll + 16), 0x800000FF);
        }
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        this.doEntryScissoring();
        GlStateManager.translate(this.entryX, this.entryY, 0.0F);
        GlStateManager.translate(0.0F, Math.round(-this.scroll * this.dHeight), 0.0F);

        if( this.entry != null ) {
            this.dHeight = this.entry.getPageHeight() - ITurretInfoEntry.MAX_ENTRY_HEIGHT;
            this.entry.drawPage(mouseX - this.entryX, mouseY - this.entryY, Math.round(this.scroll * this.dHeight), partTicks);
        } else if( this.category != null ) {
            this.dHeight = this.entryButtons.size() * 14 + 20 - ITurretInfoEntry.MAX_ENTRY_HEIGHT;
            this.fontRenderer.drawString(TextFormatting.ITALIC + Lang.translate(this.category.getTitle()), 2, 2, 0xFF33AA33, false);
            Gui.drawRect(2, 12, ITurretInfoEntry.MAX_ENTRY_WIDTH - 2, 13, 0xFF33AA33);

        }

        for( GuiButton btn : this.entryButtons ) {
            btn.enabled = btn.y - Math.round(this.scroll * this.dHeight) > 0 && btn.y - Math.round(this.scroll * this.dHeight) + btn.height < ITurretInfoEntry.MAX_ENTRY_HEIGHT;
            btn.drawButton(this.mc, mouseX - this.entryX, mouseY - this.entryY + Math.round(this.scroll * this.dHeight), partTicks);
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();

        if( !mouseDown && this.isScrolling ) {
            this.isScrolling = false;
        } else if( mouseDown && !this.isScrolling ) {
            if( mouseY >= this.entryY && mouseY < this.entryY + ITurretInfoEntry.MAX_ENTRY_HEIGHT ) {
                if( mouseX >= this.entryX + ITurretInfoEntry.MAX_ENTRY_WIDTH && mouseX < this.entryX + ITurretInfoEntry.MAX_ENTRY_WIDTH + 6 ) {
                    this.isScrolling = this.dHeight > 0;
                }
            }
        }

        if( this.isScrolling ) {
            int mouseDelta = Math.min(ITurretInfoEntry.MAX_ENTRY_HEIGHT - 16, Math.max(0, mouseY - (this.entryY + 8)));
            this.scroll = mouseDelta / (ITurretInfoEntry.MAX_ENTRY_HEIGHT - 16.0F);
        }


        super.drawScreen(mouseX, mouseY, partTicks);

        if( this.categoryHighlight != null ) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(mouseX + 12, mouseY - 12, 32.0F);

            String title = Lang.translate(this.categoryHighlight.getTitle());
            int bkgColor = 0xF0101000;
            int lightBg = 0x5050FF00;
            int darkBg = (lightBg & 0xFEFEFE) >> 1 | lightBg & 0xFF000000;
            int textWidth = this.fontRenderer.getStringWidth(title);
            int tHeight = 8;

            this.drawGradientRect(-3,            -4,          textWidth + 3, -3,          bkgColor, bkgColor);
            this.drawGradientRect(-3,            tHeight + 3, textWidth + 3, tHeight + 4, bkgColor, bkgColor);
            this.drawGradientRect(-3,            -3,          textWidth + 3, tHeight + 3, bkgColor, bkgColor);
            this.drawGradientRect(-4,            -3,          -3,            tHeight + 3, bkgColor, bkgColor);
            this.drawGradientRect(textWidth + 3, -3,          textWidth + 4, tHeight + 3, bkgColor, bkgColor);

            this.drawGradientRect(-3,            -3 + 1,      -3 + 1,        tHeight + 3 - 1, lightBg, darkBg);
            this.drawGradientRect(textWidth + 2, -3 + 1,      textWidth + 3, tHeight + 3 - 1, lightBg, darkBg);
            this.drawGradientRect(-3,            -3,          textWidth + 3, -3 + 1,          lightBg, lightBg);
            this.drawGradientRect(-3,            tHeight + 2, textWidth + 3, tHeight + 3,     darkBg,  darkBg);

            this.fontRenderer.drawString(title, 0, 0, 0xFFFFFFFF, true);
            GlStateManager.popMatrix();
            this.categoryHighlight = null;
        }
    }

    @Override
    public void doEntryScissoring(int x, int y, int width, int height) {
        int prevX = x;
        int yShifted = y - Math.round(this.scroll * this.dHeight);

        int maxWidth = Math.min(width, width - (x + width - ITurretInfoEntry.MAX_ENTRY_WIDTH));
        int maxHeight = Math.min(height, height - (y + height - ITurretInfoEntry.MAX_ENTRY_HEIGHT) + Math.round(this.scroll * this.dHeight));

        x = this.entryX + Math.max(0, prevX);
        y = this.entryY + Math.max(0, yShifted);

        width = Math.max(0, Math.min(maxWidth, width + prevX));
        height = Math.max(0, Math.min(maxHeight, height + yShifted));

        GuiUtils.glScissor(x, y, width, height);
    }

    @Override
    public void doEntryScissoring() {
        GuiUtils.glScissor(this.entryX, this.entryY, ITurretInfoEntry.MAX_ENTRY_WIDTH, ITurretInfoEntry.MAX_ENTRY_HEIGHT);
    }

    @Override
    public void handleMouseInput() throws IOException {
        if( this.dHeight > 0 ) {
            int dwheel = Mouse.getEventDWheel() / 120;
            if( dwheel != 0 ) {
                this.scroll = Math.min(1.0F, Math.max(0.0F, (this.scroll * this.dHeight - dwheel * 16.0F) / this.dHeight));
            }
        }

        super.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if( button instanceof GuiButtonNav ) {
            switch( ((GuiButtonNav) button).buttonType ) {
                case 0:
                    if( this.entry != null && this.category != null ) {
                        if( this.category.getEntryCount() == 1 ) {
                            TurretModRebirth.proxy.openGui(this.mc.player, EnumGui.GUI_TINFO, -1, -1, 0);
                        } else {
                            TurretModRebirth.proxy.openGui(this.mc.player, EnumGui.GUI_TINFO, this.category.getIndex(), -1, 0);
                        }
                    } else if( this.entry == null && this.category != null ) {
                        TurretModRebirth.proxy.openGui(this.mc.player, EnumGui.GUI_TINFO, -1, -1, 0);
                    }
                    break;
                case 1:
                    TurretModRebirth.proxy.openGui(this.mc.player, EnumGui.GUI_TINFO, -1, -1, 0);
                    break;
                case 2:
                    this.mc.player.closeScreen();
                    break;
            }
        } else if( button instanceof GuiButtonCategory ) {
            if( TurretInfoCategoryRegistry.INSTANCE.getCategory(((GuiButtonCategory) button).catIndex).getEntryCount() == 1 ) {
                TurretModRebirth.proxy.openGui(this.mc.player, EnumGui.GUI_TINFO, ((GuiButtonCategory) button).catIndex, 0, 0);
            } else {
                TurretModRebirth.proxy.openGui(this.mc.player, EnumGui.GUI_TINFO, ((GuiButtonCategory) button).catIndex, -1, 0);
            }
        } else if( button instanceof GuiButtonEntry ) {
            TurretModRebirth.proxy.openGui(this.mc.player, EnumGui.GUI_TINFO, this.category.getIndex(), ((GuiButtonEntry) button).entIndex, 0);
        } else if( button instanceof GuiButtonLink ) {
            try {
                this.clickedURI = new URI(((GuiButtonLink) button).link);
                if (this.mc.gameSettings.chatLinksPrompt) {
                    this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, this.clickedURI.toString(), 0, false));
                } else {
                    this.openLink(this.clickedURI);
                }
            } catch( URISyntaxException e ) {
                TmrConstants.LOG.log(Level.ERROR, "Cannot create invalid URI", e);
                this.clickedURI = null;
            }
        } else if( this.entry == null || !this.entry.actionPerformed(button) ) {
            super.actionPerformed(button);
        }
    }

    @Override
    public void confirmClicked(boolean isYes, int id) {
        if( id == 0 ) {
            if( isYes ) {
                this.openLink(this.clickedURI);
            }

            this.clickedURI = null;
            this.mc.displayGuiScreen(this);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseBtn) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseBtn);

        if( mouseBtn == 0 ) {
            for( GuiButton btn : this.entryButtons ) {
                if( btn.mousePressed(this.mc, mouseX - this.entryX, mouseY - this.entryY + Math.round(this.scroll * this.dHeight)) ) {
                    btn.playPressSound(this.mc.getSoundHandler());
                    this.actionPerformed(btn);
                }
            }
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void openLink(URI uri) {
        try {
            java.awt.Desktop.getDesktop().browse(uri);
        } catch( Throwable throwable ) {
            TmrConstants.LOG.log(Level.ERROR, "Couldn\'t open link", throwable);
        }
    }

    @Override
    public void drawMiniItem(int x, int y, int mouseX, int mouseY, int scrollY, @Nonnull ItemStack stack, boolean drawTooltip) {
        this.mc.getTextureManager().bindTexture(Resources.GUI_TURRETINFO.getResource());
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x - 0.5F, y - 0.5F, 0.0F);
        GlStateManager.scale(0.5F, 0.5F, 1.0F);
        this.drawTexturedModalRect(0, 0, 192, 0, 18, 18);
        GlStateManager.popMatrix();

        boolean mouseOver = mouseY >= 0 && mouseY < ITurretInfoEntry.MAX_ENTRY_HEIGHT && mouseX >= x && mouseX < x + 8 && mouseY >= y - scrollY && mouseY < y + 8 - scrollY;
        if( mouseOver && ItemStackUtils.isValid(stack) ) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, ITurretInfoEntry.MAX_ENTRY_HEIGHT - 20 + scrollY, 32.0F);
            Gui.drawRect(0, 0, ITurretInfoEntry.MAX_ENTRY_WIDTH, 20, 0xD0000000);

            List tooltip = GuiUtils.getTooltipWithoutShift(stack);
            this.mc.fontRenderer.drawString(tooltip.get(0).toString(), 22, 2, 0xFFFFFFFF, false);
            if( drawTooltip && tooltip.size() > 1 ) {
                this.mc.fontRenderer.drawString(tooltip.get(1).toString(), 22, 11, 0xFF808080, false);
            }

            RenderUtils.renderStackInGui(stack, 2, 2, 1.0F, this.mc.fontRenderer);

            GlStateManager.popMatrix();
        }

        if( ItemStackUtils.isValid(stack) ) {
            RenderUtils.renderStackInGui(stack, x, y, 0.5F);
        }

        if( mouseOver ) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 0.0F, 64.0F);
            Gui.drawRect(x, y, x + 8, y + 8, 0x80FFFFFF);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public int getEntryX() {
        return this.entryX;
    }

    @Override
    public int getEntryY() {
        return this.entryY;
    }

    @Override
    public List<GuiButton> __getButtons() {
        return this.buttonList;
    }

    @Override
    public Minecraft __getMc() {
        return this.mc;
    }

    @Override
    public void __drawTexturedRect(int x, int y, int u, int v, int w, int h) {
        this.drawTexturedModalRect(x, y, u, v, w, h);
    }

    @Override
    public void renderStack(@Nonnull ItemStack stack, int x, int y, double scale) {
        RenderUtils.renderStackInGui(stack, x, y, scale);
    }

    public static class GuiButtonLink
            extends GuiButton
    {
        public final String link;

        public GuiButtonLink(int id, int x, int y, String text, String link) {
            super(id, x, y, Minecraft.getMinecraft().fontRenderer.getStringWidth(text), Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT, text);
            this.link = link;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partTicks) {
            if( this.visible ) {
                String clrCode = (this.enabled ? TextFormatting.BLUE : TextFormatting.GRAY).toString();
                mc.fontRenderer.drawString(clrCode + TextFormatting.UNDERLINE + this.displayString, this.x, this.y, 0xFF000000, false);
            }
        }
    }

    private static final class GuiButtonNav
            extends GuiButton
    {
        public final int buttonType;

        public GuiButtonNav(int id, int x, int y, int type) {
            super(id, x, y, 25 + (type == 1 ? 1 : 0), 25, "");
            this.buttonType = type;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partTicks) {
            if( this.visible ) {
                boolean over = mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height;

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(Resources.GUI_TURRETINFO.getResource());
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                switch( this.buttonType ) {
                    case 0:
                        this.drawTexturedModalRect(this.x + 5, this.y + 8, 0, 236 + (over ? 0 : 10), 15, 9);
                        break;
                    case 1:
                        this.drawTexturedModalRect(this.x + 8, this.y + 8, 16, 236 + (over ? 0 : 10), 10, 9);
                        break;
                    case 2:
                        this.drawTexturedModalRect(this.x + 8, this.y + 8, 27, 236 + (over ? 0 : 10), 9, 9);
                        break;
                }
                GlStateManager.disableBlend();
            }
        }
    }
}
