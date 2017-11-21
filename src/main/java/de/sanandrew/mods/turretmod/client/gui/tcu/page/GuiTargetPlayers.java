/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.util.PlayerList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Pattern;

@SideOnly(Side.CLIENT)
public class GuiTargetPlayers
        extends GuiTargets<UUID>
{
    @Override
    protected SortedMap<UUID, Boolean> getTargetList(ITurretInst turretInst) {
        TreeMap<UUID, Boolean> btwSortMapNm = new TreeMap<>(new TargetComparator());
        btwSortMapNm.putAll(PlayerList.INSTANCE.getDefaultPlayerList());
        btwSortMapNm.putAll(turretInst.getTargetProcessor().getPlayerTargets());
        return btwSortMapNm;
    }

    @Override
    protected void updateEntry(ITurretInst turretInst, UUID type, boolean active) {
        turretInst.getTargetProcessor().updatePlayerTarget(type, active);
    }

    @Override
    protected void drawEntry(IGuiTcuInst<?> gui, UUID type, int posX, int posY) {
        int textColor = 0xFFFFFF;
        gui.getFontRenderer().drawString(PlayerList.INSTANCE.getPlayerName(type), posX, posY, textColor, false);
    }

    @Override
    protected boolean isEntryVisible(UUID type, String srcText) {
        return PlayerList.INSTANCE.getPlayerName(type).toUpperCase().contains(srcText.toUpperCase());
    }

    private static final class TargetComparator
            implements Comparator<UUID>
    {
        @Override
        public int compare(UUID o1, UUID o2) {
            return PlayerList.INSTANCE.getPlayerName(o2).compareTo(PlayerList.INSTANCE.getPlayerName(o1));
        }
    }
}
