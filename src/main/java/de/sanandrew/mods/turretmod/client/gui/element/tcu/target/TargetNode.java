package de.sanandrew.mods.turretmod.client.gui.element.tcu.target;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.shieldcolor.CheckBox;
import de.sanandrew.mods.turretmod.client.gui.tcu.TargetType;
import org.apache.commons.lang3.Range;

import java.io.IOException;

public class TargetNode<T>
        extends CheckBox
{
    private int[] margins;

//    private GuiElementInst checkbox;

    private final T             targetId;
    private final TargetType<T> targetType;
    private final int           width;

    private String name;

    public TargetNode(T id, TargetType<T> type, int width) {
        this.targetId = id;
        this.targetType = type;
        this.width = width;
    }

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {

        GuiDefinition guiDef = gui.getDefinition();
        ITurretInst turretInst = ((IGuiTcuInst<?>) gui).getTurretInst();
        TargetType.EntityType type = this.targetType.getType(turretInst, this.targetId);
////        JsonObject ckbData = MiscUtils.defIfNull(, JsonObject::new).getAsJsonObject();
//
        this.margins = JsonUtils.getIntArray(data.get("margins"), new int[] {2, 2, 0, 2}, Range.is(4));

        JsonObject lblData = new JsonObject();

        data.add("labelText", lblData);

        super.bakeData(gui, data, inst);
//
//        CheckBox ckb = new CheckBox();
//        this.checkbox = new GuiElementInst(ckb, data.getAsJsonObject("checkbox")).initialize(gui);
//
//        JsonObject lblData = MiscUtils.defIfNull(this.checkbox.data.getAsJsonObject("label"), JsonObject::new);
//        JsonUtils.addDefaultJsonProperty(lblData, "text", this.name);
//        JsonUtils.addDefaultJsonProperty(lblData, "color", getTextColor(lblData, type, ""));
//        JsonUtils.addDefaultJsonProperty(lblData, "colorHover", getTextColor(lblData, type, "Hover"));
//        JsonUtils.addDefaultJsonProperty(lblData, "colorDisabled", getTextColor(lblData, type, "Disabled"));
//
//        JsonUtils.addDefaultJsonProperty(this.checkbox.data, "size", new int[] { 8, 8 });
//        JsonUtils.addDefaultJsonProperty(this.checkbox.data, "uv", new int[] { 176, 12 });
//
//        this.checkbox.data.add("label", lblData);
//
//        ckb.bakeData(gui, this.checkbox.data, this.checkbox);
//        ckb.setOnCheckedChanged(byUser -> {
//            if( byUser ) {
//                this.targetType.updateTarget(turretInst, this.targetId, ckb.isChecked());
//            }
//        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends Text> U getLabel(IGui gui, JsonObject data) {
        return (U) new Label();
    }

//    private static JsonObject getTextColor(JsonObject textData, TargetType.EntityType type) {
//        String[] suffixes = {"default", "hover", "disabled"};

//        if( suffix.equalsIgnoreCase("disabled") ) {
//            return JsonUtils.getStringVal(textData.get("colorDisabled"), "0xFF404040");
//        }
//
//        switch( type ) {
//            case HOSTILE:
//                return JsonUtils.getStringVal(textData.get("colorHostile" + suffix), suffix.equalsIgnoreCase("hover") ? "0xFFC00000" : "0xFF800000");
//            case PEACEFUL:
//                return JsonUtils.getStringVal(textData.get("colorPeaceful" + suffix), suffix.equalsIgnoreCase("hover") ? "0xFF00C000" : "0xFF008000");
//            default:
//                return JsonUtils.getStringVal(textData.get("color" + suffix), suffix.equalsIgnoreCase("hover") ? "0xFF0040A0" : "0xFF000000");
//        }
//    }

    @Override
    public void update(IGui gui, JsonObject data) {
//        CheckBox ckb = this.checkbox.get(CheckBox.class);
//        ckb.update(gui, this.checkbox.data);
        this.setChecked(this.targetType.isTargeted(((IGuiTcuInst<?>) gui).getTurretInst(), this.targetId), false);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        super.render(gui, partTicks, x + margins[0], y + margins[3], mouseX, mouseY, data);
//        this.checkbox.get().render(gui, partTicks, x + this.margins[0], y + this.margins[3], mouseX, mouseY, data);
    }

//    @Override
//    public void handleMouseInput(IGui gui) throws IOException {
//        this.checkbox.get().handleMouseInput(gui);
//    }

//    @Override
//    public boolean mouseClicked(IGui gui, int mouseX, int mouseY, int mouseButton) throws IOException {
//        return this.checkbox.get().mouseClicked(gui, mouseX, mouseY, mouseButton);
//    }
//
//    @Override
//    public void mouseReleased(IGui gui, int mouseX, int mouseY, int state) {
//        this.checkbox.get().mouseReleased(gui, mouseX, mouseY, state);
//    }
//
//    @Override
//    public void mouseClickMove(IGui gui, int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
//        this.checkbox.get().mouseClickMove(gui, mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
//    }
//
//    @Override
//    public void guiClosed(IGui gui) {
//        this.checkbox.get().guiClosed(gui);
//    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return super.getHeight() + this.margins[0] + this.margins[2];
    }

//    @Override
//    public boolean keyTyped(IGui gui, char typedChar, int keyCode) throws IOException {
//        return this.checkbox.get().keyTyped(gui, typedChar, keyCode);
//    }

    public String getName(ITurretInst turretInst) {
        if( this.name == null ) {
            this.name = this.targetType.getName(turretInst, this.targetId);
        }
        return this.name;
    }

    private final class Label
            extends Text
    {
        @Override
        public String getBakedText(IGui gui, JsonObject data) {
            return "";
        }

        @Override
        public String getDynamicText(IGui gui, String originalText) {
            return MiscUtils.defIfNull(TargetNode.this.getName(((IGuiTcuInst<?>) gui).getTurretInst()), "");
        }
    }
}
