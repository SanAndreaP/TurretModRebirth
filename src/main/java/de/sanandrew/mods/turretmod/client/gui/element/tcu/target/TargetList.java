package de.sanandrew.mods.turretmod.client.gui.element.tcu.target;

import com.google.common.collect.Range;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.gui.tcu.TargetType;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TargetList
        extends ScrollArea
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "target_list");

    private TargetType<?> type;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.type = TargetType.fromString(JsonUtils.getStringVal(data.get("targetType")));

        super.bakeData(gui, data, inst);
    }

    @Override
    public boolean bakeElements() {
        return false;
    }

    @Override
    public GuiElementInst[] getElements(IGui gui, JsonObject data) {
        return getElements(gui, data, JsonUtils.getStringVal(data.get("filter"), ""));
    }

    private GuiElementInst[] getElements(IGui gui, JsonObject data, String filter) {
        ITurretInst turretInst = ((IGuiTcuInst<?>) gui).getTurretInst();
        List<GuiElementInst> elements = new ArrayList<>();

        this.type.buildElements(gui, turretInst, data.getAsJsonObject("node"), this.areaSize[0], filter, elements);

        return elements.toArray(new GuiElementInst[0]);
    }

    public void rebuild(IGui gui, JsonObject data, String filter) {
        this.scroll = 0.0F;

        data = JsonUtils.deepCopy(data);
        JsonUtils.addJsonProperty(data, "filter", filter);

        this.rebuildElements(gui, data);
    }
}
