package de.sanandrew.mods.turretmod.client.gui.element.tcu.shieldcolor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;
import org.lwjgl.input.Mouse;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({"WeakerAccess", "unused"})
public class CheckBox
        extends ElementParent<String>
{
    public static final ResourceLocation ID = new ResourceLocation("check_box");

    public static final String LABEL = "label";

    private boolean                  checked;
    private boolean                  enabled   = true;
    private int[]                    currSize;
    private int[]                    lblPos    = new int[2];
    private int[]                    btnPos    = new int[2];
    private int[]                    lblOffset = new int[2];
    private boolean                  prevMouseDown;
    private GuiElementInst.Justify[] alignment;

    private Function<Boolean, Boolean> checkedChanging = b -> true;
    private Consumer<Boolean>          checkedChanged  = b -> {};

    @Override
    public void buildChildren(IGui gui, JsonObject data, Map<String, GuiElementInst> listToBuild) {
        JsonElement lbl = data.get("label");
        GuiElementInst lblInst = null;
        if( lbl != null ) {
            lblInst = JsonUtils.GSON.fromJson(lbl, GuiElementInst.class).initialize(gui);
        } else {
            lbl = data.get("labelText");
            if( lbl != null ) {
                int[] lblPos = new int[] { 2, 0 };
                JsonObject lblData;

                if( lbl.isJsonPrimitive() ) {
                    JsonObject colors = new JsonObject();
                    colors.addProperty("default", "0xFF000000");
                    colors.addProperty("hover", "0xFF0040A0");
                    colors.addProperty("disabled", "0xFF404040");

                    lblData = new JsonObject();
                    JsonUtils.addJsonProperty(lblData, "text", lbl.getAsString());
                    lblData.add("color", colors);
                } else {
                    lblData = lbl.getAsJsonObject();
                    lblPos = JsonUtils.getIntArray(lblData.get("offset"), new int[] { 2, 0 }, Range.is(2));
                }
                lblInst = new GuiElementInst(lblPos, this.getLabel(gui, lblData), lblData).initialize(gui);
            }
        }

        if( lblInst != null ) {
            this.lblOffset = lblInst.pos;
            lblInst.alignment = new String[] { "left", "center" };
            listToBuild.put(LABEL, lblInst);
        }

        GuiElementInst tx = new GuiElementInst(new Texture(), JsonUtils.deepCopy(data)).initialize(gui);
        listToBuild.put(ButtonTextures.DEFAULT.n, tx);
        tx.setVisible(false);

        tx = new GuiElementInst(new Texture(), JsonUtils.deepCopy(data)).initialize(gui);
        tx.data.add("uv", data.get("uvHover"));
        listToBuild.put(ButtonTextures.HOVER.n, tx);
        tx.setVisible(false);

        tx = new GuiElementInst(new Texture(), JsonUtils.deepCopy(data)).initialize(gui);
        tx.data.add("uv", data.get("uvChecked"));
        listToBuild.put(ButtonTextures.CHECKED.n, tx);
        tx.setVisible(false);

        tx = new GuiElementInst(new Texture(), JsonUtils.deepCopy(data)).initialize(gui);
        tx.data.add("uv", data.get("uvCheckedHover"));
        listToBuild.put(ButtonTextures.CHECKED_HOVER.n, tx);
        tx.setVisible(false);

        tx = new GuiElementInst(new Texture(), JsonUtils.deepCopy(data)).initialize(gui);
        tx.data.add("uv", data.get("uvDisabled"));
        listToBuild.put(ButtonTextures.DISABLED.n, tx);
        tx.setVisible(false);
    }

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        this.alignment = new GuiElementInst.Justify[] { inst.getAlignmentH(), inst.getAlignmentV() };

        int[] size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
        int[] uv = JsonUtils.getIntArray(data.get("uv"), Range.is(2));
        JsonUtils.addDefaultJsonProperty(data, "uvHover", new int[] { uv[0], uv[1] + size[1] });
        JsonUtils.addDefaultJsonProperty(data, "uvChecked", new int[] { uv[0], uv[1] + size[1] * 2 });
        JsonUtils.addDefaultJsonProperty(data, "uvCheckedHover", new int[] { uv[0], uv[1] + size[1] * 3 });
        JsonUtils.addDefaultJsonProperty(data, "uvDisabled", new int[] { uv[0], uv[1] + size[1] * 4 });

        super.bakeData(gui, data, inst);

        GuiElementInst lbl = this.getChild(LABEL);
        if( lbl != null ) {
            lbl.alignment = inst.alignment;
        }

        this.calcSize();
    }

    @SuppressWarnings("unchecked")
    public <T extends Text> T getLabel(IGui gui, JsonObject data) {
        return (T) new Text();
    }

    public void setOnCheckedChanging(Function<Boolean, Boolean> cchg) {
        this.checkedChanging = cchg;
    }

    public void setOnCheckedChanged(Consumer<Boolean> cchg) {
        this.checkedChanged = cchg;
    }

    public void setChecked(boolean checked, boolean userInput) {
        if( this.checkedChanging.apply(checked) ) {
            this.checked = checked;
            this.checkedChanged.accept(userInput);
        }
    }

    public boolean isChecked() {
        return this.checked;
    }

    protected void calcSize() {
        GuiElementInst label = this.getChild(LABEL);

        this.currSize = new int[2];
        this.btnPos = new int[2];
        this.lblPos = new int[2];

        int[] lblSize = { 0, 0 };
        int[] cLblPos = this.lblOffset;
        IGuiElement e = this.getChild(ButtonTextures.DEFAULT.n).get();
        int[] buttonSize = { e.getWidth(), e.getHeight() };

        if( label != null ) {
            lblSize = new int[] { label.get().getWidth(), label.get().getHeight() };
        }

        switch( this.alignment[0] ) {
            case CENTER:
                this.btnPos[0] = Math.max(0, (lblSize[0] + cLblPos[0] - buttonSize[0]) / 2);
                this.lblPos[0] = Math.max(buttonSize[0], lblSize[0]) / 2 + cLblPos[0];
                this.currSize[0] = Math.max(buttonSize[0], lblSize[0] + cLblPos[0]);
                break;
            case RIGHT:
                this.btnPos[0] = lblSize[0] + cLblPos[0];
                this.lblPos[0] = lblSize[0];
                this.currSize[0] = this.btnPos[0] + buttonSize[0];
                break;
            default:
                this.btnPos[0] = 0;
                this.lblPos[0] = buttonSize[0] + cLblPos[0];
                this.currSize[0] = this.lblPos[0] + lblSize[0];
                break;
        }

        if( this.alignment[0] == GuiElementInst.Justify.CENTER ) {
            switch( this.alignment[1] ) {
                case TOP:
                    this.btnPos[1] = 0;
                    this.lblPos[1] = buttonSize[1] + cLblPos[1];
                    this.currSize[1] = this.lblPos[1] + lblSize[1];
                    break;
                case BOTTOM:
                    this.btnPos[1] = lblSize[1] + cLblPos[1];
                    this.lblPos[1] = 0;
                    this.currSize[1] = this.btnPos[1] + buttonSize[1];
                    break;
            }
        } else {
            switch( this.alignment[1] ) {
                case TOP:
                    this.btnPos[1] = 0;
                    this.lblPos[1] = cLblPos[1];
                    break;
                case CENTER:
                    this.btnPos[1] = Math.max(0, (lblSize[1] + cLblPos[1] - buttonSize[1]) / 2);
                    this.lblPos[1] = Math.max(0, (buttonSize[1] - lblSize[1])) / 2 + cLblPos[1];
                    break;
                case BOTTOM:
                    this.btnPos[1] = Math.max(0, lblSize[1] + cLblPos[1] - buttonSize[1]);
                    this.lblPos[1] = Math.max(0, buttonSize[1] - lblSize[1] + cLblPos[1]);
                    break;
            }
            this.currSize[1] = Math.max(this.btnPos[1] + buttonSize[1], this.lblPos[1] + lblSize[1]);
        }
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        GuiElementInst label = this.getChild(LABEL);
        this.calcSize();

        boolean hovering = IGuiElement.isHovering(gui, x, y, mouseX, mouseY, this.currSize[0], this.currSize[1]);

        String txtColor;
        Arrays.asList(ButtonTextures.VALUES).forEach(b -> this.getChild(b.n).setVisible(false));

        GuiElementInst currBtn;
        if( this.enabled ) {
            if( Mouse.isButtonDown(0) ) {
                this.prevMouseDown = true;
            } else {
                if( this.prevMouseDown && hovering ) {
                    this.setChecked(!this.checked, true);
                }

                this.prevMouseDown = false;
            }

            if( hovering ) {
                currBtn = this.getChild((this.checked ? ButtonTextures.CHECKED_HOVER : ButtonTextures.HOVER).n);
                txtColor = "hover";
            } else {
                currBtn = this.getChild((this.checked ? ButtonTextures.CHECKED : ButtonTextures.DEFAULT).n);
                txtColor = "default";
            }
        } else {
            currBtn = this.getChild(ButtonTextures.DISABLED.n);
            txtColor = "disabled";
        }

        currBtn.setVisible(true);
        currBtn.pos = this.btnPos;

        if( label != null ) {
            IGuiElement e = label.get();
            if( e instanceof Text ) {
                ((Text) e).setColor(txtColor);
            }

            label.pos = this.lblPos;
            switch( this.alignment[1] ) {
                case CENTER:
                    label.pos[1] += label.get().getHeight() / 2;
                    break;
                case BOTTOM:
                    label.pos[1] += label.get().getHeight();
                    break;
            }
        }

        super.render(gui, partTicks, x, y, mouseX, mouseY, data);
    }

    @Override
    public int getWidth() {
        return this.currSize[0];
    }

    @Override
    public int getHeight() {
        return this.currSize[1];
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public enum ButtonTextures
    {
        DEFAULT,
        HOVER,
        CHECKED,
        CHECKED_HOVER,
        DISABLED;

        private final String n = name();

        private static final ButtonTextures[] VALUES = values();
    }
}
