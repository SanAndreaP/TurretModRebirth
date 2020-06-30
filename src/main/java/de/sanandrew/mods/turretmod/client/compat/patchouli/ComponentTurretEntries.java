package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.google.gson.annotations.SerializedName;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonEntry;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class ComponentTurretEntries
        implements ICustomComponent
{
    private int x;
    private int y;
    private int pgNum;

    @VariableHolder
    @SerializedName("turret")
    public String turretId;
    @SerializedName("max_entries_shown")
    public int    maxEntriesShown = 5;
    @SerializedName("entries_type")
    public String type;

    transient private ITurret turret;

    transient private static int entriesShownPos = 0;

    @Override
    public void build(int x, int y, int pgNum) {
        this.x = x;
        this.y = y;
        this.pgNum = pgNum;

        this.turret = TurretRegistry.INSTANCE.getObject(new ResourceLocation(this.turretId));
    }

    @Override
    public void onDisplayed(IComponentRenderContext context) {
        final GuiBook guiBook = ComponentTurretEntries.getGuiBook(context);

        if( guiBook == null ) {
            return;
        }

        // purge stuck buttons from GUI
        guiBook.getButtonList().removeIf(b -> b instanceof GuiButtonEntryFixed && !b.visible);
        final List<BookEntry> entries;
        if( type.equals("ammo") ) {
            entries = AmmunitionRegistry.INSTANCE.getGroups(this.turret).stream().map(g -> {
                ResourceLocation rl = g.getBookEntryId();
                if( rl != null ) {
                    return guiBook.book.contents.entries.get(rl);
                }

                return null;
            }).filter(e -> e != null && !e.shouldHide()).collect(Collectors.toList());
        } else if( type.equals("upgrades") ) {
            entries = UpgradeRegistry.INSTANCE.getObjects().stream().map(u -> {
                ITurret[] applicables = u.getApplicableTurrets();
                if( applicables == null || Arrays.binarySearch(applicables, this.turret) >= 0 ) {
                    ResourceLocation rl = u.getBookEntryId();
                    if( rl != null ) {
                        return guiBook.book.contents.entries.get(rl);
                    }
                }

                return null;
            }).filter(e -> e != null && !e.shouldHide()).collect(Collectors.toList());
        } else {
            return;
        }

        if( entriesShownPos + this.maxEntriesShown < entries.size() ) {
            entries.subList(entriesShownPos + this.maxEntriesShown, entries.size()).clear();
        }
        entries.subList(0, entriesShownPos).clear();
        Collections.sort(entries);

        for( int i = 0, max = entries.size(); i < max; i++ ) {
            BookEntry entry = entries.get(i);
            GuiButton button = new GuiButtonEntryFixed(guiBook, this.x, this.y + 20 + i * 11, entry, i);
            context.registerButton(button, pgNum, () -> {
                 GuiBookEntry.displayOrBookmark(guiBook, entry);
            });
        }
    }

    @Override
    public void render(IComponentRenderContext context, float partTicks, int mouseX, int mouseY) {

    }

    private static GuiBook getGuiBook(IComponentRenderContext context) {
        GuiScreen gui = context.getGui();
        if( gui instanceof GuiBook ) {
            return ((GuiBook) gui);
        }

        return null;
    }

    private class GuiButtonEntryFixed
            extends GuiButtonEntry
    {
        GuiBook _parent;

        public GuiButtonEntryFixed(GuiBook parent, int x, int y, BookEntry entry, int i) {
            super(parent, x, y, entry, i);

            this._parent = parent;
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
}
