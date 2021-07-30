package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Item;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.TcuInfoValue;
import de.sanandrew.mods.turretmod.init.Lang;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AmmoProvider
        implements ITcuInfoProvider
{
    private int ammo;
    private int maxAmmo;
    private ItemStack item = ItemStack.EMPTY;

    @Override
    public String getName() {
        return "ammo";
    }

    @Nullable
    @Override
    public ITextComponent getLabel() {
        return new TranslationTextComponent(Lang.TCU_TEXT.get("info.ammo.tooltip"));
    }

    @Override
    public void tick(ITurretEntity turret) {
        MiscUtils.accept(turret.getTargetProcessor(), e -> {
            this.ammo = e.getAmmoCount();
            this.maxAmmo = e.getMaxAmmoCapacity();
            this.item = e.getAmmoStack();
        });
    }

    @Nullable
    @Override
    public ITextComponent getValueStr() {
        return new TranslationTextComponent(Lang.TCU_TEXT.get("info.ammo.suffix"), this.ammo)
                   .withStyle(Style.EMPTY.withColor(Color.fromRgb(0xFF3030A0)));
    }

    @Override
    public float getCurrValue() {
        return this.ammo;
    }

    @Override
    public float getMaxValue() {
        return this.maxAmmo;
    }

    @Nonnull
    @Override
    public ITexture buildIcon() {
        return ITexture.icon((mw, mh) -> new int[] { 86, 32 });
    }

    @Nullable
    @Override
    public ITexture buildProgressBar() {
        return ITexture.progressBar((mw, mh) -> new int[] { 0, 155 },
                                    (mw, mh) -> new int[] { 0, 152 });
    }

    private GuiElementInst itemElem;

    @Nonnull
    @Override
    public GuiElementInst[] buildCustomElements(IGui gui, JsonObject data, int maxWidth, int maxHeight) {
        JsonObject itemBgData = MiscUtils.get(data.getAsJsonObject("ammoItemBackground"), JsonObject::new);
        int[] posBg = TcuInfoValue.off(itemBgData, () -> new int[]{maxWidth - 2, 0});

        JsonUtils.addDefaultJsonProperty(itemBgData, "size", new int[] {16, 16});
        JsonUtils.addDefaultJsonProperty(itemBgData, "uv", new int[] {50, 0});

        GuiElementInst itemBackg = new GuiElementInst(posBg, new Texture(), itemBgData).initialize(gui);
        itemBackg.alignment = new String[] { "right" };

        JsonObject itemData = MiscUtils.get(data.getAsJsonObject("ammoItem"), JsonObject::new);
        int[] posItem = TcuInfoValue.off(itemData, () -> new int[]{maxWidth - 2, 0});

        this.itemElem = new GuiElementInst(posItem, new AmmoItem(), itemData).initialize(gui);
        this.itemElem.alignment = new String[] { "right" };

        return new GuiElementInst[] {itemBackg, this.itemElem};
    }

    @Override
    public boolean useCustomRenderer() {
        return true;
    }

    @Override
    public void render(Screen gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        this.renderAmmoOverlay((IGui) gui, stack, x, y, mouseX, mouseY);
    }

    private void renderAmmoOverlay(IGui gui, MatrixStack stack, int x, int y, double mouseX, double mouseY) {
        IGuiElement ie = this.itemElem.get();
        int[] size = {ie.getWidth(), ie.getHeight()};

        x += this.itemElem.pos[0] - size[0];
        y += this.itemElem.pos[1];

        if( IGuiElement.isHovering(gui, x, y, mouseX, mouseY, size[0], size[1]) ) {
            RenderSystem.disableDepthTest();
            AbstractGui.fill(stack, x, y, x + size[0], y + size[1], 0x80FFFFFF);
            RenderSystem.enableDepthTest();
        }
    }

    private class AmmoItem
            extends Item
    {
        @Override
        protected ItemStack getBakedItem(IGui gui, JsonObject data) {
            return ItemStack.EMPTY;
        }

        @Override
        protected ItemStack getDynamicStack(IGui gui) {
            return AmmoProvider.this.item;
        }
    }
}
