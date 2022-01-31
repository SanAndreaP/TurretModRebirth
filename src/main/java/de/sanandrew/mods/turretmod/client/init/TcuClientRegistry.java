package de.sanandrew.mods.turretmod.client.init;

import de.sanandrew.mods.turretmod.api.client.tcu.ILabelRegistry;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuClientRegistry;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuInfoProvider;
import de.sanandrew.mods.turretmod.api.client.tcu.ITcuScreen;
import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuInfoPage;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuScreen;
import de.sanandrew.mods.turretmod.client.gui.tcu.TcuTargetPage;
import de.sanandrew.mods.turretmod.client.gui.tcu.info.AmmoProvider;
import de.sanandrew.mods.turretmod.client.gui.tcu.info.HealthProvider;
import de.sanandrew.mods.turretmod.client.gui.tcu.info.NameProvider;
import de.sanandrew.mods.turretmod.client.gui.tcu.info.OwnerProvider;
import de.sanandrew.mods.turretmod.client.gui.tcu.info.TargetProvider;
import de.sanandrew.mods.turretmod.client.renderer.turret.LabelRegistry;
import de.sanandrew.mods.turretmod.item.TurretControlUnit;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public final class TcuClientRegistry
        implements ITcuClientRegistry
{
    public static final TcuClientRegistry INSTANCE = new TcuClientRegistry();
    private static final Map<Integer, List<ITcuInfoProvider>> TCU_INFO_PROVIDERS = new TreeMap<>();

    private TcuClientRegistry() { }

    @Override
    public ILabelRegistry getLabelRegistry() {
        return LabelRegistry.INSTANCE;
    }

    @Override
    public void registerTcuScreen(@Nonnull ResourceLocation id, Supplier<ItemStack> iconSupplier,
                                  Function<ContainerScreen<TcuContainer>, ITcuScreen> screenProvider)
    {
        TcuScreen.registerScreen(id, iconSupplier, screenProvider);
    }

    @Override
    public void registerTcuInfoProvider(int priority, ITcuInfoProvider provider) {
        TCU_INFO_PROVIDERS.computeIfAbsent(priority, i -> new ArrayList<>()).add(provider);
    }

    public static void registerTcuClient(ITcuClientRegistry registry) {
        registry.registerTcuScreen(TurretControlUnit.INFO, new SimpleItem(Items.BOOK), TcuInfoPage::new);
        registry.registerTcuScreen(TurretControlUnit.TARGETS_CREATURES, new SimpleItem(Items.ZOMBIE_HEAD), TcuTargetPage.Creatures::new);
        registry.registerTcuScreen(TurretControlUnit.TARGETS_PLAYERS, de.sanandrew.mods.turretmod.client.init.PlayerHeads::getRandomSkull, s -> null);

        registry.registerTcuInfoProvider(0, new NameProvider());
        registry.registerTcuInfoProvider(1, new HealthProvider());
        registry.registerTcuInfoProvider(2, new AmmoProvider());
        registry.registerTcuInfoProvider(3, new TargetProvider());
        registry.registerTcuInfoProvider(4, new OwnerProvider());
    }

    public static List<ITcuInfoProvider> getProviders() {
        return TCU_INFO_PROVIDERS.values().stream().collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
    }

    private static final class SimpleItem
            implements Supplier<ItemStack>
    {
        private final ItemStack stack;

        private SimpleItem(Item item) {
            this.stack = new ItemStack(item);
        }

        @Override
        public ItemStack get() {
            return this.stack;
        }
    }
}
