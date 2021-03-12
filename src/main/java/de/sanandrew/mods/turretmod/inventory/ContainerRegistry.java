package de.sanandrew.mods.turretmod.inventory;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TmrConstants.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ContainerRegistry
{
    public static final ContainerType<ContainerElectrolyteGenerator> ELECTROLYTE_GENERATOR = new ContainerType<>(ContainerElectrolyteGenerator::new);

    @SubscribeEvent
    public static void registerContainer(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(ELECTROLYTE_GENERATOR.setRegistryName(TmrConstants.ID, "electrolyte_generator"));
    }
}
