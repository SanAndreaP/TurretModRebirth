package de.sanandrew.mods.turretmod.client.gui.element.tcu.levels;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollButton;
import de.sanandrew.mods.turretmod.client.gui.element.StackedScrollArea;

public class ModifierList
        extends StackedScrollArea
{
    final JsonObject modData;

    public ModifierList(int[] areaSize, int scrollHeight, float maxScrollDelta, int[] scrollbarPos, ScrollButton scrollButton, IGui gui, JsonObject modData) {
        super(areaSize, scrollHeight, true, maxScrollDelta, scrollbarPos, scrollButton, gui);
        this.modData = modData;
    }
}
