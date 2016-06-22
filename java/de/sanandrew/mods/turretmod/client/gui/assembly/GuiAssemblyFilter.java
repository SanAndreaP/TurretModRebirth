/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.assembly;

import de.sanandrew.mods.turretmod.inventory.ContainerAssemblyFilter;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.darkhax.bookshelf.lib.util.ItemStackUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class GuiAssemblyFilter
        extends GuiContainer
{
    private int posY;
    private int posX;

    public GuiAssemblyFilter(InventoryPlayer invPlayer, ItemStack assemblyFilter) {
        super(new ContainerAssemblyFilter(invPlayer, assemblyFilter, 0));

        this.xSize = 176;
        this.ySize = 148;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();

        this.posX = (this.width - this.xSize) / 2;
        this.posY = (this.height - this.ySize) / 2;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(Resources.GUI_ASSEMBLY_FLT.getResource());

        this.drawTexturedModalRect(this.posX, this.posY, 0, 0, this.xSize, this.ySize);

        if( !ItemStackUtils.isValidStack(this.mc.thePlayer.getHeldItemMainhand()) || this.mc.thePlayer.getHeldItemMainhand().getItem() != ItemRegistry.asbFilter ) {
            this.mc.thePlayer.closeScreen();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        String s = Lang.translate(ItemRegistry.asbFilter.getUnlocalizedName() + ".name");
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(Lang.translate(Lang.CONTAINER_INV.get()), 8, this.ySize - 96 + 3, 4210752);
    }
}