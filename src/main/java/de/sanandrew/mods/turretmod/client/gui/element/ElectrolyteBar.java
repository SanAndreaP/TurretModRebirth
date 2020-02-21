package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

@SuppressWarnings({"Duplicates"})
public class ElectrolyteBar
        extends Texture
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "electrolyte_bar");

    private int energyBarWidth;
    private int slot;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        if( !(gui instanceof IGuiElectrolyte) ) {
            throw new RuntimeException("Cannot use electrolyte_bar on a GUI which doesn't implement IGuiElectrolyte");
        }

        this.slot = JsonUtils.getIntVal(data.get("slot"));

        JsonUtils.addDefaultJsonProperty(data, "size", new int[] {16, 3});
        JsonUtils.addDefaultJsonProperty(data, "uv", new int[] {176, 59});

        super.bakeData(gui, data, inst);
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        IGuiElectrolyte gel = (IGuiElectrolyte) gui;
        double energyPerc = gel.getProcess(this.slot) / (double) gel.getMaxProcess(this.slot);
        this.energyBarWidth = Math.max(0, Math.min(this.size[0], MathHelper.ceil((1.0F - energyPerc) * this.size[0])));
    }

    @Override
    protected void drawRect(IGui gui) {
        Gui.drawModalRectWithCustomSizedTexture(0, 0, this.uv[0], this.uv[1], this.energyBarWidth, this.size[1], this.textureSize[0], this.textureSize[1]);
    }

    @Override
    public int getWidth() {
        return this.energyBarWidth;
    }

    @Override
    public int getHeight() {
        return this.size[1];
    }

    public interface IGuiElectrolyte
    {
        int getProcess(int slot);

        int getMaxProcess(int slot);
    }

}
