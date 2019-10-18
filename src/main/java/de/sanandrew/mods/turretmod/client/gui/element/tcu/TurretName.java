package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import net.minecraft.util.ResourceLocation;

public class TurretName
        extends Text
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "turretName");

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        super.bakeData(gui, data);
    }

    @Override
    public String getBakedText(IGui gui, JsonObject data) {
        return "";
    }

    @Override
    public String getDynamicText(IGui gui, String originalText) {
        return super.getDynamicText(gui, originalText);
    }
}
