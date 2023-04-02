/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.gui.tcu.info;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import dev.sanandrea.mods.turretmod.api.client.tcu.ITcuScreen;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public abstract class TextProvider
        extends IllustratedProvider
{
    protected GuiElementInst text;

    @Nonnull
    public abstract BiFunction<IGui, ITextComponent, ITextComponent> getTextFunction(IGui gui, ITurretEntity turret);

    @Override
    public void loadJson(IGui gui, JsonObject data, int w, int h) {
        super.loadJson(gui, data, w, h);

        JsonObject txtData = MiscUtils.get(data.getAsJsonObject("text"), JsonObject::new);
        JsonUtils.addDefaultJsonProperty(txtData, "color", "#FF000000");
        Text txtElem = Text.Builder.fromJson(gui, txtData);
        this.text = new GuiElementInst(JsonUtils.getIntArray(txtData.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] { 18, 4 }, Range.is(2)), txtElem).initialize(gui);
    }

    @Override
    public void setup(IGui gui, ITurretEntity turret, int w, int h) {
        this.text.get(Text.class).setTextFunc(this.getTextFunction(gui, turret));

        super.setup(gui, turret, w, h);

        this.text.get().setup(gui, this.text);
    }

    @Override
    public void tick(IGui gui, ITurretEntity turret) {
        super.tick(gui, turret);

        this.text.get().tick(gui, this.text);
    }

    @Override
    public void renderContent(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        super.renderContent(gui, turret, stack, partTicks, x, y, mouseX, mouseY, maxWidth, maxHeight);

        GuiDefinition.renderElement(gui, stack, x + this.text.pos[0], y + this.text.pos[1], mouseX, mouseY, partTicks, this.text);
    }
}
