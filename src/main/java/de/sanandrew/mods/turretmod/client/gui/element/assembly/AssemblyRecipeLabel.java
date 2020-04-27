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

import java.util.ArrayList;
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

    private int  currWidth;
    private int  currHeight;
    private long currTicks;

    private final List<RenderElement> renderedIngredients = new ArrayList<>();
    private final List<RenderElement> renderedData        = new ArrayList<>();

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
        int ticksCrafting = gta.getProcessTime(gta.hoveredRecipe);
        int rfPerTick = gta.getRfPerTick(gta.hoveredRecipe);
        boolean shiftPressed = gta.isShiftPressed();

        NonNullList<Ingredient> ingredients = gta.hoveredRecipe.getIngredients();
        int max = ingredients.size();
        ItemStack[][] ingredientsVariants = new ItemStack[max][];
        int[] ingredientSize = new int[max];
        for( int i = 0; i < max; i++ ) {
            AssemblyIngredient aIng = (AssemblyIngredient) ingredients.get(i);
            ingredientsVariants[i] = aIng.getMatchingStacks();
            ingredientSize[i] = aIng.getCount();
        }

        this.itemTooltip.element.update(gui, this.itemTooltip.data);
        this.timeIcon.element.update(gui, this.timeIcon.data);
        this.rfIcon.element.update(gui, this.rfIcon.data);

        this.currTicks++;

        this.renderedIngredients.clear();
        this.renderedData.clear();

        ItemTooltipText itt = this.itemTooltip.get(ItemTooltipText.class);
        int x = 0;
        int y = 0;

        this.currWidth = itt.getWidth();
        y += itt.getHeight() + 2;
        int bar1Y = y;

        y += 2;
        int col = 0;

        for( int i = 0; i < ingredientsVariants.length; i++ ) {
            ItemStack[] variants = ingredientsVariants[i];
            ItemStack variant = variants[(int) (this.currTicks / 20) % variants.length];

            if( shiftPressed ) {
                y += 1;
                this.renderedIngredients.add(new RenderElement(x + 1, y, (xr, yr, mx, my, pt) -> RenderUtils.renderStackInGui(variant, xr, yr, 0.5D)));
                List<String> ingLines = gui.get().getItemToolTip(variant);
                ingLines.set(0, String.format("%dx %s", ingredientSize[i], ingLines.get(0)));
                for( String line : ingLines ) {
                    this.renderedIngredients.add(new RenderElement(x + 11, y, (xr, yr, mx, my, pt) -> itt.fontRenderer.drawString(line, xr, yr, itt.color, itt.shadow)));
                    y += 10;
                    this.currWidth = Math.max(this.currWidth, 11 + itt.fontRenderer.getStringWidth(line));
                }
            } else {
                int ingSize = ingredientSize[i];
                this.renderedIngredients.add(new RenderElement(x + col * 18 + 1, y + 1, (xr, yr, mx, my, pt) ->
                                           RenderUtils.renderStackInGui(variant, xr, yr, 1.0D, itt.fontRenderer, String.format("%d", ingSize), true)
                                       ));

                col++;
                this.currWidth = Math.max(this.currWidth, col * 18);

                if( col >= this.compactIngredientsColumns && i < ingredientsVariants.length - 1 ) {
                    y += 18;
                    col = 0;
                } else if( i == ingredientsVariants.length - 1 ) {
                    y += 19;
                }
            }
        }

        this.renderedData.add(new RenderElement(x - 2, bar1Y, (xr, yr, mx, my, pt) -> Gui.drawRect(xr, yr, xr + this.currWidth + 4, yr + 1, this.borderColor)));
        this.renderedData.add(new RenderElement(x - 2, y, (xr, yr, mx, my, pt) -> Gui.drawRect(xr, yr, xr + this.currWidth + 4, yr + 1, this.borderColor)));

        y += 3;
        String ticks = MiscUtils.getTimeFromTicks(ticksCrafting);
        this.renderedData.add(new RenderElement(x, y, (xr, yr, mx, my, pt) -> {
            this.timeIcon.element.render(gui, pt, xr, yr, mx, my, this.timeIcon.data);
            itt.fontRenderer.drawString(ticks, xr + this.timeIcon.element.getWidth() + 2, yr + 1, itt.color, itt.shadow);
        }));
        y += Math.max(this.timeIcon.element.getHeight() + 2, itt.fontRenderer.FONT_HEIGHT);
        this.currWidth = Math.max(this.currWidth, this.timeIcon.element.getWidth() + 2 + itt.fontRenderer.getStringWidth(ticks));

        String rf = String.format("%d RF/t", rfPerTick);
        this.renderedData.add(new RenderElement(x, y, (xr, yr, mx, my, pt) -> {
            this.rfIcon.element.render(gui, pt, xr, yr, mx, my, this.rfIcon.data);
            itt.fontRenderer.drawString(rf, xr + this.rfIcon.element.getWidth() + 2, yr + 1, itt.color, itt.shadow);
        }));
        y += Math.max(this.rfIcon.element.getHeight(), itt.fontRenderer.FONT_HEIGHT);

        this.currWidth = Math.max(this.currWidth, this.rfIcon.element.getWidth() + 2 + itt.fontRenderer.getStringWidth(rf));
        this.currHeight = y;

        GlStateManager.enableDepth();
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        GuiDefinition.renderElement(gui, x, y, mouseX, mouseY, partTicks, this.itemTooltip);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 200);
        this.renderedIngredients.forEach(ri -> ri.render.accept(x + ri.x, y + ri.y, mouseX, mouseY, partTicks));
        GlStateManager.popMatrix();

        GlStateManager.disableDepth();
        this.renderedData.forEach(ri -> ri.render.accept(x + ri.x, y + ri.y, mouseX, mouseY, partTicks));
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

    public static class RenderElement
    {
        public int            x;
        public int            y;
        public RenderFunction render;

        private RenderElement(int x, int y, RenderFunction render) {
            this.x = x;
            this.y = y;
            this.render = render;
        }
    }

    public static class ItemTooltipText
            extends Text
    {
        @Override
        public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
            JsonUtils.addDefaultJsonProperty(data, "color", "0xFFFFFFFF");
            JsonUtils.addDefaultJsonProperty(data, "shadow", true);

            super.bakeData(gui, data, inst);
        }

        @Override
        public String getBakedText(IGui gui, JsonObject data) {
            return "";
        }

        @Override
        public String getDynamicText(IGui gui, String originalText) {
            GuiTurretAssembly gta = (GuiTurretAssembly) gui;

            return gta.hoveredRecipe != null ? String.join("\n", gui.get().getItemToolTip(gta.hoveredRecipe.getRecipeOutput())) : "";
        }

        @Override
        public boolean forceRenderUpdate(IGui gui) {
            return false;
        }
    }

    @FunctionalInterface
    private interface RenderFunction
    {
        void accept(int x, int y, int mouseX, int mouseY, float partTicks);
    }
}
