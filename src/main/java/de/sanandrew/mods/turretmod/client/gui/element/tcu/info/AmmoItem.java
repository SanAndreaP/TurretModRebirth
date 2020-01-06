package de.sanandrew.mods.turretmod.client.gui.element.tcu.info;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Label;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.client.gui.element.Item;
import de.sanandrew.mods.turretmod.client.util.GuiHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class AmmoItem
        extends Item
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_info_ammo");

    @Override
    protected ItemStack getBakedStack(IGui gui, JsonObject data) {
        return ItemStack.EMPTY;
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        this.data.stack = ((IGuiTcuInst<?>) gui).getTurretInst().getTargetProcessor().getAmmoStack();
    }

    public static class AmmoLabel
            extends Label
    {
        public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_info_ammolabel");

        @Override
        public void bakeData(IGui gui, JsonObject data) {
            JsonUtils.addDefaultJsonProperty(data, "size", new int[] {16, 16});

            super.bakeData(gui, data);
        }

        @Override
        public GuiElementInst getLabel(IGui gui, JsonObject data) {
            if( !data.has("content") ) {
                GuiElementInst lbl = new GuiElementInst();
                lbl.element = new AmmoText();

                gui.getDefinition().initElement(lbl);
                lbl.get().bakeData(gui, lbl.data);

                return lbl;
            }

            return super.getLabel(gui, data);
        }

        @Override
        public void update(IGui gui, JsonObject data) {
            IGuiElement elem = this.data.content.get();
            if( elem instanceof AmmoText ) {
                ((AmmoText) elem).stack = ((IGuiTcuInst<?>) gui).getTurretInst().getTargetProcessor().getAmmoStack();
            }

            super.update(gui, data);
        }

        @Override
        public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
            int locMouseX = mouseX - gui.getScreenPosX();
            int locMouseY = mouseY - gui.getScreenPosY();
            if( locMouseX >= x && locMouseX < x + this.data.size[0] && locMouseY >= y && locMouseY < y + this.data.size[1] ) {
                GlStateManager.disableDepth();
                GlStateManager.colorMask(true, true, true, false);
                GuiUtils.drawGradientRect(x, y, this.data.size[0], this.data.size[1], 0x80FFFFFF, 0x80FFFFFF, false);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableDepth();
            }

            super.render(gui, partTicks, x, y, mouseX, mouseY, data);
        }

        @Override
        public int getWidth() {
            return this.data.size[0];
        }

        @Override
        public int getHeight() {
            return this.data.size[1];
        }

        private static final class AmmoText
                extends Text
        {
            private ItemStack stack = ItemStack.EMPTY;

            @Override
            public void bakeData(IGui gui, JsonObject data) {
                JsonUtils.addDefaultJsonProperty(data, "color", "0xFFFFFFFF");

                super.bakeData(gui, data);
            }

            @Override
            public String getBakedText(IGui gui, JsonObject data) {
                return "";
            }

            @Override
            public String getDynamicText(IGui gui, String originalText) {
                return stack.isEmpty() ? "" : stack.getDisplayName();
            }
        }
    }
}
