package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.google.gson.annotations.SerializedName;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonEntry;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComponentTurretEntries
        extends ComponentEntryList<GuiButtonEntry>
{
    @SerializedName("entries_type")
    public String entriesType;

    transient private EntryType type;

    @Override
    public void build(int x, int y, int pgNum) {
        super.build(x, y, pgNum);

        this.type = EntryType.getTypeFromString(this.entriesType);
    }

    @Override
    public void buildEntries(IComponentRenderContext context, GuiBook book, List<GuiButtonEntry> entries, int x, int y) {
        final List<BookEntry> bookEntries = this.type.grabEntries(book, this.targetId);

        for( int i = 0, max = bookEntries.size(); i < max; i++ ) {
            BookEntry      entry  = bookEntries.get(i);
            GuiButtonEntry button = new GuiButtonEntryFixed(book, x, y + 30 + i * 11, entry, i);
            context.registerButton(button, pgNum, () -> GuiBookEntry.displayOrBookmark(book, entry));
            entries.add(button);
        }
    }

    @Override
    void setEntryScroll(GuiButtonEntry entry, int prevShownPos, int currShownPos) {
        entry.y += (prevShownPos - currShownPos) * 11;
        entry.visible = entry.id >= currShownPos && entry.id < currShownPos + this.maxEntriesShown;
    }

    private class GuiButtonEntryFixed
            extends GuiButtonEntry
    {
        GuiBook _parent;

        public GuiButtonEntryFixed(GuiBook parent, int x, int y, BookEntry entry, int i) {
            super(parent, x, y, entry, i);

            this._parent = parent;
            this.id = i;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            // if the button is still rendered on a page not belonging to it, hide the button.
            // Once these types of buttons are rebuilt on the proper page, these will be purged from the buttonList first.
            if( this.visible && _parent.getPage() + 1 != MathHelper.ceil(ComponentTurretEntries.this.pgNum / 1.99F) ) {
                this.visible = false;
            }

            super.drawButton(mc, mouseX, mouseY, partialTicks);
        }
    }

    private enum EntryType
    {
        AMMO((g, s) -> {
            return AmmunitionRegistry.INSTANCE.getObjects(getTurret(s)).stream()
                                              .map(a -> g.book.contents.recipeMappings.get(PatchouliHelper.getWrapper(AmmunitionRegistry.INSTANCE, a)))
                                              .filter(Objects::nonNull).map(Pair::getKey);
        }),
        UPGRADES((g, s) -> {
            return UpgradeRegistry.INSTANCE.getObjects().stream()
                                           .filter(u -> UpgradeRegistry.INSTANCE.isApplicable(u, getTurret(s)))
                                           .map(u -> g.book.contents.recipeMappings.get(PatchouliHelper.getWrapper(UpgradeRegistry.INSTANCE, u)))
                                           .filter(Objects::nonNull).map(Pair::getKey);
        }),

        UNKNOWN((g, s) -> Stream.empty());

        private final BiFunction<GuiBook, String, Stream<BookEntry>> grabEntriesFunc;

        public static final EntryType[] VALUES = values();

        EntryType(BiFunction<GuiBook, String, Stream<BookEntry>> grabEntriesFunc) {
            this.grabEntriesFunc = grabEntriesFunc;
        }

        public List<BookEntry> grabEntries(GuiBook book, String id) {
            return this.grabEntriesFunc.apply(book, id).distinct().filter(e -> e != null && !e.shouldHide()).sorted().collect(Collectors.toList());
        }

        public static EntryType getTypeFromString(String type) {
            return Arrays.stream(VALUES).filter(e -> e.name().equalsIgnoreCase(type)).findFirst().orElse(UNKNOWN);
        }

        private static ITurret getTurret(String id) {
            return TurretRegistry.INSTANCE.getObject(new ResourceLocation(id));
        }
    }
}
