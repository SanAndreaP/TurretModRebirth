package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.render.world.RenderTurretCam;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

public class TurretCam
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
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        ITurretInst turretInst = ((IGuiTcuInst<?>) gui).getTurretInst();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderTurretCam.drawTurretCam(turretInst, this.data.quality, x, y, this.data.size[0], this.data.size[1]);
    }

    @Override
    public int getWidth() {
        return this.data.size[0];
    }

    @Override
    public int getHeight() {
        return this.data.size[1];
    }

    @SuppressWarnings("WeakerAccess")
    public static final class BakedData
    {
        public int[] size;
        public int   quality;
    }
}
