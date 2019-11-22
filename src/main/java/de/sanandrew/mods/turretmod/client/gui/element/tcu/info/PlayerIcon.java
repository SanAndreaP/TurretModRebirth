package de.sanandrew.mods.turretmod.client.gui.element.tcu.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
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
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_info_playericon");

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        boolean initialize = this.data == null;

        if( !data.has("size") ) {
            JsonArray arr = new JsonArray();
            arr.add(11); arr.add(8);
            data.add("size", arr);
        }
        if( !data.has("uv") ) {
            JsonArray arr = new JsonArray();
            arr.add(0); arr.add(0);
            data.add("uv", arr);
        }

        super.bakeData(gui, data);

        if( initialize ) {
            Iterator<JsonElement> uvArr = data.getAsJsonArray("uvs").iterator();
            List<int[]> uvList = new ArrayList<>();
            while( uvArr.hasNext() ) {
                int[] uv = JsonUtils.getIntArray(uvArr.next(), Range.is(2));
                uvList.add(uv);
            }
            if( uvList.size() > 0 ) {
                this.data.uv = uvList.get(MiscUtils.RNG.randomInt(3) == 0 ? MiscUtils.RNG.randomInt(uvList.size()) : 0);
            } else {
                throw new JsonSyntaxException("Expected uvs array needs to contain at least one valid element");
            }
        }
    }
}
