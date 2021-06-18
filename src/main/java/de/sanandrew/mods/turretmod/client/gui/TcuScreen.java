package de.sanandrew.mods.turretmod.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class TcuScreen
        extends ContainerScreen<TcuContainer>
{
    public TcuScreen(TcuContainer tcuContainer, PlayerInventory playerInv, ITextComponent title) {
        super(tcuContainer, playerInv, title);
    }

    @Override
    protected void renderBg(@Nonnull MatrixStack stack, float partialTicks, int x, int y) {
        Minecraft.getInstance().font.draw(stack, "test", 0, 0, 0xFFFFFF);
    }
}
