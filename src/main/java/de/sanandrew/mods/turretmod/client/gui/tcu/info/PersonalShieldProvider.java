/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ProgressBar;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuScreen;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.init.Lang;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.shield.ShieldData;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;

public class PersonalShieldProvider
        extends ValueProvider
{
    private   boolean        isInRecovery = false;

    private GuiElementInst recoveryIndicator;

    @Nonnull
    @Override
    public String getName() {
        return "pshield";
    }

    @Override
    public void loadJson(IGui gui, JsonObject data, int w, int h) {
        super.loadJson(gui, data, w, h);

        this.recoveryIndicator = this.loadRecoveryIndicator(gui, MiscUtils.get(data.getAsJsonObject("recoveryIndicator"), JsonObject::new));
    }

    @Override
    public void setup(IGui gui, ITurretEntity turret, int w, int h) {
        super.setup(gui, turret, w, h);

        this.recoveryIndicator.get().setup(gui, this.recoveryIndicator);

        this.indicator.get(ProgressBar.class).setPercentFunc(p -> this.maxValue != 0 && !this.isInRecovery ? this.currValue / this.maxValue : 0.0F);
        this.recoveryIndicator.get(ProgressBar.class).setPercentFunc(p -> this.maxValue != 0 && this.isInRecovery ? this.currValue / this.maxValue : 0.0F);
    }

    @Override
    public void tick(IGui gui, ITurretEntity turret) {
        super.tick(gui, turret);

        this.recoveryIndicator.get().tick(gui, this.recoveryIndicator);
    }

    @Override
    public void renderContent(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        super.renderContent(gui, turret, stack, partTicks, x, y, mouseX, mouseY, maxWidth, maxHeight);

        GuiDefinition.renderElement(gui, stack, x + this.recoveryIndicator.pos[0] + 18, y + this.recoveryIndicator.pos[1] + 9, mouseX, mouseY, partTicks, this.recoveryIndicator);
    }

    protected void calcValues(ITurretEntity turret) {
        ShieldData data = turret.getUpgradeProcessor().getUpgradeData(Upgrades.SHIELD_PERSONAL.getId());

        if( data != null ) {
            this.isInRecovery = data.isInRecovery();
            if( this.isInRecovery ) {
                this.currValue = data.getRecoveryValue() * 100.0F;
                this.maxValue = 100.0F;
            } else {
                this.currValue = data.getValue();
                this.maxValue = ShieldData.MAX_VALUE;
            }
        }
    }

    @Override
    protected int[] getDefaultIconUV() {
        return new int[] { 152, 16 };
    }

    @Override
    protected int[] getDefaultIndicatorUV() {
        return new int[] { 0, 181 };
    }

    @Override
    protected String getDefaultTooltipText() {
        return Lang.TCU_TEXT.get("info.persshield.tooltip");
    }

    @Override
    protected ITextComponent getLabelText(IGui gui, ITextComponent origTC) {
        return super.getLabelText(gui, (this.isInRecovery ? new TranslationTextComponent(this.getDefaultLabelText()) : origTC));
    }

    @Override
    protected String getDefaultLabelText() {
        return Lang.TCU_TEXT.get("info.persshield.value" + (this.isInRecovery ? ".recovery" : ""));
    }

    @Override
    protected int getDefaultLabelColor() {
        return 0xFFFC50FC;
    }

    @Override
    protected int getDefaultLabelBorderColor() {
        return 0xFF400040;
    }

    @Override
    protected String getNumberFormat(double value) {
        return String.format(this.isInRecovery ? "%.0f" : "%.1f", value);
    }

    @Override
    public boolean isVisible(ITurretEntity turret) {
        return turret.getUpgradeProcessor().hasUpgrade(Upgrades.SHIELD_PERSONAL);
    }

    private GuiElementInst loadRecoveryIndicator(IGui gui, JsonObject data) {
        JsonUtils.addDefaultJsonProperty(data, "size", this.getDefaultIndicatorSize());
        JsonUtils.addDefaultJsonProperty(data, "uv", new int[] { 0, 186 });
        JsonUtils.addDefaultJsonProperty(data, "direction", "LEFT_TO_RIGHT");

        ProgressBar indElem = ProgressBar.Builder.fromJson(gui, data);

        return new GuiElementInst(JsonUtils.getIntArray(data.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] { 0, 0 }, Range.is(2)), indElem).initialize(gui);
    }
}
