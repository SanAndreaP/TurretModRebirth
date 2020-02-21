package de.sanandrew.mods.turretmod.client.gui.element.tcu.info;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class AmmoItemTooltip
        extends Tooltip
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu.info_ammo_ttip");

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        JsonUtils.addDefaultJsonProperty(data, "size", new int[] {16, 16});

        super.bakeData(gui, data, inst);
    }

    @Override
    public GuiElementInst getContent(IGui gui, JsonObject data) {
        return new GuiElementInst(new AmmoTooltip()).initialize(gui);
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        this.getChild(CONTENT).get(AmmoTooltip.class).currStack = ((IGuiTcuInst<?>) gui).getTurretInst().getTargetProcessor().getAmmoStack();

        super.update(gui, data);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        if( IGuiElement.isHovering(gui, x, y, mouseX, mouseY, this.size[0], this.size[1]) ) {
            GlStateManager.disableDepth();
            GlStateManager.colorMask(true, true, true, false);
            GuiUtils.drawGradientRect(x, y, this.size[0], this.size[1], 0x80FFFFFF, 0x80FFFFFF, false);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableDepth();
        }

        super.render(gui, partTicks, x, y, mouseX, mouseY, data);
    }

    @Override
    public int getWidth() {
        return this.size[0];
    }

    @Override
    public int getHeight() {
        return this.size[1];
    }

    private static final class AmmoTooltip
            implements IGuiElement
    {
        private ItemStack            currStack = ItemStack.EMPTY;
        private ItemStack            prevStack = ItemStack.EMPTY;
        private List<GuiElementInst> lines     = new ArrayList<>();

        private int width;
        private int height;

        @Override
        public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) { }

        @Override
        public void update(IGui gui, JsonObject data) {
            if( !ItemStackUtils.areEqual(this.currStack, this.prevStack) ) {
                this.prevStack = this.currStack;
                this.lines.clear();
                this.width = 0;
                this.height = 0;

                List<String> ttip = gui.get().getItemToolTip(this.currStack);
                for( String line : ttip ) {
                    GuiElementInst txtElem = new GuiElementInst(new int[] {0, this.height}, new Text()).initialize(gui);

                    JsonUtils.addJsonProperty(txtElem.data, "color", "0xFFFFFFFF");
                    JsonUtils.addJsonProperty(txtElem.data, "text", line);
                    JsonUtils.addJsonProperty(txtElem.data, "shadow", true);

                    txtElem.get().bakeData(gui, txtElem.data, txtElem);

                    this.lines.add(txtElem);

                    this.height += txtElem.get().getHeight() + (this.height == 0 ? 2 : 0);
                }

                this.height -= 2 + (ttip.size() < 2 ? 2 : 0);
            }
        }

        @Override
        public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
            for( GuiElementInst line : this.lines ) {
                GuiDefinition.renderElement(gui, x + line.pos[0], y + line.pos[1], mouseX, mouseY, partTicks, line);
                this.width = Math.max(this.width, line.get().getWidth());
            }
        }

        @Override
        public int getWidth() {
            return this.width;
        }

        @Override
        public int getHeight() {
            return this.height;
        }
    }
}
