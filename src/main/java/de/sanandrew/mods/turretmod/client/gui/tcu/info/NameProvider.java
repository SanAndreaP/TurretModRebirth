package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.TextField;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.TcuInfoValue;
import de.sanandrew.mods.turretmod.init.Lang;
import de.sanandrew.mods.turretmod.network.TurretPlayerActionPacket;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NameProvider
        implements ITcuInfoProvider.Custom
{
    private boolean nameSet = false;
    private boolean nameChanged = false;
    private TextField txtField;

    @Override
    public String getName() {
        return "name";
    }

    @Nullable
    @Override
    public ITextComponent getLabel() {
        return new TranslationTextComponent(Lang.TCU_TEXT.get("info.name.tooltip"));
    }

    @Override
    public void tick(ITurretEntity turret) {
        if( !this.nameSet ) {
            MiscUtils.accept(turret.get(), e -> {
                this.nameSet = true;
                this.txtField.setMaxStringLength(260);
                this.txtField.setText(MiscUtils.get(MiscUtils.apply(e.getCustomName(), ITextComponent::getString), ""));
                this.txtField.setResponder(s -> this.nameChanged = true);
            });
        } else if( this.nameChanged && !this.txtField.isFocused() ) {
            TurretPlayerActionPacket.rename(turret, this.txtField.getText());
            this.nameChanged = false;
        }
    }

    @Override
    public void onClose(Screen gui, ITurretEntity turret) {
        if( this.nameChanged && !this.txtField.isFocused() ) {
            TurretPlayerActionPacket.rename(turret, this.txtField.getText());
        }
    }

    @Nonnull
    @Override
    public GuiElementInst[] buildCustomElements(IGui gui, JsonObject data, int maxWidth, int maxHeight) {
        JsonObject txtData = MiscUtils.get(data.getAsJsonObject("textField"), JsonObject::new);

        JsonUtils.addDefaultJsonProperty(txtData, "size", new int[] { maxWidth - 20, 10 });

        int[] pos = TcuInfoValue.off(txtData, () -> new int[]{ 2, 3 });
        GuiElementInst txt = new GuiElementInst(pos, new TextField(), txtData).initialize(gui);

        this.txtField = txt.get(TextField.class);

        return new GuiElementInst[] { txt };
    }

    @Override
    public void render(Screen gui, MatrixStack stack, float partTicks, int x, int y, double mouseX, double mouseY, int maxWidth, int maxHeight) {
        // this does not need to render anything, as it just adds a text field element
    }

    @Nonnull
    @Override
    public ITexture buildIcon() {
        return ITexture.icon((mw, mh) -> new int[] { 86, 16 });
    }
}
