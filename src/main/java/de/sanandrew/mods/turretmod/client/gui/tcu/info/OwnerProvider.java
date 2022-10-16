package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.ButtonSL;
import de.sanandrew.mods.sanlib.lib.client.gui.element.TextField;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Tooltip;
import de.sanandrew.mods.sanlib.lib.function.TriConsumer;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.sanlib.lib.util.UuidUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuScreen;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.info.PlayerIconInfo;
import de.sanandrew.mods.turretmod.init.Lang;
import de.sanandrew.mods.turretmod.network.TurretPlayerActionPacket;
import de.sanandrew.mods.turretmod.world.PlayerList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.Range;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.BiFunction;

public class OwnerProvider
        extends TextProvider
{
    private static final Style OWNER_NOT_FOUND_STYLE = Style.EMPTY.withColor(TextFormatting.RED);
    private static final Style OWNER_SUGGESTION_STYLE = Style.EMPTY.withColor(TextFormatting.DARK_GRAY);

    private GuiElementInst claimBtn;
    private GuiElementInst changeBtn;
    private GuiElementInst changeTxt;
    private GuiElementInst acceptBtn;
    private GuiElementInst cancelBtn;

    private GuiElementInst changeTxtTooltip;
    private GuiElementInst claimBtnTooltip;
    private GuiElementInst changeBtnTooltip;
    private GuiElementInst acceptBtnTooltip;
    private GuiElementInst cancelBtnTooltip;

    @Nonnull
    @Override
    public String getName() {
        return "owner";
    }

    @Override
    protected int[] getDefaultIconUV() {
        return new int[] { 120, 0 };
    }

    @Override
    protected String getDefaultTooltipText() {
        return Lang.TCU_TEXT.get("info.player.tooltip");
    }

    @Override
    public void setup(IGui gui, ITurretEntity turret, int w, int h) {
        // this needs to go before the super call, since the supplier gets called on setup
        this.icon.get(PlayerIconInfo.class).setPlayerNameSupplier(() -> turret.getOwnerName().getString());

        super.setup(gui, turret, w, h);

        MiscUtils.accept(this.changeTxt.get(TextField.class), tf -> {
            tf.setup(gui, this.changeTxt);
            tf.setMaxStringLength(260);
            tf.setFormatter(OwnerProvider::ownerFormatter);
            tf.setResponder(this::ownerResponder);
        });
        this.changeTxt.setVisible(false);

        this.claimBtn.get().setup(gui, this.claimBtn);
        this.changeBtn.get().setup(gui, this.changeBtn);
        this.acceptBtn.get().setup(gui, this.acceptBtn);
        this.cancelBtn.get().setup(gui, this.cancelBtn);

        this.changeTxtTooltip.get().setup(gui, this.changeTxtTooltip);
        this.claimBtnTooltip.get().setup(gui, this.claimBtnTooltip);
        this.changeBtnTooltip.get().setup(gui, this.changeBtnTooltip);
        this.acceptBtnTooltip.get().setup(gui, this.acceptBtnTooltip);
        this.cancelBtnTooltip.get().setup(gui, this.cancelBtnTooltip);

        this.changeBtn.get(ButtonSL.class).setFunction(b -> {
            this.changeTxt.setVisible(true);
            this.changeTxt.get(TextField.class).setText(turret.getOwnerName().getString());
        });
        this.acceptBtn.get(ButtonSL.class).setFunction(b -> {
            String txt = this.changeTxt.get(TextField.class).getText();
            if( Strings.isNullOrEmpty(txt) ) {
                TurretPlayerActionPacket.setOwner(turret, UuidUtils.EMPTY_UUID);
                this.changeTxt.setVisible(false);
            } else {
                UUID newOwner = PlayerList.getPlayerUUID(txt);
                if( !UuidUtils.EMPTY_UUID.equals(newOwner) && !UuidUtils.areUuidsEqual(turret.getOwnerId(), newOwner) ) {
                    TurretPlayerActionPacket.setOwner(turret, newOwner);
                    gui.get().onClose();
                }
            }
        });
        this.cancelBtn.get(ButtonSL.class).setFunction(b -> this.changeTxt.setVisible(false));
        this.claimBtn.get(ButtonSL.class).setFunction(b -> MiscUtils.accept(Minecraft.getInstance().player, p -> turret.changeOwner(p.getUUID())));

        this.updateButtonVisibility(turret);
    }

    private static IReorderingProcessor ownerFormatter(String s, Integer i) {
        Style txtStyle = Style.EMPTY;
        if( !Strings.isNullOrEmpty(s) && !PlayerList.playerNameExists(s) ) {
            txtStyle = OWNER_NOT_FOUND_STYLE;
        }
        return IReorderingProcessor.forward(s, txtStyle);
    }

    private static String getPartialSuggestion(String s) {
        if( !Strings.isNullOrEmpty(s) ) {
            String sugg = PlayerList.getPlayerNameSuggestion(s);
            if( !Strings.isNullOrEmpty(sugg) && !sugg.equals(s) ) {
                return sugg.substring(s.length());
            }
        }

        return "";
    }

    private void ownerResponder(String s) {
        this.changeTxt.get(TextField.class).setSuggestion(getPartialSuggestion(s));
    }

    @Override
    public void tick(IGui gui, ITurretEntity turret) {
        super.tick(gui, turret);

        this.updateButtonVisibility(turret);

        this.changeTxt.get().tick(gui, this.claimBtn);
        this.claimBtn.get().tick(gui, this.claimBtn);
        this.changeBtn.get().tick(gui, this.changeBtn);
        this.acceptBtn.get().tick(gui, this.acceptBtn);
        this.cancelBtn.get().tick(gui, this.cancelBtn);

        this.changeTxtTooltip.get().tick(gui, this.changeTxtTooltip);
        this.claimBtnTooltip.get().tick(gui, this.claimBtnTooltip);
        this.changeBtnTooltip.get().tick(gui, this.changeBtnTooltip);
        this.acceptBtnTooltip.get().tick(gui, this.acceptBtnTooltip);
        this.cancelBtnTooltip.get().tick(gui, this.cancelBtnTooltip);
    }

    @Override
    public boolean mouseClicked(IGui gui, double mouseX, double mouseY, int button) {
        return this.changeTxt.get().mouseClicked(gui, mouseX, mouseY, button)
               || (this.claimBtn.isVisible() && this.claimBtn.get().mouseClicked(gui, mouseX, mouseY, button))
               || (this.changeBtn.isVisible() && this.changeBtn.get().mouseClicked(gui, mouseX, mouseY, button))
               || (this.acceptBtn.isVisible() && this.acceptBtn.get().mouseClicked(gui, mouseX, mouseY, button))
               || (this.cancelBtn.isVisible() && this.cancelBtn.get().mouseClicked(gui, mouseX, mouseY, button));
    }

    @Override
    public boolean keyPressed(IGui gui, int keyCode, int scanCode, int modifiers) {
        if( this.changeTxt.isVisible() ) {
            TextField tf = this.changeTxt.get(TextField.class);
            if( tf.isFocused() && keyCode == GLFW.GLFW_KEY_TAB ) {
                String sugg = getPartialSuggestion(tf.getText());
                if( !Strings.isNullOrEmpty(sugg) ) {
                    tf.setText(tf.getText() + sugg);
                    return true;
                }
            }

            return tf.keyPressed(gui, keyCode, scanCode, modifiers) || tf.canConsumeInput();
        }

        return false;
    }

    @Override
    public boolean charTyped(IGui gui, char typedChar, int keyCode) {
        return this.changeTxt.isVisible() && MiscUtils.apply(this.changeTxt.get(TextField.class), tf -> tf.charTyped(gui, typedChar, keyCode) || tf.canConsumeInput());
    }

    @Override
    public void renderContent(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        super.renderContent(gui, turret, stack, partTicks, x, y, mouseX, mouseY, maxWidth, maxHeight);

        GuiDefinition.renderElement(gui, stack, x + this.changeTxt.pos[0], y + this.changeTxt.pos[1], mouseX, mouseY, partTicks, this.changeTxt);
        GuiDefinition.renderElement(gui, stack, x + this.claimBtn.pos[0] + maxWidth - this.claimBtn.get().getWidth(), y + this.claimBtn.pos[1], mouseX, mouseY, partTicks, this.claimBtn);
        GuiDefinition.renderElement(gui, stack, x + this.changeBtn.pos[0] + maxWidth - this.changeBtn.get().getWidth(), y + this.changeBtn.pos[1], mouseX, mouseY, partTicks, this.changeBtn);
        GuiDefinition.renderElement(gui, stack, x + this.acceptBtn.pos[0] + maxWidth - this.acceptBtn.get().getWidth() - this.cancelBtn.get().getWidth() - 2, y + this.acceptBtn.pos[1] + 2, mouseX, mouseY, partTicks, this.acceptBtn);
        GuiDefinition.renderElement(gui, stack, x + this.cancelBtn.pos[0] + maxWidth - this.cancelBtn.get().getWidth() - 1, y + this.cancelBtn.pos[1] + 2, mouseX, mouseY, partTicks, this.cancelBtn);
    }

    @Override
    public void renderOutside(IGui gui, ITurretEntity turret, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        super.renderOutside(gui, turret, stack, partTicks, x, y, mouseX, mouseY, maxWidth, maxHeight);

        TriConsumer<GuiElementInst, Integer, Integer> renderTtipFunc = (elem, xe, ye) ->
            GuiDefinition.renderElement(gui, stack, xe + elem.pos[0], ye + elem.pos[1], mouseX, mouseY, partTicks, elem);

        renderTtipFunc.accept(this.changeTxtTooltip, x + this.changeTxt.pos[0], y + this.changeTxt.pos[1]);
        renderTtipFunc.accept(this.claimBtnTooltip, x + this.claimBtn.pos[0] + maxWidth - this.claimBtn.get().getWidth(), y + this.claimBtn.pos[1]);
        renderTtipFunc.accept(this.changeBtnTooltip, x + this.changeBtn.pos[0] + maxWidth - this.changeBtn.get().getWidth(), y + this.changeBtn.pos[1]);
        renderTtipFunc.accept(this.acceptBtnTooltip, x + this.acceptBtn.pos[0] + maxWidth - this.acceptBtn.get().getWidth() - this.cancelBtn.get().getWidth() - 2, y + this.acceptBtn.pos[1] + 2);
        renderTtipFunc.accept(this.cancelBtnTooltip, x + this.cancelBtn.pos[0] + maxWidth - this.cancelBtn.get().getWidth() - 1, y + this.cancelBtn.pos[1] + 2);
    }

    private void updateButtonVisibility(ITurretEntity turret) {
        boolean hasOwner = turret.hasOwner();
        boolean showEdit = this.changeTxt.isVisible();
        this.claimBtn.setVisible(!hasOwner && !showEdit);
        this.changeBtn.setVisible(hasOwner && !showEdit);

        this.acceptBtn.setVisible(showEdit);
        this.cancelBtn.setVisible(showEdit);
    }

    @Nonnull
    @Override
    public BiFunction<IGui, ITextComponent, ITextComponent> getTextFunction(IGui gui, ITurretEntity turret) {
        return (g, o) -> turret.getOwnerName();
    }

    @Override
    public void loadJson(IGui gui, JsonObject data, int w, int h) {
        super.loadJson(gui, data, w, h);

        this.claimBtn = loadBtn(gui, data, "claimButton", "claim_button", 16, 16, 222, 16);
        this.changeBtn = loadBtn(gui, data, "changeButton", "change_button", 16, 16, 238, 16);
        this.acceptBtn = loadBtn(gui, data, "acceptButton", "accept_button", 12, 12, 204, 66);
        this.cancelBtn = loadBtn(gui, data, "cancelButton", "cancel_button", 12, 12, 216, 66);

        JsonObject tfData = MiscUtils.get(data.getAsJsonObject("changeTextfield"), JsonObject::new);
        JsonUtils.addDefaultJsonProperty(tfData, "size", new int[] { w - 23 - this.acceptBtn.get().getWidth() - this.cancelBtn.get().getWidth(), 10 });
        JsonUtils.addDefaultJsonProperty(tfData, "placeholderText", Lang.TCU_TEXT.get("info.no_owner"));
        TextField tfElem = TextField.Builder.fromJson(gui, tfData);
        this.changeTxt = new GuiElementInst(ITcuScreen.getOffset(tfData, 19, 3), "change_text", tfElem).initialize(gui);

        this.changeTxtTooltip = loadTooltip(gui, data, "changeTextfieldTtip", this.changeTxt, "info.change_info");
        this.claimBtnTooltip = loadTooltip(gui, data, "claimButtonTtip", this.claimBtn, "info.claim");
        this.changeBtnTooltip = loadTooltip(gui, data, "changeButtonTtip", this.changeBtn, "info.change_owner");
        this.acceptBtnTooltip = loadTooltip(gui, data, "acceptButtonTtip", this.acceptBtn, "info.change_accept");
        this.cancelBtnTooltip = loadTooltip(gui, data, "cancelButtonTtip", this.cancelBtn, "info.change_cancel");
    }

    private static GuiElementInst loadTooltip(IGui gui, JsonObject parentData, String childKey, GuiElementInst forElem, String text) {
        JsonObject childData = MiscUtils.get(parentData.getAsJsonObject(childKey), JsonObject::new);
        JsonUtils.addDefaultJsonProperty(childData, "size", new int[] {forElem.get().getWidth(), forElem.get().getHeight()});
        JsonUtils.addDefaultJsonProperty(childData, "for", forElem.id);
        JsonUtils.addDefaultJsonProperty(childData, "text", Lang.TCU_TEXT.get(text));
        Tooltip elem = Tooltip.Builder.fromJson(gui, childData);
        return new GuiElementInst(ITcuScreen.getOffset(childData), elem).initialize(gui);
    }

    private static GuiElementInst loadBtn(IGui gui, JsonObject parentData, String childKey, String id, int width, int height, int u, int v) {
        JsonObject childData = MiscUtils.get(parentData.getAsJsonObject(childKey), JsonObject::new);
        JsonUtils.addDefaultJsonProperty(childData, "size", new int[] {width, height});
        JsonUtils.addDefaultJsonProperty(childData, "uvSize", new int[] {width, height});
        JsonUtils.addDefaultJsonProperty(childData, "uvEnabled", new int[] {u, v});
        JsonUtils.addDefaultJsonProperty(childData, "useVanillaTexture", false);
        ButtonSL elem = ButtonSL.Builder.fromJson(gui, childData);
        return new GuiElementInst(ITcuScreen.getOffset(childData), id, elem).initialize(gui);
    }

    @Override
    protected GuiElementInst loadIcon(IGui gui, JsonObject data) {
        JsonUtils.addDefaultJsonProperty(data, "size", this.getDefaultIconSize());
        if( !data.has("uvs") ) {
            JsonArray uvs = new JsonArray();
            uvs.add(uv(120, 0));
            uvs.add(uv(120, 16));
            uvs.add(uv(120, 32));
            uvs.add(uv(136, 0));
            uvs.add(uv(136, 16));
            uvs.add(uv(136, 32));
            uvs.add(uv(152, 0));
            data.add("uvs", uvs);
        }
        if( !data.has("playerUvIds") ) {
            JsonObject puv = new JsonObject();
            JsonUtils.addJsonProperty(puv, "SanAndreaP", 6);
            data.add("playerUvIds", puv);
        }

        PlayerIconInfo iconElem = PlayerIconInfo.Builder.fromJson(gui, data);

        return new GuiElementInst(JsonUtils.getIntArray(data.get(ITcuScreen.OFFSET_JSON_ELEM), new int[] { 0, 0}, Range.is(2)), iconElem).initialize(gui);
    }

    private static JsonArray uv(int u, int v) {
        JsonArray jarr = new JsonArray();
        jarr.add(u);
        jarr.add(v);

        return jarr;
    }
}
