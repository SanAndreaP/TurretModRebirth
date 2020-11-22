package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.google.gson.annotations.SerializedName;
import com.mojang.realmsclient.gui.ChatFormatting;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.electrolytegen.IElectrolyteRecipe;
import de.sanandrew.mods.turretmod.registry.electrolytegen.ElectrolyteManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ComponentElectrolytes
        extends ComponentEntryList<ComponentElectrolytes.ComponentItemRow>
{
    @VariableHolder
    @SerializedName("ttip_efficiency")
    public String tTipTxtEfficiency;
    @VariableHolder
    @SerializedName("ttip_processing_time")
    public String tTipTxtProcTime;
    @VariableHolder
    @SerializedName("ttip_trash")
    public String tTipTxtTrash;
    @VariableHolder
    @SerializedName("ttip_treasure")
    public String tTipTxtTreasure;

    private static final int ENTRY_HEIGHT = 19;

    static final int MAX_ITEMS_PER_ROW = 6;

    @Override
    int getEntryHeight() {
        return ENTRY_HEIGHT;
    }

    @Override
    public void buildEntries(IComponentRenderContext context, GuiBook book, List<ComponentItemRow> entries, int x, int y) {
        List<IElectrolyteRecipe> recipes = new ArrayList<>(ElectrolyteManager.INSTANCE.getFuels());
        recipes.sort((r1, r2) -> {
            int cmp = Float.compare(r1.getEfficiency(), r2.getEfficiency());
            if( cmp == 0 ) {
                cmp = Integer.compare(r1.getProcessTime(), r2.getProcessTime());
            }
            if( cmp == 0 ) {
                cmp = r1.getIngredients().get(0).getMatchingStacks()[0].getDisplayName().compareTo(
                        r2.getIngredients().get(0).getMatchingStacks()[0].getDisplayName()
                );
            }

            return cmp;
        });

        for( int i = 0, max = recipes.size(); i < max; i += MAX_ITEMS_PER_ROW ) {
            int id = (i / MAX_ITEMS_PER_ROW);
            ComponentItemRow row = new ComponentItemRow(id, recipes.subList(i, Math.min(i + MAX_ITEMS_PER_ROW, max)));
            row.build(x + 2, y + 2 + id * ENTRY_HEIGHT, book.getPage());

            entries.add(row);
        }
    }

    @Override
    void setEntryScroll(ComponentItemRow entry, int prevShownPos, int currShownPos) {
        entry.y += (prevShownPos - currShownPos) * ENTRY_HEIGHT;
        entry.visible = entry.id >= currShownPos && entry.id < currShownPos + this.maxEntriesShown;
    }

    @Override
    public void render(IComponentRenderContext context, float partTicks, int mouseX, int mouseY) {
        super.render(context, partTicks, mouseX, mouseY);

        this.entries.forEach(e -> e.render(context, partTicks, mouseX, mouseY));
    }

    final class ComponentItemRow
            implements ICustomComponent
    {
        private int x;
        private int y;

        private final int id;

        private final IElectrolyteRecipe[] recipes;
        private final String[][][]   tooltips;
        private final ItemStack[][]  ingredients;

        boolean visible = true;

        ComponentItemRow(int id, List<IElectrolyteRecipe> recipes) {
            this.id = id;
            this.recipes = recipes.toArray(new IElectrolyteRecipe[0]);
            this.tooltips = new String[this.recipes.length][][];
            this.ingredients = new ItemStack[this.recipes.length][];
        }

        @Override
        public void build(int x, int y, int pgNum) {
            this.x = x;
            this.y = y;

            for( int i = 0, max = this.recipes.length; i < max; i++ ) {
                if( this.recipes[i] != null ) {
                    float efficiency = this.recipes[i].getEfficiency();
                    int   procTime   = this.recipes[i].getProcessTime();
                    float trash      = this.recipes[i].getTrashChance();
                    float treasure   = this.recipes[i].getTreasureChance();

                    this.ingredients[i] = this.recipes[i].getIngredients().get(0).getMatchingStacks();
                    this.tooltips[i]    = new String[this.ingredients[i].length][];

                    for( int j = 0, maxIng = this.ingredients[i].length; j < maxIng; j++) {
                        String[] ttip = new String[5];
                        ttip[0] = this.ingredients[i][j].getDisplayName();
                        ttip[1] = ChatFormatting.GRAY + String.format(ComponentElectrolytes.this.tTipTxtEfficiency, efficiency * 100F);
                        ttip[2] = ChatFormatting.GRAY + String.format(ComponentElectrolytes.this.tTipTxtProcTime, MiscUtils.getTimeFromTicks(procTime, 1));
                        ttip[3] = ChatFormatting.GRAY + String.format(ComponentElectrolytes.this.tTipTxtTrash, trash * 100F);
                        ttip[4] = ChatFormatting.GRAY + String.format(ComponentElectrolytes.this.tTipTxtTreasure, treasure * 100F);

                        this.tooltips[i][j] = ttip;
                    }
                }
            }
        }

        @Override
        public void render(IComponentRenderContext context, float v, int mouseX, int mouseY) {
            if( !this.visible ) {
                return;
            }

            long currTime = System.currentTimeMillis() / 4250L;
            for( int i = 0, max = this.recipes.length; i < max; i++ ) {
                if( this.recipes[i] != null ) {
                    int ingId = (int) currTime % this.ingredients[i].length;
                    this.renderItem(context, this.x + i * ENTRY_HEIGHT, this.y, mouseX, mouseY, this.ingredients[i][ingId], this.tooltips[i][ingId]);
                }
            }
        }

        private void renderItem(IComponentRenderContext context, int x, int y, int mouseX, int mouseY, ItemStack stack, String[] ttip) {
            RenderItem itemRender = context.getGui().mc.getRenderItem();

            RenderHelper.enableGUIStandardItemLighting();
            itemRender.renderItemAndEffectIntoGUI(stack, x, y);
            itemRender.renderItemOverlays(context.getGui().mc.fontRenderer, stack, x, y);

            if( context.isAreaHovered(mouseX, mouseY, x, y, 16, 16) ) {
                context.setHoverTooltip(Arrays.asList(ttip));
            }

        }
    }
}
