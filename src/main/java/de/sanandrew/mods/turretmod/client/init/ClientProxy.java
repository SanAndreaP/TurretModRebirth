package de.sanandrew.mods.turretmod.client.init;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.client.gui.ElectrolyteGeneratorScreen;
import de.sanandrew.mods.turretmod.client.gui.element.ElectrolyteBar;
import de.sanandrew.mods.turretmod.client.model.ModelRegistry;
import de.sanandrew.mods.turretmod.client.renderer.tileentity.ElectrolyteGeneratorRenderer;
import de.sanandrew.mods.turretmod.init.IProxy;
import de.sanandrew.mods.turretmod.inventory.ContainerRegistry;
import de.sanandrew.mods.turretmod.world.PlayerList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;
import java.util.UUID;

public class ClientProxy
        implements IProxy
{
    @Override
    public void setupClient(FMLClientSetupEvent event) {
        ScreenManager.register(ContainerRegistry.ELECTROLYTE_GENERATOR, ElectrolyteGeneratorScreen::new);

        GuiDefinition.TYPES.put(ElectrolyteBar.ID, ElectrolyteBar::new);

        ModelRegistry.registerModels(event);
    }

    @Override
    public void fillPlayerListClient(Map<UUID, ITextComponent> map) {
        PlayerList.INSTANCE.putPlayersClient(map);
    }

    @Override
    public boolean checkTurretGlowing(ITurretInst turretInst) {
        Minecraft mc = Minecraft.getInstance();
        if( mc.crosshairPickEntity != turretInst.get() ) {
            //TODO: reimplement TCU
//            return ItemTurretControlUnit.isHeldTcuBoundToTurret(Minecraft.getMinecraft().player, turret);
        }

        return false;
    }

    public static void initGuiDef(GuiDefinition guiDef, IGui gui) {
        if( guiDef == null ) {
            gui.get().getMinecraft().setScreen(null);
            return;
        }

        guiDef.initGui(gui);
    }

    public static void drawGDBackground(GuiDefinition guiDef, MatrixStack stack, IGui gui, float partTicks, int mouseX, int mouseY) {
        stack.pushPose();
        stack.translate(gui.getScreenPosX(), gui.getScreenPosY(), 0.0F);
        guiDef.drawBackground(gui, stack, mouseX, mouseY, partTicks);
        stack.popPose();
    }
}
