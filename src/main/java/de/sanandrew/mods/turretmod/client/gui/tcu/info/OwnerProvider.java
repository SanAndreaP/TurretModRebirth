package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuScreen;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.info.PlayerIconInfo;
import de.sanandrew.mods.turretmod.init.Lang;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.Range;

import javax.annotation.Nonnull;
import java.util.function.BiFunction;

public class OwnerProvider
        extends TextProvider
{
    @Nonnull
    @Override
    public String getName() {
        return "owner";
    }

    @Override
    protected int[] getDefaultIconUV() {
        return new int[] { 120, 0 };
    }

    @Override
    protected String getDefaultTooltipText() {
        return Lang.TCU_TEXT.get("info.player.tooltip");
    }

    @Override
    public void setup(IGui gui, ITurretEntity turret, int w, int h) {
        this.icon.get(PlayerIconInfo.class).setPlayerNameSupplier(() -> turret.getOwnerName().getString());

        super.setup(gui, turret, w, h);
    }

    @Nonnull
    @Override
    public BiFunction<IGui, ITextComponent, ITextComponent> getTextFunction(IGui gui, ITurretEntity turret) {
        return (g, o) -> turret.getOwnerName();
    }

    @Override
    protected GuiElementInst loadIcon(IGui gui, JsonObject data) {
        JsonUtils.addDefaultJsonProperty(data, "size", this.getDefaultIconSize());
        if( !data.has("uvs") ) {
            JsonArray uvs = new JsonArray();
            uvs.add(uv(120, 0));
            uvs.add(uv(120, 16));
            uvs.add(uv(120, 32));
            uvs.add(uv(136, 0));
            uvs.add(uv(136, 16));
            uvs.add(uv(136, 32));
            uvs.add(uv(152, 0));
            data.add("uvs", uvs);
        }
        if( !data.has("playerUvIds") ) {
            JsonObject puv = new JsonObject();
            JsonUtils.addJsonProperty(puv, "SanAndreasP", 6);
            data.add("playerUvIds", puv);
        }

        PlayerIconInfo iconElem = PlayerIconInfo.Builder.fromJson(gui, data);

        return new GuiElementInst(JsonUtils.getIntArray(data.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] { 0, 0}, Range.is(2)), iconElem).initialize(gui);
    }

    private static JsonArray uv(int u, int v) {
        JsonArray jarr = new JsonArray();
        jarr.add(u);
        jarr.add(v);

        return jarr;
    }
}
