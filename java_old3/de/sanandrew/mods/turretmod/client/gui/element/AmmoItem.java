package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Item;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTurretProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class AmmoItem
        extends Item
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "info_ammo");

    @Override
    protected ItemStack getBakedStack(IGui gui, JsonObject data) {
        return ItemStack.EMPTY;
    }

    @Override
    protected ItemStack getDynamicStack(IGui gui) {
        return ((IGuiTurretProvider) gui).getTurretInst().getTargetProcessor().getAmmoStack();
    }
}
