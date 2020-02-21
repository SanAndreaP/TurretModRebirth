package de.sanandrew.mods.turretmod.client.gui.element.assembly;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssembly;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class AssemblyGhostItems
        extends Texture
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "assembly.ghost_items");

    public final List<SlotData> slotsRendered = new ArrayList<>();

    public final ItemStack upgIconAuto   = new ItemStack(ItemRegistry.ASSEMBLY_UPG_AUTO);
    public final ItemStack upgIconSpeed  = new ItemStack(ItemRegistry.ASSEMBLY_UPG_SPEED);
    public final ItemStack upgIconFilter = new ItemStack(ItemRegistry.ASSEMBLY_UPG_FILTER);
    public final ItemStack cartridge     = new ItemStack(ItemRegistry.AMMO_CARTRIDGE);

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        JsonUtils.addJsonProperty(data, "size", new int[] { 16, 16 });
        JsonUtils.addJsonProperty(data, "uv", new int[] { 0, 0 });
        JsonUtils.addDefaultJsonProperty(data, "color", "0xA0FFFFFF");

        super.bakeData(gui, data, inst);
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        super.update(gui, data);

        this.slotsRendered.clear();

        GuiTurretAssembly gta = (GuiTurretAssembly) gui;
        if( !gta.assembly.hasAutoUpgrade() ) {
            this.slotsRendered.add(new SlotData(upgIconAuto, 14, 100));
        }
        if( !gta.assembly.hasSpeedUpgrade() ) {
            this.slotsRendered.add(new SlotData(upgIconSpeed, 14, 118));
        }
        if( !ItemStackUtils.isValid(gta.assembly.getInventory().getStackInSlot(4)) ) {
            this.slotsRendered.add(new SlotData(cartridge, 181, 10));
        }
        if( !gta.assembly.hasFilterUpgrade() ) {
            this.slotsRendered.add(new SlotData(upgIconFilter, 202, 100));
        } else {
            NonNullList<ItemStack> filteredStacks = gta.assembly.getFilterStacks();
            for( int i = 0; i < filteredStacks.size(); i++ ) {
                ItemStack filterStack = filteredStacks.get(i);
                // if the stack in the filter is valid and the slot has no valid item in it...
                if( ItemStackUtils.isValid(filterStack) && !ItemStackUtils.isValid(gta.assembly.getInventory().getStackInSlot(i + 5)) ) {
                    int x = i % 9;
                    int y = i / 9;

                    this.slotsRendered.add(new SlotData(filterStack.copy(), 36 + x * 18, 100 + y * 18));
                }
            }
        }
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        for( SlotData item : this.slotsRendered ) {
            RenderUtils.renderStackInGui(item.stack, item.x, item.y, 1.0D);
            this.uv = new int[] { item.x, item.y };
            GlStateManager.disableDepth();
            super.render(gui, partTicks, item.x, item.y, mouseX, mouseY, data);
            GlStateManager.enableDepth();
        }
    }

    public static class SlotData
    {
        public final ItemStack stack;
        public final int       x;
        public final int       y;

        public SlotData(ItemStack stack, int x, int y) {
            this.stack = stack;
            this.x = x;
            this.y = y;
        }
    }
}
