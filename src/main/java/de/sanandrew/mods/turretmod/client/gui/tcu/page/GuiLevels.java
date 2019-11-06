/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.registry.upgrades.leveling.LevelStorage;
import de.sanandrew.mods.turretmod.registry.Lang;
import de.sanandrew.mods.turretmod.registry.Resources;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.mutable.MutableInt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiLevels
//        implements IGuiTCU
{
//    private int currLvl = 0;
//    private int minXp   = 0;
//    private int maxXp   = 0;
//    private int currXp  = 0;
//
//    private GuiDefinition guiDef;
//
//    {
//        try {
//            guiDef = GuiDefinition.getNewDefinition(Resources.GUI_STRUCT_TCU_LEVELS.resource);
//        } catch( IOException e ) {
//            e.printStackTrace();
//        }
//    }
//
//    private Map<String, ModifierInfo> currModifiers = new HashMap<>();
//
//    @Override
//    public void initialize(IGuiTcuInst<?> gui) {
//        IUpgradeProcessor processor = gui.getTurretInst().getUpgradeProcessor();
//        if( processor.hasUpgrade(Upgrades.LEVELING) ) {
//            LevelStorage stg = processor.getUpgradeInstance(Upgrades.LEVELING.getId());
//            if( stg != null ) {
//                this.currLvl = stg.getLevel();
//                this.minXp = stg.getCurrentLevelMinXp();
//                this.maxXp = stg.getNextLevelMinXp() - 1;
//                this.currXp = stg.getXp();
//            }
//        }
//    }
//
//    @Override
//    public void updateScreen(IGuiTcuInst<?> gui) {
//        this.initialize(gui);
//
//        ITurretInst turretInst = gui.getTurretInst();
//        AbstractAttributeMap attrMap = turretInst.get().getAttributeMap();
//        LevelStorage lvlStg = turretInst.getUpgradeProcessor().getUpgradeInstance(Upgrades.LEVELING.getId());
//
//        this.currModifiers.clear();
//        if( lvlStg != null ) {
//            Map<String, Map<Integer, List<Double>>> modMap = new HashMap<>();
//            lvlStg.fetchCurrentStages().forEach(s -> Arrays.stream(s.modifiers).forEach(m -> modMap.computeIfAbsent(m.attributeName, k -> new HashMap<>())
//                                                                                                   .computeIfAbsent(m.modifier.getOperation(), k -> new ArrayList<>())
//                                                                                                   .add(m.modifier.getAmount())));
//            modMap.forEach((attrName, mods) -> {
//                IAttributeInstance attrInst = attrMap.getAttributeInstanceByName(attrName);
//                if( attrInst != null ) {
//                    ModifierInfo modInfo = this.currModifiers.computeIfAbsent(attrName, a -> new ModifierInfo(attrInst.getBaseValue()));
//
//                    mods.getOrDefault(0, Collections.emptyList()).forEach(m -> modInfo.modValue += m);
//                    final double modValFn = modInfo.modValue;
//                    mods.getOrDefault(1, Collections.emptyList()).forEach(m -> modInfo.modValue += modValFn * m);
//                    mods.getOrDefault(2, Collections.emptyList()).forEach(m -> modInfo.modValue += modInfo.modValue * m);
//                }
//            });
//        }
//    }
//
//    @Override
//    public void drawBackground(IGuiTcuInst<?> gui, float partialTicks, int mouseX, int mouseY) {
//        FontRenderer fontRenderer = gui.getFontRenderer();
//        GuiScreen guiScreen = gui.getGui();
//        int posX = gui.getPosX();
//        int posY = gui.getPosY();
//
//        guiScreen.mc.renderEngine.bindTexture(Resources.GUI_TCU_INFO.resource);
//
//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        guiScreen.drawTexturedModalRect(posX, posY, 0, 0, gui.getWidth(), gui.getHeight());
//        guiScreen.drawTexturedModalRect(posX + (gui.getWidth() - 50) / 2, posY + 39, 176, 0, 50, 50);
//
//        fontRenderer.drawString(String.format("Level: %d", this.currLvl), posX + 10, posY + 92, 0xFF000000, false);
//        fontRenderer.drawString(String.format("Current XP: %d", this.currXp), posX + 10, posY + 102, 0xFF000000, false);
//        fontRenderer.drawString(String.format("Min XP: %d", this.minXp), posX + 10, posY + 112, 0xFF000000, false);
//        fontRenderer.drawString(String.format("Max XP: %d", this.maxXp), posX + 10, posY + 122, 0xFF000000, false);
//
//        MutableInt posYMod = new MutableInt();
//        this.currModifiers.forEach((attributeName, modInfo) -> {
//            fontRenderer.drawString(LangUtils.translate(Lang.ATTRIBUTE.get(attributeName)), posX + 15, posY + 132 + posYMod.getAndAdd(10), 0xFFFFA0FF, false);
//            fontRenderer.drawString(String.format("%+.0f %%", (modInfo.modValue / modInfo.baseValue - 1.0D) * 100.0D), posX + 25, posY + 132 + posYMod.getAndAdd(10), 0xFFFFA0FF, false);
//        });
//
//        GuiUtils.drawGradientRect(posX + 10, posY + 132 + posYMod.getValue(), 100, 10, 0xFF000000, 0xFF000000, true);
//        float v = (this.currXp - this.minXp) / (float) (this.maxXp - this.minXp) * 100.0F;
//        GuiUtils.drawGradientRect(posX + 10, posY + 132 + posYMod.getValue(), MathHelper.floor(v), 10, 0xFF00A000, 0xFF00FF00, false);
//    }
//
//    @Override
//    public GuiDefinition getDefinition() {
//        return null;
//    }
//
//    public static boolean showTab(IGuiTcuInst<?> gui) {
//        return gui.hasPermision() && gui.getTurretInst().getUpgradeProcessor().hasUpgrade(Upgrades.LEVELING);
//    }
//
//    private static final class ModifierInfo
//    {
//        private final double baseValue;
//        private double modValue;
//
//        private ModifierInfo(double baseValue) {
//            this.baseValue = baseValue;
//            this.modValue = baseValue;
//        }
//    }
}
