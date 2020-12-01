/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.element.assembly;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssembly;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public class AssemblyProgressBar
        extends Texture
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "assembly.progress");

    private GuiElementInst label;

    private int progressWidth;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        JsonObject lblElem = data.getAsJsonObject("label");
        if( lblElem != null ) {
            this.label = new GuiElementInst(JsonUtils.getIntArray(lblElem.get("offset"), new int[] { 0, 0 }, Range.is(2)),
                                            new AssemblyProgressLabel(), lblElem).initialize(gui);
            this.label.alignment = new String[] { JsonUtils.getStringVal(lblElem.get("alignment"), "center") };
            this.label.get().bakeData(gui, this.label.data, this.label);
        }

        JsonUtils.addDefaultJsonProperty(data, "size", new int[] { 50, 5 });
        JsonUtils.addDefaultJsonProperty(data, "uv", new int[] { 0, 222 });

        super.bakeData(gui, data, inst);
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        GuiTurretAssembly gta = (GuiTurretAssembly) gui;
        double perc = gta.assembly.getTicksCrafted() / (double) gta.assembly.getMaxTicksCrafted();
        this.progressWidth = Math.max(0, Math.min(this.size[0], (int) Math.round(perc * this.size[0])));
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        super.render(gui, partTicks, x, y, mouseX, mouseY, data);

        if( this.label != null ) {
            int lblX = x + this.label.pos[0];
            switch( this.label.getAlignmentH() ) {
                case CENTER: lblX += this.size[0] / 2; break;
                case RIGHT:  lblX += this.size[0];     break;
            }
            GuiDefinition.renderElement(gui, lblX, y + this.label.pos[1], mouseX, mouseY, partTicks, this.label);
        }
    }

    @Override
    protected void drawRect(IGui gui) {
        Gui.drawModalRectWithCustomSizedTexture(0, 0, this.uv[0], this.uv[1], this.progressWidth, this.size[1], this.textureSize[0], this.textureSize[1]);
    }

    @Override
    public int getWidth() {
        return this.progressWidth;
    }

    public static class AssemblyProgressLabel
            extends Text
    {
        @Override
        public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
            if( !data.has("color") ) {
                JsonObject colorObj = new JsonObject();
                colorObj.addProperty("default", "0xFF9999FF");
                colorObj.addProperty("stroke", "0xFF000000");
                data.add("color", colorObj);
            }

            JsonUtils.addJsonProperty(data, "shadow", false);
            JsonUtils.addJsonProperty(data, "wrapWidth", 0);

            super.bakeData(gui, data, inst);
        }

        @Override
        public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
            this.setColor("stroke");
            super.render(gui, partTicks, x + 1, y, mouseX, mouseY, data);
            super.render(gui, partTicks, x - 1, y, mouseX, mouseY, data);
            super.render(gui, partTicks, x, y + 1, mouseX, mouseY, data);
            super.render(gui, partTicks, x, y - 1, mouseX, mouseY, data);
            this.setColor(null);
            super.render(gui, partTicks, x, y, mouseX, mouseY, data);
        }

        @Override
        public String getBakedText(IGui gui, JsonObject data) {
            return "";
        }

        @Override
        public String getDynamicText(IGui gui, String originalText) {
            int cnt = ((GuiTurretAssembly) gui).getCraftingCount();
            return cnt > 0 ? (cnt == Integer.MAX_VALUE ? "\u221E" : String.format(Locale.ROOT, "%dx", cnt)) : "";
        }
    }
}
