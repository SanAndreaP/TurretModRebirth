package de.sanandrew.mods.turretmod.client.gui.element.tcu.nav;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Button;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Item;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.EnumGui;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.client.shader.ShaderGrayscale;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.registry.turret.GuiTcuRegistry;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class ButtonNav
        extends Button
{
    private static final ShaderGrayscale SHADER_GRAYSCALE = new ShaderGrayscale(TextureMap.LOCATION_BLOCKS_TEXTURE);

    public ResourceLocation pageKey;
    int pageIdx;

    private ItemStack pageStack = ItemStack.EMPTY;

    ButtonNav(int id, ResourceLocation pageKey) {
        this.pageIdx = id;
        this.pageKey = pageKey;
    }

    @Override
    public void buildChildren(IGui gui, JsonObject data, Map<String, GuiElementInst> listToBuild) {
        listToBuild.put(LABEL, new GuiElementInst(new Label()).initialize(gui));
    }

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        JsonUtils.addDefaultJsonProperty(data, "size", new int[] { 16, 16 });
        JsonUtils.addDefaultJsonProperty(data, "uvEnabled", new int[] { 0, 0 });
        JsonUtils.addDefaultJsonProperty(data, "uvDisabled", new int[] { 0, 0 });
        JsonUtils.addDefaultJsonProperty(data, "uvHover", new int[] { 0, 0 });
        JsonUtils.addDefaultJsonProperty(data, "uvSize", new int[] { 0, 0 });
        JsonUtils.addDefaultJsonProperty(data, "centralTextureWidth", 0);
        JsonUtils.addDefaultJsonProperty(data, "centralTextureHeight", 0);
        JsonUtils.addDefaultJsonProperty(data, "buttonFunction", -1);

        super.bakeData(gui, data, inst);

        this.pageStack = GuiTcuRegistry.INSTANCE.getPage(this.pageKey).getIcon();
    }

    boolean isHovering() {
        return this.isCurrHovering;
    }

    @Override
    public void performAction(IGui gui, int id) {
        IGuiTcuInst<?> tcu = (IGuiTcuInst<?>) gui;
        TurretModRebirth.proxy.openGui(gui.get().mc.player, EnumGui.TCU, tcu.getTurretInst().get().getEntityId(),
                                       GuiTcuRegistry.PAGE_KEYS.indexOf(this.pageKey), tcu.isRemote() ? 1 : 0);
    }

    public class Label
            extends Item
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
        public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
            if( ButtonNav.this.pageStack == null ) {
                ButtonNav.this.pageStack = new ItemStack(Blocks.BARRIER, 1);
            }

            if( ButtonNav.this.isEnabled() && !ButtonNav.this.isCurrHovering ) {
                SHADER_GRAYSCALE.render(() -> super.render(gui, partTicks, x, y, mouseX, mouseY, data), 1.0F);
            } else {
                super.render(gui, partTicks, x, y, mouseX, mouseY, data);
            }
        }
    }
}
