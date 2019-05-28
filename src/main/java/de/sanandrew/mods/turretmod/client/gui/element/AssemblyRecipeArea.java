/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AssemblyRecipeArea
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("assembly_recipes");

    private Map<String, GroupData> recipeGroups = null;
    private String currGroup = null;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.recipeGroups == null ) {
            this.recipeGroups = new HashMap<>();
            JsonObject dataArea = data.getAsJsonObject("scrollArea");
            for( String grp : AssemblyManager.INSTANCE.getGroups() ) {
                this.recipeGroups.put(grp, new GroupData(gui, grp, dataArea));

                if( this.currGroup == null ) {
                    this.currGroup = grp;
                }
            }
        }
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        GroupData grpData = this.recipeGroups.get(this.currGroup);
        grpData.area.render(gui, partTicks, x, y, mouseX + x - gui.getScreenPosX(), mouseY + y - gui.getScreenPosY(), grpData.data);
    }

    @Override
    public void handleMouseInput(IGui gui) throws IOException {
        this.recipeGroups.get(this.currGroup).area.handleMouseInput(gui);
    }

    @Override
    public int getHeight() {
        return this.recipeGroups.get(this.currGroup).area.getHeight();
    }

    private static final class GroupData
    {
        AssemblyRecipes area;
        JsonObject data;

        GroupData(IGui gui, String grp, JsonObject data) {
            this.area = new AssemblyRecipes(grp);
            this.data = deepCopy(data);
            this.area.bakeData(gui, this.data);
        }

        private static JsonObject deepCopy(JsonObject obj) {
            return JsonUtils.GSON.fromJson(JsonUtils.GSON.toJson(obj), JsonObject.class);
        }
    }
}
