/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.assembly;

import de.sanandrew.mods.turretmod.client.gui.control.GuiSlimButton;
import de.sanandrew.mods.turretmod.client.util.TmrClientUtils;
import de.sanandrew.mods.turretmod.inventory.ContainerTurretAssembly;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.network.PacketAssemblyToggleAutomate;
import de.sanandrew.mods.turretmod.network.PacketInitAssemblyCrafting;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.util.Textures;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretAssemblyRecipes;
import net.darkhax.bookshelf.lib.javatuples.Pair;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.*;

public class GuiTurretAssembly
        extends GuiContainer
{
    private final ItemStack upgIcon;

    private TileEntityTurretAssembly assembly;
    private List<Pair<UUID, ItemStack>> cacheRecipes;
    private FontRenderer frDetails;
    private boolean shiftPressed;

    private int scrollPos;

    private static int scrollGroupPos;
    private static int currOpenTab;

    private boolean prevIsLmbDown;
    private boolean prevIsRmbDown;
    private boolean isScrolling;

    private GuiSlimButton cancelTask;
    private GuiSlimButton automate;
    private GuiAssemblyTabNav groupUp;
    private GuiAssemblyTabNav groupDown;
    private Map<GuiAssemblyCategoryTab, TurretAssemblyRecipes.RecipeGroup> groupBtns;
    private int posY;
    private int posX;

    public GuiTurretAssembly(InventoryPlayer invPlayer, TileEntityTurretAssembly tile) {
        super(new ContainerTurretAssembly(invPlayer, tile));
        this.assembly = tile;

        this.xSize = 230;
        this.ySize = 222;

        this.assembly.syncStacks = false;

        this.upgIcon = new ItemStack(ItemRegistry.asbAuto);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();

        this.posX = (this.width - this.xSize) / 2;
        this.posY = (this.height - this.ySize) / 2;

        this.frDetails = new FontRenderer(this.mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.mc.getTextureManager(), true);

        this.buttonList.add(this.cancelTask = new GuiSlimButton(this.buttonList.size(), this.posX + 156, this.posY + 55, 50, StatCollector.translateToLocal("gui.sapturretmod.tassembly.cancel")));
        this.buttonList.add(this.automate = new GuiSlimButton(this.buttonList.size(), this.posX + 156, this.posY + 68, 50, StatCollector.translateToLocal("gui.sapturretmod.tassembly.automate.enable")));

        this.buttonList.add(this.groupUp = new GuiAssemblyTabNav(this.buttonList.size(), this.posX + 13, this.posY + 9, false));

        int pos = 0;
        TurretAssemblyRecipes.RecipeGroup[] groups = TurretAssemblyRecipes.INSTANCE.getGroups();
        Arrays.sort(groups, new Comparator<TurretAssemblyRecipes.RecipeGroup>() {
            @Override
            public int compare(TurretAssemblyRecipes.RecipeGroup o1, TurretAssemblyRecipes.RecipeGroup o2) {
                return o1.name.compareTo(o2.name);
            }
        });
        this.groupBtns = new HashMap<>(1 + (int) (groups.length / 0.75F));
        for( TurretAssemblyRecipes.RecipeGroup grp : groups ) {
            GuiAssemblyCategoryTab tab = new GuiAssemblyCategoryTab(this.buttonList.size(), this.posX + 9, this.posY + 19 + pos * 15 - 15 * scrollGroupPos, grp.icon, grp.name);
            this.groupBtns.put(tab, grp);
            this.buttonList.add(tab);

            tab.visible = pos >= scrollGroupPos && pos < scrollGroupPos + 4;

            if( pos == currOpenTab ) {
                tab.enabled = false;
                loadGroupRecipes(grp);
            }
            pos++;
        }

        this.buttonList.add(this.groupDown = new GuiAssemblyTabNav(this.buttonList.size(), this.posX + 13, this.posY + 79, true));

        if( scrollGroupPos + 4 >= groups.length ) {
            this.groupDown.visible = false;
        }
        if( scrollGroupPos <= 0 ) {
            this.groupUp.visible = false;
        }
    }

    private void loadGroupRecipes(TurretAssemblyRecipes.RecipeGroup group) {
        this.cacheRecipes = new ArrayList<>();
        for( UUID recipe : group.recipes ) {
            this.cacheRecipes.add(Pair.with(recipe, TurretAssemblyRecipes.INSTANCE.getRecipeResult(recipe)));
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partTicks, int mouseX, int mouseY) {
        boolean isLmbDown = Mouse.isButtonDown(0);
        boolean isRmbDown = Mouse.isButtonDown(1);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(Textures.GUI_ASSEMBLY_CRF.getResource());

        this.drawTexturedModalRect(this.posX, this.posY, 0, 0, this.xSize, this.ySize);

        int energy = this.assembly.getEnergyStored(ForgeDirection.DOWN);
        int maxEnergy = TileEntityTurretAssembly.MAX_FLUX_STORAGE;

        double energyPerc = energy / (double) maxEnergy;
        int energyBarY = Math.max(0, Math.min(82, MathHelper.ceiling_double_int((1.0D - energyPerc) * 82.0D)));

        this.drawTexturedModalRect(this.posX + 210, this.posY + 8 + energyBarY, 230, 12 + energyBarY, 12, 82 - energyBarY);

        double procPerc = this.assembly.isActive ? this.assembly.ticksCrafted / (double) this.assembly.maxTicksCrafted : 0.0D;
        int procBarX = Math.max(0, Math.min(50, MathHelper.ceiling_double_int(procPerc * 50.0D)));

        this.drawTexturedModalRect(this.posX + 156, this.posY + 30, 0, 222, procBarX, 5);

        int maxScroll = this.cacheRecipes.size() - 4;
        if( maxScroll > 0 && this.assembly.currCrafting == null ) {
            int scrollBtnPos = MathHelper.floor_double(79.0D / maxScroll * this.scrollPos);

            if( (mouseX >= this.posX + 144 && mouseX < this.posX + 150 && mouseY >= this.posY + 7 && mouseY < this.posY + 92) || this.isScrolling ) {
                if( isLmbDown ) {
                    scrollBtnPos = Math.min(79, Math.max(0, mouseY - 7 - this.posY));
                    this.scrollPos = MathHelper.floor_double(scrollBtnPos / 78.0D * maxScroll);
                    this.isScrolling = true;
                }
            }

            this.drawTexturedModalRect(this.posX + 144, this.posY + 7 + scrollBtnPos, 230, 6, 6, 6);
        }

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        TmrClientUtils.doGlScissor(this.posX + 33, this.posY + 8, 110, 83);

        if( this.cacheRecipes != null ) {
            int maxSz = Math.min(4, this.cacheRecipes.size());
            for( int i = 0; i < maxSz; i++) {
                int index = this.scrollPos + i;
                ItemStack stack = this.cacheRecipes.get(index).getValue1();
                boolean isActive = this.assembly.currCrafting == null
                        || (!this.assembly.isAutomated() && TmrUtils.areStacksEqual(this.assembly.currCrafting.getValue1(), stack, TmrUtils.NBT_COMPARATOR_FIXD));

                GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                RenderHelper.enableGUIStandardItemLighting();
                this.drawItemStack(stack, this.posX + 36, this.posY + 10 + 21 * i, 200.0F);
                GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                RenderHelper.disableStandardItemLighting();

                List tooltip = stack.getTooltip(this.mc.thePlayer, false);
                this.frDetails.drawString(tooltip.get(0).toString(), this.posX + 57, this.posY + 10 + 21 * i, 0xFFFFFFFF);
                if( tooltip.size() > 1 ) {
                    this.frDetails.drawString(tooltip.get(1).toString(), this.posX + 57, this.posY + 19 + 21 * i, 0xFF808080);
                }

                if( isActive ) {
                    if( mouseX >= this.posX + 35 && mouseX < this.posX + 143 && mouseY >= this.posY + 9 + 21 * i && mouseY < this.posY + 27 + 21 * i ) {
                        GL11.glDisable(GL11.GL_DEPTH_TEST);
                        GL11.glColorMask(true, true, true, false);
                        this.drawGradientRect(this.posX + 35, this.posY + 9 + 21 * i, this.posX + 143, this.posY + 27 + 21 * i, 0x2AFFFFFF, 0x2AFFFFFF);
                        GL11.glColorMask(true, true, true, true);
                        GL11.glEnable(GL11.GL_DEPTH_TEST);

                        if( isLmbDown && !this.prevIsLmbDown ) {
                            PacketRegistry.sendToServer(new PacketInitAssemblyCrafting(this.assembly, this.cacheRecipes.get(index).getValue0(), this.shiftPressed ? 16 : 1));
                        }
                        if( isRmbDown && !this.prevIsRmbDown ) {
                            PacketRegistry.sendToServer(new PacketInitAssemblyCrafting(this.assembly, this.cacheRecipes.get(index).getValue0(), this.shiftPressed ? -16 : -1));
                        }
                    }
                } else {
                    this.mc.getTextureManager().bindTexture(Textures.GUI_ASSEMBLY_CRF.getResource());

                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glEnable(GL11.GL_BLEND);
                    OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
                    this.drawTexturedModalRect(this.posX + 35, this.posY + 9 + 21 * i, 35, 9 + 21 * i, 108, 18);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);
                }
            }
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if( !ItemStackUtils.isValidStack(this.assembly.getStackInSlot(1)) ) {
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
            RenderHelper.disableStandardItemLighting();
            this.mc.renderEngine.bindTexture(this.mc.renderEngine.getResourceLocation(this.upgIcon.getItemSpriteNumber()));
            this.drawTexturedModelRectFromIcon(this.posX + 14, this.posY + 100, this.upgIcon.getIconIndex(), 16, 16);
            GL11.glDisable(GL11.GL_BLEND);
        }

        if( this.assembly.currCrafting != null ) {
            String cnt = String.format("%d", this.assembly.currCrafting.getValue1().stackSize);
            if( this.assembly.isAutomated() ) {
                cnt = String.valueOf('\u221E');
            }

            this.frDetails.drawString("Crafting:", this.posX + 156, this.posY + 40, 0xFF303030);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();
            this.drawItemStack(this.assembly.currCrafting.getValue1(), this.posX + 190, this.posY + 36, 200.0F);
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            this.fontRendererObj.drawString(cnt, this.posX + 191 + 18 - this.fontRendererObj.getStringWidth(cnt), this.posY + 37 + 18 - this.fontRendererObj.FONT_HEIGHT, 0xFF303030);
            this.fontRendererObj.drawString(cnt, this.posX + 190 + 18 - this.fontRendererObj.getStringWidth(cnt), this.posY + 36 + 18 - this.fontRendererObj.FONT_HEIGHT, 0xFFFFFFFF);

            this.cancelTask.enabled = true;
            this.automate.enabled = false;
        } else {
            this.cancelTask.enabled = false;
            this.automate.enabled = true;

            if( this.assembly.isAutomated() ) {
                this.automate.displayString = StatCollector.translateToLocal("gui.sapturretmod.tassembly.automate.disable");
            } else {
                this.automate.displayString = StatCollector.translateToLocal("gui.sapturretmod.tassembly.automate.enable");
            }
        }

        this.automate.visible = ItemStackUtils.isValidStack(this.assembly.getStackInSlot(1));
        this.prevIsLmbDown = isLmbDown;
        this.prevIsRmbDown = isRmbDown;

        if( !isLmbDown ) {
            this.isScrolling = false;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if( mouseX >= this.posX + 210 && mouseX < this.posX + 222 && mouseY >= this.posY + 8 && mouseY < this.posY + 90 ) {
            this.drawRFluxLabel(mouseX - this.posX, mouseY - posY);
        }

        if( this.cacheRecipes != null ) {
            int maxSz = Math.min(4, this.cacheRecipes.size());
            for( int i = 0; i < maxSz; i++) {
                int index = this.scrollPos + i;

                if( this.assembly.currCrafting == null ) {
                    if( mouseX >= this.posX + 35 && mouseX < this.posX + 143 && mouseY >= this.posY + 9 + 21 * i && mouseY < this.posY + 27 + 21 * i ) {
                        if( this.shiftPressed ) {
                            this.drawIngredientsDetail(mouseX - this.posX, mouseY - posY, this.cacheRecipes.get(index).getValue0());
                        } else {
                            this.drawIngredientsSmall(mouseX - this.posX, mouseY - posY, this.cacheRecipes.get(index).getValue0());
                        }
                    }
                }
            }
        }
    }

    private void drawItemStack(ItemStack stack, int x, int y, float z) {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = z;
        itemRender.zLevel = z;
        itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stack, x, y);
        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
        GL11.glTranslatef(0.0F, 0.0F, -32.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
    }

    private void drawIngredientsDetail(int mouseX, int mouseY, UUID recipe) {
        ItemStack[] ingredients = TurretAssemblyRecipes.INSTANCE.getRecipeEntry(recipe).resources;

        Map<ItemStack, String> desc = new HashMap<>(ingredients.length);
        List<Integer> lngth = new ArrayList<>(ingredients.length);

        for( ItemStack stack : ingredients ) {
            String dsc = String.format("%dx %s", stack.stackSize, stack.getTooltip(this.mc.thePlayer, false).get(0).toString());
            desc.put(stack, dsc);
            lngth.add(this.frDetails.getStringWidth(dsc));
        }

        int textWidth = Collections.max(lngth) + 10;
        int xPos = mouseX + 12;
        int yPos = mouseY - 12;
        int height = ingredients.length * 9;

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        this.zLevel = 400.0F;
        itemRender.zLevel = 300.0F;
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

        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;

        GL11.glEnable(GL11.GL_LIGHTING);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        for( int i = 0; i < ingredients.length; i++ ) {
            GL11.glPushMatrix();
            GL11.glTranslatef(xPos, yPos + i * 9, 0.0F);
            GL11.glScalef(0.5F, 0.5F, 1.0F);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();

            this.drawItemStack(ingredients[i], 0, 0, 500.0F);

            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glScalef(2.0F, 2.0F, 1.0F);
            GL11.glTranslatef(0.5F, 0.5F, 0.0F);
            this.frDetails.drawString(desc.get(ingredients[i]), 10, 0, 0xFF3F3F3F);
            GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
            this.frDetails.drawString(desc.get(ingredients[i]), 10, 0, 0xFFFFFFFF);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_LIGHTING);
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);

            GL11.glPopMatrix();
        }
    }

    private void drawIngredientsSmall(int mouseX, int mouseY, UUID recipe) {
        ItemStack[] ingredients = TurretAssemblyRecipes.INSTANCE.getRecipeEntry(recipe).resources;

        int textWidth = ingredients.length * 9;
        int xPos = mouseX + 12;
        int yPos = mouseY - 12;
        byte height = 8;

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        this.zLevel = 400.0F;
        itemRender.zLevel = 300.0F;
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

        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;

        GL11.glEnable(GL11.GL_LIGHTING);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);


        for( int i = 0; i < ingredients.length; i++ ) {
            GL11.glPushMatrix();
            GL11.glTranslatef(xPos + i * 9, yPos, 0.0F);
            GL11.glScalef(0.5F, 0.5F, 1.0F);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();

            this.drawItemStack(ingredients[i], 0, 0, 500.0F);

            GL11.glPopMatrix();
        }
    }

    private void drawRFluxLabel(int mouseX, int mouseY) {
        String amount = String.format("%d / %d RF", this.assembly.getEnergyStored(ForgeDirection.DOWN), this.assembly.getMaxEnergyStored(ForgeDirection.DOWN));
        String consumption = String.format("using %d RF/t", this.assembly.fluxConsumption);

        int textWidth = Math.max(this.fontRendererObj.getStringWidth(amount), this.fontRendererObj.getStringWidth(consumption));
        int xPos = mouseX + 12;
        int yPos = mouseY - 12;
        byte height = 18;

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        this.zLevel = 400.0F;
        itemRender.zLevel = 300.0F;
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

        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;


        GL11.glPushMatrix();
        GL11.glTranslatef(xPos, yPos, 0.0F);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
//        GL11.glScalef(2.0F, 2.0F, 1.0F);
        GL11.glTranslatef(0.5F, 0.5F, 0.0F);
        this.fontRendererObj.drawString(amount, 0, 0, 0xFF3F3F3F);
        this.fontRendererObj.drawString(consumption, 0, 9, 0xFF3F3F3F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.0F);
        this.fontRendererObj.drawString(amount, 0, 0, 0xFFFFFFFF);
        this.fontRendererObj.drawString(consumption, 0, 9, 0xFFFFFFFF);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GL11.glPopMatrix();
    }

    @Override
    public void handleKeyboardInput() {
        shiftPressed = false;

        super.handleKeyboardInput();
    }

    @Override
    public void onGuiClosed() {
        this.assembly.syncStacks = true;
        super.onGuiClosed();
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        this.shiftPressed = keyCode == Keyboard.KEY_LSHIFT;

        super.keyTyped(keyChar, keyCode);
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        if( this.cacheRecipes.size() > 4 && this.assembly.currCrafting == null ) {
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

        Arrays.sort(tabs, new Comparator<GuiAssemblyCategoryTab>() {
            @Override
            public int compare(GuiAssemblyCategoryTab o1, GuiAssemblyCategoryTab o2) {
                return o1.id - o2.id;
            }
        });

        return tabs;
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        if( btn.id == this.cancelTask.id ) {
            PacketRegistry.sendToServer(new PacketInitAssemblyCrafting(this.assembly));
        } else if( btn.id == this.automate.id ) {
            PacketRegistry.sendToServer(new PacketAssemblyToggleAutomate(this.assembly));
        } else if( btn instanceof GuiAssemblyCategoryTab && this.groupBtns.containsKey(btn) ) {
            TurretAssemblyRecipes.RecipeGroup grp = this.groupBtns.get(btn);
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
                    tabs[i].yPosition -= 15;
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
                    tabs[i].yPosition += 15;
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
