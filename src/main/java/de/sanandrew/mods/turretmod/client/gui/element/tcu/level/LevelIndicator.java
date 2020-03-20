package de.sanandrew.mods.turretmod.client.gui.element.tcu.level;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.registry.upgrades.leveling.LevelStorage;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Range;

public class LevelIndicator
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu.levels_indicator");

    private int currLvl;
    private int minXp;
    private int maxXp;
    private int currXp;

    private GuiElementInst text;
    private GuiElementInst progress;
    private GuiElementInst progressTotal;

    private int currWidth;
    private int currHeight;

    public void setLevel(LevelStorage storage) {
        this.currLvl = storage.getLevel();
        this.minXp = storage.getCurrentLevelMinXp();
        this.maxXp = storage.getNextLevelMinXp();
        this.currXp = storage.getXp();
    }

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        JsonObject txtData = MiscUtils.defIfNull(data.getAsJsonObject("text"), JsonObject::new);
        int[] offset = JsonUtils.getIntArray(txtData.get("offset"), new int[2], Range.is(2));
        this.text = new GuiElementInst(offset, new LevelText(), txtData).initialize(gui);
        this.text.get().bakeData(gui, this.text.data, this.text);

        JsonObject prgData = MiscUtils.defIfNull(data.getAsJsonObject("progressbar"), JsonObject::new);
        offset = JsonUtils.getIntArray(prgData.get("offset"), new int[] {0, this.text.pos[1] + this.text.get().getHeight() + 3}, Range.is(2));
        this.progress = new GuiElementInst(offset, new LevelBar(false), prgData).initialize(gui);
        this.progress.get().bakeData(gui, this.progress.data, this.progress);

        prgData = MiscUtils.defIfNull(data.getAsJsonObject("progressbarTotal"), JsonObject::new);
        offset = JsonUtils.getIntArray(prgData.get("offset"), new int[] {0, this.text.pos[1] + this.progress.pos[1] + this.text.get().getHeight() + 9}, Range.is(2));
        this.progressTotal = new GuiElementInst(offset, new LevelBar(true), prgData).initialize(gui);
        this.progressTotal.get().bakeData(gui, this.progressTotal.data, this.progressTotal);

        this.calcSize();
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        this.text.get().update(gui, this.text.data);
        this.progress.get().update(gui, this.progress.data);
        this.progressTotal.get().update(gui, this.progressTotal.data);

        this.calcSize();
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        GuiDefinition.renderElement(gui, x + this.text.pos[0], y + this.text.pos[1], mouseX, mouseY, partTicks, this.text);
        GuiDefinition.renderElement(gui, x + this.progress.pos[0], y + this.progress.pos[1], mouseX, mouseY, partTicks, this.progress);
        GuiDefinition.renderElement(gui, x + this.progressTotal.pos[0], y + this.progressTotal.pos[1], mouseX, mouseY, partTicks, this.progressTotal);
    }

    @Override
    public int getWidth() {
        return this.currWidth;
    }

    @Override
    public int getHeight() {
        return this.currHeight;
    }

    private void calcSize() {
        this.currWidth = Math.max(this.text.pos[0] + this.text.get().getWidth(),
                                  Math.max(this.progress.pos[0] + this.progress.get().getWidth(),
                                           this.progressTotal.pos[0] + this.progressTotal.get().getWidth()));
        this.currHeight = Math.max(this.text.pos[1] + this.text.get().getHeight(),
                                   Math.max(this.progress.pos[1] + this.progress.get().getHeight(),
                                            this.progressTotal.pos[1] + this.progressTotal.get().getHeight()));
    }

    private final class LevelText
            extends Text
    {
        @Override
        public String getDynamicText(IGui gui, String originalText) {
            return String.format(originalText, LevelIndicator.this.currLvl);
        }
    }

    private final class LevelBar
            extends Texture
    {
        private final boolean total;
        private int levelBarWidth;

        private LevelBar(boolean total) {
            this.total = total;
        }

        @Override
        public void update(IGui gui, JsonObject data) {
            float v;
            if( this.total ) {
                v = LevelIndicator.this.currXp / (float) LevelStorage.maxXp;
            } else {
                v = (LevelIndicator.this.currXp - LevelIndicator.this.minXp)
                    / (float) (LevelIndicator.this.maxXp - LevelIndicator.this.minXp);

            }
            this.levelBarWidth = Math.max(0, Math.min(this.size[0], MathHelper.ceil(v * this.size[0])));
        }

        @Override
        protected void drawRect(IGui gui) {
            Gui.drawModalRectWithCustomSizedTexture(0, 0, this.uv[0], this.uv[1], this.levelBarWidth, this.size[1],
                                                    this.textureSize[0], this.textureSize[1]);
        }

        @Override
        public int getWidth() {
            return this.levelBarWidth;
        }

        @Override
        public int getHeight() {
            return this.size[1];
        }
    }
}
