package de.sanandrew.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ButtonSL;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.item.upgrades.leveling.LevelStorage;
import de.sanandrew.mods.turretmod.network.TurretPlayerActionPacket;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
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
