/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.tinfo;

import de.sanandrew.mods.turretmod.entity.turret.EntityTurret;
import de.sanandrew.mods.turretmod.entity.turret.EntityTurretCrossbow;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.ItemTurret;
import de.sanandrew.mods.turretmod.registry.turret.TurretInfo;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import net.minecraft.util.StatCollector;

public class TurretInfoEntryTurret
        extends TurretInfoEntry
{
    private int drawHeight;
    private String unlocDesc;

    public TurretInfoEntryTurret(Class<? extends EntityTurret> turret) {
        this(TurretRegistry.INSTANCE.getInfo(turret));
    }

    private TurretInfoEntryTurret(TurretInfo info) {
        super(ItemRegistry.turret.getTurretItem(1, info), String.format("entity.%s.%s.name", TurretModRebirth.ID, info.getName()));
        this.unlocDesc = String.format("entity.%s.%s.desc", TurretModRebirth.ID, info.getName());
    }

    @Override
    public void drawPage(GuiTurretInfo gui, int mouseX, int mouseY, float partTicks) {
        String text = StatCollector.translateToLocal(this.unlocDesc);
        this.drawHeight = gui.mc.fontRenderer.splitStringWidth(text, MAX_ENTRY_WIDTH - 4);
        gui.mc.fontRenderer.drawSplitString(text, 2, 2, MAX_ENTRY_WIDTH - 4, 0xFF000000);
    }

    @Override
    public int getPageHeight() {
        return this.drawHeight;
    }
}
