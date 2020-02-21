package de.sanandrew.mods.turretmod.client.gui.element.tcu.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayerIcon
        extends Texture
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu.info_playericon");

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        JsonUtils.addDefaultJsonProperty(data, "size", new int[] { 16, 16 });
        JsonUtils.addDefaultJsonProperty(data, "uv", new int[] { 0, 0 });

        super.bakeData(gui, data, inst);

        JsonArray uvArr = data.getAsJsonArray("uvs");
        if( uvArr.size() > 0 ) {
            this.uv = JsonUtils.getIntArray(uvArr.get(MiscUtils.RNG.randomInt(3) == 0 ? MiscUtils.RNG.randomInt(uvArr.size()) : 0), Range.is(2));
        } else {
            throw new JsonSyntaxException("Expected uvs array needs to contain at least one valid element");
        }
    }
}
