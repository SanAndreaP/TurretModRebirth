package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Item;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.TcuInfoValue;
import de.sanandrew.mods.turretmod.init.Lang;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class AmmoProvider
        implements ITcuInfoProvider
{
    private int ammo;
    private int maxAmmo;
    private ItemStack item = ItemStack.EMPTY;

    private GuiElementInst itemElem;
    private GuiElementInst itemTtip;

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
    public void tick(IGui gui, ITurretEntity turret) {
        MiscUtils.accept(turret.getTargetProcessor(), e -> {
            this.ammo = e.getAmmoCount();
            this.maxAmmo = e.getMaxAmmoCapacity();
            this.item = e.getAmmoStack();

            GuiElementInst ammoTtip = this.itemTtip.get(Tooltip.class).getChild(Tooltip.CONTENT);
            ammoTtip.get(AmmoText.class).setItem(gui, ammoTtip.data, this.item);
        });

        this.itemTtip.get().tick(gui, this.itemTtip.data);
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

        JsonObject itemTtipData = MiscUtils.get(data.getAsJsonObject("ammoItemTooltip"), JsonObject::new);

        JsonUtils.addDefaultJsonProperty(itemTtipData, "size", new int[] {16, 16});

        this.itemTtip = new GuiElementInst(new int[] {posItem[0] - 16, posItem[1]}, new Tooltip() {
            @Override
            public GuiElementInst getContent(IGui gui, JsonObject data) {
                return new GuiElementInst(new AmmoText()).initialize(gui);
            }
        }, itemTtipData).initialize(gui);
        this.itemTtip.get().bakeData(gui, itemTtipData, this.itemTtip);

        return new GuiElementInst[] {itemBackg, this.itemElem};
    }

    @Override
    public boolean useCustomRenderer() {
        return true;
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
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

    @Override
    public void renderOutside(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        GuiDefinition.renderElement(gui, stack, x + this.itemTtip.pos[0], y + this.itemTtip.pos[1], mouseX, mouseY, partTicks, this.itemTtip);
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

    private static class AmmoText
            extends ElementParent<Integer>
    {
        @Nonnull
        private ItemStack item = ItemStack.EMPTY;

        public void setItem(IGui gui, JsonObject data, @Nonnull ItemStack item) {
            if( !this.item.equals(item, false) ) {
                this.item = item;
                this.rebuildChildren(gui, data, true);
            }
        }

        @Override
        public void buildChildren(IGui iGui, JsonObject jsonObject, Map<Integer, GuiElementInst> map) {
            int ln = 0;
            for( ITextComponent t : this.item.getTooltipLines(iGui.get().getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL) ) {
                JsonObject ttipTxt = MiscUtils.get(jsonObject.getAsJsonObject("text"), JsonObject::new);
                JsonUtils.addDefaultJsonProperty(ttipTxt, "color", "0xFFFFFFFF");
                map.put(ln, new GuiElementInst(new int[] {0, ln > 0 ? 12 + (ln - 1) * 10 : 0}, new Text() {
                    @Override
                    public ITextComponent getBakedText(IGui gui, JsonObject data) {
                        return t;
                    }
                }, ttipTxt).initialize(iGui));

                ln++;
            }
        }
    }
}
