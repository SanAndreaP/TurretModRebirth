package de.sanandrew.mods.turretmod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ContainerName;
import de.sanandrew.mods.turretmod.api.ResourceLocations;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.init.ClientProxy;
import de.sanandrew.mods.turretmod.inventory.container.AmmoCartridgeContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.IOException;

public class AmmoCartridgeScreen
        extends ContainerScreen<AmmoCartridgeContainer>
        implements IGui, ContainerName.IContainerName
{
    private GuiDefinition guiDef;

    private float currPartTicks;

    public AmmoCartridgeScreen(AmmoCartridgeContainer cartridge, PlayerInventory playerInv, ITextComponent title) {
        super(cartridge, playerInv, title);

        try {
            this.guiDef = GuiDefinition.getNewDefinition(ResourceLocations.GUI_CARTRIDGE);
            this.width = this.guiDef.width;
            this.height = this.guiDef.height;
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
        }
    }

    @Override
    public void init() {
        super.init();

        ClientProxy.initGuiDef(this.guiDef, this);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public void tick() {
        super.tick();

        this.guiDef.update(this);
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int x, int y) {
        this.currPartTicks = partialTicks;
        ClientProxy.drawGDBackground(this.guiDef, matrixStack, this, partialTicks, x, y);
    }

    @Override
    protected void renderLabels(@Nonnull MatrixStack matrixStack, int x, int y) {
        this.guiDef.drawForeground(this, matrixStack, x, y, this.currPartTicks);
    }

    @Override
    public Screen get() {
        return this;
    }

    @Override
    public GuiDefinition getDefinition() {
        return this.guiDef;
    }

    @Override
    public int getScreenPosX() {
        return this.leftPos;
    }

    @Override
    public int getScreenPosY() {
        return this.topPos;
    }

    @Override
    public String getContainerName() {
        return this.getTitle().getString();
    }
}
