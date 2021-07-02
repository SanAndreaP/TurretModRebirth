package de.sanandrew.mods.turretmod.api.client.tcu;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import javafx.stage.Screen;
import net.minecraft.client.Minecraft;

import javax.annotation.Nonnull;

public interface ITcuScreen
{
    default void init(Minecraft mc, int leftPos, int topPos) { }

    default void tick() { }

    void render(@Nonnull MatrixStack mStack, int mouseX, int mouseY, float partTicks);

    default boolean mouseClicked(double mx, double my, int btn) {
        return false;
    }

    default boolean mouseDragged(double mx, double my, int btn, double dx, double dy) {
        return false;
    }

    default boolean mouseReleased(double mx, double my, int btn) {
        return false;
    }

    default boolean mouseScrolled(double mx, double my, double scroll) {
        return false;
    }

    default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    default boolean charTyped(char typedChar, int keyCode) {
        return false;
    }

    default void onClose() { }
}
