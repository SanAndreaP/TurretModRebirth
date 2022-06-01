package de.sanandrew.mods.turretmod.client.gui.element.tcu.levels;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Rectangle;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollButton;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.element.StackedScrollArea;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuLevelsPage;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuTargetPage;
import de.sanandrew.mods.turretmod.item.upgrades.Upgrades;
import de.sanandrew.mods.turretmod.item.upgrades.leveling.LevelStorage;
import de.sanandrew.mods.turretmod.item.upgrades.leveling.Stage;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Map;

public class ModifierList
        extends StackedScrollArea
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_modifier_list");

    protected JsonObject nodeData;

    protected Integer prevModListHash = null;

    public ModifierList(int[] areaSize, int scrollHeight, float maxScrollDelta, int[] scrollbarPos, ScrollButton scrollButton, JsonObject nodeData, IGui gui) {
        super(areaSize, scrollHeight, true, maxScrollDelta, scrollbarPos, scrollButton, gui);

        this.nodeData = nodeData;
    }

    @Override
    public void tick(IGui gui, GuiElementInst inst) {
        super.tick(gui, inst);

        LevelStorage storage  = ((TcuLevelsPage) gui).getTurret().getUpgradeProcessor().getUpgradeData(Upgrades.LEVELING.getId());
        int currModListHash = storage.getModHash();

        if( this.prevModListHash == null || this.prevModListHash != currModListHash ) {
            this.prevModListHash = currModListHash;
            Map<Attribute, Stage.ModifierInfo> attribs = storage.fetchCurrentModifiers();
            this.clear();
            attribs.forEach((key, val) -> {
                this.add(new GuiElementInst(Modifier.Builder.fromJson(gui, this.nodeData, key, val)));
            });
        }
    }

    public static class Builder
            extends StackedScrollArea.Builder
    {
        protected JsonObject nodeData;

        public Builder(int[] areaSize, JsonObject nodeData) {
            super(areaSize);

            this.nodeData = nodeData;
        }

        @Override
        public ModifierList get(IGui gui) {
            this.sanitize(gui);
            ModifierList sa = new ModifierList(this.areaSize, this.scrollHeight, this.maxScrollDelta, this.scrollbarPos, this.scrollButton, this.nodeData, gui);
            if( this.elements != null && this.elements.length > 0 ) {
                Arrays.stream(this.elements).forEach(e -> sa.add(e.initialize(gui), true));
                sa.update();
            }

            return sa;
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            ScrollArea.Builder sab = ScrollArea.Builder.buildFromJson(gui, data, b -> (g, e) -> new GuiElementInst[0]);
            return IBuilder.copyValues(sab, new Builder(sab.areaSize, data.getAsJsonObject("node")));
        }

        public static ModifierList fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }
}
