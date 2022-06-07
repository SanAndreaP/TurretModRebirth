package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ButtonSL;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.item.upgrades.smarttargeting.AdvTargetSettings;
import de.sanandrew.mods.turretmod.network.SmartTargetingActionPacket;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class TcuSmartTargetingPage
        extends JsonTcuPage
{
    private ButtonSL       turretIgnore;
    private ButtonSL turretCheckSame;
    private ButtonSL turretCheckAll;

    private AdvTargetSettings settings;

    public TcuSmartTargetingPage(ContainerScreen<TcuContainer> tcuScreen) {
        super(tcuScreen);
    }

    @Override
    protected GuiDefinition buildGuiDefinition() {
        try {
            return GuiDefinition.getNewDefinition(Resources.GUI_TCU_TARGET_SMART);
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
            return null;
        }
    }

    @Override
    protected void initGd() {
        this.settings = this.turret.getUpgradeProcessor().getUpgradeData(Upgrades.SMART_TGT.getId());

        this.turretIgnore = this.guiDefinition.getElementById("turretIgnore").get(ButtonSL.class);
        this.turretIgnore.setFunction(b -> TurretModRebirth.NETWORK.sendToServer(new SmartTargetingActionPacket(this.turret, AdvTargetSettings.TurretAwareness.UNAWARE)));
        this.turretCheckSame = this.guiDefinition.getElementById("turretCheckSame").get(ButtonSL.class);
        this.turretCheckSame.setFunction(b -> TurretModRebirth.NETWORK.sendToServer(new SmartTargetingActionPacket(this.turret, AdvTargetSettings.TurretAwareness.SAME_TYPE)));
        this.turretCheckAll = this.guiDefinition.getElementById("turretCheckAll").get(ButtonSL.class);
        this.turretCheckAll.setFunction(b -> TurretModRebirth.NETWORK.sendToServer(new SmartTargetingActionPacket(this.turret, AdvTargetSettings.TurretAwareness.ALL_TYPES)));

        this.updateButtons();
    }

    @Override
    public void tick() {
        super.tick();

        this.updateButtons();
    }

    private void updateButtons() {
        AdvTargetSettings.TurretAwareness turretAwareness = this.settings.getTurretAwareness();
        this.turretIgnore.setActive(turretAwareness != AdvTargetSettings.TurretAwareness.UNAWARE);
        this.turretCheckSame.setActive(turretAwareness != AdvTargetSettings.TurretAwareness.SAME_TYPE);
        this.turretCheckAll.setActive(turretAwareness != AdvTargetSettings.TurretAwareness.ALL_TYPES);
    }
}
