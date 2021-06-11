package de.sanandrew.mods.turretmod.client.init;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.ElectrolyteGeneratorScreen;
import de.sanandrew.mods.turretmod.client.gui.element.ElectrolyteBar;
import de.sanandrew.mods.turretmod.client.model.ModelRegistry;
import de.sanandrew.mods.turretmod.client.renderer.RenderClassProvider;
import de.sanandrew.mods.turretmod.client.renderer.color.TippedBoltColor;
import de.sanandrew.mods.turretmod.init.IProxy;
import de.sanandrew.mods.turretmod.init.IRenderClassProvider;
import de.sanandrew.mods.turretmod.inventory.ContainerRegistry;
import de.sanandrew.mods.turretmod.item.TurretControlUnit;
import de.sanandrew.mods.turretmod.item.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.item.ammo.Ammunitions;
import de.sanandrew.mods.turretmod.world.PlayerList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class ClientProxy
        implements IProxy
{
    @Override
    public void setupClient(FMLClientSetupEvent event) {
        ScreenManager.register(ContainerRegistry.ELECTROLYTE_GENERATOR, ElectrolyteGeneratorScreen::new);

        GuiDefinition.TYPES.put(ElectrolyteBar.ID, ElectrolyteBar::new);

        ModelRegistry.registerModels(event);

        Minecraft.getInstance().getItemColors().register(new TippedBoltColor(), AmmunitionRegistry.INSTANCE.getItem(Ammunitions.TIPPED_BOLT.getId()).getItem());
//        Minecraft.getInstance().getItemColors().register(new ColorCartridge(), ItemRegistry.AMMO_CARTRIDGE);
    }

    @Override
    public void fillPlayerListClient(Map<UUID, ITextComponent> map) {
        PlayerList.INSTANCE.putPlayersClient(map);
    }

    @Override
    public boolean checkTurretGlowing(ITurretEntity turretInst) {
        Minecraft mc = Minecraft.getInstance();
        if( mc.crosshairPickEntity != turretInst.get() ) {
            return TurretControlUnit.isHeldTcuBoundToTurret(Minecraft.getInstance().player, turretInst);
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

    @Override
    public PlayerEntity getNetworkPlayer(Supplier<NetworkEvent.Context> networkContextSupplier) {
        return Minecraft.getInstance().player;
    }

    @Override
    public IRenderClassProvider getRenderClassProvider() {
        return RenderClassProvider.INSTANCE;
    }
}
