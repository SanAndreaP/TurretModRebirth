package de.sanandrew.mods.turretmod.client.gui.element.tcu.target;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Button;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import org.apache.commons.lang3.Range;

public class TargetNode<T>
        implements IGuiElement
{
    private boolean initialized = false;
    private int[] margins;
    private int bttDistance;

    private GuiElementInst buttonOn;
    private GuiElementInst buttonOff;
    private GuiElementInst text;

    private final T             targetId;
    private final TargetType<T> targetType;
    private final int width;

    private int posX;
    private int posY;
    private boolean enabled;

    private String name;

    TargetNode(T id, TargetType<T> type, int width) {
        this.targetId = id;
        this.targetType = type;
        this.width = width;
    }

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        if( !this.initialized ) {
            this.initialized = true;

            GuiDefinition guiDef = gui.getDefinition();
            ITurretInst turretInst = ((IGuiTcuInst<?>) gui).getTurretInst();

            this.margins = JsonUtils.getIntArray(data.get("margins"), new int[] {2, 2, 2, 2}, Range.is(4));
            this.bttDistance = JsonUtils.getIntVal(data.get("buttonToTextDistance"), 2);
            this.name = this.targetType.getName(turretInst, this.targetId);

            JsonObject btnData = MiscUtils.defIfNull(data.get("button"), JsonObject::new).getAsJsonObject();
            JsonObject txtData = MiscUtils.defIfNull(data.get("text"), JsonObject::new).getAsJsonObject();

            this.buttonOn = new GuiElementInst();
            this.buttonOn.element = new Button();
            guiDef.initElement(this.buttonOn);
            JsonUtils.addJsonProperty(this.buttonOn.data, "texture", gui.getDefinition().getTexture(btnData.get("texture")).toString());
            JsonUtils.addJsonProperty(this.buttonOn.data, "size", JsonUtils.getIntArray(btnData.get("size"), new int[] { 8, 8 }, Range.is(2)));
            JsonUtils.addJsonProperty(this.buttonOn.data, "uvSize", JsonUtils.getIntArray(btnData.get("uvSize"), new int[] { 8, 8 }, Range.is(2)));
            JsonUtils.addJsonProperty(this.buttonOn.data, "uvEnabled", JsonUtils.getIntArray(btnData.get("uvEnabledOn"), new int[] { 176, 28 }, Range.is(2)));
            JsonUtils.addJsonProperty(this.buttonOn.data, "uvDisabled", new int[] { -8, -8 });
            JsonUtils.addJsonProperty(this.buttonOn.data, "uvHover", JsonUtils.getIntArray(btnData.get("uvHoverOn"), new int[] { 176, 36 }, Range.is(2)));
            JsonUtils.addJsonProperty(this.buttonOn.data, "buttonFunction", -1);
            this.buttonOn.get().bakeData(gui, this.buttonOn.data);

            this.buttonOff = new GuiElementInst();
            this.buttonOff.element = new Button();
            guiDef.initElement(this.buttonOff);
            this.buttonOn.data.entrySet().forEach(e -> this.buttonOff.data.add(e.getKey(), e.getValue()));
            JsonUtils.addJsonProperty(this.buttonOff.data, "uvEnabled", JsonUtils.getIntArray(btnData.get("uvEnabledOff"), new int[] { 176, 12 }, Range.is(2)));
            JsonUtils.addJsonProperty(this.buttonOff.data, "uvHover", JsonUtils.getIntArray(btnData.get("uvHoverOff"), new int[] { 176, 20 }, Range.is(2)));
            this.buttonOff.get().bakeData(gui, this.buttonOff.data);

            this.text = new GuiElementInst();
            this.text.element = new Text();
            guiDef.initElement(this.text);
            JsonUtils.addJsonProperty(this.text.data, "text", this.name);
            JsonUtils.addJsonProperty(this.text.data, "color", getTextColor(txtData, this.targetType.getType(turretInst, this.targetId)));
            JsonUtils.addJsonProperty(this.text.data, "shadow", JsonUtils.getBoolVal(txtData.get("shadow"), false));
            JsonUtils.addJsonProperty(this.text.data, "justifyRight", JsonUtils.getBoolVal(txtData.get("justifyRight"), false));
            if( txtData.has("font") ) this.text.data.add("font", txtData.get("font"));
            this.text.get().bakeData(gui, this.text.data);
        }
    }

    private static String getTextColor(JsonObject textData, TargetType.EntityType type) {
        switch( type ) {
            case HOSTILE:
                return JsonUtils.getStringVal(textData.get("colorHostile"), "0xFF800000");
            case PEACEFUL:
                return JsonUtils.getStringVal(textData.get("colorPeaceful"), "0xFF008000");
            default:
                return JsonUtils.getStringVal(textData.get("color"), "0xFF000000");
        }
    }

    @Override
    public void update(IGui gui, JsonObject data) {
        this.enabled = this.targetType.isTargeted(((IGuiTcuInst<?>) gui).getTurretInst(), this.targetId);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        this.posX = x;
        this.posY = y;

        Button btn;
        if( this.enabled ) {
            btn = this.buttonOn.get(Button.class);
            this.buttonOn.get().render(gui, partTicks, x + this.margins[3], y + this.margins[0], mouseX, mouseY, this.buttonOn.data);
        } else {
            btn = this.buttonOff.get(Button.class);
            this.buttonOff.get().render(gui, partTicks, x + this.margins[3], y + this.margins[0], mouseX, mouseY, this.buttonOff.data);
        }
        this.text.get().render(gui, partTicks, x + this.margins[3] + btn.data.size[0] + bttDistance, y + this.margins[0], mouseX, mouseY, this.text.data);
    }

    @Override
    public boolean mouseClicked(IGui gui, int mouseX, int mouseY, int mouseButton) {
        if( IGuiElement.isHovering(gui, this.posX, this.posY, mouseX, mouseY, this.width, this.getHeight()) ) {
            ITurretInst turretInst = ((IGuiTcuInst<?>) gui).getTurretInst();
            this.targetType.updateTarget(turretInst, this.targetId, !this.enabled);
            return true;
        }

        return false;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return 8 + this.margins[0] + this.margins[2];
    }

    public String getName() {
        return this.name;
    }
}
