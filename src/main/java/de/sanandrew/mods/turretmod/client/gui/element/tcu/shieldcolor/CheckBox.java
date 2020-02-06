package de.sanandrew.mods.turretmod.client.gui.element.tcu.shieldcolor;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;
import org.lwjgl.input.Mouse;

import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({"WeakerAccess", "unused"})
public class CheckBox
        implements IGuiElement
{
    public static final ResourceLocation ID = new ResourceLocation("check_box");

    public ResourceLocation texture;
    public int              fixedWidth;
    public int[]            size;
    public int[]            textureSize;
    public int[]            uv;
    public int[]            uvHover;
    public int[]            uvChecked;
    public int[]            uvCheckedHover;
    public int[]            uvDisabled;
    public boolean          ckbRight;

    public GuiElementInst label;
    public int labelColor;
    public int labelColorHover;
    public int labelColorDisabled;

    private boolean checked;
    private boolean visible = true;
    private boolean enabled = true;
    private int currWidth;
    private int currHeight;
    private boolean prevMouseDown;

    private Function<Boolean, Boolean> checkedChanging = b -> true;
    private Consumer<Boolean>          checkedChanged  = b -> {};

    @Override
    public void bakeData(IGui gui, JsonObject data) {
        this.texture = gui.getDefinition().getTexture(data.get("texture"));
        this.size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
        this.uv = JsonUtils.getIntArray(data.get("uv"), Range.is(2));
        this.uvHover = JsonUtils.getIntArray(data.get("uvHover"), new int[] { this.uv[0], this.uv[1] + this.size[1] }, Range.is(2));
        this.uvChecked = JsonUtils.getIntArray(data.get("uvChecked"), new int[] { this.uv[0], this.uv[1] + this.size[1] * 2 }, Range.is(2));
        this.uvCheckedHover = JsonUtils.getIntArray(data.get("uvCheckedHover"), new int[] { this.uv[0], this.uv[1] + this.size[1] * 3 }, Range.is(2));
        this.uvDisabled = JsonUtils.getIntArray(data.get("uvDisabled"), new int[] { this.uv[0], this.uv[1] + this.size[1] * 4 }, Range.is(2));
        this.textureSize = JsonUtils.getIntArray(data.get("textureSize"), new int[] { 256, 256 }, Range.is(2));
        this.fixedWidth = JsonUtils.getIntVal(data.get("fixedWidth"), 0);
        this.ckbRight = JsonUtils.getBoolVal(data.get("alignRight"), false);

        JsonObject lbl = MiscUtils.defIfNull(data.getAsJsonObject("label"), JsonObject::new);
        this.labelColorHover = MiscUtils.hexToInt(JsonUtils.getStringVal(lbl.get("colorHover"), "0xFF0040A0"));
        this.labelColorDisabled = MiscUtils.hexToInt(JsonUtils.getStringVal(lbl.get("colorDisabled"), "0xFF404040"));

        this.label = new GuiElementInst();
        this.label.pos = JsonUtils.getIntArray(lbl.get("offset"), new int[] {2, 0}, Range.is(2));
        this.label.element = this.getLabelElement(gui, lbl);
        this.label.data = lbl;
        gui.getDefinition().initElement(this.label);
        this.label.get().bakeData(gui, this.label.data);

        this.labelColor = this.label.get(Text.class).data.color;

        this.currWidth = this.fixedWidth > 0 ? this.fixedWidth : Math.max(this.size[0], this.size[0] + this.label.pos[0] + this.label.get().getWidth());
        this.currHeight = Math.max(this.size[1], this.label.pos[1] + this.label.get().getHeight());
    }

    @SuppressWarnings("unchecked")
    public <T extends Text> T getLabelElement(IGui gui, JsonObject data) {
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

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        this.currWidth = this.fixedWidth > 0 ? this.fixedWidth : Math.max(this.size[0], this.size[0] + this.label.pos[0] + this.label.get().getWidth());
        this.currHeight = Math.max(this.size[1], this.label.pos[1] + this.label.get().getHeight());

        boolean hovering = IGuiElement.isHovering(gui, x, y, mouseX, mouseY, this.currWidth, this.currHeight);

        int[] uv = new int[2];
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
                uv = this.checked ? this.uvCheckedHover : this.uvHover;
                this.label.get(Text.class).data.color = this.labelColorHover;
            } else {
                uv = this.checked ? this.uvChecked : this.uv;
                this.label.get(Text.class).data.color = this.labelColor;
            }
        } else {
            this.uv = this.uvDisabled;
            this.label.get(Text.class).data.color = this.labelColorDisabled;
        }

        gui.get().mc.renderEngine.bindTexture(this.texture);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.drawModalRectWithCustomSizedTexture(x, y, uv[0], uv[1], this.size[0], this.size[1], this.textureSize[0], this.textureSize[1]);
        this.label.get().render(gui, partTicks, x + this.size[0] + this.label.pos[0], y + this.label.pos[1], mouseX, mouseY, this.label.data);
    }

    @Override
    public int getWidth() {
        return this.currWidth;
    }

    @Override
    public int getHeight() {
        return this.currHeight;
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
