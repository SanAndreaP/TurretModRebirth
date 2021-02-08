package de.sanandrew.mods.turretmod.client.gui.element.tinfo;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTurretProvider;
import de.sanandrew.mods.turretmod.entity.turret.UpgradeProcessor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Range;

import java.util.ArrayList;
import java.util.List;

public class InfoUpgradeTooltips
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tinfo_upgrade_ttips");

    private GuiElementInst[] ttips;

    private int width;
    private int height;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        int rows = JsonUtils.getIntVal(data.get("rows"), 2);

        UpgradeProcessor upgProc = (UpgradeProcessor) ((IGuiTurretProvider) gui).getTurretInst().getUpgradeProcessor();
        this.ttips = new GuiElementInst[upgProc.getSizeInventory()];


        double scale = JsonUtils.getDoubleVal(data.get("itemScale"), 0.5D);
        int[] offset = JsonUtils.getIntArray(data.get("offset"), new int[] { -1, -1 }, Range.is(2));
        int colCount = MathHelper.ceil((double) this.ttips.length / rows);
        int itemSz = (int) Math.round(16 * scale);

        for( int i = 0, max = this.ttips.length; i < max; i++ ) {
            JsonObject itemData = MiscUtils.defIfNull(data.getAsJsonObject("tooltip"), JsonObject::new);
            JsonUtils.addDefaultJsonProperty(itemData, "size", new int[] {itemSz + 2, itemSz + 2});

            int posX = (i % colCount) * (2 + itemSz);
            int posY = (i / colCount) * (2 + itemSz);
            this.ttips[i] = new GuiElementInst(new int[] {offset[0] + posX, offset[1] + posY},
                                               new Tooltip(upgProc.getStackInSlot(i)), itemData);
            this.ttips[i].get().bakeData(gui, itemData, this.ttips[i]);
        }

        this.width = colCount * (2 + itemSz) - 2;
        this.height = rows * (2 + itemSz) - 2;

    }

    @Override
    public void update(IGui gui, JsonObject data) {
        UpgradeProcessor upgProc = (UpgradeProcessor) ((IGuiTurretProvider) gui).getTurretInst().getUpgradeProcessor();

        for( int i = 0, max = this.ttips.length; i < max; i++ ) {
            this.ttips[i].get(Tooltip.class).stack = upgProc.getStackInSlot(i);
            this.ttips[i].get().update(gui, this.ttips[i].data);
        }
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        for( GuiElementInst upgradeItem : this.ttips ) {
            upgradeItem.get().render(gui, partTicks, x + upgradeItem.pos[0], y + upgradeItem.pos[1], mouseX, mouseY, upgradeItem.data);
        }
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    private static final class Tooltip
            extends de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip
    {
        private boolean visible = true;

        ItemStack stack;

        Tooltip(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
            JsonUtils.addDefaultJsonProperty(data, "size", new int[] { 16, 16 });

            super.bakeData(gui, data, inst);
        }

        @Override
        public GuiElementInst getContent(IGui gui, JsonObject data) {
            return new GuiElementInst(new TooltipText()).initialize(gui);
        }

        @Override
        public void update(IGui gui, JsonObject data) {
            this.getChild(CONTENT).get(TooltipText.class).currStack = this.stack;
            this.visible = ItemStackUtils.isValid(this.stack);

            super.update(gui, data);
        }

        @Override
        public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
            if( !this.visible ) {
                return;
            }

            if( IGuiElement.isHovering(gui, x, y, mouseX, mouseY, this.size[0], this.size[1]) ) {
                GlStateManager.disableDepth();
                GlStateManager.colorMask(true, true, true, false);
                GuiUtils.drawGradientRect(x + 1, y + 1, this.size[0] - 1, this.size[1] - 1, 0x80FFFFFF, 0x80FFFFFF, false);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableDepth();
            }

            super.render(gui, partTicks, x, y, mouseX, mouseY, data);
        }

        @Override
        public int getWidth() {
            return this.size[0];
        }

        @Override
        public int getHeight() {
            return this.size[1];
        }

        //TODO: make a more abstract class usable by this and AmmoItemTooltip!
        private static final class TooltipText
                implements IGuiElement
        {
            private final List<GuiElementInst> lines = new ArrayList<>();

            private ItemStack currStack = ItemStack.EMPTY;
            private ItemStack prevStack = ItemStack.EMPTY;

            private int width;
            private int height;

            @Override
            public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) { }

            @Override
            public void update(IGui gui, JsonObject data) {
                if( !ItemStackUtils.areEqual(this.currStack, this.prevStack) ) {
                    this.prevStack = this.currStack;
                    this.lines.clear();
                    this.width = 0;
                    this.height = 0;

                    List<String> ttip = gui.get().getItemToolTip(this.currStack);
                    for( String line : ttip ) {
                        GuiElementInst txtElem = new GuiElementInst(new int[] { 0, this.height }, new Text()).initialize(gui);

                        JsonUtils.addJsonProperty(txtElem.data, "color", "0xFFFFFFFF");
                        JsonUtils.addJsonProperty(txtElem.data, "text", line);
                        JsonUtils.addJsonProperty(txtElem.data, "shadow", true);

                        txtElem.get().bakeData(gui, txtElem.data, txtElem);

                        this.lines.add(txtElem);

                        this.height += txtElem.get().getHeight() + (this.height == 0 ? 2 : 0);
                    }

                    this.height -= 2 + (ttip.size() < 2 ? 2 : 0);
                }
            }

            @Override
            public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
                this.update(gui, data);
                for( GuiElementInst line : this.lines ) {
                    GuiDefinition.renderElement(gui, x + line.pos[0], y + line.pos[1], mouseX, mouseY, partTicks, line);
                    this.width = Math.max(this.width, line.get().getWidth());
                }
            }

            @Override
            public int getWidth() {
                return this.width;
            }

            @Override
            public int getHeight() {
                return this.height;
            }
        }
    }
}
