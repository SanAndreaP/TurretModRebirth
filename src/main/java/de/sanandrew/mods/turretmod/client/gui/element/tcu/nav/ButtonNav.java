package de.sanandrew.mods.turretmod.client.gui.element.tcu.nav;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Button;
import de.sanandrew.mods.sanlib.lib.client.gui.element.IButtonLabel;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.client.gui.element.Item;
import de.sanandrew.mods.turretmod.client.shader.ShaderGrayscale;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.registry.turret.GuiTcuRegistry;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ButtonNav
        extends Button
{
    private static final ShaderGrayscale SHADER_GRAYSCALE = new ShaderGrayscale(TextureMap.LOCATION_BLOCKS_TEXTURE);

    public ResourceLocation page;
    int pageIdx;
    private ItemStack pageStack = ItemStack.EMPTY;

    ButtonNav(int id, ResourceLocation page) {
        this.pageIdx = id;
        this.page = page;
    }

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        boolean initialize = this.data == null;

        JsonUtils.addDefaultJsonProperty(data, "size", new int[] { 16, 16});
        JsonUtils.addDefaultJsonProperty(data, "uvEnabled", new int[] {0, 0});
        JsonUtils.addDefaultJsonProperty(data, "uvDisabled", new int[] {0, 0});
        JsonUtils.addDefaultJsonProperty(data, "uvHover", new int[] {0, 0});
        JsonUtils.addDefaultJsonProperty(data, "uvSize", new int[] {0, 0});
        JsonUtils.addDefaultJsonProperty(data, "ctHorizontal", 0);
        JsonUtils.addDefaultJsonProperty(data, "ctVertical", 0);
        JsonUtils.addDefaultJsonProperty(data, "buttonFunction", -1);

        super.bakeData(gui, data);

        if( initialize ) {
            this.pageStack = GuiTcuRegistry.INSTANCE.getGuiEntry(this.page).getIcon();

            if( this.data.label == null ) {
                this.data.label = new GuiElementInst();
                this.data.label.element = new Label();
                gui.getDefinition().initElement(this.data.label);
                this.data.label.get().bakeData(gui, this.data.label.data);
            }
        }
    }

    @Override
    public void performAction(IGui gui, int id) {
        TurretModRebirth.proxy.openGui(gui.get().mc.player, EnumGui.TCU, ((IGuiTcuInst) gui).getTurretInst().get().getEntityId(),
                                       GuiTcuRegistry.GUI_ENTRIES.indexOf(this.page), 0);
    }

    public class Label
            extends Item
            implements IButtonLabel
    {
        @Override
        protected ItemStack getBakedStack(IGui gui, JsonObject data) {
            return ItemStack.EMPTY;
        }

        @Override
        protected ItemStack getDynamicStack(IGui gui) {
            return ButtonNav.this.pageStack;
        }

        @Override
        public int getWidth() {
            return 16;
        }

        @Override
        public int getHeight() {
            return 16;
        }

        @Override
        public void renderLabel(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data, boolean enabled, boolean hovered) {
            if( ButtonNav.this.pageStack == null ) {
                ButtonNav.this.pageStack = new ItemStack(Blocks.BARRIER, 1);
            }

            if( enabled && !hovered ) {
                SHADER_GRAYSCALE.render(() -> super.render(gui, partTicks, x, y, mouseX, mouseY, data), 1.0F);
            } else {
                super.render(gui, partTicks, x, y, mouseX, mouseY, data);
            }
        }
    }
}
