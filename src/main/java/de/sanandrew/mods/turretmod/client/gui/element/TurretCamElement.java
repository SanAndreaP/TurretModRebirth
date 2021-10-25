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
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuInfoPage;
import de.sanandrew.mods.turretmod.client.renderer.turret.TurretCamera;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

public class TurretCamElement
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "turret_cam");

    private final int[] size;

    public TurretCamElement(int[] size) {
        this.size = size;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void render(IGui gui, MatrixStack mStack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst e) {
        ITurretEntity turretInst = ((TcuInfoPage) gui).getTurret();

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        TurretCamera.drawTurretCam(turretInst, mStack, x, y, this.size[0], this.size[1]);
    }

    @Override
    public int getWidth() {
        return this.size[0];
    }

    @Override
    public int getHeight() {
        return this.size[1];
    }

    public static class Builder
            implements IBuilder<TurretCamElement>
    {
        public final int[] size;

        public Builder(int[] size) {
            this.size = size;
        }


        @Override
        public void sanitize(IGui gui) {
            // no-op
        }

        @Override
        public TurretCamElement get(IGui gui) {
            return new TurretCamElement(this.size);
        }

        @SuppressWarnings("unused")
        public static Builder buildFromJson(IGui gui, JsonObject data) {
            return new Builder(JsonUtils.getIntArray(data.get("size"), Range.is(2)));
        }

        public static TurretCamElement fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
