/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.element.assembly;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssembly;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AssemblyRecipes
        extends ScrollArea
{
    private final String group;

    private ResourceLocation areaTexture;
    private int[]            areaTextureUV;
    private int[]            areaTextureSize;
    private int              columns;

    AssemblyRecipes(String group) {
        this.group = group;
    }

    @Override
    public GuiElementInst[] getElements(IGui gui, JsonObject elementData) {
        List<IAssemblyRecipe> recipes = AssemblyManager.INSTANCE.getRecipes(this.group);
        Map<Integer, List<IAssemblyRecipe>> rowMap = new HashMap<>();
        List<GuiElementInst> newRows = new ArrayList<>();

        for( int cellId = 0, max = recipes.size() - 1; cellId <= max; cellId++ ) {
            int rowId = cellId / this.columns;
            int colId = (cellId % this.columns) + 1;
            List<IAssemblyRecipe> rowRecipes = rowMap.computeIfAbsent(rowId, k -> new ArrayList<>());
            rowRecipes.add(recipes.get(cellId));
            if( colId == this.columns || cellId == max ) {
                GuiElementInst rowInst = new GuiElementInst();
                rowInst.pos = new int[] { 0, rowId * 18 };
                rowInst.element = new Row(rowRecipes.toArray(new IAssemblyRecipe[0]));
                newRows.add(rowInst);
            }
        }

        return newRows.toArray(new GuiElementInst[0]);
    }

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.areaTexture = gui.getDefinition().getTexture(data.get("areaTexture"));
        this.areaTextureUV = JsonUtils.getIntArray(data.get("areaTextureUV"), Range.is(2));
        this.areaTextureSize = JsonUtils.getIntArray(data.get("areaTextureSize"), new int[] { 256, 256 }, Range.is(2));
        this.columns = JsonUtils.getIntVal(data.get("itemColumns"), 6);

        int rowCount = JsonUtils.getIntVal(data.get("itemRows"), 4);

        JsonUtils.addJsonProperty(data, "areaSize", new int[] { this.columns * 18, rowCount * 18 });
        JsonUtils.addJsonProperty(data, "rasterized", true);

        super.bakeData(gui, data, inst);
    }

    private final class Row
            implements IGuiElement
    {
        private IAssemblyRecipe[] recipes;
        private boolean           isHoveredOver;

        private Row(IAssemblyRecipe[] recipes) {
            this.recipes = recipes;
        }

        @Override
        public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) { }

        @Override
        public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
            this.isHoveredOver = false;

            GuiTurretAssembly gta = (GuiTurretAssembly) gui;
            IAssemblyRecipe currRecipe = gta.assembly.currRecipe;
            int localMouseX = mouseX - gui.getScreenPosX();
            int localMouseY = mouseY - gui.getScreenPosY();

            for( int i = 0; i < this.recipes.length; i++ ) {
                int slotX = x + i * 18;
                RenderUtils.renderStackInGui(this.recipes[i].getRecipeOutput(), slotX + 1, y + 1, 1.0, gui.get().mc.fontRenderer);
                if( currRecipe != null ) {
                    if( this.recipes[i].getId().equals(currRecipe.getId()) ) {
                        gta.currRecipeCoords = new int[] { slotX, y };
                    } else {
                        this.renderDisabledSlot(gta.mc, slotX, y);
                    }
                }

                if( localMouseX >= slotX && localMouseX < slotX + 18 && localMouseY >= y && localMouseY < y + 18 ) {
                    if( currRecipe == null || this.recipes[i].getId().equals(currRecipe.getId()) ) {
                        this.isHoveredOver = true;

                        this.renderHoveredSlot(slotX, y);

                        gta.hoveredRecipe = this.recipes[i];
                        gta.hoveredRecipeCoords = new int[] { slotX, y };
                    }
                }
            }
        }

        private void renderDisabledSlot(Minecraft mc, int x, int y) {
            GlStateManager.disableDepth();
            GlStateManager.colorMask(true, true, true, false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.75F);
            mc.renderEngine.bindTexture(AssemblyRecipes.this.areaTexture);
            Gui.drawModalRectWithCustomSizedTexture(x + 1, y + 1,
                                                    AssemblyRecipes.this.areaTextureUV[0] + x + 1, AssemblyRecipes.this.areaTextureUV[1] + y + 1,
                                                    16, 16,
                                                    AssemblyRecipes.this.areaTextureSize[0], AssemblyRecipes.this.areaTextureSize[1]);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableDepth();
        }

        private void renderHoveredSlot(int x, int y) {
            GlStateManager.disableDepth();
            GlStateManager.colorMask(true, true, true, false);
            Gui.drawRect(x + 1, y + 1, x + 17, y + 17, 0x80FFFFFF);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableDepth();
        }

        @Override
        public boolean mouseClicked(IGui gui, int mouseX, int mouseY, int mouseButton) {
            return this.isHoveredOver && mouseButton == 0 && gui.performAction(this, -1);
        }

        @Override
        public int getWidth() {
            return this.recipes.length * 18;
        }

        @Override
        public int getHeight() {
            return 18;
        }
    }
}
