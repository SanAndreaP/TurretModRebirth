/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;

public class AssemblyProgressBar
        extends Texture
{
    private GuiElementInst label;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            JsonElement lblElem = data.get("label");
            if( lblElem != null ) {
                this.label = JsonUtils.GSON.fromJson(lblElem, GuiElementInst.class);
                this.label.get().bakeData(gui, this.label.data);
            }

        }
        super.bakeData(gui, data);
    }

    //TODO: update SanLib to expose this method
    void drawRect(IGui gui) {

    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}
