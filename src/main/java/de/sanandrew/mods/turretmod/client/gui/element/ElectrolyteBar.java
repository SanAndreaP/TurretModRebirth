package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings({"Duplicates"})
public class ElectrolyteBar
        extends Texture
{
    public static final ResourceLocation ID = new ResourceLocation("electrolyte_bar");

    private int energyBarWidth;
    private int slot;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( !(gui instanceof IGuiElectrolyte) ) {
            throw new RuntimeException("Cannot use electrolyte_bar on a GUI which doesn't implement IGuiElectrolyte");
        }
        boolean init = this.data == null;
        if( init ) {
            this.slot = JsonUtils.getIntVal(data.get("slot"));
        }

        if( !data.has("location") ) data.addProperty("location", "sapturretmod:textures/gui/potatogen.png");
        if( !data.has("size") ) TmrUtils.addJsonProperty(data, "size", new int[] {16, 3});
        if( !data.has("uv") ) TmrUtils.addJsonProperty(data, "uv", new int[] {176, 59});

        super.bakeData(gui, data);
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        IGuiElectrolyte gel = (IGuiElectrolyte) gui;
        double energyPerc = gel.getProcess(this.slot) / (double) gel.getMaxProcess(this.slot);
        this.energyBarWidth = Math.max(0, Math.min(this.data.size[0], MathHelper.ceil((1.0F - energyPerc) * this.data.size[0])));
    }

    @Override
    protected void drawRect(IGui gui) {
        Gui.drawModalRectWithCustomSizedTexture(0, 0, this.data.uv[0], this.data.uv[1], this.energyBarWidth, this.data.size[1], this.data.textureSize[0], this.data.textureSize[1]);
    }

    @Override
    public int getWidth() {
        return this.energyBarWidth;
    }

    @Override
    public int getHeight() {
        return this.data.size[1];
    }

    public interface IGuiElectrolyte
    {
        int getProcess(int slot);

        int getMaxProcess(int slot);
    }

}
