package de.sanandrew.mods.turretmod.api.client.tcu;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public interface ITcuInfoProvider
{
    int[] DEFAULT_TEXTURE_SIZE = { 256, 256 };

    String getName();

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
    ITexture buildProgressBar();

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

        int[] getUV(int maxWidth, int maxHeight);

        default int[] getBackgroundUV(int maxWidth, int maxHeight) {
            return null;
        }

        default int[] getMargins() {
            return new int[4];
        }

        static ITexture icon(BiFunction<Integer, Integer, int[]> uv) {
            return icon(uv, () -> null, new int[4]);
        }

        static ITexture icon(BiFunction<Integer, Integer, int[]> uv, int[] margins) {
            return icon(uv, () -> null, margins);
        }

        static ITexture icon(BiFunction<Integer, Integer, int[]> uv, Supplier<ResourceLocation> texture) {
            return icon(uv, texture, new int[4]);
        }

        static ITexture icon(BiFunction<Integer, Integer, int[]> uv, Supplier<ResourceLocation> texture, int[] margins) {
            return new ITexture() {
                @Override
                public int[] getUV(int maxWidth, int maxHeight) {
                    return uv.apply(maxWidth, maxHeight);
                }

                @Nullable
                @Override
                public ResourceLocation getTexture() {
                    return texture.get();
                }

                @Override
                public int[] getMargins() {
                    return margins;
                }
            };
        }

        static ITexture progressBar(BiFunction<Integer, Integer, int[]> uv, BiFunction<Integer, Integer, int[]> bgUv) {
            return progressBar(uv, bgUv, () -> null, new int[4]);
        }

        static ITexture progressBar(BiFunction<Integer, Integer, int[]> uv, BiFunction<Integer, Integer, int[]> bgUv, int[] margins) {
            return progressBar(uv, bgUv, () -> null, margins);
        }

        static ITexture progressBar(BiFunction<Integer, Integer, int[]> uv, BiFunction<Integer, Integer, int[]> bgUv,
                                    Supplier<ResourceLocation> texture)
        {
            return progressBar(uv, bgUv, texture, new int[4]);
        }

        static ITexture progressBar(BiFunction<Integer, Integer, int[]> uv, BiFunction<Integer, Integer, int[]> bgUv,
                                    Supplier<ResourceLocation> texture, int[] margins)
        {
            return new ITexture() {
                @Override
                public int[] getUV(int maxWidth, int maxHeight) {
                    return uv.apply(maxWidth, maxHeight);
                }

                @Override
                public int[] getBackgroundUV(int maxWidth, int maxHeight) {
                    return bgUv.apply(maxWidth, maxHeight);
                }

                @Nullable
                @Override
                public ResourceLocation getTexture() {
                    return texture.get();
                }

                @Override
                public int[] getMargins() {
                    return margins;
                }
            };
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
        default ITexture buildProgressBar() {
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
