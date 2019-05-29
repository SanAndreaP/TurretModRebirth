/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssemblyNEW;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class AssemblyGroupButton
        extends Texture
{
    public static final ResourceLocation ID = new ResourceLocation("assembly_group_button");

    private String group = "upgrades";

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        super.render(gui, partTicks, x, y, mouseX, mouseY, data);

        mouseX -= gui.getScreenPosX();
        mouseY -= gui.getScreenPosY();
        if( mouseX >= x && mouseX < x + 10 && mouseY >= y && mouseY < y + 10 && Mouse.isButtonDown(0) ) {
            ((GuiTurretAssemblyNEW) gui).currGroup = this.group;
        }
    }
}
