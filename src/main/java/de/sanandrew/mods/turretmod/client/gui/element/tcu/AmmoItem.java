package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTCU;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssembly;
import de.sanandrew.mods.turretmod.client.gui.element.Item;
import de.sanandrew.mods.turretmod.registry.assembly.AssemblyManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class AmmoItem
        extends Item
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tinfo_ammo");

    @Override
    protected ItemStack getBakedStack(IGui gui, JsonObject data) {
        return ItemStack.EMPTY;
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        this.data.stack = ((IGuiTcuInst) gui).getTurretInst().getTargetProcessor().getAmmoStack();
    }
}
