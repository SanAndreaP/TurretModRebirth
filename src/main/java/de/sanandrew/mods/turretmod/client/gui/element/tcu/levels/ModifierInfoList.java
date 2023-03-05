package de.sanandrew.mods.turretmod.client.gui.element.tcu.levels;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollButton;
import de.sanandrew.mods.sanlib.lib.client.gui.element.StackedScrollArea;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.init.Lang;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.leveling.LevelData;
import de.sanandrew.mods.turretmod.item.upgrades.delegate.leveling.Stage;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.sanandrew.mods.turretmod.client.gui.element.tcu.levels.Modifier.MOD_FORMAT;

public class ModifierInfoList
        extends StackedScrollArea
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu_modifier_info_list");

    protected JsonObject nodeData;

    public ModifierInfoList(int[] areaSize, int scrollHeight, float maxScrollDelta, int[] scrollbarPos, ScrollButton scrollButton, JsonObject nodeData, IGui gui) {
        super(areaSize, scrollHeight, false, maxScrollDelta, scrollbarPos, scrollButton, gui);

        this.nodeData = nodeData;
    }

    @Override
    public void setup(IGui gui, GuiElementInst inst) {
        super.setup(gui, inst);

        Map<ResourceLocation, Stage> availableStages = LevelData.getAvailableStages();
        List<Integer> levels = availableStages.values().stream().map(stage -> stage.level).distinct().sorted().collect(Collectors.toList());
        for( int i = 0, max = levels.size(); i < max; i++ ) {
            int lvl = levels.get(i);
            this.add(getText(3, i != 0 ? 5 : 3, gui, this.nodeData,
                             new TranslationTextComponent(Lang.TCU_TEXT.get("leveling.info.level"), lvl), null));

            Map<Attribute, Stage.ModifierInfo> mods = Stage.fetchModifiers(availableStages.values().stream().filter(stage -> stage.level == lvl)
                                                                                          .collect(Collectors.toList()));

            mods.forEach((attr, info) -> {
                Row row = new Row();
                row.add(getText(8, 0, gui, this.nodeData, new TranslationTextComponent(attr.getDescriptionId()), "modLabel"));
                GuiElementInst mod = getText(this.areaSize[0] - 3, 0, gui, this.nodeData,
                                             new TranslationTextComponent(Lang.TCU_TEXT.get("leveling.modValue"), MOD_FORMAT.format(info.getModPercentage())),
                                             "modValue");
                mod.alignment = new String[] { GuiElementInst.Justify.RIGHT.toString() };
                row.add(mod);
                this.add(new GuiElementInst(new int[] {0, 3}, row).initialize(gui));
            });
        }
    }

    private static GuiElementInst getText(int x, int y, IGui gui, JsonObject data, ITextComponent text, String color) {
        Text txt = Text.Builder.fromJson(gui, data);
        txt.setTextFunc((g, orig) -> text);
        txt.setColor(color);

        return new GuiElementInst(new int[] {x, y}, txt).initialize(gui);
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
        public ModifierInfoList get(IGui gui) {
            this.sanitize(gui);
            ModifierInfoList sa = new ModifierInfoList(this.areaSize, this.scrollHeight, this.maxScrollDelta, this.scrollbarPos, this.scrollButton, this.nodeData, gui);
            if( this.elements != null && this.elements.length > 0 ) {
                Arrays.stream(this.elements).forEach(e -> sa.add(e.initialize(gui), true));
                sa.update();
            }

            return sa;
        }

        public static Builder buildFromJson(IGui gui, JsonObject data) {
            ScrollArea.Builder sab = ScrollArea.Builder.buildFromJson(gui, data, null);
            return IBuilder.copyValues(sab, new Builder(sab.areaSize, data.getAsJsonObject("node")));
        }

        public static ModifierInfoList fromJson(IGui gui, JsonObject data) {
            return buildFromJson(gui, data).get(gui);
        }
    }

    private static final class Row
            extends ElementParent<Integer>
    {
        public void add(GuiElementInst elem) {
            this.put(elem.pos[0], elem);
        }
    }
}
