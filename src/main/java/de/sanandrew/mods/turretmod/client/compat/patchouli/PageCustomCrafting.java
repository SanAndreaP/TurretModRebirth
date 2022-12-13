package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.Level;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.ClientBookRegistry;
import vazkii.patchouli.client.book.page.abstr.PageWithText;
import vazkii.patchouli.common.util.ItemStackUtil;

import java.util.Collections;

@SuppressWarnings({ "WeakerAccess", "unused" })
public class PageCustomCrafting
        extends PageWithText
{
    public static final int X = 9;
    public static final int Y = 4;
    public static final int RECIPE_HEIGHT = 78;

    public String     title;
    @SerializedName("pattern_1")
    public String[]     pattern1;
    @SerializedName("pattern_2")
    public String[]     pattern2  = null;
    @SerializedName("shaped_1")
    public boolean    shaped1   = true;
    @SerializedName("shaped_2")
    public boolean    shaped2   = true;
    @SerializedName("mappings_1")
    public JsonObject mappings1;
    @SerializedName("mappings_2")
    public JsonObject mappings2 = null;
    @SerializedName("output_1")
    public String recipeOutput1;
    @SerializedName("output_2")
    public String recipeOutput2 = null;

    transient private String    title1;
    transient private String    title2;
    transient private final NonNullList<Ingredient> recipe1 = NonNullList.withSize(9, Ingredient.EMPTY);
    transient private final NonNullList<Ingredient> recipe2 = NonNullList.withSize(9, Ingredient.EMPTY);
    transient private ItemStack output1 = ItemStack.EMPTY;
    transient private ItemStack output2 = ItemStack.EMPTY;

    public static void registerPage() {
        if( Loader.isModLoaded("patchouli") ) {
            ClientBookRegistry.INSTANCE.pageTypes.put(TmrConstants.ID + ':' + "custom_crafting", PageCustomCrafting.class);
        }
    }

    @Override
    public void build(BookEntry entry, int pageNum) {
        this.output1 = loadPattern(this.recipeOutput1, this.pattern1, this.mappings1, this.recipe1);
        this.output2 = loadPattern(this.recipeOutput2, this.pattern2, this.mappings2, this.recipe2);

        boolean customTitle = !Strings.isNullOrEmpty(this.title);
        this.title1 = !customTitle ? this.output1.getDisplayName() : this.title;
        this.title2 = "";
        if( ItemStackUtils.isValid(this.output2) ) {
            this.title2 = !customTitle ? this.output2.getDisplayName() : "";
            if( this.title1.equals(this.title2) ) {
                this.title2 = "";
            }
        }
    }

    private static ItemStack loadPattern(String outputId, String[] pattern, JsonObject mappings, NonNullList<Ingredient> recipe)
    {
        String pStr = pattern != null ? String.join("", pattern) : "";

        if( !Strings.isNullOrEmpty(pStr) && mappings != null && outputId != null ) {
            ItemStack output = ItemStackUtil.loadStackFromString(outputId);

            if( !ItemStackUtils.isValid(output) ) {
                return ItemStack.EMPTY;
            }
            char[] p = pStr.toCharArray();
            for( int i = 0, max = p.length; i < max; i++ ) {
                if( p[i] != ' ' ) {
                    String key = String.valueOf(p[i]);
                    if( mappings.has(key) ) {
                        recipe.set(i, ItemStackUtil.loadIngredientFromString(mappings.getAsJsonPrimitive(key).getAsString()));
                    } else {
                        TmrConstants.LOG.log(Level.WARN, String.format("Skipping ingredient '%s' in pattern, as it isn't contained in the mapping", p[i]));
                    }
                }
            }

            return output;
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void render(int mouseX, int mouseY, float pticks) {
        if( ItemStackUtils.isValid(this.output1) ) {
            drawRecipe(this.parent, this.title1, this.output1, this.recipe1, this.shaped1, X, Y, mouseX, mouseY);
        }
        if( ItemStackUtils.isValid(this.output2) ) {
            drawRecipe(this.parent, this.title2, this.output2, this.recipe2, this.shaped2,
                       X, Y + RECIPE_HEIGHT - (Strings.isNullOrEmpty(this.title2) ? 10 : 0),
                       mouseX, mouseY);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static void drawRecipe(IComponentRenderContext context, String title, ItemStack output, NonNullList<Ingredient> recipe, boolean shaped,
                                   int recipeX, int recipeY, int mouseX, int mouseY)
    {
        FontRenderer fr = context.getFont();
        int          wrap;

        Minecraft.getMinecraft().renderEngine.bindTexture(context.getCraftingTexture());
        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(recipeX - 2, recipeY - 2, 0.0F, 0.0F, 100, 62, 128.0F, 128.0F);

        if( !shaped ) {
            int iconX = recipeX + 62;
            wrap = recipeY + 2;
            Gui.drawModalRectWithCustomSizedTexture(iconX, wrap, 0.0F, 64.0F, 11, 11, 128.0F, 128.0F);
            if( context.isAreaHovered(mouseX, mouseY, iconX, wrap, 11, 11) ) {
                context.setHoverTooltip(Collections.singletonList(LangUtils.translate("patchouli.gui.lexicon.shapeless")));
            }
        }

        fr.drawString(title, 58 - fr.getStringWidth(title) / 2.0F, recipeY - 10, context.getHeaderColor(), false);
        context.renderItemStack(recipeX + 79, recipeY + 22, mouseX, mouseY, output);
        wrap = 3;

        for( int i = 0, max = recipe.size(); i < max; ++i ) {
            context.renderIngredient(recipeX + i % wrap * 19 + 3, recipeY + i / wrap * 19 + 3, mouseX, mouseY, recipe.get(i));
        }
    }

    @Override
    public int getTextHeight() {
        return 4 + RECIPE_HEIGHT * (ItemStackUtils.isValid(this.output2) ? 2 : 1) - (Strings.isNullOrEmpty(this.title2) ? 23 : 13);
    }
}
