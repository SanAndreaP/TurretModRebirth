package de.sanandrew.mods.turretmod.client.gui.element.tcu.level;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ScrollArea;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.registry.upgrades.leveling.LevelStorage;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LevelModifiers
        extends ElementParent<Integer>
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tcu.levels_modifiers");

    private GuiElementInst                         modifierList;
    private int                                    prevModifierHash = 0;
    private Map<String, LevelStorage.ModifierInfo> modifiers        = Collections.emptyMap();

    public void setModifierList(IGui gui, LevelStorage storage, ITurretInst turretInst) {
        this.modifiers = storage.fetchCurrentModifiers(turretInst);
        int currModifierHash = this.modifiers.hashCode();
        if( this.prevModifierHash != currModifierHash ) {
            this.modifierList.get(ModifierArea.class).rebuildElements(gui, this.modifierList.data);
            this.prevModifierHash = currModifierHash;
        }
    }

    @Override
    public void buildChildren(IGui gui, JsonObject data, Map<Integer, GuiElementInst> listToBuild) {
        this.modifierList = new GuiElementInst(new ModifierArea(), data.getAsJsonObject("area")).initialize(gui);
        listToBuild.put(0, this.modifierList);
    }

    private final class ModifierArea
            extends ScrollArea
    {
        @Override
        public GuiElementInst[] getElements(IGui gui, JsonObject elementData) {
            List<GuiElementInst> elements = new ArrayList<>();
            int posY = 0;
            for( Map.Entry<String, LevelStorage.ModifierInfo> e : LevelModifiers.this.modifiers.entrySet() ) {
                JsonObject elemData = new JsonObject();
                elemData.addProperty("text", String.format("%s: +%.2f", e.getKey(), e.getValue().getModValue() - e.getValue().baseValue));
                GuiElementInst elem = new GuiElementInst(new int[] { 0, posY }, new Text(), elemData).initialize(gui);
                elem.get().bakeData(gui, elemData, elem);
                elements.add(elem);
                posY += elem.get().getHeight() + 3;
            }

            return elements.toArray(new GuiElementInst[0]);
        }

        @Override
        public boolean bakeElements() {
            return false;
        }
    }
}
