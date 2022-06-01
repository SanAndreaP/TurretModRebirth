package de.sanandrew.mods.turretmod.client.gui.element.tcu.targets;

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
import de.sanandrew.mods.turretmod.init.config.Targets;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.util.Strings;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TargetList
        extends StackedScrollArea
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_target_list");

    private String filterStr;

    public TargetList(int[] areaSize, int scrollHeight, float maxScrollDelta, int[] scrollbarPos, ScrollButton scrollButton, IGui gui, GuiElementInst[] elements) {
        super(areaSize, scrollHeight, true, maxScrollDelta, scrollbarPos, scrollButton, gui);

        Arrays.stream(elements).forEach(e -> this.add(e.initialize(gui)));
    }

    @Override
    protected List<GuiElementInst> sortAndFilter(List<GuiElementInst> elements) {
        return elements.stream().sorted(this::compareTargets).filter(this::filterTarget).collect(Collectors.toList());
    }

    public void filter(IGui gui, String filterStr) {
        this.filterStr = filterStr;

        this.update(gui);
    }

    private int compareTargets(GuiElementInst e1, GuiElementInst e2) {
        Target t1 = e1.get(Target.class);
        Target t2 = e2.get(Target.class);
        int c = Integer.compare(getTargetTypeSort(t2), getTargetTypeSort(t1));

        if( c == 0 ) {
            c = t1.text.getString().compareToIgnoreCase(t2.text.getString());
        }

        return c;
    }

    private static int getTargetTypeSort(Target t) {
        if( !t.isCreature() ) {
            return 0;
        }

        EntityClassification c = Targets.getCondensedType(Targets.getTargetType(t.creatureId));
        if( c == EntityClassification.MONSTER ) {
            return 2;
        } else if( c == EntityClassification.CREATURE ) {
            return 1;
        }

        return 0;
    }

    private boolean filterTarget(GuiElementInst e) {
        if( Strings.isNotBlank(this.filterStr) ) {
            return e.get(Target.class).text.getString().toLowerCase(Locale.ROOT).contains(this.filterStr.toLowerCase(Locale.ROOT));
        }

        return true;
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
    }
}
