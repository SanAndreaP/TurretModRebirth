package de.sanandrew.mods.turretmod.client.gui.tcu;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuScreen;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;

import javax.annotation.Nonnull;

public abstract class JsonTcuPage
        implements IGui, ITcuScreen
{
    final ContainerScreen<TcuContainer> tcuScreen;
    final ITurretEntity turret;
    final boolean       isRemote;
    final GuiDefinition guiDefinition = this.buildGuiDefinition();

    Minecraft mc;
    int leftPos;
    int topPos;

    JsonTcuPage(ContainerScreen<TcuContainer> tcuScreen) {
        this.tcuScreen = tcuScreen;

        this.turret = tcuScreen.getMenu().turret;
        this.isRemote = tcuScreen.getMenu().isRemote;

        if( this.guiDefinition != null ) {
            this.guiDefinition.width = tcuScreen.getXSize();
            this.guiDefinition.height = tcuScreen.getYSize();
        }
    }

    protected abstract GuiDefinition buildGuiDefinition();

    @Override
    public void init(Minecraft mc, int leftPos, int topPos) {
        this.mc = mc;
        this.leftPos = leftPos;
        this.topPos = topPos;

        if (GuiDefinition.initialize(this.guiDefinition, this)) {
            this.initGd();
            this.tick();
        }
    }

    protected void initGd() { }

    @Override
    public void tick() {
        this.guiDefinition.tick(this);
    }

    @Override
    public void render(@Nonnull MatrixStack mStack, int mouseX, int mouseY, float partTicks) {
        this.guiDefinition.drawBackground(this, mStack, mouseX, mouseY, partTicks);
        this.guiDefinition.drawForeground(this, mStack, mouseX, mouseY, partTicks);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        return this.guiDefinition.mouseClicked(this, mx, my, btn);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        return this.guiDefinition.mouseDragged(this, mx, my, btn, dx, dy);
    }

    @Override
    public boolean mouseReleased(double mx, double my, int btn) {
        return this.guiDefinition.mouseReleased(this, mx, my, btn);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scroll) {
        return this.guiDefinition.mouseScrolled(this, mx, my, scroll);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return this.guiDefinition.keyPressed(this, keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return this.guiDefinition.keyReleased(this, keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char typedChar, int keyCode) {
        return this.guiDefinition.charTyped(this, typedChar, keyCode);
    }

    @Override
    public void onClose() {
        this.guiDefinition.onClose(this);
    }

    @Override
    public Screen get() {
        return this.tcuScreen;
    }

    @Override
    public GuiDefinition getDefinition() {
        return this.guiDefinition;
    }

    @Override
    public int getScreenPosX() {
        return this.leftPos;
    }

    @Override
    public int getScreenPosY() {
        return this.topPos;
    }
}
