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
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.client.gui.assembly.GuiTurretAssembly;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import java.util.Locale;

@SuppressWarnings("WeakerAccess")
public class AssemblyProgressBar
        extends Texture
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "assembly.progress");

    private GuiElementInst label;
    private boolean centerLabel;
    private int progressWidth;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.data == null ) {
            JsonElement lblElem = data.get("label");
            if( lblElem != null ) {
                this.label = JsonUtils.GSON.fromJson(lblElem, GuiElementInst.class);
                gui.getDefinition().initElement(this.label);
                this.label.get().bakeData(gui, this.label.data);
                this.centerLabel = JsonUtils.getBoolVal(data.get("centerLabel"), true);
            }

            if( !data.has("size") ) TmrUtils.addJsonProperty(data, "size", new int[] {50, 5});
            if( !data.has("uv") ) TmrUtils.addJsonProperty(data, "uv", new int[] {0, 222});
        }

        super.bakeData(gui, data);
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        GuiTurretAssembly gta = (GuiTurretAssembly) gui;
        double perc = gta.assembly.getTicksCrafted() / (double) gta.assembly.getMaxTicksCrafted();
        this.progressWidth = Math.max(0, Math.min(this.data.size[0], (int) Math.round(perc * this.data.size[0])));
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        super.render(gui, partTicks, x, y, mouseX, mouseY, data);

        if( this.label != null ) {
            int lblX = x + this.label.pos[0] - (this.centerLabel ? (this.label.get().getWidth() / 2) : 0);
            this.label.get().render(gui, partTicks, lblX, y + this.label.pos[1], mouseX, mouseY, this.label.data);
        }
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
        public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "assembly.progress_text");

        public int mainColor;
        public int strokeColor;

        @Override
        public void bakeData(IGui gui, JsonObject data) {
            boolean init = this.data == null;

            if( !data.has("color") ) data.addProperty("color", "0xFF00F000");
            data.addProperty("shadow", false);
            data.addProperty("wrapWidth", 0);

            super.bakeData(gui, data);

            if( init ) {
                this.mainColor = this.data.color;
                this.strokeColor = MiscUtils.hexToInt(JsonUtils.getStringVal(data.get("strokeColor"), "0xFF000000"));
            }
        }

        @Override
        public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
            this.data.color = this.strokeColor;
            super.render(gui, partTicks, x + 1, y, mouseX, mouseY, data);
            super.render(gui, partTicks, x - 1, y, mouseX, mouseY, data);
            super.render(gui, partTicks, x, y + 1, mouseX, mouseY, data);
            super.render(gui, partTicks, x, y - 1, mouseX, mouseY, data);
            this.data.color = this.mainColor;
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
