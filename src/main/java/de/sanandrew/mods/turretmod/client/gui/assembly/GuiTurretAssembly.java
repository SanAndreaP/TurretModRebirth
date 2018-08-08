/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.assembly;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.client.ShaderHelper;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.assembly.IRecipeEntry;
import de.sanandrew.mods.turretmod.api.assembly.IRecipeGroup;
import de.sanandrew.mods.turretmod.client.gui.control.GuiSlimButton;
import de.sanandrew.mods.turretmod.client.shader.ShaderItemAlphaOverride;
import de.sanandrew.mods.turretmod.client.util.Shaders;
import de.sanandrew.mods.turretmod.inventory.ContainerTurretAssembly;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketAssemblyToggleAutomate;
import de.sanandrew.mods.turretmod.network.PacketInitAssemblyCrafting;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.tileentity.assembly.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GuiTurretAssembly
        extends GuiContainer
{
    @Nonnull
    private final ItemStack upgIconAuto;
    @Nonnull
    private final ItemStack upgIconSpeed;
    @Nonnull
    private final ItemStack upgIconFilter;

    private final TileEntityTurretAssembly assembly;
    private List<Tuple> cacheRecipes;
    private FontRenderer frDetails;
    private boolean shiftPressed;

    private static int scrollGroupPos;
    private static int currOpenTab;

    private boolean prevIsLmbDown;
    private boolean prevIsRmbDown;
    private boolean isScrolling;
    private int scrollPos;

    private GuiSlimButton cancelTask;
    private GuiSlimButton automate;
    private GuiSlimButton manual;
    private GuiAssemblyTabNav groupUp;
    private GuiAssemblyTabNav groupDown;
    private Map<GuiAssemblyCategoryTab, IRecipeGroup> groupBtns;

    private long lastTimestamp;
    private int maxEnergy;
    private int currEnergy;
    private int ticksCrafted;
    private int maxTicksCraft;
    private boolean hasCrafting;
    private UUID currCrfUUID;
    private ItemStack currCrfItem;

    private final ShaderItemAlphaOverride shaderCallback = new ShaderItemAlphaOverride();

    public GuiTurretAssembly(InventoryPlayer invPlayer, TileEntityTurretAssembly tile) {
        super(new ContainerTurretAssembly(invPlayer, tile));
        this.assembly = tile;

        this.xSize = 230;
        this.ySize = 222;

//        this.assembly.syncStacks = false;

        this.upgIconAuto = new ItemStack(ItemRegistry.ASSEMBLY_UPG_AUTO);
        this.upgIconSpeed = new ItemStack(ItemRegistry.ASSEMBLY_UPG_SPEED);
        this.upgIconFilter = new ItemStack(ItemRegistry.ASSEMBLY_UPG_FILTER);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();

        this.buttonList.clear();

        this.frDetails = new FontRenderer(this.mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.mc.getTextureManager(), true);

        this.buttonList.add(this.cancelTask = new GuiSlimButton(this.buttonList.size(), this.guiLeft + 156, this.guiTop + 55, 50, LangUtils.translate(Lang.TASSEMBLY_BTN_CANCEL.get())));
        this.buttonList.add(this.automate = new GuiSlimButton(this.buttonList.size(), this.guiLeft + 156, this.guiTop + 68, 50, LangUtils.translate(Lang.TASSEMBLY_BTN_AUTOENABLE.get())));
        this.buttonList.add(this.manual = new GuiSlimButton(this.buttonList.size(), this.guiLeft + 156, this.guiTop + 81, 50, LangUtils.translate(Lang.TASSEMBLY_BTN_AUTODISABLE.get())));

        this.buttonList.add(this.groupUp = new GuiAssemblyTabNav(this.buttonList.size(), this.guiLeft + 13, this.guiTop + 9, false));

        int pos = 0;
        IRecipeGroup[] groups = TurretAssemblyRegistry.INSTANCE.getGroups();
        Arrays.sort(groups, Comparator.comparing(IRecipeGroup::getName));
        this.groupBtns = new HashMap<>(1 + (int) (groups.length / 0.75F));
        for( IRecipeGroup grp : groups ) {
            GuiAssemblyCategoryTab tab = new GuiAssemblyCategoryTab(this.buttonList.size(), this.guiLeft + 9, this.guiTop + 19 + pos * 15 - 15 * scrollGroupPos, grp.getIcon(), LangUtils.translate(grp.getName()));
            this.groupBtns.put(tab, grp);
            this.buttonList.add(tab);

            tab.visible = pos >= scrollGroupPos && pos < scrollGroupPos + 4;

            if( pos == currOpenTab ) {
                tab.enabled = false;
                loadGroupRecipes(grp);
            }
            pos++;
        }

        this.buttonList.add(this.groupDown = new GuiAssemblyTabNav(this.buttonList.size(), this.guiLeft + 13, this.guiTop + 79, true));

        if( scrollGroupPos + 4 >= groups.length ) {
            this.groupDown.visible = false;
        }
        if( scrollGroupPos <= 0 ) {
            this.groupUp.visible = false;
        }
    }

    private void loadGroupRecipes(IRecipeGroup group) {
        this.cacheRecipes = new ArrayList<>();
        this.cacheRecipes.addAll(group.getRecipeIdList().stream().map(recipe -> new Tuple(recipe, TurretAssemblyRegistry.INSTANCE.getRecipeResult(recipe))).collect(Collectors.toList()));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        IEnergyStorage energyCap = this.assembly.getCapability(CapabilityEnergy.ENERGY, EnumFacing.DOWN);
        if( energyCap != null ) {
            this.currEnergy = energyCap.getEnergyStored();
            this.maxEnergy = energyCap.getMaxEnergyStored();
        }

        if( this.assembly.isActive ) {
            this.ticksCrafted = this.assembly.getTicksCrafted();
        } else {
            this.ticksCrafted = 0;
        }

        this.maxTicksCraft = this.assembly.getMaxTicksCrafted();
        this.hasCrafting = this.assembly.currCrafting != null;
        if( this.hasCrafting ) {
            this.currCrfUUID = this.assembly.currCrafting.getValue(0);
            this.currCrfItem = this.assembly.currCrafting.getValue(1);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partTicks, int mouseX, int mouseY) {
        boolean isLmbDown = Mouse.isButtonDown(0);
        boolean isRmbDown = Mouse.isButtonDown(1);
        int energyBarY = Math.max(0, Math.min(82, MathHelper.ceil((1.0D - (this.currEnergy / (double) this.maxEnergy)) * 82.0D)));
        double procPerc = this.ticksCrafted / (double) this.maxTicksCraft;
        int procBarX = Math.max(0, Math.min(50, MathHelper.ceil(procPerc * 50.0D)));

        this.mc.getTextureManager().bindTexture(Resources.GUI_ASSEMBLY_CRF.resource);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(this.guiLeft + 210, this.guiTop + 8 + energyBarY, 230, 12 + energyBarY, 12, 82 - energyBarY);
        this.drawTexturedModalRect(this.guiLeft + 156, this.guiTop + 30, 0, 222, procBarX, 5);

        if( this.cacheRecipes != null ) {
            int maxScroll = this.cacheRecipes.size() - 4;
            if( maxScroll > 0 && !this.hasCrafting ) {
                int scrollBtnPos = MathHelper.floor(79.0D / maxScroll * this.scrollPos);

                if( (mouseX >= this.guiLeft + 144 && mouseX < this.guiLeft + 150 && mouseY >= this.guiTop + 7 && mouseY < this.guiTop + 92) || this.isScrolling ) {
                    if( isLmbDown ) {
                        scrollBtnPos = Math.min(79, Math.max(0, mouseY - 7 - this.guiTop));
                        this.scrollPos = MathHelper.floor(scrollBtnPos / 78.0D * maxScroll);
                        this.isScrolling = true;
                    }
                }

                this.drawTexturedModalRect(this.guiLeft + 144, this.guiTop + 7 + scrollBtnPos, 230, 6, 6, 6);
            }

            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GuiUtils.glScissor(this.guiLeft + 33, this.guiTop + 8, 110, 83);

            int maxSz = Math.min(4, this.cacheRecipes.size());
            for( int i = 0; i < maxSz; i++) {
                int index = this.scrollPos + i;
                ItemStack stack = this.cacheRecipes.get(index).getValue(1);
                boolean isActive = !this.hasCrafting || (!this.assembly.isAutomated() && ItemStackUtils.areEqual(this.currCrfItem, stack));

                RenderUtils.renderStackInGui(stack, this.guiLeft + 36, this.guiTop + 10 + 21 * i, 1.0F, this.fontRenderer);

                List tooltip = GuiUtils.getTooltipWithoutShift(stack);
                this.frDetails.drawString(tooltip.get(0).toString(), this.guiLeft + 57, this.guiTop + 10 + 21 * i, 0xFFFFFFFF);
                if( tooltip.size() > 1 ) {
                    this.frDetails.drawString(tooltip.get(1).toString(), this.guiLeft + 57, this.guiTop + 19 + 21 * i, 0xFF808080);
                }

                if( isActive ) {
                    if( mouseX >= this.guiLeft + 35 && mouseX < this.guiLeft + 143 && mouseY >= this.guiTop + 9 + 21 * i && mouseY < this.guiTop + 27 + 21 * i ) {
                        GlStateManager.disableDepth();
                        GlStateManager.colorMask(true, true, true, false);
                        this.drawGradientRect(this.guiLeft + 35, this.guiTop + 9 + 21 * i, this.guiLeft + 143, this.guiTop + 27 + 21 * i, 0x2AFFFFFF, 0x2AFFFFFF);
                        GlStateManager.colorMask(true, true, true, true);
                        GlStateManager.enableDepth();

                        if( isLmbDown && !this.prevIsLmbDown ) {
                            PacketRegistry.sendToServer(new PacketInitAssemblyCrafting(this.assembly, this.cacheRecipes.get(index).getValue(0), this.shiftPressed ? 16 : 1));
                        }
                        if( isRmbDown && !this.prevIsRmbDown ) {
                            PacketRegistry.sendToServer(new PacketInitAssemblyCrafting(this.assembly, this.cacheRecipes.get(index).getValue(0), this.shiftPressed ? -16 : -1));
                        }
                    }
                } else {
                    this.mc.getTextureManager().bindTexture(Resources.GUI_ASSEMBLY_CRF.resource);

                    GlStateManager.disableDepth();
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.75F);
                    this.drawTexturedModalRect(this.guiLeft + 35, this.guiTop + 9 + 21 * i, 35, 9 + 21 * i, 108, 18);
                    GlStateManager.disableBlend();
                    GlStateManager.enableDepth();
                }
            }

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }

        if( !this.assembly.hasAutoUpgrade() ) {
            this.shaderCallback.alphaMulti = 0.35F;
            ShaderHelper.useShader(Shaders.alphaOverride, this.shaderCallback::call);
            RenderUtils.renderStackInGui(this.upgIconAuto, this.guiLeft + 14, this.guiTop + 100, 1.0F);
            ShaderHelper.releaseShader();
        }
        if( !this.assembly.hasSpeedUpgrade() ) {
            this.shaderCallback.alphaMulti = 0.35F;
            ShaderHelper.useShader(Shaders.alphaOverride, this.shaderCallback::call);
            RenderUtils.renderStackInGui(this.upgIconSpeed, this.guiLeft + 14, this.guiTop + 118, 1.0F);
            ShaderHelper.releaseShader();
        }
        if( this.assembly.hasFilterUpgrade() ) {
            NonNullList<ItemStack> filteredStacks = this.assembly.getFilterStacks();
            for( int i = 0; i < filteredStacks.size(); i++ ) {
                ItemStack filterStack = filteredStacks.get(i);
                if( ItemStackUtils.isValid(filterStack) && !ItemStackUtils.isValid(this.assembly.getInventory().getStackInSlot(i + 5)) ) {
                    int x = i % 9;
                    int y = i / 9;

                    RenderUtils.renderStackInGui(filterStack, this.guiLeft + 36 + x * 18, this.guiTop + 100 + y * 18, 1.0D);

                    this.mc.getTextureManager().bindTexture(Resources.GUI_ASSEMBLY_CRF.resource);
                    GlStateManager.disableDepth();
                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 0.75F);
                    this.drawTexturedModalRect(this.guiLeft + 35 + x * 18, this.guiTop + 99 + y * 18, 35 + x * 18, 99 + 18 * y, 18, 18);
                    GlStateManager.disableBlend();
                    GlStateManager.enableDepth();
                }
            }
        } else {
            this.shaderCallback.alphaMulti = 0.35F;
            ShaderHelper.useShader(Shaders.alphaOverride, this.shaderCallback::call);
            RenderUtils.renderStackInGui(this.upgIconFilter, this.guiLeft + 202, this.guiTop + 100, 1.0F);
            ShaderHelper.releaseShader();
        }

        if( this.hasCrafting ) {
            String cnt = String.format("%d", this.currCrfItem.getCount());
            if( this.assembly.isAutomated() ) {
                cnt = String.valueOf('\u221E');
            }

            this.frDetails.drawString(LangUtils.translate(Lang.TASSEMBLY_CRAFTING.get()), this.guiLeft + 156, this.guiTop + 40, 0xFF303030);
            RenderUtils.renderStackInGui(this.currCrfItem, this.guiLeft + 190, this.guiTop + 36, 1.0F, this.fontRenderer, cnt, true);

            this.cancelTask.enabled = true;
            this.automate.enabled = false;
            this.manual.enabled = false;
        } else {
            this.cancelTask.enabled = false;
            this.automate.enabled = !this.assembly.isAutomated();
            this.manual.enabled = !this.automate.enabled;
        }

        this.automate.visible = ItemStackUtils.isValid(this.assembly.getInventory().getStackInSlot(1));
        this.manual.visible = this.automate.visible;
        this.prevIsLmbDown = isLmbDown;
        this.prevIsRmbDown = isRmbDown;

        if( !isLmbDown ) {
            this.isScrolling = false;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        RenderHelper.disableStandardItemLighting();

        if( mouseX >= this.guiLeft + 210 && mouseX < this.guiLeft + 222 && mouseY >= this.guiTop + 8 && mouseY < this.guiTop + 90 ) {
            this.drawRFluxLabel(mouseX - this.guiLeft, mouseY - this.guiTop);
        }

        if( this.cacheRecipes != null ) {
            int maxSz = Math.min(4, this.cacheRecipes.size());
            for( int i = 0; i < maxSz; i++) {
                int index = this.scrollPos + i;

                if( !this.hasCrafting ) {
                    if( mouseX >= this.guiLeft + 35 && mouseX < this.guiLeft + 143 && mouseY >= this.guiTop + 9 + 21 * i && mouseY < this.guiTop + 27 + 21 * i ) {
                        if( this.shiftPressed ) {
                            this.drawIngredientsDetail(mouseX - this.guiLeft, mouseY - this.guiTop, this.cacheRecipes.get(index).getValue(0), false);
                        } else {
                            this.drawIngredientsSmall(mouseX - this.guiLeft, mouseY - this.guiTop, this.cacheRecipes.get(index).getValue(0), false);
                        }
                    }
                }
            }
        }

        if( this.hasCrafting && mouseX >= this.guiLeft + 190 && mouseX < this.guiLeft + 206 && mouseY >= this.guiTop + 36 && mouseY < this.guiTop + 52 ) {
            if( this.shiftPressed ) {
                this.drawIngredientsDetail(mouseX - this.guiLeft, mouseY - this.guiTop, this.currCrfUUID, true);
            } else {
                this.drawIngredientsSmall(mouseX - this.guiLeft, mouseY - this.guiTop, this.currCrfUUID, true);
            }
        }

        long time = System.currentTimeMillis();
        if( this.lastTimestamp + 1000 < time ) {
            this.lastTimestamp = time;
        }

        RenderHelper.enableGUIStandardItemLighting();
    }

    private void drawIngredientsDetail(int mouseX, int mouseY, UUID recipe, boolean showOnLeft) {
        TurretAssemblyRegistry.RecipeEntry recipeEntry = TurretAssemblyRegistry.INSTANCE.getRecipeEntry(recipe);
        if( recipeEntry == null ) {
            return;
        }

        IRecipeEntry[] ingredients = recipeEntry.resources;
        if( ingredients.length < 1 ) {
            this.drawIngredientsSmall(mouseX, mouseY, recipe, showOnLeft);
            return;
        }

        Map<IRecipeEntry, Tuple> desc = new HashMap<>(ingredients.length);
        List<Integer> lngth = new ArrayList<>(ingredients.length);
        int tHeight = 0;

        for( IRecipeEntry entry : ingredients ) {
            ItemStack[] entryStacks = entry.getEntryItemStacks();
            ItemStack stack = entryStacks[(int)((this.lastTimestamp / 1000L) % entryStacks.length)];
            List<?> tooltip = GuiUtils.getTooltipWithoutShift(stack);

            String dscL1 = String.format("%dx %s", stack.getCount(), tooltip.get(0));
            String dscL2 = null;
            tHeight+=9;

            if( tooltip.size() > 1 && entry.shouldDrawTooltip() ) {
                dscL2 = String.format("%s", tooltip.get(1));
                tHeight+=9;
            }

            desc.put(entry, new Tuple(stack, dscL1, dscL2));
            lngth.add(Math.max(this.frDetails.getStringWidth(dscL1), dscL2 == null ? 0 : this.frDetails.getStringWidth(dscL2)));
        }

        int textWidth = (lngth.size() > 0 ? Collections.max(lngth) : 0) + 10;
        int xPos = mouseX + (showOnLeft ? -textWidth - 12 : 12);
        int yPos = mouseY - 12;

        int bkgColor = 0xF0100010;
        int lightBg = 0x505000FF;
        int darkBg = (lightBg & 0xFEFEFE) >> 1 | lightBg & 0xFF000000;

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 300.0F);

        this.drawGradientRect(xPos - 3, yPos - 4, xPos + textWidth + 3, yPos - 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 3, yPos + tHeight + 3, xPos + textWidth + 3, yPos + tHeight + 4, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 3, yPos - 3, xPos + textWidth + 3, yPos + tHeight + 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 4, yPos - 3, xPos - 3, yPos + tHeight + 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos + textWidth + 3, yPos - 3, xPos + textWidth + 4, yPos + tHeight + 3, bkgColor, bkgColor);

        this.drawGradientRect(xPos - 3, yPos - 3 + 1, xPos - 3 + 1, yPos + tHeight + 3 - 1, lightBg, darkBg);
        this.drawGradientRect(xPos + textWidth + 2, yPos - 3 + 1, xPos + textWidth + 3, yPos + tHeight + 3 - 1, lightBg, darkBg);
        this.drawGradientRect(xPos - 3, yPos - 3, xPos + textWidth + 3, yPos - 3 + 1, lightBg, lightBg);
        this.drawGradientRect(xPos - 3, yPos + tHeight + 2, xPos + textWidth + 3, yPos + tHeight + 3, darkBg, darkBg);

        for( int i = 0, j = 0; i < ingredients.length; i++, j++ ) {
            Tuple descIng = desc.get(ingredients[i]);

            RenderHelper.enableGUIStandardItemLighting();
            RenderUtils.renderStackInGui(descIng.getValue(0), xPos, yPos + j * 9, 0.5F);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableDepth();

            this.frDetails.drawString(descIng.getValue(1), xPos + 10, yPos + j * 9, 0xFF3F3F3F);
            this.frDetails.drawString(descIng.getValue(1), xPos + 10, yPos + j * 9, 0xFFFFFFFF);
            if( descIng.getValue(2) != null ) {
                this.frDetails.drawString(descIng.getValue(2), xPos + 10, yPos + j * 9 + 9, 0xFF6F6F6F);
                j++;
            }

            GlStateManager.enableDepth();
        }

        GlStateManager.popMatrix();
    }

    private void drawIngredientsSmall(int mouseX, int mouseY, UUID recipe, boolean showOnLeft) {
        TurretAssemblyRegistry.RecipeEntry recipeEntry = TurretAssemblyRegistry.INSTANCE.getRecipeEntry(recipe);
        if( recipeEntry == null ) {
            return;
        }

        IRecipeEntry[] ingredients = recipeEntry.resources;

        String rf = String.format("%d RF/t", MathHelper.ceil(recipeEntry.fluxPerTick * (this.assembly.hasSpeedUpgrade() ? 1.1F : 1.0F)));
        String ticks = MiscUtils.getTimeFromTicks(recipeEntry.ticksProcessing);

        int textWidth = Math.max(Math.max(ingredients.length * 9, this.frDetails.getStringWidth(rf) + 10), this.frDetails.getStringWidth(ticks) + 10);
        int xPos = mouseX + (showOnLeft ? -textWidth - 12 : 12);
        int yPos = mouseY - 12;
        int height = ingredients.length > 0 ? 31 : 18;

        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0D, 0.0D, 300.0D);

        int bkgColor = 0xF0100010;
        int lightBg = 0x505000FF;
        int darkBg = (lightBg & 0xFEFEFE) >> 1 | lightBg & 0xFF000000;

        this.drawGradientRect(xPos - 3, yPos - 4, xPos + textWidth + 3, yPos - 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 3, yPos + height + 3, xPos + textWidth + 3, yPos + height + 4, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 3, yPos - 3, xPos + textWidth + 3, yPos + height + 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 4, yPos - 3, xPos - 3, yPos + height + 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos + textWidth + 3, yPos - 3, xPos + textWidth + 4, yPos + height + 3, bkgColor, bkgColor);

        this.drawGradientRect(xPos - 3, yPos - 3 + 1, xPos - 3 + 1, yPos + height + 3 - 1, lightBg, darkBg);
        this.drawGradientRect(xPos + textWidth + 2, yPos - 3 + 1, xPos + textWidth + 3, yPos + height + 3 - 1, lightBg, darkBg);
        this.drawGradientRect(xPos - 3, yPos - 3, xPos + textWidth + 3, yPos - 3 + 1, lightBg, lightBg);
        this.drawGradientRect(xPos - 3, yPos + height + 2, xPos + textWidth + 3, yPos + height + 3, darkBg, darkBg);

        if( ingredients.length > 0 ) {
            this.drawGradientRect(xPos - 2, yPos + 10, xPos + textWidth + 2, yPos + 11, lightBg, darkBg);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(Resources.GUI_ASSEMBLY_CRF.resource);
        GlStateManager.pushMatrix();
        if( ingredients.length < 1 ) {
            GlStateManager.translate(0.0F, -10.0F, 0.0F);
        } else {
            GlStateManager.translate(0.0F, 3.0F, 0.0F);
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(xPos, yPos, 0.0F);
        GlStateManager.scale(0.5F, 0.5F, 1.0F);
        this.drawTexturedModalRect(0, 20, 230, 94, 16, 16);
        this.drawTexturedModalRect(0, 40, 230, 110, 16, 16);
        GlStateManager.popMatrix();

        GlStateManager.disableDepth();
        this.frDetails.drawString(rf, xPos + 10, yPos + 10, 0xFFFFFFFF);
        this.frDetails.drawString(ticks, xPos + 10, yPos + 20, 0xFFFFFFFF);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();

        RenderHelper.enableGUIStandardItemLighting();
        for( int i = 0; i < ingredients.length; i++ ) {
            ItemStack[] entryStacks = ingredients[i].getEntryItemStacks();
            ItemStack stack = entryStacks[(int)((this.lastTimestamp / 1000L) % entryStacks.length)];
            RenderUtils.renderStackInGui(stack, xPos + i * 9, yPos, 0.5F);
        }
        RenderHelper.disableStandardItemLighting();

        GlStateManager.popMatrix();
    }

    private void drawRFluxLabel(int mouseX, int mouseY) {
        IEnergyStorage stg = this.assembly.getCapability(CapabilityEnergy.ENERGY, EnumFacing.DOWN);
        if( stg == null ) {
            return;
        }
        String amount = String.format("%d / %d RF", stg.getEnergyStored(), stg.getMaxEnergyStored());
        String consumption = LangUtils.translate(Lang.TASSEMBLY_RF_USING.get(), this.assembly.getFluxConsumption() * (this.assembly.hasSpeedUpgrade() ? 4 : 1));

        int textWidth = Math.max(this.fontRenderer.getStringWidth(amount), this.fontRenderer.getStringWidth(consumption));
        int xPos = mouseX - 12 - textWidth;
        int yPos = mouseY - 12;
        byte height = 18;

        RenderHelper.disableStandardItemLighting();

        int bkgColor = 0xF0100010;
        int lightBg = 0x505000FF;
        int darkBg = (lightBg & 0xFEFEFE) >> 1 | lightBg & 0xFF000000;

        this.zLevel = 400.0F;
        this.drawGradientRect(xPos - 3, yPos - 4, xPos + textWidth + 3, yPos - 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 3, yPos + height + 3, xPos + textWidth + 3, yPos + height + 4, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 3, yPos - 3, xPos + textWidth + 3, yPos + height + 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos - 4, yPos - 3, xPos - 3, yPos + height + 3, bkgColor, bkgColor);
        this.drawGradientRect(xPos + textWidth + 3, yPos - 3, xPos + textWidth + 4, yPos + height + 3, bkgColor, bkgColor);

        this.drawGradientRect(xPos - 3, yPos - 3 + 1, xPos - 3 + 1, yPos + height + 3 - 1, lightBg, darkBg);
        this.drawGradientRect(xPos + textWidth + 2, yPos - 3 + 1, xPos + textWidth + 3, yPos + height + 3 - 1, lightBg, darkBg);
        this.drawGradientRect(xPos - 3, yPos - 3, xPos + textWidth + 3, yPos - 3 + 1, lightBg, lightBg);
        this.drawGradientRect(xPos - 3, yPos + height + 2, xPos + textWidth + 3, yPos + height + 3, darkBg, darkBg);
        this.zLevel = 0.0F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(xPos, yPos, 0.0F);

        GlStateManager.disableDepth();
        GlStateManager.translate(0.5F, 0.5F, 0.0F);
        this.fontRenderer.drawString(amount, 0, 0, 0xFF3F3F3F);
        this.fontRenderer.drawString(consumption, 0, 9, 0xFF3F3F3F);
        GlStateManager.translate(-0.5F, -0.5F, -0.0F);
        this.fontRenderer.drawString(amount, 0, 0, 0xFFFFFFFF);
        this.fontRenderer.drawString(consumption, 0, 9, 0xFFFFFFFF);
        GlStateManager.enableDepth();
        RenderHelper.enableGUIStandardItemLighting();

        GlStateManager.popMatrix();
    }

    @Override
    public void handleKeyboardInput() throws IOException {
        this.shiftPressed = false;

        super.handleKeyboardInput();
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) throws IOException {
        this.shiftPressed = keyCode == Keyboard.KEY_LSHIFT;

        super.keyTyped(keyChar, keyCode);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if( this.cacheRecipes != null && this.cacheRecipes.size() > 4 && !this.hasCrafting ) {
            int dWheelDir = Mouse.getEventDWheel();
            if( dWheelDir < 0 ) {
                this.scrollPos = Math.min(this.cacheRecipes.size() - 4, this.scrollPos + 1);
            } else if( dWheelDir > 0 ) {
                this.scrollPos = Math.max(0, this.scrollPos - 1);
            }
        }
    }

    private GuiAssemblyCategoryTab[] getSortedTabs() {
        GuiAssemblyCategoryTab[] tabs = this.groupBtns.keySet().toArray(new GuiAssemblyCategoryTab[this.groupBtns.size()]);

        Arrays.sort(tabs, Comparator.comparingInt(o -> o.id));

        return tabs;
    }

    @Override
    protected void actionPerformed(GuiButton btn) throws IOException {
        if( btn.id == this.cancelTask.id ) {
            PacketRegistry.sendToServer(new PacketInitAssemblyCrafting(this.assembly));
        } else if( btn.id == this.automate.id || btn.id == this.manual.id ) {
            PacketRegistry.sendToServer(new PacketAssemblyToggleAutomate(this.assembly));
        } else if( btn instanceof GuiAssemblyCategoryTab && this.groupBtns.containsKey(btn) ) {
            this.scrollPos = 0;
            IRecipeGroup grp = this.groupBtns.get(btn);
            GuiAssemblyCategoryTab[] tabs = getSortedTabs();
            for( int i = 0; i < tabs.length; i++ ) {
                tabs[i].enabled = true;
                if( tabs[i] == btn ) {
                    currOpenTab = i;
                }
            }
            btn.enabled = false;
            this.loadGroupRecipes(grp);
        } else if( btn.id == this.groupDown.id ) {
            GuiAssemblyCategoryTab[] tabs = this.getSortedTabs();
            if( scrollGroupPos + 4 < tabs.length ) {
                scrollGroupPos++;
                for( int i = 0; i < tabs.length; i++ ) {
                    tabs[i].y -= 15;
                    tabs[i].visible = i >= scrollGroupPos && i < scrollGroupPos + 4;
                }

                this.groupUp.visible = true;
                if( scrollGroupPos + 4 >= tabs.length ) {
                    this.groupDown.visible = false;
                }
            }
        } else if( btn.id == this.groupUp.id ) {
            GuiAssemblyCategoryTab[] tabs = this.getSortedTabs();
            if( scrollGroupPos > 0 ) {
                scrollGroupPos--;
                for( int i = 0; i < tabs.length; i++ ) {
                    tabs[i].y += 15;
                    tabs[i].visible = i >= scrollGroupPos && i < scrollGroupPos + 4;
                }

                this.groupDown.visible = true;
                if( scrollGroupPos <= 0 ) {
                    this.groupUp.visible = false;
                }
            }
        } else {
            super.actionPerformed(btn);
        }
    }
}
