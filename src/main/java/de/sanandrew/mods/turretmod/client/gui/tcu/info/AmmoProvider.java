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
import de.sanandrew.mods.turretmod.client.gui.element.tcu.IndicatorText;
import de.sanandrew.mods.turretmod.init.Lang;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;

//TODO: render ammo item
public class AmmoProvider
        extends IndicatorProvider
{
    private ItemStack      item = ItemStack.EMPTY;

    private GuiElementInst itemIcon;

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
//    private int ammo;
//    private int maxAmmo;
//
    private GuiElementInst itemTtip;
//
//    @Nonnull
//    @Override
//    public String getName() {
//        return "ammo";
//    }
//
//    @Nonnull
//    @Override
//    public IIcon getIcon() {
//        return IIcon.get((mw, mh) -> new int[] { 86, 32 });
//    }
//
//    @Nonnull
//    @Override
//    public ITextComponent getLabel() {
//        return new TranslationTextComponent();
//    }


    @Override
    public void loadJson(IGui gui, JsonObject data, int w, int h) {
        super.loadJson(gui, data, w, h);


        JsonObject itemData = MiscUtils.get(data.getAsJsonObject("ammoItem"), JsonObject::new);

        AmmoItem itemElem = AmmoItem.Builder.fromJson(gui, itemData);
        this.itemIcon = new GuiElementInst(JsonUtils.getIntArray(itemData.get(OFFSET_JSON_ELEM), new int[] {0, 0}, Range.is(2)), itemElem).initialize(gui);
    }

    @Override
    public void setup(IGui gui, ITurretEntity turret, int w, int h) {
        super.setup(gui, turret, w, h);

        this.itemIcon.get(AmmoItem.class).setItemSupplier(() -> this.item);

        this.itemIcon.get().setup(gui, this.itemIcon);
    }

    @Override
    public void tick(IGui gui, ITurretEntity turret) {
//        MiscUtils.accept(turret.getTargetProcessor(), e -> {
//            this.ammo = e.getAmmoCount();
//            this.maxAmmo = e.getMaxAmmoCapacity();
//            this.item = e.getAmmoStack();
//
//            GuiElementInst ammoTtip = this.itemTtip.get(Tooltip.class).get(Tooltip.CONTENT);
//            ammoTtip.get(AmmoText.class).setItem(gui, ammoTtip.data, this.item);
//        });
        super.tick(gui, turret);

        this.itemIcon.get().tick(gui, this.itemIcon);
//        this.itemTtip.get().tick(gui, this.itemTtip);
    }

    @Override
    public void renderContent(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        super.renderContent(gui, turret, stack, partTicks, x, y, mouseX, mouseY, maxWidth, maxHeight);

        GuiDefinition.renderElement(gui, stack, x + this.itemIcon.pos[0] + 144, y + this.itemIcon.pos[1], mouseX, mouseY, partTicks, this.itemIcon);
    }

    //
//    //    @Nullable
////    @Override
////    public ITextComponent getValueStr() {
////        return new TranslationTextComponent(, this.ammo)
////                   .withStyle(Style.EMPTY.withColor(Color.fromRgb(0xFF3030A0)));
////    }
////
////    @Override
////    public float getCurrValue() {
////        return this.ammo;
////    }
////
////    @Override
////    public float getMaxValue() {
////        return this.maxAmmo;
////    }
////
////    @Nonnull
////    @Override
////    public ITexture buildIcon() {
////        return ITexture.icon((mw, mh) -> );
////    }
////
////    @Nullable
////    @Override
////    public ITexture buildProgressBar() {
////        return ITexture.progressBar((mw, mh) -> ,
////                                    (mw, mh) -> new int[] { 0, 152 });
////    }
////
//    @Nonnull
//    @Override
//    public GuiElementInst[] buildCustomElements(IGui gui, JsonObject data, int maxWidth, int maxHeight) {
//        JsonObject itemBgData = MiscUtils.get(data.getAsJsonObject("ammoItemBackground"), JsonObject::new);
//        int[] posBg = TcuInfoValue.off(itemBgData, () -> new int[]{ maxWidth - 2, 0});
//
//        JsonUtils.addDefaultJsonProperty(itemBgData, "size", new int[] {16, 16});
//        JsonUtils.addDefaultJsonProperty(itemBgData, "uv", new int[] {50, 0});
//
//        GuiElementInst itemBackg = new GuiElementInst(posBg, new Texture(), itemBgData).initialize(gui);
//        itemBackg.alignment = new String[] { "right" };
//
//        JsonObject itemData = MiscUtils.get(data.getAsJsonObject("ammoItem"), JsonObject::new);
//        int[] posItem = TcuInfoValue.off(itemData, () -> new int[]{maxWidth - 2, 0});
//
//        this.itemElem = new GuiElementInst(posItem, new AmmoItem(), itemData).initialize(gui);
//        this.itemElem.alignment = new String[] { "right" };
//
//        JsonObject itemTtipData = MiscUtils.get(data.getAsJsonObject("ammoItemTooltip"), JsonObject::new);
//
//        JsonUtils.addDefaultJsonProperty(itemTtipData, "size", new int[] {16, 16});
//
//        this.itemTtip = new GuiElementInst(new int[] {posItem[0] - 16, posItem[1]}, new Tooltip() {
//            @Override
//            public GuiElementInst getContent(IGui gui, JsonObject data) {
//                return new GuiElementInst(new AmmoText()).initialize(gui);
//            }
//        }, itemTtipData).initialize(gui);
//        this.itemTtip.get().bakeData(gui, itemTtipData, this.itemTtip);
//
//        return new GuiElementInst[] {itemBackg, this.itemElem};
//    }
//
//    @Override
//    public void renderContent(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
//        IGuiElement ie = this.itemElem.get();
//        int[] size = {ie.getWidth(), ie.getHeight()};
//
//        x += this.itemElem.pos[0] - size[0];
//        y += this.itemElem.pos[1];
//
//        if( IGuiElement.isHovering(gui, x, y, mouseX, mouseY, size[0], size[1]) ) {
//            RenderSystem.disableDepthTest();
//            AbstractGui.fill(stack, x, y, x + size[0], y + size[1], 0x80FFFFFF);
//            RenderSystem.enableDepthTest();
//        }
//    }
//
//
//    @Override
//    public void renderOutside(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
//        GuiDefinition.renderElement(gui, stack, x + this.itemTtip.pos[0], y + this.itemTtip.pos[1], mouseX, mouseY, partTicks, this.itemTtip);
//    }
//
//    private class AmmoItem
//            extends Item
//    {
//        @Override
//        protected ItemStack getBakedItem(IGui gui, JsonObject data) {
//            return ItemStack.EMPTY;
//        }
//
//        @Override
//        protected ItemStack getDynamicStack(IGui gui) {
//            return AmmoProvider.this.item;
//        }
//    }
//
//    private static class AmmoText
//            extends ElementParent<Integer>
//    {
//        @Nonnull
//        private ItemStack item = ItemStack.EMPTY;
//
//        public void setItem(IGui gui, JsonObject data, @Nonnull ItemStack item) {
//            if( !this.item.equals(item, false) ) {
//                this.item = item;
//                this.rebuildChildren(gui, data, true);
//            }
//        }
//
//        @Override
//        public void buildChildren(IGui iGui, JsonObject jsonObject, Map<Integer, GuiElementInst> map) {
//            int ln = 0;
//            for( ITextComponent t : this.item.getTooltipLines(iGui.get().getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL) ) {
//                JsonObject ttipTxt = MiscUtils.get(jsonObject.getAsJsonObject("text"), JsonObject::new);
//                JsonUtils.addDefaultJsonProperty(ttipTxt, "color", "0xFFFFFFFF");
//                map.put(ln, new GuiElementInst(new int[] {0, ln > 0 ? 12 + (ln - 1) * 10 : 0}, new Text() {
//                    @Override
//                    public ITextComponent getBakedText(IGui gui, JsonObject data) {
//                        return t;
//                    }
//                }, ttipTxt).initialize(iGui));
//
//                ln++;
//            }
//        }
//    }
}
