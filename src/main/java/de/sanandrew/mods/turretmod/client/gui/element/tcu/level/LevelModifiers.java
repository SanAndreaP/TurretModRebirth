package de.sanandrew.mods.turretmod.client.gui.element.tcu.level;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.upgrades.leveling.LevelStorage;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LevelModifiers
            extends ScrollArea
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu.levels_modifiers");
    private static final DecimalFormat formatter = new DecimalFormat("+0.0;-0.0");

    private int                                    prevModifierHash = 0;
    private Map<String, LevelStorage.ModifierInfo> modifiers        = Collections.emptyMap();

    public void setModifierList(IGui gui, JsonObject data, LevelStorage storage, ITurretInst turretInst) {
        this.modifiers = storage.fetchCurrentModifiers(turretInst);
        int currModifierHash = this.modifiers.hashCode();
        if( this.prevModifierHash != currModifierHash ) {
            this.rebuildElements(gui, data);
            this.prevModifierHash = currModifierHash;
        }
    }

    @Override
    public GuiElementInst[] getElements(IGui gui, JsonObject elementData) {
        List<GuiElementInst> elements = new ArrayList<>();
        int posY = 0;

        for( Map.Entry<String, LevelStorage.ModifierInfo> e : LevelModifiers.this.modifiers.entrySet() ) {
            JsonObject elemData = JsonUtils.deepCopy(elementData.getAsJsonObject("node"));
            LevelStorage.ModifierInfo info = e.getValue();

            GuiElementInst elem = new GuiElementInst(new int[] { 0, posY }, new ModifierNode(e.getKey(), (info.getModValue() - info.baseValue) / info.baseValue * 100.0F), elemData)
                                                    .initialize(gui);
            elem.get().bakeData(gui, elemData, elem);
            elements.add(elem);
            posY += elem.get().getHeight() + 1;
        }

        return elements.toArray(new GuiElementInst[0]);
    }

    @Override
    public boolean bakeElements() {
        return false;
    }

    private static final class ModifierNode
        implements IGuiElement
    {
        private final String attributeName;
        private final double modifierValue;

        private GuiElementInst attrName;
        private GuiElementInst modValue;
        private GuiElementInst background;

        private ModifierNode(String attributeName, double modifierValue) {
            this.attributeName = attributeName;
            this.modifierValue = modifierValue;
        }

        @Override
        public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
            JsonObject jsonBackg = MiscUtils.defIfNull(data.getAsJsonObject("background"), JsonObject::new);
            this.background = new GuiElementInst(new Texture(), jsonBackg).initialize(gui);
            this.background.get().bakeData(gui, jsonBackg, this.background);

            JsonObject jsonAttrName = MiscUtils.defIfNull(data.getAsJsonObject("attributeName"), JsonObject::new);
            JsonUtils.addJsonProperty(jsonAttrName, "text", "attribute." + this.attributeName);
            this.attrName = new GuiElementInst(JsonUtils.getIntArray(jsonAttrName.get("offset"), new int[] {5, 5}, Range.is(2)), new Text(), jsonAttrName)
                                              .initialize(gui);
            this.attrName.get().bakeData(gui, jsonAttrName, this.attrName);

            JsonObject jsonModValue = MiscUtils.defIfNull(data.getAsJsonObject("modifierValue"), JsonObject::new);
            this.modValue = new GuiElementInst(JsonUtils.getIntArray(jsonModValue.get("offset"), new int[] {this.background.get().getWidth() - 5, 5}, Range.is(2)),
                                               new ModifierValueText(), jsonModValue).initialize(gui);
            this.modValue.alignment = new String[] { GuiElementInst.Justify.RIGHT.name(), GuiElementInst.Justify.TOP.name() };
            this.modValue.get().bakeData(gui, jsonModValue, this.modValue);

        }

        @Override
        public void update(IGui gui, JsonObject data) {
            this.background.get().update(gui, this.background.data);
            this.attrName.get().update(gui, this.attrName.data);
            this.modValue.get().update(gui, this.modValue.data);
        }

        @Override
        public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
            GuiDefinition.renderElement(gui, x, y, mouseX, mouseY, partTicks, this.background);
            GuiDefinition.renderElement(gui, x + this.attrName.pos[0], y + this.attrName.pos[1], mouseX, mouseY, partTicks, this.attrName);
            GuiDefinition.renderElement(gui, x + this.modValue.pos[0], y + this.modValue.pos[1], mouseX, mouseY, partTicks, this.modValue);
        }

        @Override
        public int getWidth() {
            return this.background.get().getWidth();
        }

        @Override
        public int getHeight() {
            return this.background.get().getHeight();
        }

        private final class ModifierValueText
                extends Text
        {

            @Override
            public String getDynamicText(IGui gui, String originalText) {
                return String.format(originalText, formatter.format(ModifierNode.this.modifierValue));
            }
        }
    }
}
