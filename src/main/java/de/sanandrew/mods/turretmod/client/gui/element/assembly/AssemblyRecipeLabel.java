package de.sanandrew.mods.turretmod.client.gui.element.assembly;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.AssemblyIngredient;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssembly;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class AssemblyRecipeLabel
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "assembly.recipe_label");

    GuiElementInst itemTooltip;
    GuiElementInst timeIcon;
    GuiElementInst rfIcon;

    int borderColor;
    int compactIngredientsColumns;

    int currWidth;
    int currHeight;
    long currTicks;
    int ticksCrafting;
    int rfPerTick;
    ItemStack[][] ingredients;
    int[] ingredientSize;
    boolean shiftPressed;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        this.currTicks = 0L;

        if( this.itemTooltip == null ) {
            this.itemTooltip = new GuiElementInst();
            this.itemTooltip.element = new ItemTooltipText();
            this.itemTooltip.data = data.has("itemTooltipData") ? data.get("itemTooltipData").getAsJsonObject() : new JsonObject();
            this.itemTooltip.element.bakeData(gui, this.itemTooltip.data);

            this.borderColor = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("borderColor")));
            this.compactIngredientsColumns = JsonUtils.getIntVal(data.get("compactIngredientsColumns"), 6);
        }

        if( this.timeIcon == null ) {
            this.timeIcon = new GuiElementInst();
            this.timeIcon.element = new Texture();
            this.timeIcon.data = data.has("timeIconData") ? data.get("timeIconData").getAsJsonObject() : new JsonObject();
            JsonUtils.addDefaultJsonProperty(this.timeIcon.data, "size", new int[] { 9, 9 });
            JsonUtils.addDefaultJsonProperty(this.timeIcon.data, "uv", new int[] { 230, 94 });
            this.timeIcon.element.bakeData(gui, this.timeIcon.data);
        }

        if( this.rfIcon == null ) {
            this.rfIcon = new GuiElementInst();
            this.rfIcon.element = new Texture();
            this.rfIcon.data = data.has("rfIconData") ? data.get("rfIconData").getAsJsonObject() : new JsonObject();
            JsonUtils.addJsonProperty(this.rfIcon.data, "size", new int[] { 9, 9 });
            JsonUtils.addJsonProperty(this.rfIcon.data, "uv", new int[] { 230, 103 });
            this.rfIcon.element.bakeData(gui, this.rfIcon.data);
        }
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        GuiTurretAssembly gta = (GuiTurretAssembly) gui;
        this.ticksCrafting = gta.getProcessTime(gta.hoveredRecipe);
        this.rfPerTick = gta.getRfPerTick(gta.hoveredRecipe);
        NonNullList<Ingredient> ingredients = gta.hoveredRecipe.getIngredients();
        int max = ingredients.size();
        this.ingredients = new ItemStack[max][];
        this.ingredientSize = new int[max];
        for( int i = 0; i < max; i++ ) {
            AssemblyIngredient aIng = (AssemblyIngredient) ingredients.get(i);
            this.ingredients[i] = aIng.getMatchingStacks();
            this.ingredientSize[i] = aIng.getCount();
        }
        this.shiftPressed = gta.isShiftPressed();

        this.itemTooltip.element.update(gui, this.itemTooltip.data);
        this.timeIcon.element.update(gui, this.timeIcon.data);
        this.rfIcon.element.update(gui, this.rfIcon.data);

        this.currTicks++;
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        Text.BakedData ittData = ((ItemTooltipText) this.itemTooltip.element).data;
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
            ItemStack variant = variants[(int) (this.currTicks / 20) % variants.length];

            if( this.shiftPressed ) {
                y += 1;
                RenderUtils.renderStackInGui(variant, x + 1, y, 0.5D);
                List<String> ingLines = gui.get().getItemToolTip(variant);
                ingLines.set(0, String.format("%dx %s", this.ingredientSize[i], ingLines.get(0)));
                for( String line : ingLines ) {
                    ittData.fontRenderer.drawString(line, x + 11, y, ittData.color, ittData.shadow);
                    y += 10;
                    this.currWidth = Math.max(this.currWidth, 11 + ittData.fontRenderer.getStringWidth(line));
                }
            } else {
                RenderUtils.renderStackInGui(variant, x + col * 18 + 1, y + 1, 1.0D, ittData.fontRenderer, String.format("%d", this.ingredientSize[i]), true);

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

        int yDiv2 = y;
        y += 3;

        GlStateManager.disableDepth();
        String ticks = MiscUtils.getTimeFromTicks(this.ticksCrafting);
        this.timeIcon.element.render(gui, partTicks, x, y, mouseX, mouseY, this.timeIcon.data);
        ittData.fontRenderer.drawString(ticks, x + this.timeIcon.element.getWidth() + 2, y + 1, ittData.color, ittData.shadow);
        y += Math.max(this.timeIcon.element.getHeight() + 2, ittData.fontRenderer.FONT_HEIGHT);
        this.currWidth = Math.max(this.currWidth, this.timeIcon.element.getWidth() + 2 + ittData.fontRenderer.getStringWidth(ticks));

        String rf = String.format("%d RF/t", this.rfPerTick);
        this.rfIcon.element.render(gui, partTicks, x, y, mouseX, mouseY, this.rfIcon.data);
        ittData.fontRenderer.drawString(rf, x + this.rfIcon.element.getWidth() + 2, y + 1, ittData.color, ittData.shadow);
        y += Math.max(this.rfIcon.element.getHeight(), ittData.fontRenderer.FONT_HEIGHT);
        this.currWidth = Math.max(this.currWidth, this.rfIcon.element.getWidth() + 2 + ittData.fontRenderer.getStringWidth(rf));

        this.currHeight = y - origY;

        Gui.drawRect(x - 2, yDiv1, x + this.currWidth + 2, yDiv1 + 1, this.borderColor);
        Gui.drawRect(x - 2, yDiv2, x + this.currWidth + 2, yDiv2 + 1, this.borderColor);
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

            GuiTurretAssembly gta = (GuiTurretAssembly) gui;
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
