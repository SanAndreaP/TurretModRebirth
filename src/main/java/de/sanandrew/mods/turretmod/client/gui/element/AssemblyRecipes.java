/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssemblyNEW;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AssemblyRecipes
        extends ScrollArea
{
    private final String group;

    private GuiElementInst[] rows;

    AssemblyRecipes(String group) {
        this.group = group;
    }

    @Override
    protected GuiElementInst[] getElements(IGui gui, JsonObject elementData) {
        return this.rows;
    }

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            if( this.rows == null ) {
                List<IAssemblyRecipe> recipes = AssemblyManager.INSTANCE.getRecipes(this.group);
                int rowCount = JsonUtils.getIntVal(data.get("itemRows"), 4);
                int cols = JsonUtils.getIntVal(data.get("itemColumns"), 6);
                Map<Integer, List<IAssemblyRecipe>> rowMap = new HashMap<>();
                List<GuiElementInst> newRows = new ArrayList<>();

                for( int cellId = 0, max = recipes.size() - 1; cellId <= max; cellId++ ) {
                    int rowId = cellId / cols;
                    int colId = (cellId % cols) + 1;
                    List<IAssemblyRecipe> rowRecipes = rowMap.computeIfAbsent(rowId, k -> new ArrayList<>());
                    rowRecipes.add(recipes.get(cellId));
                    if( colId == cols || cellId == max ) {
                        GuiElementInst rowInst = new GuiElementInst();
                        rowInst.pos = new int[] {0, rowId * 18};
                        rowInst.element = new Row(rowRecipes.toArray(new IAssemblyRecipe[0]));
                        newRows.add(rowInst);
                    }
                }

                this.rows = newRows.toArray(new GuiElementInst[0]);

                JsonArray areaSize = new JsonArray();
                areaSize.add(cols * 18);
                areaSize.add(rowCount * 18);
                data.add("areaSize", areaSize);
            }

            data.addProperty("rasterized", true);
        }

        super.bakeData(gui, data);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        super.render(gui, partTicks, x, y, mouseX, mouseY, data);
    }

    private static final class Row
            implements IGuiElement
    {
        private IAssemblyRecipe[] recipes;

        Row(IAssemblyRecipe[] recipes) {
            this.recipes = recipes;
        }

        @Override
        public void bakeData(IGui gui, JsonObject data) { }

        @Override
        public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
            Gui.drawRect(mouseX, mouseY, mouseX + 10, mouseY + 10, 0xFFFF00FF);
            for( int i = 0; i < this.recipes.length; i++ ) {
                int stackX = x + 1 + i * 18;
                int stackY = y + 1;
                RenderUtils.renderStackInGui(this.recipes[i].getRecipeOutput(), stackX, stackY, 1.0, gui.get().mc.fontRenderer);
                if( mouseX >= stackX && mouseX < stackX + 16 && mouseY >= stackY && mouseY < stackY + 16 ) {
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    GlStateManager.colorMask(true, true, true, false);
                    Gui.drawRect(stackX, stackY, stackX + 16, stackY + 16, 0x80FFFFFF);
                    GlStateManager.colorMask(true, true, true, true);
                    GlStateManager.enableDepth();
                    GlStateManager.enableLighting();

                    ((GuiTurretAssemblyNEW) gui).hoveredRecipe = this.recipes[i];
                }
            }
        }

        @Override
        public int getHeight() {
            return 18;
        }
    }
}
