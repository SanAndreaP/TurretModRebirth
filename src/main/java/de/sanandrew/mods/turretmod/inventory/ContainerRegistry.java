package de.sanandrew.mods.turretmod.inventory;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TmrConstants.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ContainerRegistry
{
    public static final ContainerType<ElectrolyteGeneratorContainer> ELECTROLYTE_GENERATOR = new ContainerType<>(ElectrolyteGeneratorContainer.Factory.INSTANCE);

    @SubscribeEvent
    public static void registerContainer(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(ELECTROLYTE_GENERATOR.setRegistryName(TmrConstants.ID, "electrolyte_generator"));
    }
}
