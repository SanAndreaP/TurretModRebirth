package de.sanandrew.mods.turretmod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.init.ClientProxy;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.IOException;

public class TcuInfoScreen
        extends Screen
        implements IGui
{
    private final ITurretEntity turret;
    private final GuiDefinition guiDef;
    private int leftPos;
    private int topPos;

    public TcuInfoScreen(ITurretEntity turret, int guiWidth, int guiHeight) {
        super(StringTextComponent.EMPTY);

        this.turret = turret;
        this.guiDef = this.initGuiDef();
        if( this.guiDef != null ) {
            this.guiDef.width = guiWidth;
            this.guiDef.height = guiHeight;
        }
    }

    private GuiDefinition initGuiDef() {
        try {
            return GuiDefinition.getNewDefinition(Resources.GUI_TCU_INFO);
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
            return null;
        }
    }

    @Override
    protected void init() {
        super.init();

        ClientProxy.initGuiDef(this.guiDef, this);

        this.leftPos = (this.width - this.guiDef.width) / 2;
        this.topPos = (this.height - this.guiDef.height) / 2;

        this.tick();
    }

    @Override
    public void render(@Nonnull MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        ClientProxy.drawGDBackground(this.guiDef, mStack, this, partialTicks, mouseX, mouseY);
        mStack.pushPose();
        mStack.translate(this.getScreenPosX(), this.getScreenPosY(), 0);
        this.guiDef.drawForeground(this, mStack, mouseX, mouseY, partialTicks);
        mStack.popPose();
    }

    public ITurretEntity getTurret() {
        return this.turret;
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
}
