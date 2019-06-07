/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.element;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssemblyNEW;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.util.Locale;

public class AssemblyProgressBar
        extends Texture
{
    public static final ResourceLocation ID = new ResourceLocation("assembly_progress");

    private GuiElementInst label;
    private int progressWidth;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            JsonElement lblElem = data.get("label");
            if( lblElem != null ) {
                this.label = JsonUtils.GSON.fromJson(lblElem, GuiElementInst.class);
                this.label.get().bakeData(gui, this.label.data);
            }

            if( !data.has("location") ) data.addProperty("location", "sapturretmod:textures/gui/turretassembly/assembly.png");
            if( !data.has("size") ) TmrUtils.addJsonProperty(data, "size", new int[] {50, 5});
            if( !data.has("uv") ) TmrUtils.addJsonProperty(data, "uv", new int[] {0, 222});
        }

        super.bakeData(gui, data);
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        GuiTurretAssemblyNEW gta = (GuiTurretAssemblyNEW) gui;
        double energyPerc = gta.getProgress() / (double) gta.getMaxProgress();
        this.progressWidth = Math.max(0, Math.min(this.data.size[0], (int) Math.round(energyPerc * this.data.size[0])));
    }

    @Override
    protected void drawRect(IGui gui) {
        Gui.drawModalRectWithCustomSizedTexture(0, 0, this.data.uv[0], this.data.uv[1], this.progressWidth, this.data.size[1], this.data.textureSize[0], this.data.textureSize[1]);
    }

    @Override
    public int getWidth() {
        return this.progressWidth;
    }

    @Override
    public int getHeight() {
        return this.data.size[1];
    }

    public static class AssemblyProgressLabel
            extends Text
    {
        @Override
        public String getBakedText(IGui gui, JsonObject data) {
            return "";
        }

        @Override
        public String getDynamicText(IGui gui, String originalText) {
            int cnt = ((GuiTurretAssemblyNEW) gui).getCraftingCount();
            return cnt > 0 ? (cnt == Integer.MAX_VALUE ? "-1" : String.format(Locale.ROOT, "%d", cnt)) : "";
        }
    }
}
