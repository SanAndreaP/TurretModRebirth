package de.sanandrew.mods.turretmod.inventory;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.inventory.container.AmmoCartridgeContainer;
import de.sanandrew.mods.turretmod.inventory.container.TurretCrateContainer;
import de.sanandrew.mods.turretmod.inventory.container.ElectrolyteGeneratorContainer;
import de.sanandrew.mods.turretmod.inventory.container.TcuContainerFactory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ContainerRegistry
{
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, TmrConstants.ID);

    public static final ContainerType<ElectrolyteGeneratorContainer> ELECTROLYTE_GENERATOR = new ContainerType<>(ElectrolyteGeneratorContainer.Factory.INSTANCE);
    public static final ContainerType<AmmoCartridgeContainer> AMMO_CARTRIGE = new ContainerType<>(AmmoCartridgeContainer.Factory.INSTANCE);
    public static final ContainerType<TcuContainer>         TCU          = new ContainerType<>(TcuContainerFactory.INSTANCE);
    public static final ContainerType<TurretCrateContainer> TURRET_CRATE = new ContainerType<>(TurretCrateContainer.Factory.INSTANCE);

    private ContainerRegistry() { /* no-op */ }

    public static void register(IEventBus bus) {
        CONTAINERS.register("electrolyte_generator", () -> ELECTROLYTE_GENERATOR);
        CONTAINERS.register("ammo_cartridge", () -> AMMO_CARTRIGE);
        CONTAINERS.register("turret_control_unit", () -> TCU);
        CONTAINERS.register("turret_crate", () -> TURRET_CRATE);

        CONTAINERS.register(bus);
    }
}
