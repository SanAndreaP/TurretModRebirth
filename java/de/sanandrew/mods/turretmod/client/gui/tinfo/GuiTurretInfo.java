/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo;

import de.sanandrew.mods.turretmod.client.event.ClientTickHandler;
import de.sanandrew.mods.turretmod.client.gui.tinfo.entry.TurretInfoEntry;
import de.sanandrew.mods.turretmod.client.util.TmrClientUtils;
import de.sanandrew.mods.turretmod.util.EnumGui;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.EnumChatFormatting;

import org.apache.logging.log4j.Level;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class GuiTurretInfo
        extends GuiScreen
        implements GuiYesNoCallback
{
    private static final int X_SIZE = 192;
    private static final int Y_SIZE = 236;

    private int guiLeft;
    private int guiTop;

    public float timeDelta;
    private float lastTime;

    public final TurretInfoCategory category;
    public final TurretInfoEntry entry;

    public float scroll = 0.0F;
    private int dHeight;
    private boolean isScrolling;
    public int entryX;
    public int entryY;
    private URI clickedURI;

    public List<GuiButton> entryButtons;

    public GuiTurretInfo(int category, int entry) {
        this.category = category < 0 ? null : TurretInfoCategory.getCategory(category);
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
            int catLng = TurretInfoCategory.getCategoryCount();
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

        float time = ClientTickHandler.ticksInGame + partTicks;
        this.timeDelta = time - this.lastTime;
        this.lastTime = time;

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawDefaultBackground();

        this.mc.renderEngine.bindTexture(Resources.GUI_TURRETINFO.getResource());

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, X_SIZE, Y_SIZE);

        GL11.glPushMatrix();
        GL11.glTranslatef(this.entryX + TurretInfoEntry.MAX_ENTRY_WIDTH, this.entryY, 0.0F);
        drawRect(0, 0, 6, TurretInfoEntry.MAX_ENTRY_HEIGHT, 0x30000000);
        if( this.dHeight > 0 ) {
            drawRect(0, Math.round((TurretInfoEntry.MAX_ENTRY_HEIGHT - 16) * this.scroll), 6, Math.round((TurretInfoEntry.MAX_ENTRY_HEIGHT - 16) * this.scroll + 16), 0x800000FF);
        }
        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        this.doEntryScissoring();
        GL11.glTranslatef(this.entryX, this.entryY, 0.0F);
        GL11.glTranslatef(0.0F, Math.round(-this.scroll * this.dHeight), 0.0F);

        if( this.entry != null ) {
            this.dHeight = this.entry.getPageHeight() - TurretInfoEntry.MAX_ENTRY_HEIGHT;
            this.entry.drawPage(this, mouseX - this.entryX, mouseY - this.entryY, Math.round(this.scroll * this.dHeight), partTicks);
        } else if( this.category != null ) {
            this.dHeight = this.entryButtons.size() * 14 + 20 - TurretInfoEntry.MAX_ENTRY_HEIGHT;
            this.fontRendererObj.drawString(EnumChatFormatting.ITALIC + this.category.getTitle(), 2, 2, 0xFF33AA33, false);
            Gui.drawRect(2, 12, TurretInfoEntry.MAX_ENTRY_WIDTH - 2, 13, 0xFF33AA33);

        }

        for( GuiButton btn : this.entryButtons ) {
            btn.enabled = btn.yPosition - Math.round(this.scroll * this.dHeight) > 0
                    && btn.yPosition - Math.round(this.scroll * this.dHeight) + btn.height < TurretInfoEntry.MAX_ENTRY_HEIGHT;
            btn.drawButton(this.mc, mouseX - this.entryX, mouseY - this.entryY + Math.round(this.scroll * this.dHeight));
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GL11.glPopMatrix();

        if( !mouseDown && this.isScrolling ) {
            this.isScrolling = false;
        } else if( mouseDown && !this.isScrolling ) {
            if( mouseY >= this.entryY && mouseY < this.entryY + TurretInfoEntry.MAX_ENTRY_HEIGHT ) {
                if( mouseX >= this.entryX + TurretInfoEntry.MAX_ENTRY_WIDTH && mouseX < this.entryX + TurretInfoEntry.MAX_ENTRY_WIDTH + 6 ) {
                    this.isScrolling = this.dHeight > 0;
                }
            }
        }

        if( this.isScrolling ) {
            int mouseDelta = Math.min(TurretInfoEntry.MAX_ENTRY_HEIGHT - 16, Math.max(0, mouseY - (this.entryY + 8)));
            this.scroll = mouseDelta / (TurretInfoEntry.MAX_ENTRY_HEIGHT - 16.0F);
        }

        super.drawScreen(mouseX, mouseY, partTicks);
    }

    public void doEntryScissoring(int x, int y, int width, int height) {
        int prevX = x;
        int yShifted = y - Math.round(this.scroll * this.dHeight);

        int maxWidth = Math.min(width, width - (x + width - TurretInfoEntry.MAX_ENTRY_WIDTH));
        int maxHeight = Math.min(height, height - (y + height - TurretInfoEntry.MAX_ENTRY_HEIGHT) + Math.round(this.scroll * this.dHeight));

        x = this.entryX + Math.max(0, prevX);
        y = this.entryY + Math.max(0, yShifted);

        width = Math.max(0, Math.min(maxWidth, width + prevX));
        height = Math.max(0, Math.min(maxHeight, height + yShifted));

        TmrClientUtils.doGlScissor(x, y, width, height);
    }

    public void doEntryScissoring() {
        TmrClientUtils.doGlScissor(this.entryX, this.entryY, TurretInfoEntry.MAX_ENTRY_WIDTH, TurretInfoEntry.MAX_ENTRY_HEIGHT);
    }

    @Override
    public void handleMouseInput() {
        if( this.dHeight > 0 ) {
            int dwheel = Mouse.getEventDWheel() / 120;
            if( dwheel != 0 ) {
                this.scroll = Math.min(1.0F, Math.max(0.0F, (this.scroll * this.dHeight - dwheel * 16.0F) / this.dHeight));
            }
        }

        super.handleMouseInput();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if( button instanceof GuiButtonNav ) {
            switch( ((GuiButtonNav) button).buttonType ) {
                case 0:
                    if( this.entry != null && this.category != null ) {
                        if( this.category.getEntryCount() == 1 ) {
                            TurretModRebirth.proxy.openGui(this.mc.thePlayer, EnumGui.GUI_TINFO, -1, -1, 0);
                        } else {
                            TurretModRebirth.proxy.openGui(this.mc.thePlayer, EnumGui.GUI_TINFO, this.category.index, -1, 0);
                        }
                    } else if( this.entry == null && this.category != null ) {
                        TurretModRebirth.proxy.openGui(this.mc.thePlayer, EnumGui.GUI_TINFO, -1, -1, 0);
                    }
                    break;
                case 1:
                    TurretModRebirth.proxy.openGui(this.mc.thePlayer, EnumGui.GUI_TINFO, -1, -1, 0);
                    break;
                case 2:
                    this.mc.thePlayer.closeScreen();
                    break;
            }
        } else if( button instanceof GuiButtonCategory ) {
            if( TurretInfoCategory.getCategory(((GuiButtonCategory) button).catIndex).getEntryCount() == 1 ) {
                TurretModRebirth.proxy.openGui(this.mc.thePlayer, EnumGui.GUI_TINFO, ((GuiButtonCategory) button).catIndex, 0, 0);
            } else {
                TurretModRebirth.proxy.openGui(this.mc.thePlayer, EnumGui.GUI_TINFO, ((GuiButtonCategory) button).catIndex, -1, 0);
            }
        } else if( button instanceof GuiButtonEntry ) {
            TurretModRebirth.proxy.openGui(this.mc.thePlayer, EnumGui.GUI_TINFO, this.category.index, ((GuiButtonEntry) button).entIndex, 0);
        } else if( button instanceof GuiButtonLink ) {
            try {
                this.clickedURI = new URI(((GuiButtonLink) button).link);
                if (this.mc.gameSettings.chatLinksPrompt) {
                    this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, this.clickedURI.toString(), 0, false));
                } else {
                    this.openLink(this.clickedURI);
                }
            } catch( URISyntaxException e ) {
                TurretModRebirth.LOG.log(Level.ERROR, "Cannot create invalid URI", e);
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
    protected void mouseClicked(int mouseX, int mouseY, int mouseBtn) {
        super.mouseClicked(mouseX, mouseY, mouseBtn);

        if( mouseBtn == 0 ) {
            for( GuiButton btn : this.entryButtons ) {
                if( btn.mousePressed(this.mc, mouseX - this.entryX, mouseY - this.entryY + Math.round(this.scroll * this.dHeight)) ) {
                    btn.func_146113_a(this.mc.getSoundHandler());
                    this.actionPerformed(btn);
                }
            }
        }
    }

    public List getButtonList() {
        return this.buttonList;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void openLink(URI uri) {
        try {
            Class<?> clsDesktop = Class.forName("java.awt.Desktop");
            Object objDesktop = clsDesktop.getMethod("getDesktop", new Class[0]).invoke(null);
            clsDesktop.getMethod("browse", new Class[] {URI.class}).invoke(objDesktop, uri);
        } catch( Throwable throwable ) {
            TurretModRebirth.LOG.log(Level.ERROR, "Couldn\'t open link", throwable);
        }
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
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if( this.visible ) {
                String clrCode = (this.enabled ? EnumChatFormatting.BLUE : EnumChatFormatting.GRAY).toString();
                mc.fontRenderer.drawString(clrCode + EnumChatFormatting.UNDERLINE + this.displayString, this.xPosition, this.yPosition, 0xFF000000, false);
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
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if( this.visible ) {
                boolean over = mouseX >= this.xPosition && mouseX < this.xPosition + this.width && mouseY >= this.yPosition && mouseY < this.yPosition + this.height;

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                mc.getTextureManager().bindTexture(Resources.GUI_TURRETINFO.getResource());
                GL11.glEnable(GL11.GL_BLEND);
                OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                switch( this.buttonType ) {
                    case 0:
                        this.drawTexturedModalRect(this.xPosition + 5, this.yPosition + 8, 0, 236 + (over ? 0 : 10), 15, 9);
                        break;
                    case 1:
                        this.drawTexturedModalRect(this.xPosition + 8, this.yPosition + 8, 16, 236 + (over ? 0 : 10), 10, 9);
                        break;
                    case 2:
                        this.drawTexturedModalRect(this.xPosition + 8, this.yPosition + 8, 27, 236 + (over ? 0 : 10), 9, 9);
                        break;
                }
                GL11.glDisable(GL11.GL_BLEND);
            }
        }
    }
}
