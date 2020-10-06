package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.google.gson.annotations.SerializedName;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.leveling.LevelStorage;
import de.sanandrew.mods.turretmod.registry.upgrades.leveling.Stage;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonEntry;
import vazkii.patchouli.client.book.template.component.ComponentText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComponentLevelingMilestones
        extends ComponentEntryList<ComponentText>
{
    private List<ComponentText> myEntryList;

    @Override
    public void buildEntries(IComponentRenderContext context, GuiBook book, List<ComponentText> entries, int x, int y) {
//        final List<String> bookEntries = this.type.grabEntries(book, this.targetId);
        Stage[] stages = LevelStorage.getStages();
        Map<Integer, List<String>> milestones = new HashMap<>();


        for( int i = 0, max = stages.length; i < max; i++ ) {
            Stage s = stages[i];
            List<String> modStr = Stage.fetchModifiers(Collections.singleton(s), null).entrySet().stream()
                                       .map(e -> String.format("%s: %.2f", e.getKey(), e.getValue().getModValue())).collect(Collectors.toList());
            milestones.computeIfAbsent(s.level, ArrayList::new).addAll(modStr);
//            BookEntry      entry  = bookEntries.get(i);
//            GuiButtonEntry button = new GuiButtonEntryFixed(book, x, y + 30 + i * 11, entry, i);
//            context.registerButton(button, pgNum, () -> GuiBookEntry.displayOrBookmark(book, entry));
//            entries.add(button);
        }

        this.myEntryList = entries;
    }

    @Override
    void setEntryScroll(ComponentText entry, int prevShownPos, int currShownPos) {
        int entryId = this.myEntryList.indexOf(entry);

        entry.y += (prevShownPos - currShownPos) * 11;
        entry.guard = entryId >= currShownPos && entryId < currShownPos + this.maxEntriesShown ? null : "false";
    }
}
