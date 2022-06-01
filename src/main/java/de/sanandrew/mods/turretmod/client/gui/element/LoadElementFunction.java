package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonElement;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;

import java.util.function.BiFunction;
import java.util.function.Function;

//TODO: put in SanLib
@FunctionalInterface
public interface LoadElementFunction<T extends IGuiElement, B extends IGuiElement.IBuilder<T>>
        extends Function<B, BiFunction<IGui, JsonElement, GuiElementInst>>
{
    default GuiElementInst apply(B builder, IGui gui, JsonElement element) {
        return MiscUtils.apply(builder, b -> MiscUtils.apply(this.apply(b), f -> f.apply(gui, element)));
    }
}
