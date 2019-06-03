package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;

public interface IButtonLabel
        extends IGuiElement
{
    default void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {}

    void renderLabel(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data, boolean enabled, boolean hovered);
}
