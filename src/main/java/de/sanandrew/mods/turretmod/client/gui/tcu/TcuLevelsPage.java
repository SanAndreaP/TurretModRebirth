/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ButtonSL;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ProgressBar;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.init.Lang;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.leveling.LevelData;
import de.sanandrew.mods.turretmod.network.TurretPlayerActionPacket;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class TcuLevelsPage
        extends JsonTcuPage
{
    private ButtonSL retrieveExcess;
    private GuiElementInst showInfoBtn;
    private GuiElementInst hideInfoBtn;
    private GuiElementInst infoBg;
    private GuiElementInst infoArea;

    private boolean showInfo = false;

    public TcuLevelsPage(ContainerScreen<TcuContainer> tcuScreen) {
        super(tcuScreen);
    }

    @Override
    protected GuiDefinition buildGuiDefinition() {
        try {
            return GuiDefinition.getNewDefinition(Resources.GUI_TCU_LEVELS);
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
            return null;
        }
    }

    @Override
    protected void initGd() {
        this.retrieveExcess = this.guiDefinition.getElementById("retrieveExcess").get(ButtonSL.class);
        this.retrieveExcess.setFunction(btn -> TurretPlayerActionPacket.retrieveXp(turret, mc.player));

        this.showInfoBtn = this.guiDefinition.getElementById("showInfo");
        this.showInfoBtn.get(ButtonSL.class).setFunction(btn -> this.showInfo = true);
        this.hideInfoBtn = this.guiDefinition.getElementById("hideInfo");
        this.hideInfoBtn.get(ButtonSL.class).setFunction(btn -> this.showInfo = false);
        this.infoBg = this.guiDefinition.getElementById("infoBackground");
        this.infoArea = this.guiDefinition.getElementById("level_modifiers_info");

        this.updateElements();

        this.guiDefinition.getElementById("current_xp_progress").get(ProgressBar.class)
                          .setPercentFunc(p -> MiscUtils.apply(this.getLvlStorage(), ls -> {
            double minXp = ls.getCurrentLevelMinXp();
            double maxXp = ls.getNextLevelMinXp() - minXp;

            return maxXp < 1 ? 0.0D : (ls.getXp() - minXp) / maxXp;
        }));

        this.guiDefinition.getElementById("total_xp_progress").get(ProgressBar.class)
                          .setPercentFunc(p -> MiscUtils.apply(this.getLvlStorage(), ls -> ls.getXp() / (double) LevelData.maxXp));

        this.guiDefinition.getElementById("level_text").get(Text.class)
                          .setTextFunc((g, ot) -> {
                              LevelData lvlStg = this.getLvlStorage();
                              if( lvlStg != null ) {
                                  return new TranslationTextComponent(Lang.TCU_TEXT.get("leveling.current_level"), String.format("%d", lvlStg.getLevel()));
                              }

                              return StringTextComponent.EMPTY;
                          });
        this.guiDefinition.getElementById("exc_xp_text").get(Text.class)
                          .setTextFunc((g, ot) -> {
                              LevelData lvlStg = this.getLvlStorage();
                              if( lvlStg != null ) {
                                  int excXp = lvlStg.getExcessXp();
                                  if( excXp > 0 ) {
                                      return new TranslationTextComponent(Lang.TCU_TEXT.get("leveling.excess_xp"), String.format("%d", excXp));
                                  }
                              }

                              return StringTextComponent.EMPTY;
                          });
    }

    private LevelData getLvlStorage() {
        return this.tcuScreen.getMenu().turret.getUpgradeProcessor().getUpgradeData(Upgrades.LEVELING.getId());
    }

    @Override
    public void tick() {
        super.tick();

        this.updateElements();
    }

    private void updateElements() {
        this.retrieveExcess.setActive(this.canRetrieveXp());
        this.showInfoBtn.setVisible(!this.showInfo);
        this.hideInfoBtn.setVisible(this.showInfo);
        this.infoBg.setVisible(this.showInfo);
        this.infoArea.setVisible(this.showInfo);
    }

    private boolean canRetrieveXp() {
        LevelData stg = this.turret.getUpgradeProcessor().getUpgradeData(Upgrades.LEVELING.getId());
        return stg != null && stg.getExcessXp() > 0;
    }

    public ITurretEntity getTurret() {
        return this.turret;
    }
}
