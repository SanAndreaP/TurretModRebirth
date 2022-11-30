package de.sanandrew.mods.turretmod.client.init;

import de.sanandrew.mods.sanlib.lib.client.gui.GuiDefinition;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.StackPanel;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Text;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.turret.IForcefield;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.client.event.SneakKeyHandler;
import de.sanandrew.mods.turretmod.client.gui.AmmoCartridgeScreen;
import de.sanandrew.mods.turretmod.client.gui.AssemblyFilterScreen;
import de.sanandrew.mods.turretmod.client.gui.ElectrolyteGeneratorScreen;
import de.sanandrew.mods.turretmod.client.gui.TurretAssemblyScreen;
import de.sanandrew.mods.turretmod.client.gui.TurretCrateScreen;
import de.sanandrew.mods.turretmod.client.gui.element.AssemblyFilterItems;
import de.sanandrew.mods.turretmod.client.gui.element.ErrorTooltip;
import de.sanandrew.mods.turretmod.client.gui.element.TurretCamElement;
import de.sanandrew.mods.turretmod.client.gui.element.nav.PageNavigation;
import de.sanandrew.mods.turretmod.client.gui.element.nav.PageNavigationTooltip;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.TcuInfo;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.TurretTypeName;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.levels.ModifierList;
import de.sanandrew.mods.turretmod.client.gui.element.tcu.targets.TargetList;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuScreen;
import de.sanandrew.mods.turretmod.client.model.ModelRegistry;
import de.sanandrew.mods.turretmod.client.renderer.RenderClassProvider;
import de.sanandrew.mods.turretmod.client.renderer.color.AmmoCartridgeColor;
import de.sanandrew.mods.turretmod.client.renderer.color.TippedBoltColor;
import de.sanandrew.mods.turretmod.client.renderer.turret.ForcefieldRender;
import de.sanandrew.mods.turretmod.client.shader.Shaders;
import de.sanandrew.mods.turretmod.init.IProxy;
import de.sanandrew.mods.turretmod.init.IRenderClassProvider;
import de.sanandrew.mods.turretmod.init.Lang;
import de.sanandrew.mods.turretmod.init.TurretModRebirth;
import de.sanandrew.mods.turretmod.inventory.ContainerRegistry;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.item.TurretControlUnit;
import de.sanandrew.mods.turretmod.item.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.item.ammo.Ammunitions;
import de.sanandrew.mods.turretmod.network.OpenRemoteTcuGuiPacket;
import de.sanandrew.mods.turretmod.tileentity.assembly.AssemblyManager;
import de.sanandrew.mods.turretmod.world.PlayerList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
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
        ScreenManager.register(ContainerRegistry.ASSEMBLY, TurretAssemblyScreen::new);
        ScreenManager.register(ContainerRegistry.ASSEMBLY_FILTER, AssemblyFilterScreen::new);

        GuiDefinition.TYPES.put(TurretTypeName.ID, TurretTypeName.Builder::fromJson);
        GuiDefinition.TYPES.put(PageNavigation.ID, PageNavigation.Builder::fromJson);
        GuiDefinition.TYPES.put(PageNavigationTooltip.ID, PageNavigationTooltip.Builder::fromJson);
        GuiDefinition.TYPES.put(TurretCamElement.ID, TurretCamElement.Builder::fromJson);
        GuiDefinition.TYPES.put(ErrorTooltip.ID, ErrorTooltip.Builder::fromJson);
        GuiDefinition.TYPES.put(TcuInfo.ID, TcuInfo.Builder::fromJson);
        GuiDefinition.TYPES.put(TargetList.ID, TargetList.Builder::fromJson);
        GuiDefinition.TYPES.put(ModifierList.ID, ModifierList.Builder::fromJson);
        GuiDefinition.TYPES.put(AssemblyFilterItems.ID, AssemblyFilterItems.Builder::fromJson);

        ModelRegistry.registerModels();
        Minecraft.getInstance().execute(Shaders::initShaders);
        PlayerHeads.preLoadPlayerHeadsAsync();

        Minecraft.getInstance().getItemColors().register(new TippedBoltColor(), AmmunitionRegistry.INSTANCE.getItem(Ammunitions.TIPPED_BOLT.getId()).getItem());
        Minecraft.getInstance().getItemColors().register(new AmmoCartridgeColor(), ItemRegistry.AMMO_CARTRIDGE);

        TurretModRebirth.PLUGINS.forEach(plugin -> plugin.registerTcuClient(TcuClientRegistry.INSTANCE));
    }

    @Override
    public void fillPlayerListClient(Map<UUID, PlayerList.PlayerData> map) {
        PlayerList.syncPlayersClient(map);
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
    public void openTcuGuiRemote(ItemStack stack, ITurretEntity turret, ResourceLocation type, boolean initial) {
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
            TurretModRebirth.NETWORK.sendToServer(new OpenRemoteTcuGuiPacket(turret, tcuHeld, type, initial));
        }
    }

    @Override
    public boolean isSneakPressed() {
        return SneakKeyHandler.isSneakPressed();
    }

    @Override
    public boolean hasClientForcefield(ITurretEntity turretEntity, Class<? extends IForcefield> forcefieldClass) {
        return ForcefieldRender.INSTANCE.hasForcefield(turretEntity.get(), forcefieldClass);
    }

    @Override
    public void addClientForcefield(ITurretEntity turretEntity, IForcefield forcefield) {
        ForcefieldRender.INSTANCE.addForcefieldRenderer(turretEntity.get(), forcefield);
    }

    @Override
    public void removeClientForcefield(ITurretEntity turretEntity, Class<? extends IForcefield> forcefieldClass) {
        ForcefieldRender.INSTANCE.removeForcefieldRenderer(turretEntity.get(), forcefieldClass);
    }

    @Override
    public MinecraftServer getServer(World level) {
        return Minecraft.getInstance().getSingleplayerServer();
    }

    public static void buildItemTooltip(IGui gui, StackPanel p, ItemStack stack, boolean addCount, boolean doSetup, GuiElementInst... additionalLines) {
        final IntUnaryOperator yOffGetter = tti -> {
            if( tti == 1 ) {
                return 4;
            }

            return tti > 1 ? 2 : 0;
        };

        p.clear();
        if( ItemStackUtils.isValid(stack) ) {
            int tti = 0;
            for( ITextComponent tc : stack.getTooltipLines(gui.get().getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL) ) {
                if( addCount && tti == 0 ) {
                    tc = new TranslationTextComponent(Lang.GUI_ITEM_TITLE_COUNT.get(), String.format("%d", stack.getCount()), tc);
                }
                Text.Builder tb = new Text.Builder(tc);
                tb.shadow(true);
                tb.color(0xFFA0A0A0);

                GuiElementInst e = new GuiElementInst(new int[] {0, yOffGetter.applyAsInt(tti)}, tb.get(gui)).initialize(gui);
                if( doSetup ) {
                    e.get().setup(gui, e);
                }
                p.add(e);
                tti++;
            }
            for( GuiElementInst elem : additionalLines ) {
                elem.pos[1] += yOffGetter.applyAsInt(tti);
                if( doSetup ) {
                    elem.get().setup(gui, elem);
                }
                p.add(elem);
                tti++;
            }
        }

        if( doSetup ) {
            p.update();
        }
    }
}
