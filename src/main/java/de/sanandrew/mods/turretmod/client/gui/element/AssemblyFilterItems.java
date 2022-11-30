package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Item;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.gui.TurretAssemblyScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class AssemblyFilterItems
        extends ElementParent<Integer>
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "assembly_filter_items");

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        for( int i = 0; i < 18; i++ ) {
            final int ind = i;

            Item itm = new Item.Builder(ItemStack.EMPTY).get(gui);
            itm.setItemSupplier(() -> getStack(gui, ind));

            int x = 18 * (i % 9);
            int y = 18 * (i / 9);
            this.put(i, new GuiElementInst(new int[] {x, y}, itm).initialize(gui));

            Texture tx = new FilterSlotTexture.Builder(new int[] {16, 16}).uv(36 + x, 91 + y).texture(gui.getDefinition().getTexture(null)).color(0x80FFFFFF).get(gui);
            this.put(18 + i, new GuiElementInst(new int[] {x, y}, tx).initialize(gui));
        }

        super.setup(gui, inst);
    }

    @Override
    public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
        if( !(gui instanceof TurretAssemblyScreen) ) {
            return;
        }

        if( !((TurretAssemblyScreen) gui).getMenu().tile.hasFilterUpgrade() ) {
            return;
        }

        super.render(gui, stack, partTicks, x, y, mouseX, mouseY, inst);
    }

    private static ItemStack getStack(IGui gui, int id) {
        if( gui instanceof TurretAssemblyScreen ) {
            NonNullList<ItemStack> filterStacks = ((TurretAssemblyScreen) gui).getMenu().tile.getFilterStacks();
            if( 0 <= id && id < filterStacks.size() ) {
                return filterStacks.get(id);
            }
        }

        return ItemStack.EMPTY;
    }

    public static class Builder
            implements IBuilder<AssemblyFilterItems>
    {
        @Override
        public void sanitize(IGui gui) { /* no-op (yet) */ }

        @Override
        public AssemblyFilterItems get(IGui gui) {
            this.sanitize(gui);

            return new AssemblyFilterItems();
        }

        public static Builder buildFromJson(/*IGui gui, JsonObject data*/) {
            return new Builder();
        }

        @SuppressWarnings("unused")
        public static AssemblyFilterItems fromJson(IGui gui, JsonObject data) {
            return buildFromJson(/*gui, data*/).get(gui);
        }
    }

    public static class FilterSlotTexture
            extends Texture
    {
        public FilterSlotTexture(ResourceLocation txLocation, int[] size, int[] textureSize, int[] uv, float[] scale, ColorObj color) {
            super(txLocation, size, textureSize, uv, scale, color);
        }

        @Override
        public void render(IGui gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, GuiElementInst inst) {
            RenderSystem.disableDepthTest();
            super.render(gui, stack, partTicks, x, y, mouseX, mouseY, inst);
            RenderSystem.enableDepthTest();
        }

        public static class Builder
                extends Texture.Builder
        {
            public Builder(int[] size) {
                super(size);
            }

            @Override
            public FilterSlotTexture get(IGui gui) {
                this.sanitize(gui);
                return new FilterSlotTexture(this.texture, this.size, this.textureSize, this.uv, this.scale, this.color);
            }

            public static Builder buildFromJson(IGui gui, JsonObject data) {
                JsonUtils.addDefaultJsonProperty(data, "color", "0x80FFFFFF");

                Texture.Builder tb = Texture.Builder.buildFromJson(gui, data);

                return IBuilder.copyValues(tb, new Builder(tb.size));
            }

            public static FilterSlotTexture fromJson(IGui gui, JsonObject data) {
                return buildFromJson(gui, data).get(gui);
            }
        }
    }
}
