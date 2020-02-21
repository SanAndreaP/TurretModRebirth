package de.sanandrew.mods.turretmod.client.gui.element.tcu;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.client.util.GuiUtils;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TurretName
        extends Text
{
    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "turret_name");

    private long marqueeTime;

    private int textfieldLength;

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        super.bakeData(gui, data, inst);

        this.textfieldLength = JsonUtils.getIntVal(data.get("textfieldLength"), 144);
    }

    @Override
    public void render(IGui gui, float partTicks, int x, int y, int mouseX, int mouseY, JsonObject data) {
        String name = this.getDynamicText(gui, "");
        int strWidth = this.fontRenderer.getStringWidth(name);
        if( strWidth > this.textfieldLength ) {
            long currTime = System.currentTimeMillis();
            if( this.marqueeTime < 1L ) {
                this.marqueeTime = currTime;
            }
            int marquee = -this.textfieldLength + (int) (currTime - this.marqueeTime) / 25;
            if( marquee > strWidth ) {
                this.marqueeTime = currTime;
            }
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GuiUtils.glScissor(gui.getScreenPosX() + x, gui.getScreenPosY() + y, this.textfieldLength, 12);
            this.fontRenderer.drawString(name, x - marquee, y, this.color, false);
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        } else {
            this.fontRenderer.drawString(name, x + (this.textfieldLength - this.fontRenderer.getStringWidth(name)) / 2.0F, y, this.color, false);
        }
    }

    @Override
    public String getBakedText(IGui gui, JsonObject data) {
        return "";
    }

    @Override
    public String getDynamicText(IGui gui, String originalText) {
        return LangUtils.translate(LangUtils.ENTITY_NAME.get(((IGuiTcuInst<?>) gui).getTurretInst().getTurret().getId()));
    }
}
