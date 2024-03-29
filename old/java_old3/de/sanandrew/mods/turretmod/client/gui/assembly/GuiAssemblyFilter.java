/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.assembly;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.inventory.container.ContainerAssemblyFilter;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.Resources;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class GuiAssemblyFilter
        extends GuiContainer
{
    private int posY;
    private int posX;

    public GuiAssemblyFilter(InventoryPlayer invPlayer, @Nonnull ItemStack assemblyFilter) {
        super(new ContainerAssemblyFilter(invPlayer, assemblyFilter, 0));

        this.xSize = 176;
        this.ySize = 148;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.posX = (this.width - this.xSize) / 2;
        this.posY = (this.height - this.ySize) / 2;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawDefaultBackground();

        this.mc.getTextureManager().bindTexture(Resources.GUI_ASSEMBLY_FLT.resource);

        this.drawTexturedModalRect(this.posX, this.posY, 0, 0, this.xSize, this.ySize);

        if( !ItemStackUtils.isValid(this.mc.player.getHeldItemMainhand()) || this.mc.player.getHeldItemMainhand().getItem() != ItemRegistry.ASSEMBLY_UPG_FILTER ) {
            this.mc.player.closeScreen();
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        String s = LangUtils.translate(ItemRegistry.ASSEMBLY_UPG_FILTER.getTranslationKey() + ".name");
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString(LangUtils.translate(LangUtils.CONTAINER_INV.get()), 8, this.ySize - 96 + 3, 4210752);
    }
}
