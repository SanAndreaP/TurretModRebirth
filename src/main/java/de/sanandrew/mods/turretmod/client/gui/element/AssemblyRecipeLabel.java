package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.assembly.AssemblyIngredient;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssemblyNEW;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

public class AssemblyRecipeLabel
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("assembly_recipe_label");

    GuiElementInst itemTooltip;

    int borderColor;
    int compactIngredientsColumns;

    int currWidth = 0;
    int currHeight = 0;
    long currTicks = 0L;
    ItemStack[][] ingredients;
    int[] ingredientSize;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.itemTooltip == null ) {
            this.itemTooltip = new GuiElementInst();
            this.itemTooltip.element = new ItemTooltipText();
            this.itemTooltip.data = data.has("itemTooltipData") ? data.get("itemTooltipData").getAsJsonObject() : new JsonObject();
            this.itemTooltip.element.bakeData(gui, this.itemTooltip.data);

            this.borderColor = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("borderColor")));
            this.compactIngredientsColumns = JsonUtils.getIntVal(data.get("compactIngredientsColumns"), 6);
        }
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        this.itemTooltip.element.update(gui, this.itemTooltip.data);

        NonNullList<Ingredient> ingredients = ((GuiTurretAssemblyNEW) gui).hoveredRecipe.getIngredients();
        int max = ingredients.size();
        this.ingredients = new ItemStack[max][];
        this.ingredientSize = new int[max];
        for( int i = 0; i < max; i++ ) {
            AssemblyIngredient aIng = (AssemblyIngredient) ingredients.get(i);
            this.ingredients[i] = aIng.getMatchingStacks();
            this.ingredientSize[i] = aIng.getCount();
        }

        this.currTicks++;
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        GuiTurretAssemblyNEW gta = (GuiTurretAssemblyNEW) gui;
        FontRenderer fr = gta.mc.fontRenderer;
        int origY = y;

        this.itemTooltip.element.render(gui, partTicks, x, y, mouseX, mouseY, this.itemTooltip.data);

        this.currWidth = this.itemTooltip.element.getWidth();
        y += this.itemTooltip.element.getHeight() + 2;
        int yDiv1 = y;
        y += 2;
        int col = 0;

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 200);
        for( int i = 0; this.ingredients != null && i < this.ingredients.length; i++ ) {
            ItemStack[] variants = this.ingredients[i];

            RenderUtils.renderStackInGui(variants[(int) (this.currTicks / 20) % variants.length], x + col * 18 + 1, y + 1, 1.0D, fr, String.format("%d", this.ingredientSize[i]), true);

            col++;
            this.currWidth = Math.max(this.currWidth, col * 18);

            if( col >= this.compactIngredientsColumns && i < this.ingredients.length - 1 ) {
                y += 18;
                col = 0;
            }
        }
        GlStateManager.popMatrix();

        y += 19;
        int yDiv2 = y;
        y += 2;


        this.currHeight = y - origY;

        GlStateManager.disableDepth();
        Gui.drawRect(x - 2, yDiv1, x + this.getWidth() + 2, yDiv1 + 1, this.borderColor);
        Gui.drawRect(x - 2, yDiv2, x + this.getWidth() + 2, yDiv2 + 1, this.borderColor);
        GlStateManager.enableDepth();
    }

    @Override
    public int getWidth() {
        return this.currWidth;
    }

    @Override
    public int getHeight() {
        return this.currHeight;
    }

    public static class ItemTooltipText
            extends Text
    {
        private List<String> lines = Collections.emptyList();
        private int currWidth;

        @Override
        public void bakeData(IGui gui, JsonObject data) {
            if( !data.has("color") ) data.addProperty("color", "0xFFFFFFFF");
            if( !data.has("shadow") ) data.addProperty("shadow", true);

            super.bakeData(gui, data);
        }

        @Override
        public void update(IGui gui, JsonObject data) {
            super.update(gui, data);

            GuiTurretAssemblyNEW gta = (GuiTurretAssemblyNEW) gui;
            this.currWidth = 0;
            this.lines = gui.get().getItemToolTip(gta.hoveredRecipe.getRecipeOutput());
            this.lines.forEach(l -> this.currWidth = Math.max(this.data.fontRenderer.getStringWidth(l), this.currWidth));
        }

        @Override
        public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
            int currY = y;
            for( String line : this.lines ) {
                this.data.text = line;
                super.render(gui, partTicks, x, currY, mouseX, mouseY, data);
                currY += this.data.fontRenderer.FONT_HEIGHT + 1;
            }
        }

        @Override
        public String getBakedText(IGui gui, JsonObject data) {
            return "";
        }

        @Override
        public int getHeight() {
            return (this.data.fontRenderer.FONT_HEIGHT + 1) * this.lines.size() - 2;
        }

        @Override
        public int getWidth() {
            return currWidth;
        }
    }
}
