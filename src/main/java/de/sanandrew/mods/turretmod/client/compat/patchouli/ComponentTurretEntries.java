package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.google.gson.annotations.SerializedName;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.GuiBookEntry;
import vazkii.patchouli.client.book.gui.button.GuiButtonBook;
import vazkii.patchouli.client.book.gui.button.GuiButtonEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("WeakerAccess")
public class ComponentTurretEntries
        implements ICustomComponent
{
    private          int x;
    private          int y;
    transient public int pgNum;

    @VariableHolder
    @SerializedName("target_id")
    public String targetId;
    @SerializedName("max_entries_shown")
    public int    maxEntriesShown = 5;
    @SerializedName("entries_type")
    public String entriesType;
    @SerializedName("title_color")
    public String colorStr;
    public String title;

    transient private       Integer         color;
    transient private final List<GuiButton> entryButtons    = new ArrayList<>();
    transient private       GuiButtonBook   prevScrollBtn;
    transient private       GuiButtonBook   nextScrollBtn;
    transient private       EntryType       type;
    transient private       int             entriesShownPos = 0;

    @Override
    public void build(int x, int y, int pgNum) {
        try {
            this.color = Integer.parseInt(this.colorStr, 16);
        } catch( NumberFormatException var5 ) {
            this.color = null;
        }

        this.x = x;
        this.y = y;
        this.pgNum = pgNum;

        this.type = EntryType.getTypeFromString(this.entriesType);
    }

    @Override
    public void onDisplayed(IComponentRenderContext context) {
        final GuiBook guiBook = ComponentTurretEntries.getGuiBook(context);

        if( guiBook == null ) {
            return;
        }

        // purge stuck buttons from GUI
        this.entryButtons.add(this.prevScrollBtn);
        this.entryButtons.add(this.nextScrollBtn);
        guiBook.getButtonList().removeIf(this.entryButtons::contains);
        this.entryButtons.clear();

        final List<BookEntry> entries = this.type.grabEntries(guiBook, this.targetId);

        for( int i = 0, max = entries.size(); i < max; i++ ) {
            BookEntry      entry  = entries.get(i);
            GuiButtonEntry button = new GuiButtonEntryFixed(guiBook, this.x, this.y + 30 + i * 11 - this.entriesShownPos * 11, entry, i);
            context.registerButton(button, pgNum, () -> GuiBookEntry.displayOrBookmark(guiBook, entry));
            this.entryButtons.add(button);
            button.visible = button.id >= this.entriesShownPos && button.id < this.entriesShownPos + this.maxEntriesShown;
        }

        if( this.entryButtons.size() > this.maxEntriesShown ) {
            this.prevScrollBtn = new GuiButtonBook(guiBook, this.x, this.y + 20, 330, 9, 11, 5,
                                                   () -> this.entriesShownPos > 0);
            this.nextScrollBtn = new GuiButtonBook(guiBook, this.x + 116 - 11, this.y + 20, 330, 15, 11, 5,
                                                   () -> this.entriesShownPos + this.maxEntriesShown < this.entryButtons.size());

            context.registerButton(this.prevScrollBtn, pgNum, () -> moveButtons(true));
            context.registerButton(this.nextScrollBtn, pgNum, () -> moveButtons(false));
        }
    }

    void moveButtons(boolean prev) {
        int prevPos = this.entriesShownPos;
        this.entriesShownPos += prev && this.entriesShownPos > 0
                                ? -1
                                : (!prev && this.entriesShownPos + this.maxEntriesShown < this.entryButtons.size() ? 1 : 0);
        if( this.entriesShownPos != prevPos ) {
            this.entryButtons.forEach(b -> {
                b.y += (prevPos - this.entriesShownPos) * 11;
                b.visible = b.id >= this.entriesShownPos && b.id < this.entriesShownPos + this.maxEntriesShown;
            });
        }
    }

    @Override
    public void render(IComponentRenderContext context, float partTicks, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        int xPos = this.x + 58 - (context.getFont().getStringWidth(this.title) / 2);
        context.getFont().drawString(this.title, xPos, this.y, MiscUtils.defIfNull(this.color, context::getHeaderColor), false);
        GlStateManager.popMatrix();

        MouseEventHandler.setCurrHoveredComponent(this, context.isAreaHovered(mouseX, mouseY, this.x, this.y + 11, 116, this.maxEntriesShown * 11 - 1));
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
            return AmmunitionRegistry.INSTANCE.getGroups(TurretRegistry.INSTANCE.getObject(new ResourceLocation(s))).stream()
                                              .map(a -> g.book.contents.entries.get(a.getBookEntryId()));
        }),
        UPGRADES((g, s) -> {
            return UpgradeRegistry.INSTANCE.getObjects().stream().map(u -> {
                if( u.isApplicable(TurretRegistry.INSTANCE.getObject(new ResourceLocation(s))) ) {
                    return g.book.contents.entries.get(u.getBookEntryId());
                }

                return null;
            }).distinct();
        }),

        UNKNOWN((g, s) -> Stream.empty());

        private final BiFunction<GuiBook, String, Stream<BookEntry>> grabEntriesFunc;

        public static final EntryType[] VALUES = values();

        EntryType(BiFunction<GuiBook, String, Stream<BookEntry>> grabEntriesFunc) {
            this.grabEntriesFunc = grabEntriesFunc;
        }

        public List<BookEntry> grabEntries(GuiBook book, String id) {
            return this.grabEntriesFunc.apply(book, id).filter(e -> e != null && !e.shouldHide()).sorted().collect(Collectors.toList());
        }

        public static EntryType getTypeFromString(String type) {
            return Arrays.stream(VALUES).filter(e -> e.name().equalsIgnoreCase(type)).findFirst().orElse(UNKNOWN);
        }
    }
}
