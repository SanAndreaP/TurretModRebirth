package de.sanandrew.mods.turretmod.inventory;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.inventory.container.AmmoCartridgeContainer;
import de.sanandrew.mods.turretmod.inventory.container.ElectrolyteGeneratorContainer;
import de.sanandrew.mods.turretmod.inventory.container.TcuContainerFactory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TmrConstants.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ContainerRegistry
{
    public static final ContainerType<ElectrolyteGeneratorContainer> ELECTROLYTE_GENERATOR = new ContainerType<>(ElectrolyteGeneratorContainer.Factory.INSTANCE);
    public static final ContainerType<AmmoCartridgeContainer> AMMO_CARTRIGE = new ContainerType<>(AmmoCartridgeContainer.Factory.INSTANCE);
    public static final ContainerType<TcuContainer>           TCU = new ContainerType<>(TcuContainerFactory.INSTANCE);

    @SubscribeEvent
    public static void registerContainer(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(ELECTROLYTE_GENERATOR.setRegistryName(TmrConstants.ID, "electrolyte_generator"));
        event.getRegistry().register(AMMO_CARTRIGE.setRegistryName(TmrConstants.ID, "ammo_cartridge"));
        event.getRegistry().register(TCU.setRegistryName(TmrConstants.ID, "turret_control_unit"));
    }
}
