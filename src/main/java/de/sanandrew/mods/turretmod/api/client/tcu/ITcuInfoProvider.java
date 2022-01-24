package de.sanandrew.mods.turretmod.api.client.tcu;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiReference;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.TcuInfoValue;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

/*

|--------|--------------------------------|
|        |                                |
|  icon  | 5 / 10 HP                      |
|        | |============================| |
|--------|--------------------------------|

 */


@SuppressWarnings("unused")
public interface ITcuInfoProvider
        extends IGuiReference
{
    String OFFSET_JSON_ELEM = "offset";

    @Nonnull
    String getName();

    default void load(IGui gui, ITurretEntity turret, int w, int h, TcuInfoValue container) { }

    default void setup(IGui gui, ITurretEntity turret, int w, int h) { }

    default void tick(IGui gui, ITurretEntity turret) { }

    default void renderContent(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) { }

    default void renderOutside(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) { }

    default void onClose(IGui gui, ITurretEntity turret) {
        this.onClose(gui);
    }

    default boolean isVisible(ITurretEntity turret) {
        return true;
    }

    default void loadJson(IGui gui, JsonObject data, int w, int h) { }

    //    @SuppressWarnings("java:S2386")
//
//    String getName();
//
//    @Nullable
//    ITextComponent getLabel();
//
//    void tick(IGui gui, ITurretEntity turret);
//
//    @Nullable
//    ITextComponent getValueStr();
//
//    float getCurrValue();
//
//    float getMaxValue();
//
//    @Nonnull
//    ITexture buildIcon();
//
//    @Nullable
//    ITexture buildProgressBar();
//
//    @SuppressWarnings("java:S107")
//    default void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) { }
//
//    @SuppressWarnings("java:S107")
//    default void renderOutside(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) { }
//
//    default void onClose(IGui gui, ITurretEntity turret) {
//        this.onClose(gui);
//    }
//
//    @Nonnull
//    default GuiElementInst[] buildCustomElements(IGui gui, JsonObject data, int maxWidth, int maxHeight) {
//        return new GuiElementInst[0];
//    }
//
//    default boolean useStandardRenderer() {
//        return true;
//    }
//
//    default boolean useCustomRenderer() {
//        return false;
//    }
//
//    default boolean isVisible() {
//        return true;
//    }
//
//    interface ITexture {
//        @Nullable
//        default ResourceLocation getTexture() {
//            return null;
//        }
//
//        default int[] getTextureSize() {
//            return DEFAULT_TEXTURE_SIZE;
//        }
//
//        int[] getUV(int maxWidth, int maxHeight);
//
//        int[] getSize(int maxWidth, int maxHeight);
//
//        int[] getOffset(int maxWidth, int maxHeight);
//
//        @Nullable
//        default int[] getBackgroundUV(int maxWidth, int maxHeight) {
//            return null;
//        }
//
//        static ITexture icon(BiFunction<Integer, Integer, int[]> uv) {
//            return icon(uv, () -> null);
//        }
//
//        static ITexture icon(BiFunction<Integer, Integer, int[]> uv, Supplier<ResourceLocation> texture) {
//            return new ITexture() {
//                @Override
//                public int[] getUV(int maxWidth, int maxHeight) {
//                    return uv.apply(maxWidth, maxHeight);
//                }
//
//                @Nullable
//                @Override
//                public ResourceLocation getTexture() {
//                    return texture.get();
//                }
//
//                @Override
//                public int[] getSize(int maxWidth, int maxHeight) {
//                    return new int[] {16, 16};
//                }
//
//                @Override
//                public int[] getOffset(int maxWidth, int maxHeight) {
//                    return new int[] {0, 0};
//                }
//            };
//        }
//
//        static ITexture progressBar(BiFunction<Integer, Integer, int[]> uv, BiFunction<Integer, Integer, int[]> bgUv) {
//            return progressBar(uv, bgUv, () -> null);
//        }
//
//        static ITexture progressBar(BiFunction<Integer, Integer, int[]> uv, BiFunction<Integer, Integer, int[]> bgUv,
//                                    Supplier<ResourceLocation> texture)
//        {
//            return new ITexture() {
//                @Override
//                public int[] getUV(int maxWidth, int maxHeight) {
//                    return uv.apply(maxWidth, maxHeight);
//                }
//
//                @Override
//                public int[] getBackgroundUV(int maxWidth, int maxHeight) {
//                    return bgUv.apply(maxWidth, maxHeight);
//                }
//
//                @Nullable
//                @Override
//                public ResourceLocation getTexture() {
//                    return texture.get();
//                }
//
//                @Override
//                public int[] getSize(int maxWidth, int maxHeight) {
//                    return new int[] {maxWidth - 20, 3};
//                }
//
//                @Override
//                public int[] getOffset(int maxWidth, int maxHeight) {
//                    return new int[] {18, 11};
//                }
//            };
//        }
//    }
//
//    interface Custom
//        extends ITcuInfoProvider
//    {
//        @Override
//        default float getCurrValue() {
//            return 0.0F;
//        }
//
//        @Override
//        default float getMaxValue() {
//            return 0.0F;
//        }
//
//        @Nullable
//        @Override
//        default ITexture buildProgressBar() {
//            return null;
//        }
//
//        @Nullable
//        @Override
//        default ITextComponent getValueStr() {
//            return null;
//        }
//
//        @Override
//        void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight);
//
//        @Override
//        default boolean useStandardRenderer() {
//            return false;
//        }
//
//        @Override
//        default boolean useCustomRenderer() {
//            return true;
//        }
//    }
}
