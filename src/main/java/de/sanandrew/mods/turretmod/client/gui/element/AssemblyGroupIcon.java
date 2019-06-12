package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssembly;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class AssemblyGroupIcon
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "assembly.group_icon");

    private ItemStack currStack = ItemStack.EMPTY;

    @Override
    public void bakeData(IGui gui, JsonObject data) { }

    @Override
    public void update(IGui gui, JsonObject data) {
        this.currStack = AssemblyManager.INSTANCE.getGroupIcon(GuiTurretAssembly.currGroup);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        RenderUtils.renderStackInGui(this.currStack, x, y, 1.0D);
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }
}
