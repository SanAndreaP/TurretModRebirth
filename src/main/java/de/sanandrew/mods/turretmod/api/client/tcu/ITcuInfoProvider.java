package de.sanandrew.mods.turretmod.api.client.tcu;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ITcuInfoProvider
{
    int[] DEFAULT_ICON_SIZE = new int[] {16, 16};
    int[] DEFAULT_TEXTURE_SIZE = { 256, 256};

    @Nullable
    ITextComponent getLabel();

    void tick(ITurretEntity turret);

    @Nullable
    ITextComponent getValueStr();

    float getCurrValue();

    float getMaxValue();

    @Nonnull
    ITexture buildIcon();

    @Nullable
    ITexture getProgressBar();

    @Nonnull
    default int[] getTextureSize() {
        return DEFAULT_TEXTURE_SIZE;
    }

    default void render(Screen gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) { }

    default boolean useStandardRenderer() {
        return true;
    }

    default boolean useCustomRenderer() {
        return false;
    }

    default boolean isVisible() {
        return true;
    }

    interface ITexture {
        @Nullable
        default ResourceLocation getTexture() {
            return null;
        }

        default int[] getTextureSize() {
            return DEFAULT_TEXTURE_SIZE;
        }

        int[] getSize(int maxWidth, int maxHeight);

        int[] getUV(int maxWidth, int maxHeight);

        default int[] getBackgroundUV(int maxWidth, int maxHeight) {
            return null;
        }
    }

    interface Custom
        extends ITcuInfoProvider
    {
        @Override
        default float getCurrValue() {
            return 0.0F;
        }

        @Override
        default float getMaxValue() {
            return 0.0F;
        }

        @Nullable
        @Override
        default ITexture getProgressBar() {
            return null;
        }

        @Nullable
        @Override
        default ITextComponent getLabel() {
            return null;
        }

        @Nullable
        @Override
        default ITextComponent getValueStr() {
            return null;
        }

        @Override
        void render(Screen gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight);

        @Override
        default boolean useStandardRenderer() {
            return false;
        }

        @Override
        default boolean useCustomRenderer() {
            return true;
        }
    }
}
