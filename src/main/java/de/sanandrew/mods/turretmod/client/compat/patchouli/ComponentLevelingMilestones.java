package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.google.gson.annotations.SerializedName;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.level.LevelModifiers;
import de.sanandrew.mods.turretmod.registry.upgrades.leveling.LevelStorage;
import de.sanandrew.mods.turretmod.registry.upgrades.leveling.Stage;
import org.apache.commons.lang3.mutable.MutableInt;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.book.gui.GuiBook;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class ComponentLevelingMilestones
        extends ComponentEntryList<ComponentCustomText>
{
    @VariableHolder
    @SerializedName("level_text")
    public String levelTxt;
    @SerializedName("level_text_color")
    public String levelTextClr = "0x000000";
    @VariableHolder
    @SerializedName("modifier_label")
    public String modValLbl;
    @SerializedName("modifier_label_color")
    public String modLblClr = "0x404040";
    @VariableHolder
    @SerializedName("modifier_value")
    public String modValTxt;
    @SerializedName("modifier_value_color")
    public String modValClr = "0x000000";

    private final Map<ComponentCustomText, Integer> entryIds = new HashMap<>();

    @Override
    public void buildEntries(IComponentRenderContext context, GuiBook book, List<ComponentCustomText> entries, int x, int y) {
        Stage[]                                       stages     = LevelStorage.getStages();
        Map<Integer, Map<String, Stage.ModifierInfo>> milestones = new HashMap<>();

        for( Stage s : stages ) {
            Map<String, Stage.ModifierInfo> modStr = Stage.fetchModifiers(Collections.singleton(s), null).entrySet().stream()
                                                          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            milestones.computeIfAbsent(s.level, HashMap::new).putAll(modStr);
        }

        this.entryIds.clear();

        int pgNum = book.getPage();
        MutableInt line = new MutableInt(0);
        milestones.entrySet().stream().sorted(Comparator.comparingInt(Map.Entry::getKey)).forEach(e -> {
            ComponentCustomText txt = new ComponentCustomText(String.format(this.levelTxt, e.getKey()), this.levelTextClr);
            txt.build(x, y + line.getValue() * 11 + 11, pgNum);
            entries.add(txt);
            this.entryIds.put(txt, line.getAndIncrement());

            e.getValue().entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(m -> {
                ComponentModifier md = new ComponentModifier(this.getModLbl(m.getKey()), this.getModVal(m.getValue()),
                                                             this.modLblClr, this.modValClr);
                md.build(x, y + line.getValue() * 11 + 11, pgNum);
                entries.add(md);
                this.entryIds.put(md, line.getAndIncrement());
            });
        });
    }

    private String getModLbl(String attrName) {
        return String.format(this.modValLbl, LangUtils.translate("attribute." + attrName));
    }

    private String getModVal(Stage.ModifierInfo m) {
        return String.format(this.modValTxt, LevelModifiers.FORMATTER.format(m.getModValue() - m.baseValue));
    }

    @Override
    public void onDisplayed(IComponentRenderContext context) {
        super.onDisplayed(context);

        for( ComponentCustomText txt : this.entryIds.keySet() ) {
            txt.onDisplayed(context);
        }
    }

    @Override
    void setEntryScroll(ComponentCustomText entry, int prevShownPos, int currShownPos) {
        int entryId = this.entryIds.get(entry);

        entry.y += (prevShownPos - currShownPos) * 11;
        entry.visible = entryId >= currShownPos && entryId < currShownPos + this.maxEntriesShown;
    }

    @Override
    public void render(IComponentRenderContext context, float partTicks, int mouseX, int mouseY) {
        super.render(context, partTicks, mouseX, mouseY);

        this.entryIds.keySet().forEach(e -> e.render(context, partTicks, mouseX, mouseY));
    }

    private static final class ComponentModifier
            extends ComponentCustomText
    {
        private final ComponentCustomText nameComponent;
        private final ComponentCustomText valueComponent;

        private ComponentModifier(String name, String value, String nameColor, String valueColor) {
            this.nameComponent = new ComponentCustomText(name, nameColor);
            this.valueComponent = new ComponentCustomText(value, valueColor);

            this.valueComponent.alignment = "right";
        }

        @Override
        public void build(int x, int y, int pgNum) {
            this.x = x;
            this.y = y;

            this.nameComponent.build(0, 0, pgNum);
            this.valueComponent.build(0, 0, pgNum);
        }

        @Override
        public void render(IComponentRenderContext context, float partTicks, int mouseX, int mouseY) {
            if( !this.visible ) {
                return;
            }

            this.nameComponent.x = this.x;
            this.nameComponent.y = this.y;
            this.valueComponent.x = this.x + 116;
            this.valueComponent.y = this.y;

            this.nameComponent.render(context, partTicks, mouseX, mouseY);
            this.valueComponent.render(context, partTicks, mouseX, mouseY);
        }

        @Override
        public void onDisplayed(IComponentRenderContext context) {
            this.nameComponent.onDisplayed(context);
            this.valueComponent.onDisplayed(context);
        }
    }
}
