/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.JsonGuiContainer;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.inventory.container.TurretCrateContainer;
import de.sanandrew.mods.turretmod.tileentity.TurretCrateEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import org.apache.logging.log4j.Level;

import javax.annotation.Nonnull;
import java.io.IOException;

public class TurretCrateScreen
        extends JsonGuiContainer<TurretCrateContainer>
{
    private final TurretCrateEntity crate;

    private TurretCrateContainer.SlotAmmo ammoSlot;

    public TurretCrateScreen(TurretCrateContainer crateContainer, PlayerInventory playerInventory, ITextComponent title) {
        super(crateContainer, playerInventory, title);

        this.crate = crateContainer.getEntity();
    }

    @Override
    protected GuiDefinition buildGuiDefinition() {
        try {
            return GuiDefinition.getNewDefinition(Resources.GUI_TCRATE);
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, e);
            return null;
        }
    }

    @Override
    protected void initGd() {
        super.initGd();

        this.ammoSlot = this.menu.getAmmoSlot();
    }

    @Override
    public void render(@Nonnull MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.ammoSlot.isRendering = true;
        super.render(mStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderLabels(@Nonnull MatrixStack matrixStack, int x, int y) {
        this.ammoSlot.isRendering = false;

        int ammoCount = this.crate.getAmmoCount();
        if( this.minecraft != null && ammoCount > 1 ) {
            IFormattableTextComponent amt = new StringTextComponent(String.format("%d", ammoCount));
            if( ammoCount > 99 ) {
                amt.setStyle(Style.EMPTY.withFont(Minecraft.UNIFORM_FONT));
            }

            this.minecraft.font.drawShadow(matrixStack, amt, this.ammoSlot.x + 17 - this.minecraft.font.width(amt), this.ammoSlot.y + 9, 0xFFFFFFFF);
        }

        super.renderLabels(matrixStack, x, y);
    }
}
