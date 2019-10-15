/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.registry.upgrades.leveling.LevelStorage;
import de.sanandrew.mods.turretmod.registry.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.registry.upgrades.leveling.Stage;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class GuiLevels
        implements IGuiTCU
{
    private int currLvl = 0;
    private int minXp = 0;
    private int maxXp = 0;
    private int currXp = 0;

    private Map<Stage, List<Stage.Modifier>> currStages = new HashMap<>();

    @Override
    public void initialize(IGuiTcuInst<?> gui) {
        IUpgradeProcessor processor = gui.getTurretInst().getUpgradeProcessor();
        if( processor.hasUpgrade(Upgrades.LEVELING) ) {
            LevelStorage stg = processor.getUpgradeInstance(Upgrades.LEVELING.getId());
            if( stg != null ) {
                this.currLvl = stg.getLevel();
                this.minXp = stg.getCurrentLevelMinXp();
                this.maxXp = stg.getNextLevelMinXp() - 1;
                this.currXp = stg.getXp();
            }
        }
    }

    @Override
    public void updateScreen(IGuiTcuInst<?> gui) {
        this.initialize(gui);

        ITurretInst turretInst = gui.getTurretInst();
        this.currStages.clear();
        LevelStorage lvlStg = turretInst.getUpgradeProcessor().getUpgradeInstance(Upgrades.LEVELING.getId());
        if( lvlStg != null ) {
            lvlStg.fetchCurrentStages().forEach(s -> {
                this.currStages.computeIfAbsent(s, stage -> new ArrayList<>()).addAll(Arrays.asList(s.modifiers));
            });
        }
    }

    @Override
    public void drawBackground(IGuiTcuInst<?> gui, float partialTicks, int mouseX, int mouseY) {
        FontRenderer fontRenderer = gui.getFontRenderer();
        GuiScreen guiScreen = gui.getGui();
        int posX = gui.getPosX();
        int posY = gui.getPosY();

        guiScreen.mc.renderEngine.bindTexture(Resources.GUI_TCU_INFO.resource);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        guiScreen.drawTexturedModalRect(posX, posY, 0, 0, gui.getWidth(), gui.getHeight());
        guiScreen.drawTexturedModalRect(posX + (gui.getWidth() - 50) / 2, posY + 39, 176, 0, 50, 50);

        fontRenderer.drawString(String.format("Level: %d", this.currLvl), posX + 10, posY + 92, 0xFF000000, false);
        fontRenderer.drawString(String.format("Current XP: %d", this.currXp), posX + 10, posY + 102, 0xFF000000, false);
        fontRenderer.drawString(String.format("Min XP: %d", this.minXp), posX + 10, posY + 112, 0xFF000000, false);
        fontRenderer.drawString(String.format("Max XP: %d", this.maxXp), posX + 10, posY + 122, 0xFF000000, false);

        AtomicInteger posYMod = new AtomicInteger();
        this.currStages.forEach((k, v) -> {
            v.forEach(modifier -> {
                fontRenderer.drawString(LangUtils.translate(Lang.ATTRIBUTE.get(modifier.attributeName)), posX + 15, posY + 132 + posYMod.getAndIncrement(), 0xFFFFA0FF, false);
            });
        });

        GuiUtils.drawGradientRect(posX + 10, posY + 150, 100, 10, 0xFF000000, 0xFF000000, true);
        float v = (this.currXp - this.minXp) / (float) (this.maxXp - this.minXp) * 100.0F;
        GuiUtils.drawGradientRect(posX + 10, posY + 150, MathHelper.floor(v), 10, 0xFF00A000, 0xFF00FF00, false);
    }

    public static boolean showTab(IGuiTcuInst<?> gui) {
        return gui.hasPermision() && gui.getTurretInst().getUpgradeProcessor().hasUpgrade(Upgrades.LEVELING);
    }
}
