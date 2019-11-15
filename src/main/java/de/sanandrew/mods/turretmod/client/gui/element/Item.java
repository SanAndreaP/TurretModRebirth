package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.util.RenderUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class Item
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("item");

    public BakedData data;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            this.data = new BakedData();
            this.data.stack = this.getBakedStack(gui, data);
            this.data.scale = JsonUtils.getDoubleVal(data.get("scale"), 1.0D);
            this.data.size = (int) Math.round(16.0D * this.data.scale);
        }
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        RenderUtils.renderStackInGui(this.getDynamicStack(gui), x, y, this.data.scale);
    }

    @Override
    public int getWidth() {
        return this.data.size;
    }

    @Override
    public int getHeight() {
        return this.data.size;
    }

    protected ItemStack getBakedStack(IGui gui, JsonObject data) {
        return JsonUtils.getItemStack(data.get("item"));
    }

    protected ItemStack getDynamicStack(IGui gui) {
        return this.data.stack;
    }

    public static class BakedData
    {
        public ItemStack stack = ItemStack.EMPTY;
        public double    scale;
        public int       size;
    }
}
