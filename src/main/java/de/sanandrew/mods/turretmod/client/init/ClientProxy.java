package de.sanandrew.mods.turretmod.client.init;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.gui.AmmoCartridgeScreen;
import de.sanandrew.mods.turretmod.client.gui.ElectrolyteGeneratorScreen;
import de.sanandrew.mods.turretmod.client.gui.TurretCrateScreen;
import de.sanandrew.mods.turretmod.client.gui.element.ElectrolyteBar;
import de.sanandrew.mods.turretmod.client.gui.element.ErrorTooltip;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.TcuInfo;
import de.sanandrew.mods.turretmod.client.gui.element.TurretCamElement;
import de.sanandrew.mods.turretmod.client.gui.element.TurretName;
import de.sanandrew.mods.turretmod.client.gui.element.nav.PageNavigation;
import de.sanandrew.mods.turretmod.client.gui.element.nav.PageNavigationTooltip;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuScreen;
import de.sanandrew.mods.turretmod.client.model.ModelRegistry;
import de.sanandrew.mods.turretmod.client.renderer.RenderClassProvider;
import de.sanandrew.mods.turretmod.client.renderer.color.AmmoCartridgeColor;
import de.sanandrew.mods.turretmod.client.renderer.color.TippedBoltColor;
import de.sanandrew.mods.turretmod.client.shader.Shaders;
import de.sanandrew.mods.turretmod.init.IProxy;
import de.sanandrew.mods.turretmod.init.IRenderClassProvider;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.inventory.ContainerRegistry;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.TurretControlUnit;
import de.sanandrew.mods.turretmod.item.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.item.ammo.Ammunitions;
import de.sanandrew.mods.turretmod.network.OpenRemoteTcuGuiPacket;
import de.sanandrew.mods.turretmod.world.PlayerList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
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
        ScreenManager.register(ContainerRegistry.AMMO_CARTRIGE, AmmoCartridgeScreen::new);
        ScreenManager.register(ContainerRegistry.TCU, TcuScreen::new);
        ScreenManager.register(ContainerRegistry.TURRET_CRATE, TurretCrateScreen::new);

        GuiDefinition.TYPES.put(ElectrolyteBar.ID, ElectrolyteBar.Builder::fromJson);
        GuiDefinition.TYPES.put(TurretName.ID, TurretName.Builder::fromJson);
        GuiDefinition.TYPES.put(PageNavigation.ID, PageNavigation.Builder::fromJson);
        GuiDefinition.TYPES.put(PageNavigationTooltip.ID, PageNavigationTooltip.Builder::fromJson);
        GuiDefinition.TYPES.put(TurretCamElement.ID, TurretCamElement.Builder::fromJson);
        GuiDefinition.TYPES.put(ErrorTooltip.ID, ErrorTooltip.Builder::fromJson);
        GuiDefinition.TYPES.put(TcuInfo.ID, TcuInfo.Builder::fromJson);

        ModelRegistry.registerModels();
        Minecraft.getInstance().execute(Shaders::initShaders);
        PlayerHeads.preLoadPlayerHeadsAsync();

        Minecraft.getInstance().getItemColors().register(new TippedBoltColor(), AmmunitionRegistry.INSTANCE.getItem(Ammunitions.TIPPED_BOLT.getId()).getItem());
        Minecraft.getInstance().getItemColors().register(new AmmoCartridgeColor(), ItemRegistry.AMMO_CARTRIDGE);

        TurretModRebirth.PLUGINS.forEach(plugin -> plugin.registerTcuClient(TcuClientRegistry.INSTANCE));
    }

    @Override
    public void fillPlayerListClient(Map<UUID, ITextComponent> map) {
        PlayerList.putPlayersClient(map);
    }

    @Override
    public boolean checkTurretGlowing(ITurretEntity turretInst) {
        Minecraft mc = Minecraft.getInstance();
        if( mc.crosshairPickEntity != turretInst.get() ) {
            return TurretControlUnit.isHeldTcuBoundToTurret(Minecraft.getInstance().player, turretInst);
        }

        return false;
    }

    @Override
    public PlayerEntity getNetworkPlayer(Supplier<NetworkEvent.Context> networkContextSupplier) {
        return Minecraft.getInstance().player;
    }

    @Override
    public IRenderClassProvider getRenderClassProvider() {
        return RenderClassProvider.INSTANCE;
    }

    @Override
    public void openTcuGuiRemote(ItemStack stack, ITurretEntity turret, ResourceLocation type) {
        PlayerEntity player = Minecraft.getInstance().player;
        if( player == null ) {
            return;
        }

        Hand tcuHeld = null;
        if( ItemStackUtils.areEqual(player.getItemInHand(Hand.MAIN_HAND), stack) ) {
            tcuHeld = Hand.MAIN_HAND;
        } else if( ItemStackUtils.areEqual(player.getItemInHand(Hand.OFF_HAND), stack) ) {
            tcuHeld = Hand.OFF_HAND;
        }

        if( tcuHeld != null ) {
            TurretModRebirth.NETWORK.sendToServer(new OpenRemoteTcuGuiPacket(turret, tcuHeld, type));
        }
    }
}
