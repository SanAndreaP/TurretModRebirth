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
    public void bakeData(IGui gui, JsonObject data) {
        if( this.type == null ) {
            this.type = TargetType.fromString(JsonUtils.getStringVal(data.get("targetType")));
        }

        super.bakeData(gui, data);
    }

    @Override
    public GuiElementInst[] getElements(IGui gui, JsonObject data) {
        return getElements(gui, data, "");
    }

    private GuiElementInst[] getElements(IGui gui, JsonObject data, String filter) {
        ITurretInst turretInst = ((IGuiTcuInst<?>) gui).getTurretInst();
        List<GuiElementInst> elements = new ArrayList<>();
        JsonObject nodeData = MiscUtils.defIfNull(data.getAsJsonObject("node"), JsonObject::new);

        this.type.buildElements(gui, turretInst, nodeData, this.data.areaSize[0], filter, elements);

        return elements.toArray(new GuiElementInst[0]);
    }

    @SuppressWarnings("UnstableApiUsage")
    public void rebuild(IGui gui, JsonObject data, String filter) {
        this.scroll = 0.0F;

        GuiElementInst[] elements = this.getElements(gui, data, filter);
        this.data.elements.clear();
        Arrays.stream(elements).forEach(e -> {
            TargetNode<?> node = e.get(TargetNode.class);
            node.bakeData(gui, e.data);
            this.data.elements.put(Range.closedOpen(e.pos[1], e.pos[1] + node.getHeight()), e);
        });
    }
}
