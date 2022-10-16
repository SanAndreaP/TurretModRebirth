package de.sanandrew.mods.turretmod.api.client.tcu;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;

public interface ITcuScreen
{
    String OFFSET_JSON_ELEM = "offset";

    default void init(Minecraft mc, int leftPos, int topPos) { }

    default void tick() { }

    void renderBackground(@Nonnull MatrixStack mStack, int mouseX, int mouseY, float partTicks);

    void renderForeground(@Nonnull MatrixStack mStack, int mouseX, int mouseY, float partTicks);

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

    static int[] getOffset(JsonObject data) {
        return getOffset(data, 0, 0);
    }

    static int[] getOffset(JsonObject data, int defX, int defY) {
        return JsonUtils.getIntArray(data.get(OFFSET_JSON_ELEM), new int[] {defX, defY}, Range.is(2));
    }
}
