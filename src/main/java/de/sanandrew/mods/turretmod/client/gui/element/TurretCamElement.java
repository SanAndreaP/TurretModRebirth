package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.TcuInfoScreen;
import de.sanandrew.mods.turretmod.client.renderer.turret.TurretCamera;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

public class TurretCamElement
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "turret_cam");

    public BakedData data;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        if( this.data == null ) {
            this.data = new BakedData();

            this.data.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
            this.data.quality = JsonUtils.getIntVal(data.get("quality"));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(IGui gui, MatrixStack mStack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
        ITurretEntity turretInst = ((TcuInfoScreen) gui).getTurret();

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        TurretCamera.drawTurretCam(turretInst, mStack, 256, x, y, this.data.size[0] * 3, this.data.size[1] * 3);
    }

    @Override
    public int getWidth() {
        return this.data.size[0];
    }

    @Override
    public int getHeight() {
        return this.data.size[1];
    }

    public static final class BakedData
    {
        public int[] size;
        public int   quality;
    }
}
