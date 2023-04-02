package de.sanandrew.mods.turretmod.client.gui.element.tcu.target;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.shieldcolor.CheckBox;
import de.sanandrew.mods.turretmod.client.gui.tcu.TargetType;
import org.apache.commons.lang3.Range;

import java.util.Locale;

public class TargetNode<T>
        extends CheckBox
{
    private int[] margins;

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
        this.margins = JsonUtils.getIntArray(data.get("margins"), new int[] {2, 2, 0, 2}, Range.is(4));

        JsonObject lblData = new JsonObject();

        data.add("labelText", lblData);
        data.remove("label");

        super.bakeData(gui, data, inst);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends Text> U getLabel(IGui gui, JsonObject data) {
        return (U) new Label();
    }

    private static String uppercaseFirst(String input) {
        return input.substring(0, 0).toUpperCase(Locale.ROOT) + input.substring(1);
    }

    private static JsonObject getTextColor(JsonObject textData, TargetType.EntityType type) {
        String[] suffixes = {"default", "hover", "disabled"};


        JsonObject colors = new JsonObject();
        for( String suffix : suffixes ) {
            switch( type ) {
                case HOSTILE:
                    colors.addProperty(suffix, JsonUtils.getStringVal(textData.get("hostile" + uppercaseFirst(suffix)),
                                                                      suffix.equalsIgnoreCase("hover") ? "0xFFC00000" : "0xFF800000"));
                    break;
                case PEACEFUL:
                    colors.addProperty(suffix, JsonUtils.getStringVal(textData.get("peaceful" + uppercaseFirst(suffix)),
                                                                      suffix.equalsIgnoreCase("hover") ? "0xFF00C000" : "0xFF008000"));
                    break;
                default:
                    colors.addProperty(suffix, JsonUtils.getStringVal(textData.get("default" + uppercaseFirst(suffix)),
                                                                      suffix.equalsIgnoreCase("hover") ? "0xFF00C000" : "0xFF008000"));
            }
        }

        return colors;
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        this.setChecked(this.targetType.isTargeted(((IGuiTcuInst<?>) gui).getTurretInst(), this.targetId), false);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        super.render(gui, partTicks, x + margins[0], y + margins[3], mouseX, mouseY, data);
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return super.getHeight() + this.margins[0] + this.margins[2];
    }

    public String getName() {
        if( this.name == null ) {
            this.name = this.targetType.getName(this.targetId);
        }
        return this.name;
    }

    private final class Label
            extends Text
    {
        @Override
        public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
            data.add("color", getTextColor(data, TargetNode.this.targetType.getType(TargetNode.this.targetId)));

            super.bakeData(gui, data, inst);
        }

        @Override
        public String getBakedText(IGui gui, JsonObject data) {
            return "";
        }

        @Override
        public String getDynamicText(IGui gui, String originalText) {
            return MiscUtils.defIfNull(TargetNode.this.getName(), "");
        }
    }
}
