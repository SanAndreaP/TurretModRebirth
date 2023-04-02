/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.TextField;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuScreen;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.init.Lang;
import de.sanandrew.mods.turretmod.network.TurretPlayerActionPacket;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;

public class NameProvider
        extends IllustratedProvider
{
    private boolean nameSet;
    private boolean nameChanged;

    protected GuiElementInst txtField;

    @Nonnull
    @Override
    public String getName() {
        return "name";
    }

    @Override
    protected int[] getDefaultIconUV() {
        return new int[] { 88, 0 };
    }

    @Override
    protected String getDefaultTooltipText() {
        return Lang.TCU_TEXT.get("info.name.tooltip");
    }

    @Override
    public void loadJson(IGui gui, JsonObject data, int w, int h) {
        this.nameSet = false;
        this.nameChanged = false;

        super.loadJson(gui, data, w, h);

        JsonObject tfData = MiscUtils.get(data.getAsJsonObject("textfield"), JsonObject::new);

        JsonUtils.addDefaultJsonProperty(tfData, "size", new int[] { w - 25, 10 });

        TextField tfElem = TextField.Builder.fromJson(gui, tfData);
        this.txtField = new GuiElementInst(JsonUtils.getIntArray(tfData.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] { 19, 3 }, Range.is(2)), tfElem).initialize(gui);
    }

    @Override
    public void setup(IGui gui, ITurretEntity turret, int w, int h) {
        super.setup(gui, turret, w, h);

        MiscUtils.accept(this.txtField.get(TextField.class), tf -> {
            tf.setup(gui, this.txtField);
            tf.setMaxStringLength(260);
            tf.setText(MiscUtils.get(MiscUtils.apply(turret.get().getCustomName(), ITextComponent::getString), ""));
            tf.setResponder(s -> this.nameChanged = true);
        });
    }

    @Override
    public void tick(IGui gui, ITurretEntity turret) {
        super.tick(gui, turret);

        MiscUtils.accept(this.txtField.get(TextField.class), tf -> {
            if( this.nameChanged && !tf.isFocused() ) {
                TurretPlayerActionPacket.rename(turret, tf.getText());
            }

            tf.tick(gui, this.txtField);
        });
    }

    @Override
    public void renderContent(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        super.renderContent(gui, turret, stack, partTicks, x, y, mouseX, mouseY, maxWidth, maxHeight);

        GuiDefinition.renderElement(gui, stack, x + this.txtField.pos[0], y + this.txtField.pos[1], mouseX, mouseY, partTicks, this.txtField);
    }

    @Override
    public void onClose(IGui gui, ITurretEntity turret) {
        super.onClose(gui, turret);

        if( this.nameChanged ) {
            TurretPlayerActionPacket.rename(turret, this.txtField.get(TextField.class).getText());
        }
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
        return this.txtField.get(TextField.class).mouseClicked(gui, mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(IGui gui, int keyCode, int scanCode, int modifiers) {
        return MiscUtils.apply(this.txtField.get(TextField.class), tf -> tf.keyPressed(gui, keyCode, scanCode, modifiers) || tf.canConsumeInput());
    }

    @Override
    public boolean charTyped(IGui gui, char typedChar, int keyCode) {
        return MiscUtils.apply(this.txtField.get(TextField.class), tf -> tf.charTyped(gui, typedChar, keyCode) || tf.canConsumeInput());
    }
}
