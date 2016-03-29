/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.gui.assembly;

import de.sanandrew.mods.turretmod.client.util.TmrClientUtils;
import de.sanandrew.mods.turretmod.inventory.ContainerTurretAssembly;
import de.sanandrew.mods.turretmod.network.PacketInitAssemblyCrafting;
import de.sanandrew.mods.turretmod.network.PacketRegistry;
import de.sanandrew.mods.turretmod.tileentity.TileEntityTurretAssembly;
import de.sanandrew.mods.turretmod.util.Textures;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import de.sanandrew.mods.turretmod.util.TurretAssemblyRecipes;
import net.darkhax.bookshelf.lib.javatuples.Pair;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GuiTurretAssembly
        extends GuiContainer
{
    private TileEntityTurretAssembly assembly;
    private List<Pair<UUID, ItemStack>> cacheRecipes;

    private boolean prevIsLmbDown = false;

    public GuiTurretAssembly(InventoryPlayer invPlayer, TileEntityTurretAssembly tile) {
        super(new ContainerTurretAssembly(invPlayer, tile));
        this.assembly = tile;

        this.xSize = 230;
        this.ySize = 222;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.cacheRecipes = TurretAssemblyRecipes.INSTANCE.getRecipeList();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partTicks, int mouseX, int mouseY) {
        boolean isLmbDown = Mouse.isButtonDown(0);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        this.mc.getTextureManager().bindTexture(Textures.GUI_ASSEMBLY_CRF.getResource());

        int posX = (this.width - this.xSize) / 2;
        int posY = (this.height - this.ySize) / 2;

        this.drawTexturedModalRect(posX, posY, 0, 0, this.xSize, this.ySize);

        int energy = this.assembly.getEnergyStored(ForgeDirection.DOWN);
        int maxEnergy = TileEntityTurretAssembly.MAX_FLUX_STORAGE;

        double energyPerc = energy / (double) maxEnergy;
        int energyBarY = Math.max(0, Math.min(82, MathHelper.ceiling_double_int((1.0D - energyPerc) * 82.0D)));

        this.drawTexturedModalRect(posX + 210, posY + 8 + energyBarY, 230, 12 + energyBarY, 12, 82 - energyBarY);

        double procPerc = this.assembly.active ? this.assembly.ticksCrafted / (double) this.assembly.maxTicksCrafted : 0.0D;
        int procBarX = Math.max(0, Math.min(50, MathHelper.ceiling_double_int(procPerc * 50.0D)));

        this.drawTexturedModalRect(posX + 146, posY + 30, 0, 222, procBarX, 5);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        TmrClientUtils.doGlScissor(posX + 11, posY + 8, 110, 83);

        if( this.cacheRecipes != null ) {
            int sz = this.cacheRecipes.size();
            for( int i = 0; i < sz; i++) {
                ItemStack stack = this.cacheRecipes.get(i).getValue1();

                this.drawItemStack(stack, posX + 14, posY + 10 + 21 * i);

                List tooltip = stack.getTooltip(this.mc.thePlayer, false);
                this.fontRendererObj.drawString(tooltip.get(0).toString(), posX + 35, posY + 10 + 21 * i, 0xFFFFFFFF);
                if( tooltip.size() > 1 ) {
                    this.fontRendererObj.drawString(tooltip.get(1).toString(), posX + 35, posY + 19 + 21 * i, 0xFF808080);
                }

                if( mouseX >= posX + 13 && mouseX < posX + 121 && mouseY >= posY + 9 + 21 * i && mouseY < posY + 27 + 21 * i ) {
                    GL11.glDisable(GL11.GL_DEPTH_TEST);
                    GL11.glColorMask(true, true, true, false);
                    this.drawGradientRect(posX + 13, posY + 9 + 21 * i, posX + 121, posY + 27 + 21 * i, 0x2AFFFFFF, 0x2AFFFFFF);
                    GL11.glColorMask(true, true, true, true);
                    GL11.glEnable(GL11.GL_DEPTH_TEST);

                    if( isLmbDown && !this.prevIsLmbDown ) {
                        PacketRegistry.sendToServer(new PacketInitAssemblyCrafting(this.assembly, this.cacheRecipes.get(i).getValue0()));
                    }
                }
            }
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if( this.assembly.getStackInSlot(0) == null && this.assembly.currentlyCraftingItem != null ) {
            this.drawItemStack(this.assembly.currentlyCraftingItem, posX + 162, posY + 10);

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glColorMask(true, true, true, false);
            this.mc.getTextureManager().bindTexture(Textures.GUI_ASSEMBLY_CRF.getResource());
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
            this.drawTexturedModalRect(posX + 162, posY + 10, 162, 10, 16, 16);
            GL11.glColorMask(true, true, true, true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        this.prevIsLmbDown = isLmbDown;
    }

    private void drawItemStack(ItemStack stack, int x, int y) {
        GL11.glTranslatef(0.0F, 0.0F, 32.0F);
        this.zLevel = 200.0F;
        itemRender.zLevel = 200.0F;
        itemRender.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stack, x, y);
        this.zLevel = 0.0F;
        itemRender.zLevel = 0.0F;
        GL11.glTranslatef(0.0F, 0.0F, -32.0F);
        GL11.glDisable(GL11.GL_LIGHTING);
    }
}
