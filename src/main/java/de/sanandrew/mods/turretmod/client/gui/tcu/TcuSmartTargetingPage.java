/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.gui.tcu;

import com.google.common.base.Strings;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ButtonSL;
import de.sanandrew.mods.sanlib.lib.client.gui.element.TextField;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.smarttargeting.AdvTargetSettings;
import de.sanandrew.mods.turretmod.network.SmartTargetingActionPacket;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.function.Function;

public class TcuSmartTargetingPage
        extends JsonTcuPage
{
    private ButtonSL turretIgnore;
    private ButtonSL turretCheckSame;
    private ButtonSL turretCheckAll;

    private ButtonSL tamedAll;
    private ButtonSL tamedPlayers;
    private ButtonSL tamedNone;

    private ButtonSL childAdult;
    private ButtonSL childOnly;
    private ButtonSL adultOnly;

    private ButtonSL noCount;
    private ButtonSL countGlobalLess;
    private ButtonSL countGlobalMore;
    private ButtonSL countIndivLess;
    private ButtonSL countIndivMore;

    private ButtonSL priorityFirst;
    private ButtonSL priorityClose;
    private ButtonSL priorityHighHealth;
    private ButtonSL priorityLowHealth;
    private ButtonSL priorityRandom;

    private TextField countAmount;

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

        this.turretIgnore = this.grabButton("turretIgnore", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.TurretAwareness.UNAWARE));
        this.turretCheckSame = this.grabButton("turretCheckSame", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.TurretAwareness.SAME_TYPE));
        this.turretCheckAll = this.grabButton("turretCheckAll", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.TurretAwareness.ALL_TYPES));

        this.tamedAll = this.grabButton("tamedAll", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.TamedAwareness.UNAWARE));
        this.tamedPlayers = this.grabButton("tamedPlayers", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.TamedAwareness.TARGETED_PLAYERS));
        this.tamedNone = this.grabButton("tamedNone", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.TamedAwareness.IGNORE_ALL_TAMED));

        this.childAdult = this.grabButton("childAdult", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.ChildAwareness.UNAWARE));
        this.childOnly = this.grabButton("childOnly", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.ChildAwareness.CHILDREN_ONLY));
        this.adultOnly = this.grabButton("adultOnly", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.ChildAwareness.ADULTS_ONLY));

        this.noCount = this.grabButton("noCount", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.CountAwareness.NO_COUNT));
        this.countGlobalLess = this.grabButton("countGlobalLess", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.CountAwareness.BELOW_GLOBAL));
        this.countGlobalMore = this.grabButton("countGlobalMore", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.CountAwareness.ABOVE_GLOBAL));
        this.countIndivLess = this.grabButton("countIndivLess", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.CountAwareness.BELOW_INDIVIDUAL));
        this.countIndivMore = this.grabButton("countIndivMore", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.CountAwareness.ABOVE_INDIVIDUAL));

        this.priorityFirst = this.grabButton("prioFirst", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.PriorityAwareness.FIRST_DETECTED));
        this.priorityClose = this.grabButton("prioClose", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.PriorityAwareness.CLOSE));
        this.priorityHighHealth = this.grabButton("prioHiHealth", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.PriorityAwareness.HIGH_HEALTH));
        this.priorityLowHealth = this.grabButton("prioLoHealth", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.PriorityAwareness.LOW_HEALTH));
        this.priorityRandom = this.grabButton("prioRandom", t -> new SmartTargetingActionPacket(t, AdvTargetSettings.PriorityAwareness.RANDOM));

        this.countAmount = this.guiDefinition.getElementById("countAmount").get(TextField.class);
        this.countAmount.setValidator(s -> Strings.isNullOrEmpty(s) || Boolean.TRUE.equals(MiscUtils.apply(MiscUtils.getInteger(s), i -> 0 <= i && i <= 256)));
        this.countAmount.setResponder(s -> MiscUtils.accept(MiscUtils.getInteger(s),
                                                            i -> TurretModRebirth.NETWORK.sendToServer(new SmartTargetingActionPacket(this.turret, i.shortValue()))));
        this.countAmount.setText(String.format("%d", this.settings.getCountEntities()));

        this.updateElements();
    }

    private ButtonSL grabButton(String name, Function<ITurretEntity, SmartTargetingActionPacket> packetFactory) {
        ButtonSL btn = this.guiDefinition.getElementById(name).get(ButtonSL.class);
        btn.setFunction(b -> TurretModRebirth.NETWORK.sendToServer(packetFactory.apply(this.turret)));
        return btn;
    }

    @Override
    public void tick() {
        super.tick();

        this.updateElements();
    }

    private void updateElements() {
        AdvTargetSettings.TurretAwareness turretAwareness = this.settings.getTurretAwareness();
        this.turretIgnore.setActive(turretAwareness != AdvTargetSettings.TurretAwareness.UNAWARE);
        this.turretCheckSame.setActive(turretAwareness != AdvTargetSettings.TurretAwareness.SAME_TYPE);
        this.turretCheckAll.setActive(turretAwareness != AdvTargetSettings.TurretAwareness.ALL_TYPES);

        AdvTargetSettings.TamedAwareness tamedAwareness = this.settings.getTamedAwareness();
        this.tamedAll.setActive(tamedAwareness != AdvTargetSettings.TamedAwareness.UNAWARE);
        this.tamedPlayers.setActive(tamedAwareness != AdvTargetSettings.TamedAwareness.TARGETED_PLAYERS);
        this.tamedNone.setActive(tamedAwareness != AdvTargetSettings.TamedAwareness.IGNORE_ALL_TAMED);

        AdvTargetSettings.ChildAwareness childAwareness = this.settings.getChildAwareness();
        this.childAdult.setActive(childAwareness != AdvTargetSettings.ChildAwareness.UNAWARE);
        this.childOnly.setActive(childAwareness != AdvTargetSettings.ChildAwareness.CHILDREN_ONLY);
        this.adultOnly.setActive(childAwareness != AdvTargetSettings.ChildAwareness.ADULTS_ONLY);

        AdvTargetSettings.CountAwareness countAwareness = this.settings.getCountAwareness();
        this.noCount.setActive(countAwareness != AdvTargetSettings.CountAwareness.NO_COUNT);
        this.countGlobalLess.setActive(countAwareness != AdvTargetSettings.CountAwareness.BELOW_GLOBAL);
        this.countGlobalMore.setActive(countAwareness != AdvTargetSettings.CountAwareness.ABOVE_GLOBAL);
        this.countIndivLess.setActive(countAwareness != AdvTargetSettings.CountAwareness.BELOW_INDIVIDUAL);
        this.countIndivMore.setActive(countAwareness != AdvTargetSettings.CountAwareness.ABOVE_INDIVIDUAL);

        this.countAmount.setEditable(countAwareness != AdvTargetSettings.CountAwareness.NO_COUNT);

        AdvTargetSettings.PriorityAwareness priorityAwareness = this.settings.getPriorityAwareness();
        this.priorityFirst.setActive(priorityAwareness != AdvTargetSettings.PriorityAwareness.FIRST_DETECTED);
        this.priorityClose.setActive(priorityAwareness != AdvTargetSettings.PriorityAwareness.CLOSE);
        this.priorityHighHealth.setActive(priorityAwareness != AdvTargetSettings.PriorityAwareness.HIGH_HEALTH);
        this.priorityLowHealth.setActive(priorityAwareness != AdvTargetSettings.PriorityAwareness.LOW_HEALTH);
        this.priorityRandom.setActive(priorityAwareness != AdvTargetSettings.PriorityAwareness.RANDOM);
    }
}
