/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tcu;

import com.google.common.collect.Maps;
import de.sanandrew.core.manpack.util.client.helpers.GuiUtils;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.client.gui.control.GuiSlimButton;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretBase;
import de.sanandrew.mods.turretmod.network.packet.PacketSendMultiTargetFlag;
import de.sanandrew.mods.turretmod.network.packet.PacketSendTargetFlag;
import de.sanandrew.mods.turretmod.util.Textures;
import de.sanandrew.mods.turretmod.util.TurretMod;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class GuiTcuTargets
        extends AGuiTurretControlUnit
{
    private Map<Class<? extends EntityLiving>, Boolean> tempTargetList = new HashMap<>();

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

    public GuiTcuTargets(EntityTurretBase turret) {
        super(turret);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();

        int center = this.guiLeft + (this.xSize - 150) / 2;
        this.buttonList.add(this.selectAll = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 138, 150, translateBtn("selectAll")));
        this.buttonList.add(this.deselectAll = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 151, 150, translateBtn("deselectAll")));
        this.buttonList.add(this.selectMobs = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 164, 150, translateBtn("selectMobs")));
        this.buttonList.add(this.selectAnimals = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 177, 150, translateBtn("selectAnimals")));
        this.buttonList.add(this.selectOther = new GuiSlimButton(this.buttonList.size(), center, this.guiTop + 190, 150, translateBtn("selectOther")));

        this.pageTargets.enabled = false;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        TreeMap<Class<? extends EntityLiving>, Boolean> btwSortMapNm = new TreeMap<>(new TargetComparatorName());
        TreeMap<Class<? extends EntityLiving>, Boolean> btwSortMapCl = new TreeMap<>(new TargetComparatorClass());
        btwSortMapNm.putAll(this.myTurret.getTargetHandler().getTargetList());
        btwSortMapCl.putAll(btwSortMapNm);
        this.tempTargetList = btwSortMapCl;

        this.canScroll = this.tempTargetList.size() >= 11;
        this.scrollAmount = Math.max(0.0F, 1.0F / (this.tempTargetList.size() - 11.0F));
    }

    @Override
    public void drawScreenPostBkg(int mouseX, int mouseY, float partTicks) {
        boolean isLmbDown = Mouse.isButtonDown(0);
        int scrollMinX = this.guiLeft + 163;
        int scrollMaxX = this.guiLeft + 163 + 9;
        int scrollMinY = this.guiTop + 19;
        int scrollMaxY = this.guiTop + 134;

        if( !this.isScrolling && this.canScroll && isLmbDown && mouseX >= scrollMinX && mouseX < scrollMaxX && mouseY > scrollMinY && mouseY < scrollMaxY ) {
            this.isScrolling = true;
        } else if( !isLmbDown ) {
            this.isScrolling = false;
        }

        if( this.isScrolling ) {
            this.scroll = Math.max(0.0F, Math.min(1.0F, (mouseY - 2 - scrollMinY) / 109.0F));
        }

        this.mc.renderEngine.bindTexture(Textures.GUI_TCU_TARGETS.getResource());

        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(this.guiLeft + 163, this.guiTop + 19 + MathHelper.floor_float(scroll * 109.0F), 176, this.canScroll ? 0 : 6, 6, 6);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GuiUtils.doGlScissor(this.guiLeft + 6, this.guiTop + 19, this.xSize - 23, 115);

        int offsetY = Math.round(-this.scroll * (this.tempTargetList.size() - 11)) * (this.fontRendererObj.FONT_HEIGHT + 1);
        Map<Class<? extends EntityLiving>, Boolean> newTargetStgs = Maps.newHashMap();
        for( Entry<Class<? extends EntityLiving>, Boolean> entry : this.tempTargetList.entrySet() ) {
            int btnTexOffY = 12 + (entry.getValue() ? 16 : 0);
            int btnMinOffY = this.guiTop + 20;
            int btnMaxOffY = this.guiTop + 20 + 110;

            if( this.doSelectAll && !entry.getValue() ) {
                newTargetStgs.put(entry.getKey(), true);
            } else if( this.doDeselectAll && entry.getValue() ) {
                newTargetStgs.put(entry.getKey(), false);
            } else if( this.doSelectMobs && !entry.getValue() && IMob.class.isAssignableFrom(entry.getKey()) ) {
                newTargetStgs.put(entry.getKey(), true);
            } else if( this.doSelectAnimals && !entry.getValue() && IAnimals.class.isAssignableFrom(entry.getKey()) && !IMob.class.isAssignableFrom(entry.getKey()) ) {
                newTargetStgs.put(entry.getKey(), true);
            } else if( this.doSelectOther && !entry.getValue() && !IMob.class.isAssignableFrom(entry.getKey()) && !IAnimals.class.isAssignableFrom(entry.getKey())) {
                newTargetStgs.put(entry.getKey(), true);
            }

            if( mouseY >= btnMinOffY && mouseY < btnMaxOffY ) {
                if( mouseX >= this.guiLeft + 10 && mouseX < this.guiLeft + 18 && mouseY >= this.guiTop + 20 + offsetY && mouseY < this.guiTop + 28 + offsetY ) {
                    btnTexOffY += 8;
                    if( isLmbDown && !this.prevIsLmbDown ) {
                        this.applyTarget(entry.getKey(), !entry.getValue());
                    }
                }
            }

            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            this.mc.renderEngine.bindTexture(Textures.GUI_TCU_TARGETS.getResource());
            this.drawTexturedModalRect(this.guiLeft + 10, this.guiTop + 20 + offsetY, 176, btnTexOffY, 8, 8);

            int textColor = 0xFFFFFF;
            if( IMob.class.isAssignableFrom(entry.getKey()) ) {
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

        if( newTargetStgs.size() > 0 ) {
            applyMultiTarget(newTargetStgs);
        }

        this.prevIsLmbDown = isLmbDown;
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
        if( button == this.selectAll ) {
            this.doSelectAll = true;
        } else if( button == this.deselectAll ) {
            this.doDeselectAll = true;
        } else if( button == this.selectMobs ) {
            this.doSelectMobs = true;
        } else if( button == this.selectAnimals ) {
            this.doSelectAnimals = true;
        } else if( button == this.selectOther ) {
            this.doSelectOther = true;
        } else {
            super.actionPerformed(button);
        }
    }

    private void applyTarget(Class<? extends EntityLiving> entityCls, boolean active) {
        PacketSendTargetFlag.sendToServer(this.myTurret, entityCls, active);
    }

    private void applyMultiTarget(Map<Class<? extends EntityLiving>, Boolean> targets) {
        PacketSendMultiTargetFlag.sendToServer(this.myTurret, targets);
    }

    static String getTranslatedEntityName(Class<?> entityCls) {
        String namedEntry = EntityList.classToStringMapping.get(entityCls).toString();
        String name = "entity." + namedEntry + ".name";
        if( !StatCollector.canTranslate(name) ) {
            name = namedEntry;
        }

        return SAPUtils.translate(name);
    }

    private static String translateBtn(String s) {
        return SAPUtils.translatePreFormat("gui.%s.tcu.page.targets.button.%s", TurretMod.MOD_ID, s);
    }

    private static final class TargetComparatorClass
            implements Comparator<Class<? extends EntityLiving>>
    {
        @Override
        public int compare(Class<? extends EntityLiving> o1, Class<? extends EntityLiving> o2) {
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
            implements Comparator<Class<? extends EntityLiving>>
    {
        @Override
        public int compare(Class<? extends EntityLiving> o1, Class<? extends EntityLiving> o2) {
            return getTranslatedEntityName(o2).compareTo(getTranslatedEntityName(o1));
        }
    }
}
