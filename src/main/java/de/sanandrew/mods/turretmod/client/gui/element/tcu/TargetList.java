package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollButton;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.element.StackedScrollArea;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuTargetPage;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

// TODO: use StackPanel to prevent using set positions for elements
public class TargetList
        extends StackedScrollArea
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_target_list");

    public TargetList(int[] areaSize, int scrollHeight, float maxScrollDelta, int[] scrollbarPos, ScrollButton scrollButton, IGui gui, GuiElementInst[] elements) {
        super(areaSize, scrollHeight, true, maxScrollDelta, scrollbarPos, scrollButton, gui);

        Arrays.stream(elements).forEach(e -> this.add(e.initialize(gui)));
    }

    public static class Builder
            extends ScrollArea.Builder
    {
        protected final boolean listCreatures;
        protected int elementHeight = 12;

        public Builder(int[] areaSize, boolean listCreatures) {
            super(areaSize);
            this.listCreatures = listCreatures;
        }

        public Builder elementHeight(int height)            { this.elementHeight = height;        return this; }

        @Override
        public TargetList get(IGui gui) {
            return new TargetList(this.areaSize, this.scrollHeight, this.maxScrollDelta, this.scrollbarPos, this.scrollButton, gui, this.elements);
        }

        @Nonnull
        @Override
        protected GuiElementInst[] loadElements(IGui gui, JsonElement je) {
            final JsonObject             elemData  = je.getAsJsonObject();
            final int                    w         = this.areaSize[0];
            final int                    h         = this.elementHeight;
            final ITurretEntity          turret    = ((TcuTargetPage) gui).getTurret();

            GuiElementInst[] elem;
            if( this.listCreatures ) {
                List<ResourceLocation> creatures = new ArrayList<>(turret.getTargetProcessor().getEntityTargets().keySet());
                elem = IntStream.range(0, creatures.size())
                                .mapToObj(i -> getTargetEntry(gui, elemData, turret, creatures.get(i), null, w, h, h * i))
                                .toArray(GuiElementInst[]::new);
            } else {
                List<UUID> players = new ArrayList<>(turret.getTargetProcessor().getPlayerTargets().keySet());
                elem = IntStream.range(0, players.size())
                                .mapToObj(i -> getTargetEntry(gui, elemData, turret, null, players.get(i), w, h, h * i))
                                .toArray(GuiElementInst[]::new);
            }
//            GuiElementInst[] elem = IntStream.range(0, providers.size())
//                                             .mapToObj(i -> getInfoValue(gui, elemData, providers.get(i), turret, w, h, h * i))
//                                             .toArray(GuiElementInst[]::new);
            Arrays.stream(elem).forEach(e -> e.initialize(gui));

            return elem;
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            ScrollArea.Builder sab = ScrollArea.Builder.buildFromJson(gui, data, b -> (g, j) -> null);
            Builder    b   = IBuilder.copyValues(sab, new Builder(sab.areaSize, JsonUtils.getBoolVal(data.get("listCreatures"))));

            JsonUtils.fetchInt(data.get("elementHeight"), b::elementHeight);

            b.elements(b.loadElements(gui, MiscUtils.get(data.get("elementData"), JsonObject::new)));

            return b;
        }

        public static TargetList fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }

        private static GuiElementInst getTargetEntry(IGui gui, JsonObject data, ITurretEntity turret, ResourceLocation creatureId, UUID playerId, int w, int h, int y) {
            return new GuiElementInst(new int[] { 0, 0 }, Target.Builder.fromJson(gui, data, turret, creatureId, playerId, w, h));
        }

//        private static GuiElementInst getInfoValue(IGui gui, JsonObject data, ITcuInfoProvider provider, ITurretEntity turret, int w, int h, int y) {
//            TcuInfoValue.Builder b = TcuInfoValue.Builder.buildFromJson(gui, data, provider, turret, w, h);
//
//            return new GuiElementInst(new int[] { 0, y }, b.get(gui));
//        }
    }
}
