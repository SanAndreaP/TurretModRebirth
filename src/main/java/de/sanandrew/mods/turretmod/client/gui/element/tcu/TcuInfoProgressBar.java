package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Range;

public final class TcuInfoProgressBar
//        extends Texture
{
//    private final ITcuInfoProvider provider;
//    private int[] uvBg;
//
//    public TcuInfoProgressBar(ITcuInfoProvider provider) {
//        this.provider = provider;
//    }
//
//    @Override
//    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
//        this.uvBg = JsonUtils.getIntArray(data.get("uvBackground"), Range.is(2));
//
//        super.bakeData(gui, data, inst);
//    }
//
//    @Override
//    protected void drawRect(IGui gui, MatrixStack stack) {
////        int barX = Math.max(0, Math.min(this.size[0], MathHelper.ceil((this.provider.getCurrValue() / this.provider.getMaxValue()) * (double)this.size[0])));
////
////        AbstractGui.blit(stack, 0, 0, this.uvBg[0], this.uvBg[1], this.size[0], this.size[1], this.textureSize[0], this.textureSize[1]);
////        AbstractGui.blit(stack, 0, 0, this.uv[0], this.uv[1], barX, this.size[1], this.textureSize[0], this.textureSize[1]);
//    }
}
