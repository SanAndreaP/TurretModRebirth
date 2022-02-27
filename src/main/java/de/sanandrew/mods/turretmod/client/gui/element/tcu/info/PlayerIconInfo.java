package de.sanandrew.mods.turretmod.client.gui.element.tcu.info;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;
import org.apache.logging.log4j.util.Strings;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PlayerIconInfo
        extends Texture
{
    protected int[][] uvs;
    protected Map<String, Integer> playerUvIds;

    @Nonnull
    private Supplier<String> playerNameSupplier = () -> null;

    public PlayerIconInfo(ResourceLocation txLocation, int[] size, int[] textureSize, int[][] uvs, float[] scale, ColorObj color, Map<String, Integer> playerUvIds) {
        super(txLocation, size, textureSize, uvs[0], scale, color);

        this.uvs = uvs;
        this.playerUvIds = playerUvIds;
    }

    public void setPlayerNameSupplier(@Nonnull Supplier<String> playerNameSupplier) {
        this.playerNameSupplier = playerNameSupplier;
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        super.setup(gui, inst);

        String playerName = this.playerNameSupplier.get();
        if( !Strings.isBlank(playerName) && this.playerUvIds.containsKey(playerName) ) {
            this.uv = this.uvs[this.playerUvIds.get(playerName)];
        } else if( this.uvs.length > 1 && MiscUtils.RNG.randomInt(3) == 0 ) {
            this.uv = this.uvs[1 + MiscUtils.RNG.randomInt(this.uvs.length - 1)];
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public static class Builder
            extends Texture.Builder
    {
        protected int[][]          uvs;
        protected Map<String, Integer> playerUvIds = new HashMap<>();

        public Builder(int[] size) {
            super(size);
        }

        @Override
        public Builder uv(int[] uv) {
            return this;
        }

        public Builder uvs(int[][] uvs) {
            this.uvs = uvs;

            return this;
        }

        public Builder playerUvId(String playerName, int id) {
            this.playerUvIds.put(playerName, id);

            return this;
        }

        @Override
        public PlayerIconInfo get(IGui gui) {
            this.sanitize(gui);
            return new PlayerIconInfo(this.texture, this.size, this.textureSize, this.uvs, this.scale, this.color, this.playerUvIds);
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            JsonUtils.addJsonProperty(data, "uv", new int[2]);

            Texture.Builder tb = Texture.Builder.buildFromJson(gui, data);
            Builder b = IBuilder.copyValues(tb, new Builder(tb.size));

            List<int[]> uvs = new ArrayList<>();
            for( JsonElement uv : data.getAsJsonArray("uvs") ) {
                uvs.add(JsonUtils.getIntArray(uv, Range.is(2)));
            }
            b.uvs(uvs.toArray(new int[0][]));

            if( data.has("playerUvIds") ) {
                for( Map.Entry<String, JsonElement> o : data.getAsJsonObject("playerUvIds").entrySet()) {
                    b.playerUvId(o.getKey(), JsonUtils.getIntVal(o.getValue()));
                }
            }

            return b;
        }

        public static PlayerIconInfo fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
