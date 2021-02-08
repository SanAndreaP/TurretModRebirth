package de.sanandrew.mods.turretmod.client.gui.element.tinfo;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Item;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTurretProvider;
import de.sanandrew.mods.turretmod.entity.turret.UpgradeProcessor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Range;

public class InfoUpgradeItems
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tinfo_upgrade_items");

    private GuiElementInst[] upgradeItems;

    private int width;
    private int height;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        int rows = JsonUtils.getIntVal(data.get("rows"), 2);

        UpgradeProcessor upgProc = (UpgradeProcessor) ((IGuiTurretProvider) gui).getTurretInst().getUpgradeProcessor();
        this.upgradeItems = new GuiElementInst[upgProc.getSizeInventory()];


        double scale = JsonUtils.getDoubleVal(data.get("itemScale"), 0.5D);
        int[] offset = JsonUtils.getIntArray(data.get("offset"), new int[2], Range.is(2));
        int colCount = MathHelper.ceil((double) this.upgradeItems.length / rows);
        int itemSz = (int) Math.round(16 * scale);

        for( int i = 0, max = this.upgradeItems.length; i < max; i++ ) {
            JsonObject itemData = new JsonObject();
            JsonUtils.addJsonProperty(itemData, "scale", scale);

            int posX = (i % colCount) * (2 + itemSz);
            int posY = (i / colCount) * (2 + itemSz);
            this.upgradeItems[i] = new GuiElementInst(new int[] {offset[0] + posX, offset[1] + posY},
                                                      new UpgradeItem(upgProc.getStackInSlot(i)), itemData);
            this.upgradeItems[i].get().bakeData(gui, itemData, this.upgradeItems[i]);
        }

        this.width = colCount * (2 + itemSz) - 2;
        this.height = rows * (2 + itemSz) - 2;
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        UpgradeProcessor upgProc = (UpgradeProcessor) ((IGuiTurretProvider) gui).getTurretInst().getUpgradeProcessor();

        for( int i = 0, max = this.upgradeItems.length; i < max; i++ ) {
            this.upgradeItems[i].get(UpgradeItem.class).stack = upgProc.getStackInSlot(i);
            this.upgradeItems[i].get().update(gui, this.upgradeItems[i].data);
        }
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        for( GuiElementInst upgradeItem : this.upgradeItems ) {
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

    private static final class UpgradeItem
            extends Item
    {
        UpgradeItem(ItemStack item) {
            this.stack = item;
        }

        @Override
        protected ItemStack getBakedStack(IGui gui, JsonObject data) {
            return this.stack;
        }
    }
}
