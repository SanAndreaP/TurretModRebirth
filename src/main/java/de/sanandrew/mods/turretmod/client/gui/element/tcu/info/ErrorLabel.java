package de.sanandrew.mods.turretmod.client.gui.element.tcu.info;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.gui.ChatFormatting;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Label;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.util.ResourceLocation;

public class ErrorLabel
        extends Label
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_info_error");

    private long errTimeActivated;
    private int errTimeDurationMS;
    private boolean shown;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            this.errTimeDurationMS = JsonUtils.getIntVal(data.get("timeShown"), 5000);

            JsonUtils.addDefaultJsonProperty(data, "backgroundColor", "0xF0100000");
            JsonUtils.addDefaultJsonProperty(data, "borderTopColor", "0x50FF0000");
            JsonUtils.addDefaultJsonProperty(data, "borderBottomColor", "0x507F0000");
        }

        super.bakeData(gui, data);
    }

    @Override
    public GuiElementInst getLabel(IGui gui, JsonObject data) {
        if( !data.has("content") ) {
            GuiElementInst txtInst = new GuiElementInst();
            txtInst.element = new Text();
            txtInst.data = new JsonObject();

            JsonUtils.addJsonProperty(txtInst.data, "text", JsonUtils.getStringVal(data.get("text")));
            JsonUtils.addJsonProperty(txtInst.data, "color", JsonUtils.getStringVal(data.get("color"), "0xFFFF8080"));
            JsonUtils.addJsonProperty(txtInst.data, "wrapWidth", JsonUtils.getIntVal(data.get("wrapWidth"), gui.getDefinition().width));

            gui.getDefinition().initElement(txtInst);
            txtInst.get().bakeData(gui, txtInst.data);

            return txtInst;
        }
        return super.getLabel(gui, data);
    }

    public void activate() {
        this.errTimeActivated = System.currentTimeMillis();
    }

    public void deactivate() {
        this.errTimeActivated = System.currentTimeMillis() - this.errTimeDurationMS;
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        super.update(gui, data);

        this.shown = this.errTimeActivated >= System.currentTimeMillis() - this.errTimeDurationMS;
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        super.render(gui, partTicks, x, y + 24, mouseX, mouseY + 24, data);
    }

    @Override
    public boolean isVisible() {
        return this.shown && super.isVisible();
    }
}
