/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.util.ResourceLocation;

public class ButtonTextLabel
        extends Text
        implements IButtonLabel
{
    public static final ResourceLocation ID = new ResourceLocation("button_text");

    protected int colorEnabled;
    protected int colorHover;
    protected int colorDisabled;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        boolean init = this.data == null;

        super.bakeData(gui, data);

        if( init ) {
            this.data.shadow = JsonUtils.getBoolVal(data.get("shadow"), true);
            this.colorEnabled = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("color"), "0xFFFFFFFF"));
            this.colorHover = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("colorHover"), "0xFFFFFFA0"));
            this.colorDisabled = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("colorHover"), "0xFFA0A0A0"));
        }
    }

    @Override
    public void renderLabel(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data, boolean enabled, boolean hovered) {
        this.data.color = enabled ? (hovered ? this.colorHover : this.colorEnabled) : this.colorDisabled;

        super.render(gui, partTicks, x, y, mouseX, mouseY, data);
    }
}
