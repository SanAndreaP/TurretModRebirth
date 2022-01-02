package de.sanandrew.mods.turretmod.client.gui.tcu.info;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.TextField;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IIcon;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.TcuInfoValue;
import de.sanandrew.mods.turretmod.init.Lang;
import de.sanandrew.mods.turretmod.network.TurretPlayerActionPacket;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;

public class NameProvider
        implements ITcuInfoProvider
{
//    private boolean nameSet = false;
//    private boolean nameChanged = false;
//    private TextField txtField;

    @Nonnull
    @Override
    public String getName() {
        return "name";
    }

//    @Nonnull
//    @Override
//    public ITextComponent getLabel() {
//        return new TranslationTextComponent(Lang.TCU_TEXT.get("info.name.tooltip"));
//    }
//
//    @Override
//    public void tick(IGui gui, ITurretEntity turret) {
////        if( !this.nameSet ) {
////            MiscUtils.accept(turret.get(), e -> {
////                this.nameSet = true;
////                this.txtField.setMaxStringLength(260);
////                this.txtField.setText(MiscUtils.get(MiscUtils.apply(e.getCustomName(), ITextComponent::getString), ""));
////                this.txtField.setResponder(s -> this.nameChanged = true);
////            });
////        } else if( this.nameChanged && !this.txtField.isFocused() ) {
////            TurretPlayerActionPacket.rename(turret, this.txtField.getText());
////            this.nameChanged = false;
////        }
//    }
//
//    @Override
//    public void onClose(IGui gui, ITurretEntity turret) {
////        if( this.nameChanged ) {
////            TurretPlayerActionPacket.rename(turret, this.txtField.getText());
////        }
//    }
//
////    @Nonnull
////    @Override
////    public GuiElementInst[] buildCustomElements(IGui gui, JsonObject data, int maxWidth, int maxHeight) {
////        this.nameSet = false;
////        this.nameChanged = false;
////
////        JsonObject txtData = MiscUtils.get(data.getAsJsonObject("textField"), JsonObject::new);
////
////        JsonUtils.addDefaultJsonProperty(txtData, "size", new int[] { maxWidth - 24, 10 });
////
////        int[] pos = TcuInfoValue.off(txtData, () -> new int[]{ 20, 3 });
////        GuiElementInst txt = new GuiElementInst(pos, new TextField(), txtData).initialize(gui);
////
////        this.txtField = txt.get(TextField.class);
////
////        return new GuiElementInst[] { txt };
////    }
////
////    @Override
////    public boolean keyPressed(IGui gui, int keyCode, int scanCode, int modifiers) {
////        return this.txtField.keyPressed(gui, keyCode, scanCode, modifiers) || this.txtField.canConsumeInput();
////    }
//
//    @Nonnull
//    @Override
//    public IIcon getIcon() {
//        return IIcon.get((mw, mh) -> new int[] { 86, 0 });
//    }
}
