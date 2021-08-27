package de.sanandrew.mods.turretmod.api.client.tcu;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public interface IIcon
{
    int DEFAULT_TEXTURE_WIDTH = 256;
    int DEFAULT_TEXTURE_HEIGHT = 256;

    @Nullable
    default ResourceLocation getTexture() {
        return null;
    }

    default int[] getTextureSize() {
        return new int[] { DEFAULT_TEXTURE_WIDTH, DEFAULT_TEXTURE_HEIGHT };
    }

    int[] getUV(int maxWidth, int maxHeight);

    int[] getSize(int maxWidth, int maxHeight);

    int[] getOffset(int maxWidth, int maxHeight);

    static IIcon get(BiFunction<Integer, Integer, int[]> uv) {
        return get(uv, () -> null);
    }

    static IIcon get(BiFunction<Integer, Integer, int[]> uv, Supplier<ResourceLocation> texture) {
        return new IIcon()
        {
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
            public int[] getSize(int maxWidth, int maxHeight) {
                return new int[] { 16, 16 };
            }

            @Override
            public int[] getOffset(int maxWidth, int maxHeight) {
                return new int[] { 0, 0 };
            }
        };
    }
}
