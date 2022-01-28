package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.AmmoItem;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.AmmoItemTooltip;
import de.sanandrew.mods.turretmod.init.Lang;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;

public class AmmoProvider
        extends IndicatorProvider
{
    private ItemStack      item = ItemStack.EMPTY;

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
        return new int[] { 0, 155 };
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
        return 0xFF3030A0;
    }

    @Override
    protected String getNumberFormat(double value) {
        return String.format("%.0f", value);
    }


    @Override
    public void loadJson(IGui gui, JsonObject data, int w, int h) {
        super.loadJson(gui, data, w, h);

        JsonObject itemData = MiscUtils.get(data.getAsJsonObject("ammoItem"), JsonObject::new);
        AmmoItem itemElem = AmmoItem.Builder.fromJson(gui, itemData);
        this.itemIcon = new GuiElementInst(JsonUtils.getIntArray(itemData.get(OFFSET_JSON_ELEM), new int[] {0, 0}, Range.is(2)), itemElem).initialize(gui);

        JsonObject ttipData = MiscUtils.get(data.getAsJsonObject("ammoTooltip"), JsonObject::new);
        JsonUtils.addDefaultJsonProperty(ttipData, "size", new int[] {itemElem.getWidth(), itemElem.getHeight()});
        AmmoItemTooltip ttipElem = AmmoItemTooltip.Builder.fromJson(gui, ttipData);
        this.itemTtip = new GuiElementInst(JsonUtils.getIntArray(ttipData.get(OFFSET_JSON_ELEM), new int[] {0, 0}, Range.is(2)), ttipElem).initialize(gui);
    }

    @Override
    public void setup(IGui gui, ITurretEntity turret, int w, int h) {
        super.setup(gui, turret, w, h);

        MiscUtils.accept(this.itemIcon.get(AmmoItem.class), e -> {
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

        this.itemIcon.get().tick(gui, this.itemIcon);
        this.itemTtip.get().tick(gui, this.itemTtip);
    }

    @Override
    public void renderContent(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        super.renderContent(gui, turret, stack, partTicks, x, y, mouseX, mouseY, maxWidth, maxHeight);

        GuiDefinition.renderElement(gui, stack, x + this.itemIcon.pos[0] + 144, y + this.itemIcon.pos[1], mouseX, mouseY, partTicks, this.itemIcon);
    }

    @Override
    public void renderOutside(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        super.renderOutside(gui, turret, stack, partTicks, x, y, mouseX, mouseY, maxWidth, maxHeight);

        GuiDefinition.renderElement(gui, stack, x + this.itemTtip.pos[0] + 144, y + this.itemTtip.pos[1], mouseX, mouseY, partTicks, this.itemTtip);
    }
}
