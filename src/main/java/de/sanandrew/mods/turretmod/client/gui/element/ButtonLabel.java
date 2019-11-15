package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Button;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Label;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class ButtonLabel
        extends Label
{
    public static final ResourceLocation ID = new ResourceLocation("button_label");

    private Button linkedBtn;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        GuiElementInst btnElem = gui.getDefinition().getElementById(JsonUtils.getStringVal(data.get("buttonId")));
        this.linkedBtn = btnElem.get(Button.class);
        if( !data.has("size") ) {
            data.add("size", btnElem.data.get("size"));
        }
        if( !data.has("content") && data.has("text") ) {
            JsonObject textObj = new JsonObject();
            JsonObject textData = new JsonObject();

            textData.addProperty("color", "0xFFFFFFFF");
            textData.add("text", data.get("text"));
            textObj.addProperty("type", Text.ID.toString());
            textObj.add("data", textData);

            data.add("content", textObj);
        }

        super.bakeData(gui, data);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        if( this.linkedBtn.isVisible() ) {
            super.render(gui, partTicks, x, y, mouseX, mouseY, data);
        }
    }
}
