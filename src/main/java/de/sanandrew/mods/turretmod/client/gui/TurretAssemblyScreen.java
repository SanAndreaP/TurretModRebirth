/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui;

import com.google.common.base.Strings;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.JsonGuiContainer;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ButtonSL;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Item;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ProgressBar;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.client.gui.element.StackPanel;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.api.assembly.ICountedIngredient;
import de.sanandrew.mods.turretmod.client.init.ClientProxy;
import de.sanandrew.mods.turretmod.init.Lang;
import de.sanandrew.mods.turretmod.inventory.container.TurretAssemblyContainer;
import de.sanandrew.mods.turretmod.item.AssemblyUpgradeItem;
import de.sanandrew.mods.turretmod.network.AssemblyActionPacket;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyEnergyStorage;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.apache.commons.lang3.Range;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

@SuppressWarnings("java:S110")
public class TurretAssemblyScreen
        extends JsonGuiContainer<TurretAssemblyContainer>
{
    private static       String          lastGroup;
    private String group;
    private IAssemblyRecipe currHoverRecipe;
    private boolean isShiftPressed;

    private ITextComponent prevGroupName = StringTextComponent.EMPTY;
    private ITextComponent currGroupName = StringTextComponent.EMPTY;
    private ITextComponent nextGroupName = StringTextComponent.EMPTY;

    private ButtonSL cancelButton;
    private ButtonSL automateButton;
    private ButtonSL   manualButton;
    private ButtonSL   prevGroupButton;
    private ButtonSL   nextGroupButton;

    private GuiElementInst recipeList;
    private GuiElementInst recipeMarker;
    private GuiElementInst recipeTooltip;

    private GuiElementInst smallIngredientsList;
    private GuiElementInst bigIngredientsList;

    private boolean hasMultipleIngredients;
    private long prevSystemSeconds;

    public TurretAssemblyScreen(TurretAssemblyContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    @Override
    protected GuiDefinition buildGuiDefinition() {
        try {
            return GuiDefinition.getNewDefinition(Resources.GUI_ASSEMBLY);
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
            return null;
        }
    }

    @Override
    protected void initGd() {
        this.cancelButton = this.guiDefinition.getElementById("cancel-button").get(ButtonSL.class);
        this.automateButton = this.guiDefinition.getElementById("automate-button").get(ButtonSL.class);
        this.manualButton = this.guiDefinition.getElementById("manual-button").get(ButtonSL.class);
        this.prevGroupButton = this.guiDefinition.getElementById("prev-group").get(ButtonSL.class);
        this.nextGroupButton = this.guiDefinition.getElementById("next-group").get(ButtonSL.class);
        this.recipeList = this.guiDefinition.getElementById("recipe-list");
        this.recipeMarker = this.guiDefinition.getElementById("recipe-marker");
        this.recipeTooltip = this.guiDefinition.getElementById("recipe-tooltip");

        this.recipeTooltip.setVisible(false);

        this.loadRecipes(MiscUtils.apply(this.getCurrentRecipe(), IRecipe::getGroup, Strings.isNullOrEmpty(lastGroup)
                                                                                 ? AssemblyManager.INSTANCE.getGroups(this.getLevel())[0]
                                                                                 : lastGroup));
        this.recipeList.get(ScrollArea.class).update(this);

        this.updateButtons();
        this.updateRecipeMarker(true);

        this.prevGroupButton.setFunction(b -> this.loadRecipes(this.getPrevGroup()));
        this.prevGroupButton.get(ButtonSL.LABEL).get(Item.class).setItemSupplier(() -> AssemblyManager.INSTANCE.getGroupIcon(this.getPrevGroup()));

        this.nextGroupButton.setFunction(b -> this.loadRecipes(this.getNextGroup()));
        this.nextGroupButton.get(ButtonSL.LABEL).get(Item.class).setItemSupplier(() -> AssemblyManager.INSTANCE.getGroupIcon(this.getNextGroup()));

        this.cancelButton.setFunction(b -> AssemblyActionPacket.cancelCraft(this.menu.tile));
        this.automateButton.setFunction(b -> AssemblyActionPacket.setAutomate(this.menu.tile));
        this.manualButton.setFunction(b -> AssemblyActionPacket.setManual(this.menu.tile));

        this.guiDefinition.getElementById("energy").get(ProgressBar.class)
                          .setPercentFunc(g -> this.menu.data.getEnergyStored() / (double) AssemblyEnergyStorage.MAX_FLUX_STORAGE);
        this.guiDefinition.getElementById("energy_tooltip").get(Text.class)
                          .setTextFunc((g, t) -> new StringTextComponent(String.format("%d / %d RF", this.menu.data.getEnergyStored(), AssemblyEnergyStorage.MAX_FLUX_STORAGE)));
        this.guiDefinition.getElementById("group-icon").get(Item.class)
                          .setItemSupplier(() -> AssemblyManager.INSTANCE.getGroupIcon(this.group));
        this.guiDefinition.getElementById("crafting-progress").get(ProgressBar.class)
                          .setPercentFunc(g -> this.menu.data.getCraftingProgress());
        this.guiDefinition.getElementById("recipe-count").get(Text.class)
                          .setTextFunc((g, ot) -> {
                              int count = this.menu.data.getCraftingAmount();
                              if( count > 0 ) {
                                  return new StringTextComponent(String.format("%d", count));
                              } else if( count < 0 ) {
                                  return new StringTextComponent("∞");
                              } else {
                                  return StringTextComponent.EMPTY;
                              }
                          });

        this.guiDefinition.getElementById("prev-group-ttip").get(Tooltip.class).get(Tooltip.CONTENT).get(Text.class).setTextFunc((g, ot) -> this.prevGroupName);
        this.guiDefinition.getElementById("curr-group-ttip").get(Tooltip.class).get(Tooltip.CONTENT).get(Text.class).setTextFunc((g, ot) -> this.currGroupName);
        this.guiDefinition.getElementById("next-group-ttip").get(Tooltip.class).get(Tooltip.CONTENT).get(Text.class).setTextFunc((g, ot) -> this.nextGroupName);
    }

    @Override
    public void tick() {
        super.tick();

        this.updateButtons();
        this.updateRecipeMarker(false);
    }

    @Override
    protected void renderGd(@Nonnull MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        super.renderGd(mStack, mouseX, mouseY, partialTicks);

        updateRecipeTooltip();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if( keyCode == this.getMinecraft().options.keyShift.getKey().getValue() ) {
            this.isShiftPressed = true;
            this.updateRecipeTooltip();
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if( keyCode == this.getMinecraft().options.keyShift.getKey().getValue() ) {
            this.isShiftPressed = false;
            this.updateRecipeTooltip();
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private void updateButtons() {
        boolean hasRecipe = this.menu.tile.getCurrentRecipeId() != null;
        this.cancelButton.setActive(hasRecipe);
        this.automateButton.setVisible(this.menu.tile.hasAutoUpgrade());
        this.automateButton.setActive(!this.menu.tile.isAutomated() && !hasRecipe);
        this.manualButton.setVisible(this.menu.tile.hasAutoUpgrade());
        this.manualButton.setActive(this.menu.tile.isAutomated() && !hasRecipe);
    }

    private static void forEachRecipeItem(ScrollArea recipeListInst, BiPredicate<GuiElementInst, GuiElementInst> onItem, Runnable onExit) {
        for( GuiElementInst row : recipeListInst.getAll() ) {
            for( GuiElementInst itm : row.get(Row.class).getAll() ) {
                if( Boolean.TRUE.equals(onItem.test(row, itm)) ) {
                    return;
                }
            }
        }

        MiscUtils.accept(onExit, Runnable::run);
    }

    private void updateRecipeMarker(boolean isInit) {
        IAssemblyRecipe recipe = this.getCurrentRecipe();
        ScrollArea recipeListInst = this.recipeList.get(ScrollArea.class);

        if( recipe != null && (!this.recipeMarker.isVisible() || isInit) ) {
            this.prevGroupButton.setActive(false);
            this.nextGroupButton.setActive(false);

            forEachRecipeItem(recipeListInst, (row, itm) -> {
                Row        rowInst = row.get(Row.class);
                RecipeItem itmInst = itm.get(RecipeItem.class);

                if( itmInst.recipe.getId().equals(recipe.getId()) ) {
                    recipeListInst.setEnabled(false);

                    if( isInit ) {
                        recipeListInst.scrollTo(row);
                    }

                    Texture recipeMarkerInst = this.recipeMarker.get(Texture.class);
                    int offX = this.recipeList.pos[0] + (itmInst.getWidth() - recipeMarkerInst.getWidth()) / 2;
                    int offY = this.recipeList.pos[1] + (rowInst.getHeight() - recipeMarkerInst.getHeight()) / 2 - recipeListInst.getScrollY();

                    this.recipeMarker.pos[0] = itm.pos[0] + offX;
                    this.recipeMarker.pos[1] = row.pos[1] + offY;
                    this.recipeMarker.setVisible(true);

                    return true;
                }

                return false;
            }, null);
        } else if( recipe == null ) {
            this.prevGroupButton.setActive(true);
            this.nextGroupButton.setActive(true);
            recipeListInst.setEnabled(true);
            this.recipeMarker.setVisible(false);
        }
    }

    private void updateRecipeTooltip() {
        ScrollArea recipeListInst = this.recipeList.get(ScrollArea.class);

        forEachRecipeItem(recipeListInst, (row, itm) -> {
            RecipeItem itmInst = itm.get(RecipeItem.class);
            if( itmInst.isHovering ) {
                this.updateRecipeTooltip(itmInst.recipe, new int[] {this.recipeList.pos[0] + itm.pos[0] - 1,
                                                                    this.recipeList.pos[1] + row.pos[1] - recipeListInst.getScrollY() - 1});

                return true;
            }

            return false;
        }, () -> {
            this.currHoverRecipe = null;
            this.recipeTooltip.setVisible(false);
        });
    }

    private void updateRecipeTooltip(IAssemblyRecipe recipe, int[] pos) {
        long currSysSecs = System.nanoTime() / 1_000_000_000L;
        boolean updateViaTime = this.hasMultipleIngredients && this.prevSystemSeconds != currSysSecs;
        this.prevSystemSeconds = currSysSecs;
        if( recipe != null && (this.currHoverRecipe == null || !recipe.getId().equals(this.currHoverRecipe.getId()) || updateViaTime) ) {
            this.currHoverRecipe = recipe;

            this.buildRecipeTooltip();
            this.recipeTooltip.pos = pos;
            this.recipeTooltip.setVisible(true);
        } else if( recipe == null ) {
            this.currHoverRecipe = null;
            this.recipeTooltip.setVisible(false);
        } else {
            this.smallIngredientsList.setVisible(!this.isShiftPressed);
            this.bigIngredientsList.setVisible(this.isShiftPressed);
        }
    }

    private BiFunction<IGui, ITextComponent, ITextComponent> getProcessTime() {
        ITextComponent t = new StringTextComponent(MiscUtils.getTimeFromTicks(AssemblyUpgradeItem.Speed.getProcessingTime(this.menu.tile, this.currHoverRecipe)));
        return (g, o) -> t;
    }

    private BiFunction<IGui, ITextComponent, ITextComponent> getEnergyConsumption() {
        int i = AssemblyUpgradeItem.Speed.getEnergyConsumption(this.menu.tile, this.currHoverRecipe) * AssemblyUpgradeItem.Speed.getLoops(this.menu.tile);
        return (g, o) -> injectParams(o, i);
    }

    private void buildRecipeTooltip() {
        this.hasMultipleIngredients = false;

        NonNullList<ICountedIngredient> ingredients = this.currHoverRecipe.getCountedIngredients();
        StackPanel info = new StackPanel.Builder().horizontal(true).get(this);
        StackPanel sil = new StackPanel.Builder().horizontal(true).get(this);
        StackPanel bil = new StackPanel.Builder().horizontal(false).get(this);

        JsonObject timeIconData = this.getTooltipItemData("timeIcon");
        JsonObject timeTextData = JsonUtils.addDefaultJsonProperty(this.getTooltipItemData("timeText"), "color", "0xFFFFFF");
        JsonObject energyIconData = this.getTooltipItemData("energyIcon");
        JsonObject energyTextData = JsonUtils.addDefaultJsonProperty(this.getTooltipItemData("energyText"), "color", "0xFFFFFF");

        info.add(new GuiElementInst(getOffset(timeIconData, 0, 0), Texture.Builder.fromJson(this, timeIconData)).initialize(this));
        info.add(new GuiElementInst(getOffset(timeTextData, 3, 1), setDefaultTextAttr(Text.Builder.fromJson(this, timeTextData), this.getProcessTime())).initialize(this));
        info.add(new GuiElementInst(getOffset(energyIconData, 6, 0), Texture.Builder.fromJson(this, energyIconData)).initialize(this));
        info.add(new GuiElementInst(getOffset(energyTextData, 3, 1), setDefaultTextAttr(Text.Builder.fromJson(this, energyTextData), getEnergyConsumption())).initialize(this));

        this.smallIngredientsList = new GuiElementInst(sil).initialize(this);
        this.smallIngredientsList.setVisible(!this.isShiftPressed);

        this.bigIngredientsList = new GuiElementInst(bil).initialize(this);
        this.bigIngredientsList.setVisible(this.isShiftPressed);

        JsonObject cmpItemData = JsonUtils.addDefaultJsonProperty(this.getTooltipItemData("compactIngredients"), "overlayFont", "standard");
        JsonObject detRowData = this.getTooltipItemData("detailedIngredients");
        JsonObject detItemData = JsonUtils.addDefaultJsonProperty(MiscUtils.get(detRowData.getAsJsonObject("item"), JsonObject::new), "scale", 0.5);
        JsonObject detTtipData = MiscUtils.get(detRowData.getAsJsonObject("tooltip"), JsonObject::new);

        int[] cmpItemOffset = getOffset(cmpItemData, 1, 0);
        int[] detRowOffset = getOffset(detRowData, 0, 5);
        int[] detItemOffset = getOffset(detItemData, 0, 0);
        int[] detTtipOffset = getOffset(detTtipData, 5, 0);

        for( ICountedIngredient i : ingredients ) {
            ItemStack[] stacks = i.getItems();
            this.hasMultipleIngredients |= stacks.length > 1;
            final ItemStack currItem = stacks[stacks.length > 1 ? (int) (this.prevSystemSeconds % stacks.length) : 0];

            sil.add(new GuiElementInst(cmpItemOffset, Item.Builder.buildFromJson(this, cmpItemData, (g, j) -> currItem).get(this)).initialize(this));

            bil.add(new GuiElementInst(detRowOffset, MiscUtils.apply(new StackPanel.Builder().horizontal(true).get(this), spi -> {
                spi.add(new GuiElementInst(detItemOffset, Item.Builder.buildFromJson(this, detItemData, (g, j) -> currItem).get(this)).initialize(this));
                spi.add(new GuiElementInst(detTtipOffset, MiscUtils.apply(new StackPanel.Builder().get(this), spt -> {
                    ClientProxy.buildItemTooltip(this, spt, currItem, true, false);
                    return spt;
                })).initialize(this));
                return spi;
            })).initialize(this));
        }

        MiscUtils.accept(this.recipeTooltip.get(Tooltip.class).get(Tooltip.CONTENT).get(StackPanel.class),
                         p -> ClientProxy.buildItemTooltip(this, p, this.currHoverRecipe.getResultItem(), false, true,
                                                           new GuiElementInst(info).initialize(this), this.smallIngredientsList, this.bigIngredientsList));
    }

    private static ITextComponent injectParams(ITextComponent tc, Object... args) {
        String key;
        if( tc instanceof TranslationTextComponent ) {
            key = ((TranslationTextComponent) tc).getKey();
        } else {
            key = tc.getString();
        }

        return new TranslationTextComponent(key, args).withStyle(tc.getStyle());
    }

    private JsonObject getTooltipItemData(String name) {
        return MiscUtils.get(this.recipeTooltip.data.getAsJsonObject("content").getAsJsonObject(name), JsonObject::new);
    }

    private static int[] getOffset(JsonObject itmData, int defX, int defY) {
        return JsonUtils.getIntArray(itmData.get("offset"), new int[] {defX, defY}, Range.is(2));
    }

    private static Text setDefaultTextAttr(Text textElem, BiFunction<IGui, ITextComponent, ITextComponent> txtFunc) {
        textElem.setTextFunc(txtFunc);
        return textElem;
    }

    private String getPrevGroup() {
        return AssemblyManager.INSTANCE.getPreviousGroup(this.getLevel(), this.group);
    }

    private String getNextGroup() {
        return AssemblyManager.INSTANCE.getNextGroup(this.getLevel(), this.group);
    }

    private World getLevel() {
        return this.getMinecraft().level;
    }

    private void loadRecipes(String group) {
        if( !Strings.isNullOrEmpty(group) ) {
            if( !Objects.equals(this.group, group) ) {
                ScrollArea recipeListInst = this.recipeList.get(ScrollArea.class);
                recipeListInst.clear();

                List<IAssemblyRecipe> recipes = AssemblyManager.INSTANCE.getRecipes(this.getLevel(), group);
                int recipesCount = recipes.size();
                final int cols = 6;
                for( int row = 0, max = MathHelper.ceil(recipesCount / (float) cols); row < max; row++ ) {
                    Row rowElem = new Row();
                    for( int col = 0; col < cols; col++ ) {
                        int i = row * cols + col;
                        if( i >= recipesCount ) {
                            break;
                        }

                        Item itm = new RecipeItem(recipes.get(i), 1.0F);
                        rowElem.add(new GuiElementInst(new int[] { 18 * col, 0 }, itm).initialize(this));
                    }
                    recipeListInst.add(new GuiElementInst(new int[] { 0, 18 * row }, rowElem).initialize(this));
                }
            }

            this.group = group;

            this.prevGroupName = new TranslationTextComponent(Lang.ASSEMBLY_GROUP_LABEL.get(this.getPrevGroup()));
            this.currGroupName = new TranslationTextComponent(Lang.ASSEMBLY_GROUP_LABEL.get(this.group));
            this.nextGroupName = new TranslationTextComponent(Lang.ASSEMBLY_GROUP_LABEL.get(this.getNextGroup()));

            updateLastGroup(group);
        }
    }

    private static synchronized void updateLastGroup(String group) {
        lastGroup = group;
    }

    public String getGroup() {
        return this.group;
    }

    @Nullable
    public IAssemblyRecipe getCurrentRecipe() {
        ResourceLocation recipeId = this.menu.tile.getCurrentRecipeId();
        if( recipeId != null ) {
            return AssemblyManager.INSTANCE.getRecipe(this.getMinecraft().level, recipeId);
        }

        return null;
    }

    @Nonnull
    @Override
    public ITextComponent getTitle() {
        return super.getTitle();
    }

    private static final class Row
            extends ElementParent<Integer>
    {
        public void add(GuiElementInst elem) {
            this.put(elem.pos[0], elem);
        }
    }

    private final class RecipeItem
            extends Item
    {
        private final GuiElementInst disabledTexture;
        final IAssemblyRecipe recipe;
        private boolean isEnabled = true;
        private boolean isHovering = false;

        public RecipeItem(IAssemblyRecipe recipe, float scale) {
            super(recipe.getResultItem(), scale, MouseOverType.VANILLA, TurretAssemblyScreen.this.font);
            this.recipe = recipe;

            this.disabledTexture = new GuiElementInst(new Texture.Builder(new int[] {16, 16}).uv(86, 222).get(TurretAssemblyScreen.this))
                                                     .initialize(TurretAssemblyScreen.this);
        }

        @Override
        public void setup(IGui gui, GuiElementInst inst) {
            super.setup(gui, inst);

            this.disabledTexture.get().setup(gui, this.disabledTexture);
        }

        @Override
        public void tick(IGui gui, GuiElementInst inst) {
            IAssemblyRecipe cr = TurretAssemblyScreen.this.getCurrentRecipe();
            this.isEnabled = cr == null || cr.getId().equals(this.recipe.getId());
            this.mouseOverType = this.isEnabled ? MouseOverType.VANILLA : MouseOverType.NONE;

            super.tick(gui, inst);
        }

        @Override
        public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
            this.isHovering = this.mouseOverType.isHovering(gui, x, y, mouseX, mouseY, this.size, this.size);

            super.render(gui, stack, partTicks, x, y, mouseX, mouseY, inst);

            if( !this.isEnabled ) {
                RenderSystem.disableDepthTest();
                GuiDefinition.renderElement(gui, stack, x, y, mouseX, mouseY, partTicks, this.disabledTexture);
                RenderSystem.enableDepthTest();
            }
        }

        @Override
        public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
            if( this.isEnabled && this.isHovering ) {
                AssemblyActionPacket.setRecipe(TurretAssemblyScreen.this.menu.tile, this.recipe.getId(), 1);
                return true;
            }

            return super.mouseClicked(gui, mouseX, mouseY, button);
        }
    }
}
