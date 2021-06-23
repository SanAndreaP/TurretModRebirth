package de.sanandrew.mods.turretmod.client.gui.element.nav;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ButtonSL;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Item;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.TcuScreen;
import de.sanandrew.mods.turretmod.client.shader.ShaderGrayscale;
import de.sanandrew.mods.turretmod.item.TurretControlUnit;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

public class ButtonNav
        extends ButtonSL
{
    private static final ShaderGrayscale SHADER_GRAYSCALE = new ShaderGrayscale(PlayerContainer.BLOCK_ATLAS);

    public final ResourceLocation pageKey;
    public final int order;

    private ItemStack pageStack = ItemStack.EMPTY;

    ButtonNav(ResourceLocation pageKey, int order) {
        this.pageKey = pageKey;
        this.order = order;
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

        this.pageStack = TcuScreen.getIcon(this.pageKey);
        this.setFunction(btn -> {
            PlayerEntity player = Minecraft.getInstance().player;
            ITurretEntity turret = gui instanceof TcuScreen ? ((TcuScreen) gui).getTurret() : null;
            if( player != null && turret != null ) {
                TurretControlUnit.openTcu(null, TurretControlUnit.getHeldTcu(player), turret, this.pageKey);
            }
        });
    }

    boolean isHovering() {
        return this.isCurrHovering;
    }

//    @Override
//    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
//        return super.mouseClicked(gui, mouseX, mouseY, button);
//    }
//
//    @Override
//    public void performAction(IGui gui, int id) {
//
////        IGuiTcuInst<?> tcu = (IGuiTcuInst<?>) gui;
////        TurretModRebirth.proxy.openGui(gui.get().mc.player, EnumGui.TCU, tcu.getTurretInst().get().getEntityId(),
////                                       GuiTcuRegistry.PAGE_KEYS.indexOf(this.pageKey), tcu.isRemote() ? 1 : 0);
//    }

    public class Label
            extends Item
    {
        @Override
        protected ItemStack getBakedItem(IGui gui, JsonObject data) {
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
        public void render(IGui gui, MatrixStack mStack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
            if( ButtonNav.this.pageStack == null ) {
                ButtonNav.this.pageStack = new ItemStack(Blocks.BARRIER, 1);
            }

            if( !ButtonNav.this.isActive() ) {
                super.render(gui, mStack, partTicks, x, y, mouseX, mouseY, data);
            } else if( !ButtonNav.this.isCurrHovering ) {
                SHADER_GRAYSCALE.render(() -> super.render(gui, mStack, partTicks, x, y, mouseX, mouseY, data), 1.0F);
            } else {
                super.render(gui, mStack, partTicks, x, y, mouseX, mouseY, data);
            }
        }
    }
}
