package de.sanandrew.mods.turretmod.client.gui.element.tcu.target;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TargetList
        extends ScrollArea
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "target_list");

    private TargetType type;

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( this.type == null ) {
            this.type = TargetType.fromString(JsonUtils.getStringVal(data.get("targetType")));
        }

        super.bakeData(gui, data);
    }

    @Override
    public GuiElementInst[] getElements(IGui gui, JsonObject data) {
        ITurretInst turretInst = ((IGuiTcuInst<?>) gui).getTurretInst();
        List<GuiElementInst> elements = new ArrayList<>();
        JsonObject nodeData = MiscUtils.defIfNull(data.getAsJsonObject("node"), JsonObject::new);

        switch( this.type ) {
            case PLAYER:
                applyTargets(gui, nodeData, this.type, elements, turretInst.getTargetProcessor().getPlayerTargets());
            case CREATURE:
                applyTargets(gui, nodeData, this.type, elements, turretInst.getTargetProcessor().getEntityTargets());
        }

        return elements.toArray(new GuiElementInst[0]);
    }

    private static void applyTargets(IGui gui, JsonObject nodeData, TargetType type, List<GuiElementInst> elements, Map<?, Boolean> targets) {
        MutableInt posY = new MutableInt(0);
        targets.forEach((id, enabled) -> {
            GuiElementInst elem = new GuiElementInst();
            elem.element = new TargetNode<>(id, type);
            gui.getDefinition().initElement(elem);
            elem.data = nodeData;
            elements.add(elem);
            elem.pos[1] = posY.getAndAdd(elem.get().getHeight());
        });
    }
}
