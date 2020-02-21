package de.sanandrew.mods.turretmod.client.gui.element.tcu.shieldcolor;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.event.RenderForcefieldHandler;
import de.sanandrew.mods.turretmod.client.render.ForcefieldCube;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import java.util.Collections;

public class ShieldRender
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu.colorizer_shield");

    private int cubeSize;

    private ForcefieldCube cube;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.cubeSize = JsonUtils.getIntVal(data.get("cubeSize"), 61);
        this.cube = getCube(new ColorObj(0));
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        float cubeSizeF = this.cubeSize;
        cubeSizeF /= 3.0F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, cubeSizeF + (cubeSizeF / 2.0F) + y, cubeSizeF * 2);
        GlStateManager.scale(cubeSizeF, cubeSizeF, cubeSizeF);
        GlStateManager.rotate(22.5F, 1, 0, 0);
        GlStateManager.rotate(45, 0, 1, 0);
        RenderForcefieldHandler.INSTANCE.renderCubes(Collections.singletonList(this.cube), gui.get().mc, partTicks);
        GlStateManager.enableBlend();
        GlStateManager.popMatrix();
    }

    @Override
    public int getWidth() {
        return (int) (this.cubeSize * 1.25F);
    }

    @Override
    public int getHeight() {
        return this.cubeSize;
    }

    public void setColor(int color) {
        this.cube = getCube(new ColorObj(color));
    }

    private static ForcefieldCube getCube(ColorObj color) {
        return new ForcefieldCube(new Vec3d(0, 0, 0), new AxisAlignedBB(-1, -1, -1, 1, 1, 1), color, true);
    }
}
