package de.sanandrew.mods.turretmod.client.gui.element.tcu.shieldcolor;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.IGuiElement;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ElementParent;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Range;
import org.lwjgl.input.Mouse;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings({"WeakerAccess", "unused"})
public class CheckBox
        extends ElementParent<String>
{
    public static final ResourceLocation ID = new ResourceLocation("check_box");

    public static final String LABEL = "label";
    public enum BtnTextures { DEFAULT, HOVER, CHECKED, CHECKED_HOVER, DISABLED; private final String n = name(); };

    public boolean          ckbRight;

    private boolean checked;
    private boolean enabled = true;
    private int currWidth;
    private int currHeight;
    private boolean prevMouseDown;

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
                    lblPos = JsonUtils.getIntArray(lblData.get("offset"), new int[] {2, 0}, Range.is(2));
                }
                lblInst = new GuiElementInst(lblPos, this.getLabelElement(gui, lblData), lblData).initialize(gui);
            }
        }

        if( lblInst != null ) {
            lblInst.alignment = new String[] { "left", "center" };

            listToBuild.put(LABEL, lblInst);
        }

        GuiElementInst tx = new GuiElementInst(new Texture(), JsonUtils.deepCopy(data)).initialize(gui);
        listToBuild.put(BtnTextures.DEFAULT.n, tx);
        tx = new GuiElementInst(new Texture(), JsonUtils.deepCopy(data)).initialize(gui);
        tx.data.add("uv", data.get("uvHover"));
        listToBuild.put(BtnTextures.HOVER.n, tx);
        tx = new GuiElementInst(new Texture(), JsonUtils.deepCopy(data)).initialize(gui);
        tx.data.add("uv", data.get("uvChecked"));
        listToBuild.put(BtnTextures.CHECKED.n, tx);
        tx = new GuiElementInst(new Texture(), JsonUtils.deepCopy(data)).initialize(gui);
        tx.data.add("uv", data.get("uvCheckedHover"));
        listToBuild.put(BtnTextures.CHECKED_HOVER.n, tx);
        tx = new GuiElementInst(new Texture(), JsonUtils.deepCopy(data)).initialize(gui);
        tx.data.add("uv", data.get("uvDisabled"));
        listToBuild.put(BtnTextures.DISABLED.n, tx);
    }

    /*
    checkbox align: left [top center bottom]; right [top center bottom] -> label next to checkbox
    checkbox align: center [top] -> label below checkbox
    checkbox align: center [bottom] -> label above checkbox

    label align:
        CB_L: left align, text depends
        CB_R: right align, text depends
        CB_C: center align, text depends

        HL: cx = 0,                           lx = cw + lox,              w = lx + lw
        HC: cx = max(0, (lw + lox - cw) / 2), lx = max(cw, lw) / 2 + lox, w = max(cw, lw + lox)
        HR: cx = lw + lox,                    lx = lw,                    w = cx + cw

        // no HC
        VT: cy = 0,                           ly = loy,                        h = max(cy + ch, ly + lh)
        VC: cy = max(0, (lh + loy - ch) / 2), ly = max(0, (ch - lh) / 2 + loy, h = max(cy + ch, ly + lh)
        VB: cy = max(0, lh + loy - ch),       ly = max(0, ch - lh + loy),      h = max(cy + ch, ly + lh)
        // HC
        VT: cy = 0,                           ly = ch + loy,                   h = ly + lh
        VB: cy = lh + loy,                    ly = 0,                          h = ly + lh

        #///
         //
         /
        CB_L_T: cx = 0                                lx = cw + lox                        w = lx + lw
                cy = 0                                ly = loy                             h = max(cy + ch, ly + lh)
         ///
        #//
         /
        CB_L_C: cx = 0                                lx = cw + lox                        w = lx + lw
                cy = max(0, (lh + loy - ch) / 2)      ly = max(0, (ch - lh) / 2 + loy      h = max(cy + ch, ly + lh)
         ///
         //
        #/
        CB_L_B: cx = 0                                lx = cw + lox                        w = lx + lw
                cy = max(0, lh + loy - ch)            ly = max(0, ch - lh + loy)           h = max(cy + ch, ly + lh)

        \\\#
         \\
          \
        CB_R_T: cx = lw + lox                         lx = lw                              w = cx + cw
                cy = 0                                ly = loy                             h = max(cy + ch, ly + lh)
        \\\
         \\#
          \
        CB_R_C: cx = lw + lox                         lx = lw                              w = cx + cw
                cy = max(0, (lh + loy - ch) / 2)      ly = max(0, (ch - lh) / 2 + loy      h = max(cy + ch, ly + lh)
        \\\
         \\
          \#
        CB_R_B: cx = lw + lox                         lx = lw                              w = cx + cw
                cy = max(0, lh + loy - ch)            ly = max(0, ch - lh + loy)           h = max(cy + ch, ly + lh)

          #
        |||||
         |||
        CB_C_T: cx = max(0, (lw + lox - cw) / 2)      lx = max(cw, lw) / 2 + lox           w = max(cw, lw + lox)
                cy = 0                                ly = ch + loy                        h = ly + lh
        |||||
         |||
          #
        CB_C_B: cx = max(0, (lw + lox - cw) / 2)      lx = max(cw, lw) / 2 + lox           w = max(cw, lw + lox)
                cy = lh + loy                         ly = 0                               h = ly + lh

     */

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
//        this.texture = gui.getDefinition().getTexture(data.get("texture"));
        int[] size = JsonUtils.getIntArray(data.get("size"), Range.is(2));
        int[] uv = JsonUtils.getIntArray(data.get("uv"), Range.is(2));
        JsonUtils.addDefaultJsonProperty(data, "uvHover", new int[] { uv[0], uv[1] + size[1] });
        JsonUtils.addDefaultJsonProperty(data, "uvChecked", new int[] { uv[0], uv[1] + size[1] * 2 });
        JsonUtils.addDefaultJsonProperty(data, "uvCheckedHover", new int[] { uv[0], uv[1] + size[1] * 3 });
        JsonUtils.addDefaultJsonProperty(data, "uvDisabled", new int[] { uv[0], uv[1] + size[1] * 4 });

        this.ckbRight = JsonUtils.getBoolVal(data.get("alignRight"), false);

//        JsonObject lbl = MiscUtils.defIfNull(data.getAsJsonObject("label"), JsonObject::new);
//        this.labelColorHover = MiscUtils.hexToInt(JsonUtils.getStringVal(lbl.get("colorHover"), "0xFF0040A0"));
//        this.labelColorDisabled = MiscUtils.hexToInt(JsonUtils.getStringVal(lbl.get("colorDisabled"), "0xFF404040"));
//
//        this.label = new GuiElementInst();
//        this.label.pos = JsonUtils.getIntArray(lbl.get("offset"), new int[] {2, 0}, Range.is(2));
//        this.label.element = this.getLabelElement(gui, lbl);
//        this.label.data = lbl;
//        gui.getDefinition().initElement(this.label);
//        this.label.get().bakeData(gui, this.label.data);
//
//        this.labelColor = this.label.get(Text.class).data.color;
        super.bakeData(gui, data, inst);

        GuiElementInst label = this.getChild(LABEL);
        this.calcWidth(label);
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

    protected void calcWidth(GuiElementInst label) {
        IGuiElement e = this.getChild(BtnTextures.DEFAULT.n).get();
        int[] size = { e.getWidth(), e.getHeight() };
        if( label != null ) {
            this.currWidth = Math.max(size[0], size[0] + label.pos[0] + label.get().getWidth());
            this.currHeight = Math.max(size[1], label.pos[1] + label.get().getHeight());
        } else {
            this.currWidth = size[0];
            this.currHeight = size[1];
        }
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        GuiElementInst label = this.getChild(LABEL);
        this.calcWidth(label);

        boolean hovering = IGuiElement.isHovering(gui, x, y, mouseX, mouseY, this.currWidth, this.currHeight);

        GuiElementInst drawnBtn = null;
        String txtColor = "";
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
                drawnBtn = this.getChild((this.checked ? BtnTextures.CHECKED_HOVER : BtnTextures.HOVER).n);
                txtColor = "hover";
            } else {
                drawnBtn = this.getChild((this.checked ? BtnTextures.CHECKED : BtnTextures.DEFAULT).n);
                txtColor = "default";
            }
        } else {
            drawnBtn = this.getChild(BtnTextures.DISABLED.n);
            txtColor = "disabled";
        }

        int posX = x;
        if( label != null ) {
            IGuiElement e = label.get();
            if( e instanceof Text ) {
                ((Text) e).setColor(txtColor);
            }

            GuiDefinition.renderElement(gui, this.ckbRight ? x : x + label.pos[0], y + label.pos[1], mouseX, mouseY, partTicks, label);
            posX = this.ckbRight ? x + e.getWidth() + label.pos[0] : x;
        }

//        gui.get().mc.renderEngine.bindTexture(this.texture);
//        GlStateManager.enableBlend();
//        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//        Gui.drawModalRectWithCustomSizedTexture(posX, y, uv[0], uv[1], this.size[0], this.size[1], this.textureSize[0], this.textureSize[1]);
        GuiDefinition.renderElement(gui, this.ckbRight ? x + drawnBtn.get().getWidth() : x, y, mouseX, mouseY, partTicks, drawnBtn);
    }

    @Override
    public int getWidth() {
        return this.currWidth;
    }

    @Override
    public int getHeight() {
        return this.currHeight;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }
}
