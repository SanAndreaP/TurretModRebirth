package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IIcon;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public final class TcuInfoValue
        extends ElementParent<Integer>
{
    @Nonnull
    private final ITcuInfoProvider provider;
    private final int              w;
    private final int              h;

    private ITurretEntity turret;
    private final GuiElementInst icon;
    private final GuiElementInst ttip;

    TcuInfoValue(@Nonnull ITcuInfoProvider provider, int w, int h, IGui gui, GuiElementInst icon, GuiElementInst ttip) {
        this.provider = provider;
        this.w = w;
        this.h = h;
        this.icon = icon.initialize(gui);
        this.ttip = ttip.initialize(gui);
    }

    public static class Builder
            implements IBuilder<TcuInfoValue>
    {
        GuiElementInst icon;
        GuiElementInst tooltip;
        final ITcuInfoProvider provider;
        final int              w;
        final int              h;

        public Builder(ITcuInfoProvider provider, int w, int h) {
            this.provider = provider;
            this.w = w;
            this.h = h;
        }

        @Override
        public void sanitize(IGui gui) {

        }

        @Override
        public TcuInfoValue get(IGui gui) {
            return null;
        }

        public static Builder buildFromJson(IGui gui, JsonObject data, @Nonnull ITcuInfoProvider provider, int w, int h) {
            Builder b = new Builder(provider, w, h);

            JsonObject valData = JsonUtils.deepCopy(MiscUtils.get(data.getAsJsonObject(provider.getName()), () -> MiscUtils.get(data.getAsJsonObject("default"), JsonObject::new)));

            IIcon prvIcon = provider.getIcon();
            JsonObject iconData = MiscUtils.get(valData.getAsJsonObject("icon"), JsonObject::new);
            JsonUtils.addDefaultJsonProperty(iconData, "size", prvIcon.getSize(w, h));
            JsonUtils.addDefaultJsonProperty(iconData, "uv", prvIcon.getUV(w, h));
            JsonUtils.addDefaultJsonProperty(iconData, "textureSize", prvIcon.getTextureSize());
            MiscUtils.accept(prvIcon.getTexture(), tx -> JsonUtils.addDefaultJsonProperty(iconData, "texture", tx.toString()));
            b.icon = new GuiElementInst(off(iconData, () -> prvIcon.getOffset(w, h)), Texture.Builder.fromJson(gui, iconData));

            ITextComponent prvLbl = provider.getLabel();
            JsonObject ttipData = MiscUtils.get(valData.getAsJsonObject("tooltip"), JsonObject::new);
            JsonUtils.addDefaultJsonProperty(ttipData, "size", JsonUtils.getIntArray(iconData.get("size")));
            Tooltip.Builder.
//        ITextComponent lblTxt = this.provider.getLabel();
//        JsonObject ttipData = MiscUtils.get(data.getAsJsonObject("tooltip"), JsonObject::new);
//        JsonUtils.addDefaultJsonProperty(ttipData, "size", this.icon.get(Texture.class).size);
//
//        JsonObject txtData = MiscUtils.get(ttipData.getAsJsonObject("text"), JsonObject::new);
//        JsonUtils.addDefaultJsonProperty(txtData, "color", "0xFFFFFFFF");
//
//        final GuiElementInst txtElem = new GuiElementInst(new Text() {
//            @Override
//            public ITextComponent getBakedText(IGui gui, JsonObject data) {
//                return lblTxt;
//            }
//        }, txtData).initialize(gui);
//
//        this.ttip = new GuiElementInst(this.icon.pos, new Tooltip() {
//            @Override
//            public GuiElementInst getContent(IGui gui, JsonObject data) {
//                return txtElem;
//            }
//        }, ttipData).initialize(gui);

            Tooltip.Builder tb = Tooltip.Builder.buildFromJson(gui, ttipData);
        }

        private static int[] cData(int w, int h, JsonObject data, IIcon t) {

            return off(data, () -> t.getOffset(w, h));
        }

        public static int[] off(JsonObject data, Supplier<int[]> t) {
            return MiscUtils.get(JsonUtils.getIntArray(data.get("offset"), (int[]) null), t);
        }
    }

//    @Override
//    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
//        super.bakeData(gui, data, inst);
//
//        if( gui instanceof TcuInfoPage ) {
//            this.turret = ((TcuInfoPage) gui).getTurret();
//
//            this.setTooltip(gui, MiscUtils.get(data.getAsJsonObject(this.provider.getName()),
//                                               () -> MiscUtils.get(data.getAsJsonObject("default"), JsonObject::new)));
//            this.ttip.get().bakeData(gui, this.ttip.data, this.ttip);
//        }
//    }

//    @Override
//    public void buildChildren(IGui gui, JsonObject data, Map<Integer, GuiElementInst> children) {
//        JsonObject valData = MiscUtils.get(data.getAsJsonObject(this.provider.getName()),
//                                           () -> MiscUtils.get(data.getAsJsonObject("default"), JsonObject::new));
//
//        this.setIcon(gui, valData);
//        children.put(0, this.icon);
//
//        GuiElementInst[] cstChildren = this.provider.buildCustomElements(gui, valData, this.w, this.h);
//        for( int i = 0, max = cstChildren.length; i < max; i++ ) {
//            children.put(3 + i, cstChildren[i]);
//        }
//    }

//    private void setTooltip(IGui gui, JsonObject data) {
//        ITextComponent lblTxt = this.provider.getLabel();
//        JsonObject ttipData = MiscUtils.get(data.getAsJsonObject("tooltip"), JsonObject::new);
//        JsonUtils.addDefaultJsonProperty(ttipData, "size", this.icon.get(Texture.class).size);
//
//        JsonObject txtData = MiscUtils.get(ttipData.getAsJsonObject("text"), JsonObject::new);
//        JsonUtils.addDefaultJsonProperty(txtData, "color", "0xFFFFFFFF");
//
//        final GuiElementInst txtElem = new GuiElementInst(new Text() {
//            @Override
//            public ITextComponent getBakedText(IGui gui, JsonObject data) {
//                return lblTxt;
//            }
//        }, txtData).initialize(gui);
//
//        this.ttip = new GuiElementInst(this.icon.pos, new Tooltip() {
//            @Override
//            public GuiElementInst getContent(IGui gui, JsonObject data) {
//                return txtElem;
//            }
//        }, ttipData).initialize(gui);
//    }

    @Override
    public void tick(IGui gui, GuiElementInst e) {
        this.provider.tick(gui, this.turret);

        this.ttip.get().tick(gui, this.ttip);

        super.tick(gui, e);
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst e) {
        super.render(gui, stack, partTicks, x, y, mouseX, mouseY, e);

        this.provider.renderContent(gui, this.turret, stack, partTicks, x, y, mouseX, mouseY, this.w, this.h);
    }

    public void renderOutside(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY) {
        GuiDefinition.renderElement(gui, stack, x + this.ttip.pos[0], y + this.ttip.pos[1], mouseX, mouseY, partTicks, this.ttip, true);

        this.provider.renderOutside(gui, this.turret, stack, partTicks, x, y, mouseX, mouseY, this.w, this.h);
    }

    @Override
    public void onClose(IGui gui) {
        super.onClose(gui);

        this.provider.onClose(gui, this.turret);
    }

    @Override
    public int getWidth() {
        return this.w;
    }

    @Override
    public int getHeight() {
        return this.h;
    }

    @Override
    public boolean isVisible() {
        return this.provider.isVisible(this.turret);
    }

    @Override
    public boolean mouseScrolled(IGui gui, double mouseX, double mouseY, double mouseScroll) {
        return this.provider.mouseScrolled(gui, mouseX, mouseY, mouseScroll)
               || super.mouseScrolled(gui, mouseX, mouseY, mouseScroll);
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
        return this.provider.mouseClicked(gui, mouseX, mouseY, button)
               || super.mouseClicked(gui, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(IGui gui, double mouseX, double mouseY, int button) {
        return this.provider.mouseReleased(gui, mouseX, mouseY, button)
               || super.mouseReleased(gui, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(IGui gui, double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.provider.mouseDragged(gui, mouseX, mouseY, button, dragX, dragY)
               || super.mouseDragged(gui, mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean keyPressed(IGui gui, int keyCode, int scanCode, int modifiers) {
        return this.provider.keyPressed(gui, keyCode, scanCode, modifiers)
               || super.keyPressed(gui, keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(IGui gui, int keyCode, int scanCode, int modifiers) {
        return this.provider.keyReleased(gui, keyCode, scanCode, modifiers)
               || super.keyReleased(gui, keyCode, scanCode, modifiers);
    }
}
