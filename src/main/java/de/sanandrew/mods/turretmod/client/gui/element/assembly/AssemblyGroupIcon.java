package de.sanandrew.mods.turretmod.client.gui.element.assembly;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssembly;
import de.sanandrew.mods.turretmod.client.gui.element.Item;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class AssemblyGroupIcon
        extends Item
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "assembly.group_icon");

    @Override
    protected ItemStack getBakedStack(IGui gui, JsonObject data) {
        return ItemStack.EMPTY;
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        this.data.stack = AssemblyManager.INSTANCE.getGroupIcon(GuiTurretAssembly.currGroup);
    }
}
