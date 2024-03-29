/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.gui.tcu.info;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Item;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import dev.sanandrea.mods.turretmod.api.client.tcu.ITcuScreen;
import dev.sanandrea.mods.turretmod.api.turret.ITurretEntity;
import dev.sanandrea.mods.turretmod.client.gui.element.tcu.info.AmmoItemTooltip;
import dev.sanandrea.mods.turretmod.init.Lang;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;

public class AmmoProvider
        extends ValueProvider
{
    private ItemStack      item = ItemStack.EMPTY;

    private GuiElementInst itemBg;
    private GuiElementInst itemIcon;
    private GuiElementInst itemTtip;

    @Nonnull
    @Override
    public String getName() {
        return "ammo";
    }

    @Override
    protected void calcValues(ITurretEntity turret) {
        MiscUtils.accept(turret.getTargetProcessor(), e -> {
            this.currValue = e.getAmmoCount();
            this.maxValue = e.getMaxAmmoCapacity();
            this.item = e.getAmmoStack();
        });
    }

    @Override
    protected int[] getDefaultIconUV() {
        return new int[] { 88, 32 };
    }

    @Override
    protected int[] getDefaultIndicatorUV() {
        return new int[] { 0, 161 };
    }

    @Override
    protected String getDefaultTooltipText() {
        return Lang.TCU_TEXT.get("info.ammo.tooltip");
    }

    @Override
    protected String getDefaultLabelText() {
        return Lang.TCU_TEXT.get("info.ammo.value");
    }

    @Override
    protected int getDefaultLabelColor() {
        return 0xFF50AEFC;
    }

    @Override
    protected int getDefaultLabelBorderColor() {
        return 0xFF002040;
    }

    @Override
    protected String getNumberFormat(double value) {
        return String.format("%.0f", value);
    }

    @Override
    public void loadJson(IGui gui, JsonObject data, int w, int h) {
        super.loadJson(gui, data, w, h);

        JsonObject itemData = MiscUtils.get(data.getAsJsonObject("ammoItem"), JsonObject::new);
        JsonUtils.addDefaultJsonProperty(itemData, "doMouseOver", true);
        Item itemElem = Item.Builder.fromJson(gui, itemData);
        this.itemIcon = new GuiElementInst(JsonUtils.getIntArray(itemData.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] {0, 0}, Range.is(2)), itemElem).initialize(gui);

        JsonObject bgData = MiscUtils.get(data.getAsJsonObject("ammoBackground"), JsonObject::new);
        JsonUtils.addDefaultJsonProperty(bgData, "size", new int[] {itemElem.getWidth(), itemElem.getHeight()});
        JsonUtils.addDefaultJsonProperty(bgData, "uv", new int[] {50, 0});
        Texture bgElem = Texture.Builder.fromJson(gui, bgData);
        this.itemBg = new GuiElementInst(JsonUtils.getIntArray(bgData.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] {0, 0}, Range.is(2)), bgElem);

        JsonObject ttipData = MiscUtils.get(data.getAsJsonObject("ammoTooltip"), JsonObject::new);
        JsonUtils.addDefaultJsonProperty(ttipData, "size", new int[] {itemElem.getWidth(), itemElem.getHeight()});
        AmmoItemTooltip ttipElem = AmmoItemTooltip.Builder.fromJson(gui, ttipData);
        this.itemTtip = new GuiElementInst(JsonUtils.getIntArray(ttipData.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] {0, 0}, Range.is(2)), ttipElem).initialize(gui);
    }

    @Override
    public void setup(IGui gui, ITurretEntity turret, int w, int h) {
        super.setup(gui, turret, w, h);

        this.itemBg.get().setup(gui, this.itemBg);
        MiscUtils.accept(this.itemIcon.get(Item.class), e -> {
            e.setItemSupplier(() -> this.item);
            e.setup(gui, this.itemIcon);
        });
        MiscUtils.accept(this.itemTtip.get(AmmoItemTooltip.class), e -> {
            e.setItemSupplier(() -> this.item);
            e.setup(gui, this.itemTtip);
        });
    }

    @Override
    public void tick(IGui gui, ITurretEntity turret) {
        super.tick(gui, turret);

        this.itemBg.get().tick(gui, this.itemBg);
        this.itemIcon.get().tick(gui, this.itemIcon);
        this.itemTtip.get().tick(gui, this.itemTtip);
    }

    @Override
    public void renderContent(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        super.renderContent(gui, turret, stack, partTicks, x, y, mouseX, mouseY, maxWidth, maxHeight);

        GuiDefinition.renderElement(gui, stack, x + this.itemBg.pos[0] + 144, y + this.itemBg.pos[1], mouseX, mouseY, partTicks, this.itemBg);
        GuiDefinition.renderElement(gui, stack, x + this.itemIcon.pos[0] + 144, y + this.itemIcon.pos[1], mouseX, mouseY, partTicks, this.itemIcon);
    }

    @Override
    public void renderOutside(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        super.renderOutside(gui, turret, stack, partTicks, x, y, mouseX, mouseY, maxWidth, maxHeight);

        GuiDefinition.renderElement(gui, stack, x + this.itemTtip.pos[0] + 144, y + this.itemTtip.pos[1], mouseX, mouseY, partTicks, this.itemTtip);
    }
}
