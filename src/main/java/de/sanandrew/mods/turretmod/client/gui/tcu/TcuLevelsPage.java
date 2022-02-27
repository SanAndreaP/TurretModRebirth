package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ButtonSL;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.ValueBar;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.levels.BorderedText;
import de.sanandrew.mods.turretmod.init.Lang;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.item.upgrades.leveling.LevelStorage;
import de.sanandrew.mods.turretmod.network.TurretPlayerActionPacket;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class TcuLevelsPage
        extends JsonTcuPage
{
    private ButtonSL retrieveExcess;

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
        this.retrieveExcess.setFunction(btn -> TurretModRebirth.NETWORK.sendToServer(new TurretPlayerActionPacket(this.turret, TurretPlayerActionPacket.RETRIEVE_XP)));
        this.retrieveExcess.setActive(this.canRetrieveXp());

        this.guiDefinition.getElementById("current_xp_progress").get(ValueBar.class)
                          .setPercentageSupplier(() -> MiscUtils.apply(this.getLvlStorage(), ls -> {
            double minXp = ls.getCurrentLevelMinXp();
            double maxXp = ls.getNextLevelMinXp() - minXp;

            return maxXp < 1 ? 0.0D : (ls.getXp() - minXp) / maxXp;
        }));

        this.guiDefinition.getElementById("total_xp_progress").get(ValueBar.class)
                          .setPercentageSupplier(() -> MiscUtils.apply(this.getLvlStorage(), ls -> ls.getXp() / (double) LevelStorage.maxXp));

        this.guiDefinition.getElementById("current_level").get(BorderedText.class)
                          .setTextFunc((g, ot) -> new TranslationTextComponent(Lang.TCU_TEXT.get("leveling.current_level"),
                                                                               String.format("%d", MiscUtils.apply(this.getLvlStorage(), LevelStorage::getLevel, 0))));
        this.guiDefinition.getElementById("excess_xp").get(BorderedText.class)
                          .setTextFunc((g, ot) -> new TranslationTextComponent(Lang.TCU_TEXT.get("leveling.excess_xp"),
                                                                               String.format("%d", MiscUtils.apply(this.getLvlStorage(), LevelStorage::getExcessXp, 0))));
    }

    private LevelStorage getLvlStorage() {
        return this.tcuScreen.getMenu().turret.getUpgradeProcessor().getUpgradeData(Upgrades.LEVELING.getId());
    }

    @Override
    public void tick() {
        super.tick();

        this.retrieveExcess.setActive(this.canRetrieveXp());
    }

    private boolean canRetrieveXp() {
        LevelStorage stg = this.turret.getUpgradeProcessor().getUpgradeData(Upgrades.LEVELING.getId());
        return stg != null && stg.getExcessXp() > 0;
    }
}
