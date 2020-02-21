package de.sanandrew.mods.turretmod.client.gui.element.assembly;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.assembly.AssemblyIngredient;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssembly;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;

import java.util.Collections;
import java.util.List;

public class AssemblyRecipeLabel
        implements IGuiElement
{
    private GuiElementInst itemTooltip;
    private GuiElementInst timeIcon;
    private GuiElementInst rfIcon;

    private int borderColor;
    private int compactIngredientsColumns;

    private int           currWidth;
    private int           currHeight;
    private long          currTicks;
    private int           ticksCrafting;
    private int           rfPerTick;
    private ItemStack[][] ingredients;
    private int[]         ingredientSize;
    private boolean       shiftPressed;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.currTicks = 0L;

        this.borderColor = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("borderColor")));
        this.compactIngredientsColumns = JsonUtils.getIntVal(data.get("compactIngredientsColumns"), 6);

        this.itemTooltip = new GuiElementInst(new ItemTooltipText(), data.getAsJsonObject("itemTooltipData")).initialize(gui);
        this.itemTooltip.element.bakeData(gui, this.itemTooltip.data, this.itemTooltip);

        this.timeIcon = new GuiElementInst(new Texture(), data.getAsJsonObject("timeIconData")).initialize(gui);
        JsonUtils.addDefaultJsonProperty(this.timeIcon.data, "size", new int[] { 9, 9 });
        JsonUtils.addDefaultJsonProperty(this.timeIcon.data, "uv", new int[] { 230, 94 });
        this.timeIcon.element.bakeData(gui, this.timeIcon.data, this.timeIcon);

        this.rfIcon = new GuiElementInst(new Texture(), data.getAsJsonObject("rfIconData")).initialize(gui);
        JsonUtils.addJsonProperty(this.rfIcon.data, "size", new int[] { 9, 9 });
        JsonUtils.addJsonProperty(this.rfIcon.data, "uv", new int[] { 230, 103 });
        this.rfIcon.element.bakeData(gui, this.rfIcon.data, this.rfIcon);
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        GuiTurretAssembly gta = (GuiTurretAssembly) gui;
        this.ticksCrafting = gta.getProcessTime(gta.hoveredRecipe);
        this.rfPerTick = gta.getRfPerTick(gta.hoveredRecipe);
        this.shiftPressed = gta.isShiftPressed();

        NonNullList<Ingredient> ingredients = gta.hoveredRecipe.getIngredients();
        int max = ingredients.size();
        this.ingredients = new ItemStack[max][];
        this.ingredientSize = new int[max];
        for( int i = 0; i < max; i++ ) {
            AssemblyIngredient aIng = (AssemblyIngredient) ingredients.get(i);
            this.ingredients[i] = aIng.getMatchingStacks();
            this.ingredientSize[i] = aIng.getCount();
        }

        this.itemTooltip.element.update(gui, this.itemTooltip.data);
        this.timeIcon.element.update(gui, this.timeIcon.data);
        this.rfIcon.element.update(gui, this.rfIcon.data);

        this.currTicks++;
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        ItemTooltipText itt = this.itemTooltip.get(ItemTooltipText.class);
        int origY = y;

        GuiDefinition.renderElement(gui, x, y, mouseX, mouseY, partTicks, this.itemTooltip);

        this.currWidth = itt.getWidth();
        y += itt.getHeight() + 2;
        int bar1Y = y;

        y += 2;
        int col = 0;
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 200);
        for( int i = 0; this.ingredients != null && i < this.ingredients.length; i++ ) {
            ItemStack[] variants = this.ingredients[i];
            ItemStack variant = variants[(int) (this.currTicks / 20) % variants.length];

            if( this.shiftPressed ) {
                y += 1;
                RenderUtils.renderStackInGui(variant, x + 1, y, 0.5D);
                List<String> ingLines = gui.get().getItemToolTip(variant);
                ingLines.set(0, String.format("%dx %s", this.ingredientSize[i], ingLines.get(0)));
                for( String line : ingLines ) {
                    itt.fontRenderer.drawString(line, x + 11, y, itt.color, itt.shadow);
                    y += 10;
                    this.currWidth = Math.max(this.currWidth, 11 + itt.fontRenderer.getStringWidth(line));
                }
            } else {
                RenderUtils.renderStackInGui(variant, x + col * 18 + 1, y + 1, 1.0D, itt.fontRenderer, String.format("%d", this.ingredientSize[i]), true);

                col++;
                this.currWidth = Math.max(this.currWidth, col * 18);

                if( col >= this.compactIngredientsColumns && i < this.ingredients.length - 1 ) {
                    y += 18;
                    col = 0;
                } else if( i == this.ingredients.length - 1 ) {
                    y += 19;
                }
            }
        }
        GlStateManager.popMatrix();

        GlStateManager.disableDepth();
        Gui.drawRect(x - 2, bar1Y, x + this.currWidth + 2, bar1Y + 1, this.borderColor);
        Gui.drawRect(x - 2, y, x + this.currWidth + 2, y + 1, this.borderColor);

        y += 3;
        String ticks = MiscUtils.getTimeFromTicks(this.ticksCrafting);
        this.timeIcon.element.render(gui, partTicks, x, y, mouseX, mouseY, this.timeIcon.data);
        itt.fontRenderer.drawString(ticks, x + this.timeIcon.element.getWidth() + 2, y + 1, itt.color, itt.shadow);
        y += Math.max(this.timeIcon.element.getHeight() + 2, itt.fontRenderer.FONT_HEIGHT);
        this.currWidth = Math.max(this.currWidth, this.timeIcon.element.getWidth() + 2 + itt.fontRenderer.getStringWidth(ticks));

        String rf = String.format("%d RF/t", this.rfPerTick);
        this.rfIcon.element.render(gui, partTicks, x, y, mouseX, mouseY, this.rfIcon.data);
        itt.fontRenderer.drawString(rf, x + this.rfIcon.element.getWidth() + 2, y + 1, itt.color, itt.shadow);
        y += Math.max(this.rfIcon.element.getHeight(), itt.fontRenderer.FONT_HEIGHT);
        this.currWidth = Math.max(this.currWidth, this.rfIcon.element.getWidth() + 2 + itt.fontRenderer.getStringWidth(rf));

        this.currHeight = y - origY;

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
        private int          currWidth;

        @Override
        public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
            JsonUtils.addDefaultJsonProperty(data, "color", "0xFFFFFFFF");
            JsonUtils.addDefaultJsonProperty(data, "shadow", true);

            super.bakeData(gui, data, inst);
        }

        @Override
        public void update(IGui gui, JsonObject data) {
            super.update(gui, data);

            GuiTurretAssembly gta = (GuiTurretAssembly) gui;
            this.currWidth = 0;
            this.lines = gui.get().getItemToolTip(gta.hoveredRecipe.getRecipeOutput());
            this.lines.forEach(l -> this.currWidth = Math.max(this.fontRenderer.getStringWidth(l), this.currWidth));
        }

        @Override
        public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
            int currY = y;
            for( String line : this.lines ) {
                this.text = line;
                super.render(gui, partTicks, x, currY, mouseX, mouseY, data);
                currY += this.fontRenderer.FONT_HEIGHT + 1;
            }
        }

        @Override
        public String getBakedText(IGui gui, JsonObject data) {
            return "";
        }

        @Override
        public int getHeight() {
            return (this.fontRenderer.FONT_HEIGHT + 1) * this.lines.size() - 2;
        }

        @Override
        public int getWidth() {
            return currWidth;
        }
    }
}
