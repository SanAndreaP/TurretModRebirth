/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssemblyRecipes
        extends ScrollArea
{
    GuiElementInst[] rows;

    @Override
    protected GuiElementInst[] getElements(IGui gui, JsonObject elementData) {
        return this.rows;
    }

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            if( this.rows == null ) {
                int cols = JsonUtils.getIntVal(data.get("itemColumns"), 5);
                List<IAssemblyRecipe> recipes = AssemblyManager.INSTANCE.getRecipes();
                Map<Integer, List<IAssemblyRecipe>> rows = new HashMap<>();
                List<GuiElementInst> newRows = new ArrayList<>();

                for( int cellId = 0, max = recipes.size(); cellId < recipes.size(); cellId++ ) {
                    int rowId = cellId / cols;
                    int colId = (cellId % cols) + 1;
                    List<IAssemblyRecipe> rowRecipes = rows.computeIfAbsent(rowId, k -> new ArrayList<>());
                    rowRecipes.add(recipes.get(cellId));
                    if( colId == cols ) {
                        GuiElementInst rowInst = new GuiElementInst();
                        rowInst.element = new Row(rowRecipes.toArray(new IAssemblyRecipe[0]));
                        newRows.add(rowInst);
                    }
                }

                this.rows = newRows.toArray(new GuiElementInst[0]);
            }

            data.addProperty("rasterized", true);
        }

        super.bakeData(gui, data);
    }

    private final class Row
            implements IGuiElement
    {
        private IAssemblyRecipe[] recipes;

        Row(IAssemblyRecipe[] recipes) {
            this.recipes = recipes;
        }

        @Override
        public void bakeData(IGui gui, JsonObject data) {

        }

        @Override
        public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {

        }

        @Override
        public int getHeight() {
            return 0;
        }

        @Override
        public void handleMouseInput(IGui gui) throws IOException {

        }
    }
}
