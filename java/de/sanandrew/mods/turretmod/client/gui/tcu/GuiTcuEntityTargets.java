/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.turretmod.client.gui.control.GuiSlimButton;
import de.sanandrew.mods.turretmod.client.util.TmrClientUtils;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.network.PacketUpdateTargets;
import de.sanandrew.mods.turretmod.util.Resources;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public final class GuiTcuEntityTargets
        extends GuiScreen
        implements GuiTurretCtrlUnit
{
    private EntityTurret turret;

    private int guiLeft;
    private int guiTop;

    private Map<Class, Boolean> tempTargetList = new HashMap<>();

    private float scroll = 0.0F;
    private float scrollAmount = 0.0F;
    private boolean isScrolling;
    private boolean canScroll;
    private boolean prevIsLmbDown;

    private boolean doSelectAll;
    private boolean doDeselectAll;
    private boolean doSelectMobs;
    private boolean doSelectAnimals;
    private boolean doSelectOther;

    private GuiButton selectAll;
    private GuiButton deselectAll;
    private GuiButton selectMobs;
    private GuiButton selectAnimals;
    private GuiButton selectOther;

    public GuiTcuEntityTargets(EntityTurret turret) {
        this.turret = turret;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - GuiTCUHelper.X_SIZE) / 2;
        this.guiTop = (this.height - GuiTCUHelper.Y_SIZE) / 2;

        this.buttonList.clear();

        GuiTCUHelper.initGui(this);

        int center = this.guiLeft + (GuiTCUHelper.X_SIZE - 150) / 2;
        this.buttonList.add(this.selectAll = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 138, 150, translateBtn("selectAll")));
        this.buttonList.add(this.deselectAll = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 151, 150, translateBtn("deselectAll")));
        this.buttonList.add(this.selectMobs = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 164, 150, translateBtn("selectMobs")));
        this.buttonList.add(this.selectAnimals = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 177, 150, translateBtn("selectAnimals")));
        this.buttonList.add(this.selectOther = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 190, 150, translateBtn("selectOther")));

        GuiTCUHelper.pageEntityTargets.enabled = false;

        this.updateList();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        if( this.turret.isDead ) {
            this.mc.thePlayer.closeScreen();
        }

        this.canScroll = this.tempTargetList.size() >= 11;
        this.scrollAmount = Math.max(0.0F, 1.0F / (this.tempTargetList.size() - 11.0F));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partTicks) {
        boolean isLmbDown = Mouse.isButtonDown(0);
        int scrollMinX = this.guiLeft + 163;
        int scrollMaxX = this.guiLeft + 163 + 9;
        int scrollMinY = this.guiTop + 19;
        int scrollMaxY = this.guiTop + 134;

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawDefaultBackground();

        if( !this.isScrolling && this.canScroll && isLmbDown && mouseX >= scrollMinX && mouseX < scrollMaxX && mouseY > scrollMinY && mouseY < scrollMaxY ) {
            this.isScrolling = true;
        } else if( !isLmbDown ) {
            this.isScrolling = false;
        }

        if( this.isScrolling ) {
            this.scroll = Math.max(0.0F, Math.min(1.0F, (mouseY - 2 - scrollMinY) / 109.0F));
        }

        this.mc.renderEngine.bindTexture(Resources.GUI_TCU_TARGETS.getResource());

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, GuiTCUHelper.X_SIZE, GuiTCUHelper.Y_SIZE);
        this.drawTexturedModalRect(this.guiLeft + 163, this.guiTop + 19 + MathHelper.floor_float(scroll * 109.0F), 176, this.canScroll ? 0 : 6, 6, 6);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        TmrClientUtils.doGlScissor(this.guiLeft + 6, this.guiTop + 19, GuiTCUHelper.X_SIZE - 23, 115);

        int offsetY = Math.round(-this.scroll * (this.tempTargetList.size() - 11)) * (this.fontRendererObj.FONT_HEIGHT + 1);
        boolean targetListChanged = false;

        for( Entry<Class, Boolean> entry : this.tempTargetList.entrySet() ) {
            int btnTexOffY = 12 + (entry.getValue() ? 16 : 0);
            int btnMinOffY = this.guiTop + 20;
            int btnMaxOffY = this.guiTop + 20 + 110;

            if( this.doSelectAll && !entry.getValue() ) {
                this.turret.getTargetProcessor().updateEntityTarget(entry.getKey(), true);
                targetListChanged = true;
            } else if( this.doDeselectAll && entry.getValue() ) {
                this.turret.getTargetProcessor().updateEntityTarget(entry.getKey(), false);
                targetListChanged = true;
            } else if( this.doSelectMobs && !entry.getValue() && IMob.class.isAssignableFrom(entry.getKey()) ) {
                this.turret.getTargetProcessor().updateEntityTarget(entry.getKey(), true);
                targetListChanged = true;
            } else if( this.doSelectAnimals && !entry.getValue() && IAnimals.class.isAssignableFrom(entry.getKey()) && !IMob.class.isAssignableFrom(entry.getKey()) ) {
                this.turret.getTargetProcessor().updateEntityTarget(entry.getKey(), true);
                targetListChanged = true;
            } else if( this.doSelectOther && !entry.getValue() && !IMob.class.isAssignableFrom(entry.getKey()) && !IAnimals.class.isAssignableFrom(entry.getKey())) {
                this.turret.getTargetProcessor().updateEntityTarget(entry.getKey(), true);
                targetListChanged = true;
            }

            if( mouseY >= btnMinOffY && mouseY < btnMaxOffY ) {
                if( mouseX >= this.guiLeft + 10 && mouseX < this.guiLeft + 18 && mouseY >= this.guiTop + 20 + offsetY && mouseY < this.guiTop + 28 + offsetY ) {
                    btnTexOffY += 8;
                    if( isLmbDown && !this.prevIsLmbDown ) {
                        this.turret.getTargetProcessor().updateEntityTarget(entry.getKey(), !entry.getValue());
                        targetListChanged = true;
                    }
                }
            }

            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(Resources.GUI_TCU_TARGETS.getResource());
            this.drawTexturedModalRect(this.guiLeft + 10, this.guiTop + 20 + offsetY, 176, btnTexOffY, 8, 8);

            int textColor = 0xFFFFFF;

            if( IBossDisplayData.class.isAssignableFrom(entry.getKey()) ) {
                textColor = 0xFF3333;
            } else if( IMob.class.isAssignableFrom(entry.getKey()) ) {
                textColor = 0xFFAAAA;
            } else if( IAnimals.class.isAssignableFrom(entry.getKey()) ) {
                textColor = 0xAAFFAA;
            }

            this.fontRendererObj.drawString(getTranslatedEntityName(entry.getKey()), this.guiLeft + 20, this.guiTop + 20 + offsetY, textColor, false);

            offsetY += this.fontRendererObj.FONT_HEIGHT + 1;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        this.doSelectAll = false;
        this.doDeselectAll = false;
        this.doSelectMobs = false;
        this.doSelectAnimals = false;
        this.doSelectOther = false;

        if( targetListChanged ) {
            this.updateTargets();
            this.updateList();
        }

        this.prevIsLmbDown = isLmbDown;

        GuiTCUHelper.drawScreen(this);

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

    @Override
    protected void actionPerformed(GuiButton button) {
        if( button.id == this.selectAll.id ) {
            this.doSelectAll = true;
        } else if( button.id == this.deselectAll.id ) {
            this.doDeselectAll = true;
        } else if( button.id == this.selectMobs.id ) {
            this.doSelectMobs = true;
        } else if( button.id == this.selectAnimals.id ) {
            this.doSelectAnimals = true;
        } else if( button.id == this.selectOther.id ) {
            this.doSelectOther = true;
        } else if( !GuiTCUHelper.actionPerformed(button, this) ) {
            super.actionPerformed(button);
        }
    }

    private void updateTargets() {
        PacketRegistry.sendToServer(new PacketUpdateTargets(this.turret.getTargetProcessor()));
    }

    static String getTranslatedEntityName(Class<?> entityCls) {
        String namedEntry = EntityList.classToStringMapping.get(entityCls).toString();
        String name = "entity." + namedEntry + ".name";
        if( !StatCollector.canTranslate(name) ) {
            name = namedEntry;
        }

        return StatCollector.translateToLocal(name);
    }

    private static String translateBtn(String s) {
        return StatCollector.translateToLocal(String.format("gui.%s.tcu.page.targetsEntity.button.%s", TurretModRebirth.ID, s));
    }

    @Override
    public int getGuiLeft() {
        return this.guiLeft;
    }

    @Override
    public int getGuiTop() {
        return this.guiTop;
    }

    @Override
    public List getButtonList() {
        return this.buttonList;
    }

    @Override
    public EntityTurret getTurret() {
        return this.turret;
    }

    @Override
    public FontRenderer getFontRenderer() {
        return this.fontRendererObj;
    }

    @Override
    public Minecraft getMc() {
        return this.mc;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private static final class TargetComparatorClass
            implements Comparator<Class>
    {
        @Override
        public int compare(Class o1, Class o2) {
            if( IMob.class.isAssignableFrom(o1) && IAnimals.class.isAssignableFrom(o2) ) {
                return -1;
            }
            if( IAnimals.class.isAssignableFrom(o1) && !IMob.class.isAssignableFrom(o2) && !IAnimals.class.isAssignableFrom(o2) ) {
                return -1;
            }
            return 1;
        }
    }

    private static final class TargetComparatorName
            implements Comparator<Class>
    {
        @Override
        public int compare(Class o1, Class o2) {
            return getTranslatedEntityName(o2).compareTo(getTranslatedEntityName(o1));
        }
    }

    private void updateList() {
        TreeMap<Class, Boolean> btwSortMapNm = new TreeMap<>(new TargetComparatorName());
        TreeMap<Class, Boolean> btwSortMapCl = new TreeMap<>(new TargetComparatorClass());
        btwSortMapNm.putAll(this.turret.getTargetProcessor().getEntityTargets());
        btwSortMapCl.putAll(btwSortMapNm);
        this.tempTargetList = btwSortMapCl;
    }
}
