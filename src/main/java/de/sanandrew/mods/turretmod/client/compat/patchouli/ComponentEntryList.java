package de.sanandrew.mods.turretmod.client.compat.patchouli;

import com.google.gson.annotations.SerializedName;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.api.IComponentRenderContext;
import vazkii.patchouli.api.ICustomComponent;
import vazkii.patchouli.api.VariableHolder;
import vazkii.patchouli.client.book.gui.GuiBook;
import vazkii.patchouli.client.book.gui.button.GuiButtonBook;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
@SuppressWarnings("WeakerAccess")
public abstract class ComponentEntryList<T>
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
    @SerializedName("title_color")
    public String colorStr;
    @VariableHolder
    public String title;

    transient protected final List<T>       entries = new ArrayList<>();
    transient private       Integer       color;
    transient private       GuiButtonBook prevScrollBtn;
    transient private       GuiButtonBook   nextScrollBtn;
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
    }

    public abstract void buildEntries(IComponentRenderContext context, GuiBook book, List<T> entries, int x, int y);

    @Override
    @SuppressWarnings("SuspiciousMethodCalls")
    public void onDisplayed(IComponentRenderContext context) {
        final GuiBook guiBook = ComponentEntryList.getGuiBook(context);

        if( guiBook == null ) {
            return;
        }

        // purge entries and buttons
        guiBook.getButtonList().remove(this.prevScrollBtn);
        guiBook.getButtonList().remove(this.nextScrollBtn);
        guiBook.getButtonList().removeIf(this.entries::contains);
        this.entries.clear();

        this.buildEntries(context, guiBook, this.entries, this.x, this.y + 11);
        this.entries.forEach(e -> this.setEntryScroll(e, 0, this.entriesShownPos));

        if( this.entries.size() > this.maxEntriesShown ) {
            this.prevScrollBtn = new GuiButtonBook(guiBook, this.x, this.y + 20, 330, 9, 11, 5,
                                                   () -> this.entriesShownPos > 0 && guiBook.getPage() == this.pgNum / 2);
            this.nextScrollBtn = new GuiButtonBook(guiBook, this.x + 116 - 11, this.y + 20, 330, 15, 11, 5,
                                                   () -> this.entriesShownPos + this.maxEntriesShown < this.entries.size()
                                                         && guiBook.getPage() == this.pgNum / 2 );

            context.registerButton(this.prevScrollBtn, this.pgNum, () -> moveButtons(true));
            context.registerButton(this.nextScrollBtn, this.pgNum, () -> moveButtons(false));
        }
    }

    void moveButtons(boolean prev) {
        int prevPos = this.entriesShownPos;
        this.entriesShownPos += prev && this.entriesShownPos > 0
                                ? -1
                                : (!prev && this.entriesShownPos + this.maxEntriesShown < this.entries.size() ? 1 : 0);
        if( this.entriesShownPos != prevPos ) {
            this.entries.forEach(e -> this.setEntryScroll(e, prevPos, this.entriesShownPos));
        }
    }

    abstract int getEntryHeight();

    abstract void setEntryScroll(T entry, int prevShownPos, int currShownPos);

    @Override
    public void render(IComponentRenderContext context, float partTicks, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        int xPos = this.x + 58 - (context.getFont().getStringWidth(this.title) / 2);
        context.getFont().drawString(this.title, xPos, this.y, MiscUtils.defIfNull(this.color, context::getHeaderColor), false);
        GlStateManager.popMatrix();

        PatchouliMouseEventHandler.setCurrHoveredComponent(this, context.isAreaHovered(mouseX, mouseY, this.x, this.y + 11, 116, this.maxEntriesShown * this.getEntryHeight() - 1));
    }

    private static GuiBook getGuiBook(IComponentRenderContext context) {
        GuiScreen gui = context.getGui();
        if( gui instanceof GuiBook ) {
            return ((GuiBook) gui);
        }

        return null;
    }
}
