package de.sanandrew.mods.turretmod.client.gui.element.tinfo;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTurretProvider;
import net.minecraft.util.ResourceLocation;

public class InfoStrokeText
        extends Text
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tinfo_text");

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        if( this.colors.containsKey("stroke") ) {
            this.setColor("stroke");
            super.render(gui, partTicks, x + 1, y, mouseX, mouseY, data);
            super.render(gui, partTicks, x - 1, y, mouseX, mouseY, data);
            super.render(gui, partTicks, x, y + 1, mouseX, mouseY, data);
            super.render(gui, partTicks, x, y - 1, mouseX, mouseY, data);
            this.setColor(null);
        }

        super.render(gui, partTicks, x, y, mouseX, mouseY, data);
    }

    public static final class InfoTurretName
            extends InfoStrokeText
    {
        public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tinfo_turret_name");

        @Override
        public String getBakedText(IGui gui, JsonObject data) {
            return "";
        }

        @Override
        public String getDynamicText(IGui gui, String originalText) {
            return LangUtils.translate(LangUtils.ENTITY_NAME.get(((IGuiTurretProvider) gui).getTurretInst().getTurret().getId()));
        }
    }
}
