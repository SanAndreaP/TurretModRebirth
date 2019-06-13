package de.sanandrew.mods.turretmod.client.gui;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.util.GuiHelper;
import de.sanandrew.mods.turretmod.inventory.ContainerCartridge;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import org.apache.logging.log4j.Level;

import java.io.IOException;

public class GuiCartridge
        extends GuiContainer
        implements IGui
{
    private GuiDefinition guiDef;

    private float currPartTicks;

    public GuiCartridge(InventoryPlayer playerInv, IInventory cartridge, EntityPlayer player) {
        super(new ContainerCartridge(playerInv, cartridge, player));

        try {
            this.guiDef = GuiDefinition.getNewDefinition(Resources.GUI_STRUCT_CARTRIDGE.resource);

            this.xSize = this.guiDef.width;
            this.ySize = this.guiDef.height;
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        GuiHelper.initGuiDef(this.guiDef, this);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.drawDefaultBackground();
        this.currPartTicks = partialTicks;
        GuiHelper.drawGDBackground(this.guiDef, this, partialTicks, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        RenderHelper.disableStandardItemLighting();
        this.guiDef.drawForeground(this, mouseX, mouseY, this.currPartTicks);
        RenderHelper.enableGUIStandardItemLighting();
    }

    @Override
    public GuiScreen get() {
        return this;
    }

    @Override
    public GuiDefinition getDefinition() {
        return this.guiDef;
    }

    @Override
    public int getScreenPosX() {
        return this.guiLeft;
    }

    @Override
    public int getScreenPosY() {
        return this.guiTop;
    }
}
