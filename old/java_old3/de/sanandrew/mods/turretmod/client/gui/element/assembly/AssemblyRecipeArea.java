/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.element.assembly;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssembly;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import java.util.Arrays;
import java.util.Map;

public class AssemblyRecipeArea
        extends ElementParent<String>
{
    public static final ResourceLocation ID = new ResourceLocation("sapturretmod:assembly.recipes");

    private int width;
    private int height;

    private GuiElementInst activeRecipeMarker;
    private GuiElementInst recipeTooltip;

    @Override
    public void buildChildren(IGui gui, JsonObject data, Map<String, GuiElementInst> listToBuild) {
        JsonObject dataArea = data.getAsJsonObject("scrollArea");
        for( String grp : AssemblyManager.INSTANCE.getGroups() ) {
            GuiElementInst grpInst = new GuiElementInst(new AssemblyRecipes(grp), dataArea).initialize(gui);
            grpInst.setVisible(false);
            listToBuild.put(grp, grpInst);

            if( GuiTurretAssembly.currGroup == null ) {
                GuiTurretAssembly.currGroup = grp;
            }
        }
    }

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        JsonObject arm = data.getAsJsonObject("activeRecipeMarker");
        this.activeRecipeMarker = new GuiElementInst(JsonUtils.getIntArray(arm.get("offset"), Range.is(2)), new Texture(), arm).initialize(gui);
        this.activeRecipeMarker.get().bakeData(gui, this.activeRecipeMarker.data, this.activeRecipeMarker);

        this.recipeTooltip = new GuiElementInst(new RecipeTooltip(), data.getAsJsonObject("recipeTooltip")).initialize(gui);
        this.recipeTooltip.get().bakeData(gui, this.recipeTooltip.data, this.recipeTooltip);

        super.bakeData(gui, data, inst);
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        if( GuiTurretAssembly.currGroup == null ) {
            return;
        }

        GuiElementInst grpInst = this.getChild(GuiTurretAssembly.currGroup);
        AssemblyRecipes grpImpl = grpInst.get(AssemblyRecipes.class);

        GuiTurretAssembly gta = (GuiTurretAssembly) gui;
        this.width = grpImpl.getWidth();
        this.height = grpImpl.getHeight();

        Arrays.stream(this.getChildren()).forEach(e -> e.setVisible(false));
        grpInst.setVisible(true);

        if( gta.hoveredRecipe != null && gta.hoveredRecipeCoords != null ) {
            this.recipeTooltip.get().update(gui, this.recipeTooltip.data);
        }

        super.update(gui, data);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        super.render(gui, partTicks, x, y, mouseX, mouseY, data);

        GuiTurretAssembly gta = (GuiTurretAssembly) gui;

        if( gta.currRecipeCoords != null ) {
            GuiDefinition.renderElement(gui, this.activeRecipeMarker.pos[0] + gta.currRecipeCoords[0], this.activeRecipeMarker.pos[1] + gta.currRecipeCoords[1],
                                        mouseX, mouseY, partTicks, this.activeRecipeMarker);
        }

        if( gta.hoveredRecipe != null && gta.hoveredRecipeCoords != null ) {
            GuiDefinition.renderElement(gui, gta.hoveredRecipeCoords[0], gta.hoveredRecipeCoords[1], mouseX, mouseY, partTicks, this.recipeTooltip);
        }
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    private static final class RecipeTooltip
            extends Tooltip
    {
        @Override
        public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
            JsonUtils.addJsonProperty(data, "size", new int[] { 18, 18});

            super.bakeData(gui, data, inst);
        }

        @Override
        public GuiElementInst getContent(IGui gui, JsonObject data) {
            return new GuiElementInst(new AssemblyRecipeLabel(), data.getAsJsonObject("label")).initialize(gui);
        }
    }
}
