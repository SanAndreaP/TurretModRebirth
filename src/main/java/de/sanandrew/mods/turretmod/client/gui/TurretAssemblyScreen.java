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
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.client.ShaderHelper;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.JsonGuiContainer;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ButtonSL;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Item;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ProgressBar;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.client.shader.ShaderAlphaOverride;
import de.sanandrew.mods.turretmod.client.shader.ShaderGrayscale;
import de.sanandrew.mods.turretmod.client.shader.Shaders;
import de.sanandrew.mods.turretmod.init.Lang;
import de.sanandrew.mods.turretmod.inventory.container.TurretAssemblyContainer;
import de.sanandrew.mods.turretmod.network.AssemblyActionPacket;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyEnergyStorage;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("java:S110")
public class TurretAssemblyScreen
        extends JsonGuiContainer<TurretAssemblyContainer>
{
    private static final ShaderAlphaOverride SHADER_ALPHA_OVERRIDE = new ShaderAlphaOverride(PlayerContainer.BLOCK_ATLAS);

    private static       String          lastGroup;
    private String group;
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

        this.setGroup(MiscUtils.apply(this.getCurrentRecipe(), IRecipe::getGroup, Strings.isNullOrEmpty(lastGroup)
                                                                                 ? AssemblyManager.INSTANCE.getGroups(this.getLevel())[0]
                                                                                 : lastGroup));
        this.recipeList.get(ScrollArea.class).update(this);

        this.updateButtons();
        this.updateRecipeMarker(true);

        this.prevGroupButton.setFunction(b -> this.setGroup(this.getPrevGroup()));
        this.prevGroupButton.get(ButtonSL.LABEL).get(Item.class).setItemSupplier(() -> AssemblyManager.INSTANCE.getGroupIcon(this.getPrevGroup()));

        this.nextGroupButton.setFunction(b -> this.setGroup(this.getNextGroup()));
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

    private void updateButtons() {
        boolean hasRecipe = this.menu.tile.getCurrentRecipeId() != null;
        this.cancelButton.setActive(hasRecipe);
        this.automateButton.setVisible(this.menu.tile.hasAutoUpgrade());
        this.automateButton.setActive(!this.menu.tile.isAutomated() && !hasRecipe);
        this.manualButton.setVisible(this.menu.tile.hasAutoUpgrade());
        this.manualButton.setActive(this.menu.tile.isAutomated() && !hasRecipe);
    }

    private void updateRecipeMarker(boolean isInit) {
        IAssemblyRecipe recipe = this.getCurrentRecipe();
        ScrollArea recipeListInst = this.recipeList.get(ScrollArea.class);

        if( recipe != null && (!this.recipeMarker.isVisible() || isInit) ) {
            this.prevGroupButton.setActive(false);
            this.nextGroupButton.setActive(false);

            for( GuiElementInst row : recipeListInst.getAll() ) {
                RecipeRow rowInst = row.get(RecipeRow.class);
                for( GuiElementInst itm : rowInst.getAll() ) {
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

                        return;
                    }
                }
            }
        } else if( recipe == null ) {
            this.prevGroupButton.setActive(true);
            this.nextGroupButton.setActive(true);
            recipeListInst.setEnabled(true);
            this.recipeMarker.setVisible(false);
        }
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

    private void setGroup(String group) {
        if( !Strings.isNullOrEmpty(group) ) {
            if( !Objects.equals(this.group, group) ) { // TODO: load template data from JSON
                ScrollArea recipeListInst = this.recipeList.get(ScrollArea.class);
                recipeListInst.clear();

                List<IAssemblyRecipe> recipes = AssemblyManager.INSTANCE.getRecipes(this.getLevel(), group);
                int recipesCount = recipes.size();
                final int cols = 6;
                for( int row = 0, max = MathHelper.ceil(recipesCount / (float) cols); row < max; row++ ) {
                    RecipeRow rowElem = new RecipeRow();
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

    private static final class RecipeRow
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
            super(recipe.getResultItem(), scale, true);
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
            this.doMouseOver = this.isEnabled;

            super.tick(gui, inst);
        }

        @Override
        public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
            this.isHovering = IGuiElement.isHovering(gui, x, y, mouseX, mouseY, this.size, this.size);

//            if( !this.isEnabled ) {
//                SHADER_ALPHA_OVERRIDE.render(() -> super.render(gui, stack, partTicks, x, y, mouseX, mouseY, inst), 0.25F);
//            } else {
//            }
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
