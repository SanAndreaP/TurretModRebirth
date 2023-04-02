/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.gui.tcu;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ButtonSL;
import de.sanandrew.mods.sanlib.lib.client.gui.element.TextField;
import dev.sanandrea.mods.turretmod.api.Resources;
import dev.sanandrea.mods.turretmod.api.TmrConstants;
import dev.sanandrea.mods.turretmod.api.tcu.TcuContainer;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.client.gui.element.tcu.targets.TargetList;
import dev.sanandrea.mods.turretmod.network.TurretPlayerActionPacket;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.EntityClassification;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public abstract class TcuTargetPage
        extends JsonTcuPage
{
    TextField  search;

    TcuTargetPage(ContainerScreen<TcuContainer> tcuScreen) {
        super(tcuScreen);
    }

    @Override
    protected void initGd() {
        this.search = this.guiDefinition.getElementById("search").get(TextField.class);

        final TargetList targets = this.guiDefinition.getElementById("targetList").get(TargetList.class);
        this.search.setResponder(s -> targets.filter(this, s));
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        return this.search.mouseClicked(this, mx, my, btn) || super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.search.keyPressed(this, keyCode, scanCode, modifiers) || this.search.canConsumeInput() || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        return this.search.charTyped(this, typedChar, keyCode) || this.search.canConsumeInput() || super.charTyped(typedChar, keyCode);
    }

    public ITurretEntity getTurret() {
        return this.turret;
    }

    public static class Creatures
            extends TcuTargetPage
    {
        public Creatures(ContainerScreen<TcuContainer> tcuScreen) {
            super(tcuScreen);
        }

        @Override
        protected void initGd() {
            super.initGd();

            this.guiDefinition.getElementById("denylist").get(ButtonSL.class).setFunction(btn -> TurretPlayerActionPacket.setFilterType(this.turret, false, true));
            this.guiDefinition.getElementById("allowlist").get(ButtonSL.class).setFunction(btn -> TurretPlayerActionPacket.setFilterType(this.turret, true, true));
            this.guiDefinition.getElementById("deselectAll").get(ButtonSL.class).setFunction(btn -> TurretPlayerActionPacket.setCreatureTarget(this.turret, false, null, null));
            this.guiDefinition.getElementById("selectAll").get(ButtonSL.class).setFunction(btn -> TurretPlayerActionPacket.setCreatureTarget(this.turret, true, null, null));
            this.guiDefinition.getElementById("selectMobs").get(ButtonSL.class).setFunction(btn -> TurretPlayerActionPacket.setCreatureTarget(this.turret, true, null, EntityClassification.MONSTER));
            this.guiDefinition.getElementById("selectAnimals").get(ButtonSL.class).setFunction(btn -> TurretPlayerActionPacket.setCreatureTarget(this.turret, true, null, EntityClassification.CREATURE));
            this.guiDefinition.getElementById("selectOther").get(ButtonSL.class).setFunction(btn -> TurretPlayerActionPacket.setCreatureTarget(this.turret, true, null, EntityClassification.MISC));
        }

        @Override
        public void tick() {
            super.tick();

            boolean isDenyList = this.turret.getTargetProcessor().isEntityDenyList();
            this.guiDefinition.getElementById("denylist").get(ButtonSL.class).setVisible(isDenyList);
            this.guiDefinition.getElementById("allowlist").get(ButtonSL.class).setVisible(!isDenyList);
        }

        @Override
        public GuiDefinition buildGuiDefinition() {
            try {
                return GuiDefinition.getNewDefinition(Resources.GUI_TCU_TARGET_CREATURES);
            } catch( IOException e ) {
                TmrConstants.LOG.log(Level.ERROR, e);
                return null;
            }
        }
    }

    public static class Players
            extends TcuTargetPage
    {
        public Players(ContainerScreen<TcuContainer> tcuScreen) {
            super(tcuScreen);
        }

        @Override
        protected void initGd() {
            super.initGd();

            this.guiDefinition.getElementById("denylist").get(ButtonSL.class).setFunction(btn -> TurretPlayerActionPacket.setFilterType(this.turret, false, false));
            this.guiDefinition.getElementById("allowlist").get(ButtonSL.class).setFunction(btn -> TurretPlayerActionPacket.setFilterType(this.turret, true, false));
            this.guiDefinition.getElementById("deselectAll").get(ButtonSL.class).setFunction(btn -> TurretPlayerActionPacket.setPlayerTarget(this.turret, false, null));
            this.guiDefinition.getElementById("selectAll").get(ButtonSL.class).setFunction(btn -> TurretPlayerActionPacket.setPlayerTarget(this.turret, true, null));
        }

        @Override
        public void tick() {
            super.tick();

            boolean isDenyList = this.turret.getTargetProcessor().isPlayerDenyList();
            this.guiDefinition.getElementById("denylist").get(ButtonSL.class).setVisible(isDenyList);
            this.guiDefinition.getElementById("allowlist").get(ButtonSL.class).setVisible(!isDenyList);
        }

        @Override
        public GuiDefinition buildGuiDefinition() {
            try {
                return GuiDefinition.getNewDefinition(Resources.GUI_TCU_TARGET_PLAYERS);
            } catch( IOException e ) {
                TmrConstants.LOG.log(Level.ERROR, e);
                return null;
            }
        }
    }
}
