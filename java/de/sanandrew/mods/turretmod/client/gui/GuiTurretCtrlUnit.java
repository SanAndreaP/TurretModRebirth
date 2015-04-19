/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui;

import com.google.common.collect.Maps;
import de.sanandrew.core.manpack.util.client.helpers.GuiUtils;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.client.gui.control.GuiSlimButton;
import de.sanandrew.mods.turretmod.entity.turret.AEntityTurretBase;
import de.sanandrew.mods.turretmod.network.packet.PacketSendTargetFlag;
import de.sanandrew.mods.turretmod.util.EnumTextures;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.Map.Entry;

public class GuiTurretCtrlUnit
        extends GuiScreen
{
    protected int guiLeft;
    protected int guiTop;
    protected int xSize;
    protected int ySize;

    private final AEntityTurretBase myTurret;
    private Map<Class<? extends EntityLiving>, Boolean> tempTargetList = Maps.newHashMap();

    private float scroll = 0.0F;
    private float scrollAmount = 0.0F;
    private boolean isScrolling;
    private boolean canScroll;
    private boolean prevIsLmbDown;

    private GuiButton toggleAll;

    public GuiTurretCtrlUnit(AEntityTurretBase turret) {
        this.myTurret = turret;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.xSize = 176;
        this.ySize = 222;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.buttonList.add(this.toggleAll = new GuiSlimButton(this.buttonList.size(), this.guiLeft + (this.xSize - 150) / 2, this.guiTop + 180, 150, "select all"));
        this.buttonList.add(this.toggleAll = new GuiSlimButton(this.buttonList.size(), this.guiLeft + (this.xSize - 150) / 2, this.guiTop + 193, 150, "select all"));
        this.buttonList.add(this.toggleAll = new GuiSlimButton(this.buttonList.size(), this.guiLeft + (this.xSize - 150) / 2, this.guiTop + 206, 150, "select all"));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        this.tempTargetList = this.myTurret.getTargetList();

        this.canScroll = this.tempTargetList.size() >= 18;
        this.scrollAmount = Math.max(0.0F, 1.0F / (this.tempTargetList.size() - 18.0F));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partTicks) {
        if( this.myTurret.isDead ) {
            this.mc.thePlayer.closeScreen();
            return;
        }

        boolean isLmbDown = Mouse.isButtonDown(0);
        int scrollMinX = this.guiLeft + 163;
        int scrollMaxX = this.guiLeft + 163 + 9;
        int scrollMinY = this.guiTop + 19;
        int scrollMaxY = this.guiTop + 178;

        if( !this.isScrolling && this.canScroll && isLmbDown && mouseX >= scrollMinX && mouseX < scrollMaxX && mouseY > scrollMinY && mouseY < scrollMaxY ) {
            this.isScrolling = true;
        } else if( !isLmbDown ) {
            this.isScrolling = false;
        }

        if( this.isScrolling ) {
            this.scroll = Math.max(0.0F, Math.min(1.0F, (mouseY - 2 - scrollMinY) / 178.0F));
        }

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawDefaultBackground();

        this.mc.renderEngine.bindTexture(EnumTextures.GUI_TCU_PG1.getResource());

        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(this.guiLeft + 163, this.guiTop + 19 + MathHelper.floor_float(scroll * 178.0F), 176, this.canScroll ? 0 : 6, 6, 6);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GuiUtils.doGlScissor(this.guiLeft + 6, this.guiTop + 19, this.xSize - 23, this.ySize - 38);

        int offsetY = Math.round(-this.scroll * (this.tempTargetList.size() - 18)) * (this.fontRendererObj.FONT_HEIGHT + 1);
        for( Entry<Class<? extends EntityLiving>, Boolean> entry : this.tempTargetList.entrySet() ) {
            int btnTexOffY = 12 + (entry.getValue() ? 16 : 0);
            int btnMinOffY = this.guiTop + 20;
            int btnMaxOffY = this.guiTop + 20 + 178;
            if( mouseY >= btnMinOffY && mouseY < btnMaxOffY ) {
                if( mouseX >= this.guiLeft + 10 && mouseX < this.guiLeft + 18 && mouseY >= this.guiTop + 20 + offsetY && mouseY < this.guiTop + 28 + offsetY ) {
                    btnTexOffY += 8;
                    if( isLmbDown && !this.prevIsLmbDown ) {
                        this.applyTarget(entry.getKey(), !entry.getValue());
                    }
                }
            }
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(EnumTextures.GUI_TCU_PG1.getResource());
            this.drawTexturedModalRect(this.guiLeft + 10, this.guiTop + 20 + offsetY, 176, btnTexOffY, 8, 8);

            int textColor = 0xFFFFFF;
            if( IMob.class.isAssignableFrom(entry.getKey()) ) {
                textColor = 0xFFAAAA;
            } else if( IAnimals.class.isAssignableFrom(entry.getKey()) ) {
                textColor = 0xAAFFAA;
            }
            String namedEntry = EntityList.classToStringMapping.get(entry.getKey()).toString();
            String name = "entity." + namedEntry + ".name";
            if( !StatCollector.canTranslate(name) ) {
                name = namedEntry;
            }
            this.fontRendererObj.drawString(SAPUtils.translate(name), this.guiLeft + 20, this.guiTop + 20 + offsetY, textColor, false);

            offsetY += this.fontRendererObj.FONT_HEIGHT + 1;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        this.prevIsLmbDown = isLmbDown;

        super.drawScreen(mouseX, mouseY, partTicks);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        if( this.canScroll ) {
            int dWheelDir = Mouse.getEventDWheel();
            if( dWheelDir < 0 ) {
                this.scroll = Math.min(1.0F, this.scroll + this.scrollAmount);
            } else if( dWheelDir > 0 ) {
                this.scroll = Math.max(0.0F, this.scroll - this.scrollAmount);
            }
        }
    }

    private void applyTarget(Class<? extends EntityLiving> entityCls, boolean active) {
        PacketSendTargetFlag.sendToServer(this.myTurret, entityCls, active);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
