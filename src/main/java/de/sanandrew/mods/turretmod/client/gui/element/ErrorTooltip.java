package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.util.ResourceLocation;

public class ErrorTooltip
        extends Tooltip
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "error_tooltip");

    private long errTimeActivated;
    private int errTimeDurationMS;
    private boolean shown;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.errTimeDurationMS = JsonUtils.getIntVal(data.get("timeShown"), 5000);

        JsonUtils.addDefaultJsonProperty(data, "backgroundColor", "0xF0100000");
        JsonUtils.addDefaultJsonProperty(data, "borderTopColor", "0x50FF0000");
        JsonUtils.addDefaultJsonProperty(data, "borderBottomColor", "0x507F0000");

        super.bakeData(gui, data, inst);
    }

    @Override
    public GuiElementInst getContent(IGui gui, JsonObject data) {
        if( !data.has("content") ) {
            GuiElementInst txtInst = new GuiElementInst(new Text()).initialize(gui);

            JsonUtils.addJsonProperty(txtInst.data, "text", JsonUtils.getStringVal(data.get("text")));
            JsonUtils.addJsonProperty(txtInst.data, "color", JsonUtils.getStringVal(data.get("color"), "0xFFFF8080"));
            JsonUtils.addJsonProperty(txtInst.data, "wrapWidth", JsonUtils.getIntVal(data.get("wrapWidth"), gui.getDefinition().width));

            return txtInst;
        }

        return super.getContent(gui, data);
    }

    public void activate() {
        this.errTimeActivated = System.currentTimeMillis();
        this.shown = true;
    }

    public void deactivate() {
        this.errTimeActivated = System.currentTimeMillis() - this.errTimeDurationMS;
        this.shown = false;
    }

    @Override
    public void tick(IGui gui, JsonObject data) {
        super.tick(gui, data);

        this.shown = this.errTimeActivated >= System.currentTimeMillis() - this.errTimeDurationMS;
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, JsonObject data) {
        super.render(gui, stack, partTicks, x, y + 24, mouseX, mouseY + 24, data);
    }

    @Override
    public boolean isVisible() {
        return this.shown;
    }
}
