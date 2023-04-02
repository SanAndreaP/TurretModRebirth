package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.client.util.TmrUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

public class RemoteAccessHealth
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu.remote_access_health");

    private GuiElementInst pgTexture;
    private GuiElementInst tooltip;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.pgTexture = new GuiElementInst(new HealthTexture(), data.getAsJsonObject("heartTexture")).initialize(gui);
        this.pgTexture.pos = JsonUtils.getIntArray(this.pgTexture.data.get("offset"), Range.is(2));
        this.pgTexture.get().bakeData(gui, this.pgTexture.data, this.pgTexture);

        JsonObject ttipData = data.getAsJsonObject("tooltip");
        JsonUtils.addJsonProperty(ttipData, "size", new int[] { 16, 16 });
        this.tooltip = new GuiElementInst(new HealthTooltip(), ttipData).initialize(gui);
        this.tooltip.get().bakeData(gui, this.tooltip.data, this.tooltip);
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        this.pgTexture.get().update(gui, this.pgTexture.data);
        this.tooltip.get().update(gui, this.pgTexture.data);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        this.pgTexture.get().render(gui, partTicks, x + this.pgTexture.pos[0], y + this.pgTexture.pos[1], mouseX, mouseY, this.pgTexture.data);

        int locMouseX = mouseX - gui.getScreenPosX();
        int locMouseY = mouseY - gui.getScreenPosY();
        if( x <= locMouseX && locMouseX < x + 16 && y <= locMouseY && locMouseY < y + 16 ) {
            Gui.drawRect(x, y, x + 16, y + 16, 0x80FFFFFF);
        }

        this.tooltip.get().render(gui, partTicks, x + this.tooltip.pos[0], y + this.tooltip.pos[1], mouseX, mouseY, this.tooltip.data);
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }

    private static final class HealthTooltip
            extends Tooltip
    {

        @Override
        public GuiElementInst getContent(IGui gui, JsonObject data) {
            JsonObject cntData = new JsonObject();
            JsonUtils.addJsonProperty(cntData, "text", JsonUtils.getStringVal(data.get("text")));
            JsonUtils.addJsonProperty(cntData, "color", JsonUtils.getStringVal(data.get("textColor"), "0xFFFFFFFF"));

            return new GuiElementInst(new HealthText(), cntData).initialize(gui);
        }
    }

    private static final class HealthText
            extends Text
    {
        private String health;
        private String maxHealth;

        @Override
        public String getDynamicText(IGui gui, String originalText) {
            return String.format(originalText, this.health, this.maxHealth);
        }

        @Override
        public void update(IGui gui, JsonObject data) {
            super.update(gui, data);

            EntityLivingBase turretL = ((IGuiTcuInst<?>) gui).getTurretInst().get();
            this.health = TmrUtils.DECIMAL_FORMAT.format(turretL.getHealth() / 2.0F);
            this.maxHealth = TmrUtils.DECIMAL_FORMAT.format(turretL.getMaxHealth() / 2.0F);
        }
    }

    private static final class HealthTexture
            extends Texture
    {
        private int progressHeight;

        @Override
        public void update(IGui gui, JsonObject data) {
            super.update(gui, data);

            EntityLivingBase turretL = ((IGuiTcuInst<?>) gui).getTurretInst().get();

            float perc = 1.0F - turretL.getHealth() / turretL.getMaxHealth();
            this.progressHeight = Math.max(0, Math.min(this.size[1], Math.round(perc * this.size[1])));
        }

        @Override
        protected void drawRect(IGui gui) {
            Gui.drawModalRectWithCustomSizedTexture(0, this.progressHeight,
                                                    this.uv[0], this.uv[1] + this.progressHeight,
                                                    this.size[0], this.size[1] - this.progressHeight,
                                                    this.textureSize[0], this.textureSize[1]);
        }

        @Override
        public int getHeight() {
            return this.size[1] - this.progressHeight;
        }
    }
}
