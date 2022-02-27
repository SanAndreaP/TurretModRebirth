package de.sanandrew.mods.turretmod.client.gui.element.tcu.levels;

import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollButton;
import de.sanandrew.mods.turretmod.client.gui.element.StackedScrollArea;

public class ModifierList
        extends StackedScrollArea
{
    public ModifierList(int[] areaSize, int scrollHeight, boolean rasterized, float maxScrollDelta, int[] scrollbarPos, ScrollButton scrollButton, IGui gui) {
        super(areaSize, scrollHeight, rasterized, maxScrollDelta, scrollbarPos, scrollButton, gui);
    }
}
