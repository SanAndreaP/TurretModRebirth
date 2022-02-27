package de.sanandrew.mods.turretmod.client.gui.element.tcu.targets;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.turretmod.world.PlayerList;
import net.minecraft.client.gui.AbstractGui;

import java.util.UUID;

public class PlayerIconTarget
        implements IGuiElement
{
    private final UUID playerId;

    public PlayerIconTarget(UUID playerId) {
        this.playerId = playerId;
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        IGuiElement.super.setup(gui, inst);
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mosueY, GuiElementInst inst) {
        gui.get().getMinecraft().getTextureManager().bind(PlayerList.getSkinLocation(this.playerId));
        int i3 = 8;
        int j3 = 8;
        AbstractGui.blit(stack, x, y, 8, 8, 8.0F, i3, 8, j3, 64, 64);

        int k3 = 8;
        int l3 = 8;
        AbstractGui.blit(stack, x, y, 8, 8, 40.0F, k3, 8, l3, 64, 64);
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }
}
